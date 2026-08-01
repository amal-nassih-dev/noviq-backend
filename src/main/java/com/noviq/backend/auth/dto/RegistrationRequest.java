package com.noviq.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
    @Email String email,
    @NotBlank String fullName,
    @Size(min = 8, message = "Password must contain at least 8 characters") String password
) {
}