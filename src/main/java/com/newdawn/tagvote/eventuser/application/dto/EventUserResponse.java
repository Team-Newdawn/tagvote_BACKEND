package com.newdawn.tagvote.eventuser.application.dto;

import com.newdawn.tagvote.eventuser.domain.EventUser;

import java.time.LocalDateTime;

public record EventUserResponse(
        Long id,
        String name,
        String phone,
        boolean privacyConsent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static EventUserResponse from(final EventUser eventUser) {
        return new EventUserResponse(
                eventUser.getId(),
                eventUser.getName(),
                eventUser.getPhone(),
                eventUser.isPrivacyConsent(),
                eventUser.getCreatedAt(),
                eventUser.getUpdatedAt()
        );
    }
}
