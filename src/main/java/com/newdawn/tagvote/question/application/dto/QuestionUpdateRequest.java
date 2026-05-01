package com.newdawn.tagvote.question.application.dto;

import jakarta.validation.constraints.Positive;

public record QuestionUpdateRequest(
        String title,
        String detail,
        String imageUrl,
        @Positive Long imageRatio
) {
}
