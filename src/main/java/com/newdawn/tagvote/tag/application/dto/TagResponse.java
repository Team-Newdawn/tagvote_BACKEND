package com.newdawn.tagvote.tag.application.dto;

import com.newdawn.tagvote.tag.domain.Tag;
import com.newdawn.tagvote.tag.domain.TagType;

import java.time.LocalDateTime;

public record TagResponse(
        Long id,
        Long questionId,
        TagType type,
        String data,
        Integer duration,
        Float locationX,
        Float locationY,
        boolean isMine,
        boolean canDelete,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TagResponse from(final Tag tag, final String requestSessionId) {
        boolean isMine = tag.belongsToSession(requestSessionId);
        return new TagResponse(
                tag.getId(),
                tag.getQuestion().getId(),
                tag.getType(),
                tag.getData(),
                tag.getDuration(),
                tag.getLocationX(),
                tag.getLocationY(),
                isMine,
                isMine,
                tag.getCreatedAt(),
                tag.getUpdatedAt()
        );
    }
}
