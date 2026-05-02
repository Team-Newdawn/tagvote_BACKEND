package com.newdawn.tagvote.question.presentation;

import com.newdawn.tagvote.question.application.QuestionService;
import com.newdawn.tagvote.question.application.dto.QuestionCreateRequest;
import com.newdawn.tagvote.question.application.dto.QuestionResponse;
import com.newdawn.tagvote.question.application.dto.QuestionUpdateRequest;
import com.newdawn.tagvote.question.application.dto.QuestionWithTagsResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(final QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/api/questions")
    public ResponseEntity<QuestionResponse> create(@Valid @RequestBody final QuestionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.create(request));
    }

    @GetMapping("/api/votes/{voteId}/questions")
    public ResponseEntity<List<QuestionWithTagsResponse>> getAccessibleQuestionsByVote(@PathVariable final Long voteId) {
        return ResponseEntity.ok(questionService.getAccessibleQuestionsByVote(voteId));
    }

    @GetMapping("/api/questions/{questionId}")
    public ResponseEntity<QuestionWithTagsResponse> getAccessibleQuestion(@PathVariable final Long questionId) {
        return ResponseEntity.ok(questionService.getAccessibleQuestion(questionId));
    }

    @PatchMapping("/api/questions/{questionId}")
    public ResponseEntity<QuestionResponse> update(
            @PathVariable final Long questionId,
            @Valid @RequestBody final QuestionUpdateRequest request
    ) {
        return ResponseEntity.ok(questionService.update(questionId, request));
    }

    @DeleteMapping("/api/questions/{questionId}")
    public ResponseEntity<Void> delete(@PathVariable final Long questionId) {
        questionService.delete(questionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/public/votes/{voteId}/questions")
    public ResponseEntity<List<QuestionWithTagsResponse>> getPublicQuestionList(@PathVariable final Long voteId) {
        return ResponseEntity.ok(questionService.getPublicQuestionList(voteId));
    }
}
