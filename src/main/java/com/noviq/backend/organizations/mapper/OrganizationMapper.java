package com.noviq.backend.organizations.mapper;

import org.springframework.stereotype.Component;

import com.noviq.backend.organizations.dto.OrganizationRequest;
import com.noviq.backend.organizations.dto.OrganizationResponse;
import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.users.User;

@Component
public class OrganizationMapper {

    public OrganizationMapper(){

    }
    public Organization toEntity( OrganizationRequest request, User owner) {
        return new Organization(request.name(), request.description(), owner);
    }

    public OrganizationResponse toResponse(Organization org) {
        return new OrganizationResponse(org.getId(), org.getName(),org.getDescription());
    }
}
