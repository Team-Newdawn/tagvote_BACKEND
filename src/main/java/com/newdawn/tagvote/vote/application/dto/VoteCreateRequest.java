package com.newdawn.tagvote.vote.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record VoteCreateRequest(
        @NotNull Long createdByUserId,
        @NotBlank String name
) {
}
