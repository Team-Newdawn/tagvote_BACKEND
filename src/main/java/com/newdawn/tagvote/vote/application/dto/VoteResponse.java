package com.newdawn.tagvote.vote.application.dto;

import com.newdawn.tagvote.vote.domain.Vote;
import com.newdawn.tagvote.vote.domain.VoteStatus;

import java.time.LocalDateTime;

public record VoteResponse(
        Long id,
        Long createdByUserId,
        String name,
        VoteStatus status,
        boolean isMine,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static VoteResponse from(final Vote vote, final Long currentUserId) {
        return new VoteResponse(
                vote.getId(),
                vote.getCreatedBy().getId(),
                vote.getName(),
                vote.getStatus(),
                currentUserId != null && vote.getCreatedBy().getId().equals(currentUserId),
                vote.getCreatedAt(),
                vote.getUpdatedAt()
        );
    }
}
