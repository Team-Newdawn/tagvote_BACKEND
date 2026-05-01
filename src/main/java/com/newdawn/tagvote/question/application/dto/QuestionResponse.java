package com.newdawn.tagvote.question.application.dto;

import com.newdawn.tagvote.question.domain.Question;

import java.time.LocalDateTime;

public record QuestionResponse(
        Long id,
        Long voteId,
        String title,
        String detail,
        String imageUrl,
        Long imageRatio,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static QuestionResponse from(final Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getVote().getId(),
                question.getTitle(),
                question.getDetail(),
                question.getImageUrl(),
                question.getImageRatio(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}
