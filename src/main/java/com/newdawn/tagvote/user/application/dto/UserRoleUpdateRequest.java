package com.newdawn.tagvote.user.application.dto;

import com.newdawn.tagvote.user.domain.UserRoleEnum;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserRoleUpdateRequest(
        @NotEmpty Set<UserRoleEnum> roles
) {
}
