package com.newdawn.tagvote.auth.application.dto;

import com.newdawn.tagvote.global.security.SessionUserPrincipal;
import com.newdawn.tagvote.user.domain.UserRoleEnum;

import java.util.Set;

public record AuthUserResponse(
        Long userId,
        String name,
        Set<UserRoleEnum> roles
) {

    public static AuthUserResponse from(final SessionUserPrincipal principal) {
        return new AuthUserResponse(principal.userId(), principal.name(), principal.roles());
    }
}
