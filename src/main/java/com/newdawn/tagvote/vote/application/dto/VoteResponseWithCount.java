package com.newdawn.tagvote.vote.application.dto;

import com.newdawn.tagvote.vote.domain.Vote;
import com.newdawn.tagvote.vote.domain.VoteStatus;

import java.time.LocalDateTime;

public record VoteResponseWithCount(
        Long id,
        Long createdByUserId,
        String name,
        VoteStatus status,
        boolean isMine,
        int questionCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static VoteResponseWithCount from(final Vote vote, final Long currentUserId) {
        return new VoteResponseWithCount(
                vote.getId(),
                vote.getCreatedBy().getId(),
                vote.getName(),
                vote.getStatus(),
                currentUserId != null && vote.getCreatedBy().getId().equals(currentUserId),
                vote.getQuestions().size(),
                vote.getCreatedAt(),
                vote.getUpdatedAt()
        );
    }
}
