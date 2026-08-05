package com.noviq.backend.organizations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizationRequest(
    @NotBlank
    @Size(max=255)
    String name,
    @Size(max=500)
    String description
) {
}
