package com.newdawn.tagvote.question.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record QuestionCreateRequest(
        @NotNull Long voteId,
        String title,
        String detail,
        @NotBlank String imageUrl,
        @NotNull @Positive Long imageRatio
) {
}
