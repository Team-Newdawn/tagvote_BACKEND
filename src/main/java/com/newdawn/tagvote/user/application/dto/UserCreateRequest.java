package com.newdawn.tagvote.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank String name,
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
