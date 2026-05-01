package com.newdawn.tagvote.user.application.dto;

import com.newdawn.tagvote.user.domain.User;
import com.newdawn.tagvote.user.domain.UserRoleEnum;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String name,
        Set<UserRoleEnum> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static UserResponse from(final User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getRoleSet().getRoles(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
