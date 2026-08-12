package com.noviq.backend.organizations.dto;

import java.time.Instant;

import com.noviq.backend.organizations.entity.OrganizationRoleMember;

public record OrganizationMembersResponse(
    Long id,
    Long userId,
    String email,
    String fullName,
    OrganizationRoleMember role,
    Instant joinedAt
) {
    
}
