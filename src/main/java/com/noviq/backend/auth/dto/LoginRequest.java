package com.noviq.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;

public record LoginRequest(
    @Valid @Email String email,
    @Valid @NotBlank String password
) {
}