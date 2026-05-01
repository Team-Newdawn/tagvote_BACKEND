package com.newdawn.tagvote.vote.application.dto;

import com.newdawn.tagvote.vote.domain.VoteStatus;

public record VoteUpdateRequest(
        String name,
        VoteStatus status
) {
}
