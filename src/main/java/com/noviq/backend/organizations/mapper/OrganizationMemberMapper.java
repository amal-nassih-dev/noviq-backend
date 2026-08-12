package com.noviq.backend.organizations.mapper;

import org.springframework.stereotype.Component;

import com.noviq.backend.organizations.dto.OrganizationMembersResponse;
import com.noviq.backend.organizations.entity.OrganizationMember;
import com.noviq.backend.users.User;

@Component
public class OrganizationMemberMapper {

    public OrganizationMemberMapper() {
    }

    public OrganizationMembersResponse toResponse(
            OrganizationMember member) {

        User user = member.getUser();

        return new OrganizationMembersResponse(
                member.getId(),
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                member.getRole(),
                member.getJoinedAt()
        );
    }

}