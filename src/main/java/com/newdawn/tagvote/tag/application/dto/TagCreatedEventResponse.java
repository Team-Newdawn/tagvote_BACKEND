package com.newdawn.tagvote.tag.application.dto;

public record TagCreatedEventResponse(
        Long voteId,
        Long questionId,
        TagResponse tag
) {
}
