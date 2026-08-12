package com.noviq.backend.organizations.mapper;

import org.springframework.stereotype.Component;

import com.noviq.backend.organizations.dto.OrganizationRequest;
import com.noviq.backend.organizations.dto.OrganizationResponse;
import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.users.User;

@Component
public class OrganizationMapper {

    public Organization toEntity( OrganizationRequest request, User owner) {
        return new Organization(request.name(), request.description(), owner, request.logoUrl());
    }

    public OrganizationResponse toResponse(Organization org, long memberCount,
            long projectCount,
            long taskCount,
            long doneTaskCount) {
        return new OrganizationResponse(org.getId(), org.getName(),org.getDescription(), org.getLogoUrl(), org.getOwner().getId(), org.getCreatedAt(),memberCount,
            projectCount,
            taskCount,
            doneTaskCount);
    }
}
