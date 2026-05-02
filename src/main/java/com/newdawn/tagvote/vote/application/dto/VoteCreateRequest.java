package com.newdawn.tagvote.vote.application.dto;

import jakarta.validation.constraints.NotBlank;
public record VoteCreateRequest(
        Long createdByUserId,
        @NotBlank String name
) {
}
