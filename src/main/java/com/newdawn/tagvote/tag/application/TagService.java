package com.newdawn.tagvote.tag.application;

import com.newdawn.tagvote.question.domain.Question;
import com.newdawn.tagvote.question.domain.QuestionRepository;
import com.newdawn.tagvote.tag.application.dto.TagCreateRequest;
import com.newdawn.tagvote.tag.application.dto.TagCreatedEventResponse;
import com.newdawn.tagvote.tag.application.dto.TagResponse;
import com.newdawn.tagvote.tag.application.dto.TagUpdateRequest;
import com.newdawn.tagvote.tag.domain.Tag;
import com.newdawn.tagvote.tag.domain.TagFactory;
import com.newdawn.tagvote.tag.domain.TagRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final TagSseService tagSseService;

    public TagService(
            final TagRepository tagRepository,
            final QuestionRepository questionRepository,
            final TagSseService tagSseService
    ) {
        this.tagRepository = tagRepository;
        this.questionRepository = questionRepository;
        this.tagSseService = tagSseService;
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getTagsByQuestion(final Long questionId, final String requestSessionId) {
        requireQuestion(questionId);
        return tagRepository.findAllByQuestionIdOrderByIdAsc(questionId).stream()
                .map(tag -> TagResponse.from(tag, requestSessionId))
                .toList();
    }

    @Transactional(readOnly = true)
    public TagResponse getTag(final Long tagId, final String requestSessionId) {
        return TagResponse.from(findTag(tagId), requestSessionId);
    }

    @Transactional
    public TagResponse create(final Long questionId, final TagCreateRequest request, final String requestSessionId) {
        Question question = requireQuestion(questionId);
        Tag tag = TagFactory.create(request, question, requestSessionId);
        Tag savedTag = tagRepository.saveAndFlush(tag);
        TagResponse response = TagResponse.from(savedTag, requestSessionId);

        Long voteId = question.getVote().getId();
        Long savedQuestionId = question.getId();
        publishAfterCommit(voteId, savedQuestionId, savedTag);

        return response;
    }

    @Transactional
    public TagResponse update(final Long tagId, final TagUpdateRequest request, final String requestSessionId) {
        Tag tag = findTag(tagId);

        if (request.type() != null) {
            tag.changeType(request.type());
        }
        if (StringUtils.hasText(request.data())) {
            tag.changeData(request.data());
        }
        if (request.duration() != null) {
            tag.changeDuration(request.duration());
        }
        if (request.locationX() != null) {
            tag.changeLocationX(request.locationX());
        }
        if (request.locationY() != null) {
            tag.changeLocationY(request.locationY());
        }

        return TagResponse.from(tagRepository.saveAndFlush(tag), requestSessionId);
    }

    @Transactional
    public void delete(final Long tagId, final String requestSessionId) {
        Tag tag = findTag(tagId);
        if (!tag.belongsToSession(requestSessionId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can delete only your own tag");
        }
        tag.getQuestion().removeTag(tag);
        tagRepository.delete(tag);
    }

    private Question requireQuestion(final Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
    }

    private Tag findTag(final Long tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
    }

    private void publishAfterCommit(final Long voteId, final Long questionId, final Tag savedTag) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            tagSseService.publishTagCreated(voteId, questionId, savedTag);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                tagSseService.publishTagCreated(voteId, questionId, savedTag);
            }
        });
    }
}
