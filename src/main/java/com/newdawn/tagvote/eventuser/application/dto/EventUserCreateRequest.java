package com.newdawn.tagvote.eventuser.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventUserCreateRequest(
        @NotBlank String name,
        @NotBlank String phone,
        @NotNull Boolean privacyConsent
) {
}
