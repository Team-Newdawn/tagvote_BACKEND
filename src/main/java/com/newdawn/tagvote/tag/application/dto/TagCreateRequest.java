package com.newdawn.tagvote.tag.application.dto;

import com.newdawn.tagvote.tag.domain.TagType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TagCreateRequest(
        @NotNull Long questionId,
        TagType type,
        @NotBlank String data,
        @NotNull @PositiveOrZero Integer duration,
        Float locationX,
        Float locationY
) {
}
