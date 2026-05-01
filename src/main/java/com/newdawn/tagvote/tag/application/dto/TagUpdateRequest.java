package com.newdawn.tagvote.tag.application.dto;

import com.newdawn.tagvote.tag.domain.TagType;
import jakarta.validation.constraints.PositiveOrZero;

public record TagUpdateRequest(
        TagType type,
        String data,
        @PositiveOrZero Integer duration,
        Float locationX,
        Float locationY
) {
}
