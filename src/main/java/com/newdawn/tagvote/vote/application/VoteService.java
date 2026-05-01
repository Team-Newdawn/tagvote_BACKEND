package com.newdawn.tagvote.vote.application;

import com.newdawn.tagvote.global.security.CurrentUserProvider;
import com.newdawn.tagvote.global.security.SessionUserPrincipal;
import com.newdawn.tagvote.question.application.dto.QuestionResponse;
import com.newdawn.tagvote.question.application.dto.QuestionWithTagsResponse;
import com.newdawn.tagvote.question.domain.Question;
import com.newdawn.tagvote.question.domain.QuestionRepository;
import com.newdawn.tagvote.tag.application.dto.TagResponse;
import com.newdawn.tagvote.user.domain.User;
import com.newdawn.tagvote.user.domain.UserRepository;
import com.newdawn.tagvote.vote.application.dto.VoteCreateRequest;
import com.newdawn.tagvote.vote.application.dto.VoteDisplayResponse;
import com.newdawn.tagvote.vote.application.dto.VoteResponse;
import com.newdawn.tagvote.vote.application.dto.VoteUpdateRequest;
import com.newdawn.tagvote.vote.domain.Vote;
import com.newdawn.tagvote.vote.domain.VoteFactory;
import com.newdawn.tagvote.vote.domain.VoteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final CurrentUserProvider currentUserProvider;

    public VoteService(
            final VoteRepository voteRepository,
            final UserRepository userRepository,
            final QuestionRepository questionRepository,
            final CurrentUserProvider currentUserProvider
    ) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public VoteResponse create(final VoteCreateRequest request) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        User createdBy = userRepository.findById(principal.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Vote vote = VoteFactory.create(request, createdBy);
        return VoteResponse.from(voteRepository.saveAndFlush(vote));
    }

    @Transactional(readOnly = true)
    public List<VoteResponse> getAllAccessibleVotes() {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();

        List<Vote> votes = principal.isAdmin()
                ? voteRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                : voteRepository.findAllByCreatedByIdOrderByIdAsc(principal.userId());

        return votes.stream().map(VoteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public VoteResponse getAccessibleVote(final Long voteId) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        return VoteResponse.from(findAccessibleVote(voteId, principal));
    }

    @Transactional
    public VoteResponse update(final Long voteId, final VoteUpdateRequest request) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        Vote vote = findAccessibleVote(voteId, principal);

        if (StringUtils.hasText(request.name())) {
            vote.changeName(request.name());
        }
        if (request.status() != null) {
            vote.changeStatus(request.status());
        }

        return VoteResponse.from(voteRepository.saveAndFlush(vote));
    }

    @Transactional
    public void delete(final Long voteId) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        voteRepository.delete(findAccessibleVote(voteId, principal));
    }

    @Transactional(readOnly = true)
    public VoteDisplayResponse getPublicVoteDisplay(final Long voteId) {
        Vote vote = findVote(voteId);
        List<QuestionWithTagsResponse> questions = questionRepository.findAllWithTagsByVoteId(voteId).stream()
                .map(this::toQuestionWithTagsResponse)
                .toList();

        return new VoteDisplayResponse(vote.getId(), vote.getName(), vote.getStatus(), questions);
    }

    private Vote findVote(final Long voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote not found"));
    }

    private Vote findAccessibleVote(final Long voteId, final SessionUserPrincipal principal) {
        Vote vote = findVote(voteId);
        if (!principal.isAdmin() && !vote.getCreatedBy().getId().equals(principal.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this vote");
        }
        return vote;
    }

    private QuestionWithTagsResponse toQuestionWithTagsResponse(final Question question) {
        return new QuestionWithTagsResponse(
                QuestionResponse.from(question),
                question.getTags().stream().map(TagResponse::from).toList()
        );
    }
}
