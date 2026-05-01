package com.newdawn.tagvote.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String name,
        @NotBlank String password
) {
}
