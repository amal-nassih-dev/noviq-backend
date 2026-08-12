package com.noviq.backend.organizations.dto;

import com.noviq.backend.organizations.entity.OrganizationRoleMember;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizationMembersAddRequest(
    @NotBlank
    @Email
    String email,

    @NotNull
    OrganizationRoleMember role
) {
    
}
