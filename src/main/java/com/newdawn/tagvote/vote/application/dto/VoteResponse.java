package com.newdawn.tagvote.vote.application.dto;

import com.newdawn.tagvote.vote.domain.Vote;
import com.newdawn.tagvote.vote.domain.VoteStatus;

import java.time.LocalDateTime;

public record VoteResponse(
        Long id,
        Long createdByUserId,
        String name,
        VoteStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static VoteResponse from(final Vote vote) {
        return new VoteResponse(
                vote.getId(),
                vote.getCreatedBy().getId(),
                vote.getName(),
                vote.getStatus(),
                vote.getCreatedAt(),
                vote.getUpdatedAt()
        );
    }
}
