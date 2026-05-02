package com.newdawn.tagvote.question.application;

import com.newdawn.tagvote.global.security.CurrentUserProvider;
import com.newdawn.tagvote.global.security.SessionUserPrincipal;
import com.newdawn.tagvote.question.application.dto.QuestionCreateRequest;
import com.newdawn.tagvote.question.application.dto.QuestionResponse;
import com.newdawn.tagvote.question.application.dto.QuestionUpdateRequest;
import com.newdawn.tagvote.question.application.dto.QuestionWithTagsResponse;
import com.newdawn.tagvote.question.domain.Question;
import com.newdawn.tagvote.question.domain.QuestionFactory;
import com.newdawn.tagvote.question.domain.QuestionRepository;
import com.newdawn.tagvote.tag.application.dto.TagResponse;
import com.newdawn.tagvote.vote.domain.Vote;
import com.newdawn.tagvote.vote.domain.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final VoteRepository voteRepository;
    private final CurrentUserProvider currentUserProvider;

    public QuestionService(
            final QuestionRepository questionRepository,
            final VoteRepository voteRepository,
            final CurrentUserProvider currentUserProvider
    ) {
        this.questionRepository = questionRepository;
        this.voteRepository = voteRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public QuestionResponse create(final QuestionCreateRequest request) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        Vote vote = findVote(request.voteId());
        ensureVoteOwnerOrAdmin(vote, principal);

        Question question = QuestionFactory.create(request, vote);
        return QuestionResponse.from(questionRepository.saveAndFlush(question));
    }

    @Transactional(readOnly = true)
    public List<QuestionWithTagsResponse> getAccessibleQuestionsByVote(final Long voteId) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        Vote vote = findVote(voteId);
        ensureVoteOwnerOrAdmin(vote, principal);

        return questionRepository.findAllWithTagsByVoteId(voteId).stream()
                .map(this::toQuestionWithTagsResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionWithTagsResponse getAccessibleQuestion(final Long questionId) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        Question question = questionRepository.findWithVoteAndTagsById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        ensureVoteOwnerOrAdmin(question.getVote(), principal);
        return toQuestionWithTagsResponse(question);
    }

    @Transactional
    public QuestionResponse update(final Long questionId, final QuestionUpdateRequest request) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        ensureVoteOwnerOrAdmin(question.getVote(), principal);

        if (request.title() != null) {
            question.changeTitle(request.title());
        }
        if (request.detail() != null) {
            question.changeDetail(request.detail());
        }
        if (StringUtils.hasText(request.imageUrl())) {
            question.changeImageUrl(request.imageUrl());
        }
        if (request.imageRatio() != null) {
            question.changeImageRatio(request.imageRatio());
        }

        return QuestionResponse.from(questionRepository.saveAndFlush(question));
    }

    @Transactional
    public void delete(final Long questionId) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        ensureVoteOwnerOrAdmin(question.getVote(), principal);
        question.getVote().removeQuestion(question);
        questionRepository.delete(question);
    }

    @Transactional(readOnly = true)
    public List<QuestionWithTagsResponse> getPublicQuestionList(final Long voteId) {
        findVote(voteId);
        return questionRepository.findAllWithTagsByVoteId(voteId).stream()
                .map(this::toQuestionWithTagsResponse)
                .toList();
    }

    private Vote findVote(final Long voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote not found"));
    }

    private void ensureVoteOwnerOrAdmin(final Vote vote, final SessionUserPrincipal principal) {
        if (!principal.isAdmin() && !vote.getCreatedBy().getId().equals(principal.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this question");
        }
    }

    private QuestionWithTagsResponse toQuestionWithTagsResponse(final Question question) {
        return new QuestionWithTagsResponse(
                QuestionResponse.from(question),
                question.getTags().stream().map(TagResponse::from).toList()
        );
    }
}
