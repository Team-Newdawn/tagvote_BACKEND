package com.newdawn.tagvote.user.application.dto;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        String name,
        @Size(min = 8, max = 100) String password
) {
}
