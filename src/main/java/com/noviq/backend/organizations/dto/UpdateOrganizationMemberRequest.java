package com.noviq.backend.organizations.dto;

import com.noviq.backend.organizations.entity.OrganizationRoleMember;

import jakarta.validation.constraints.NotNull;

public record UpdateOrganizationMemberRequest(
    @NotNull
    OrganizationRoleMember role
) {
    
}
