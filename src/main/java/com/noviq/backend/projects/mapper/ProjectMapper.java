package com.noviq.backend.projects.mapper;

import org.springframework.stereotype.Component;

import com.noviq.backend.projects.dto.ProjectRequest;
import com.noviq.backend.projects.dto.ProjectResponse;
import com.noviq.backend.projects.entity.Project;
import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.users.User;

@Component
public class ProjectMapper {

    public Project toEntity(
            ProjectRequest request,
            User owner,
            Organization organization
    ) {
        return new Project(
                request.name(),
                request.description(),
                owner,
                organization
        );
    }

    public ProjectResponse toResponse(Project project, Long taskCount, Long  activeTaskCount, Long doneTaskCount) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getOwner().getId(),
                project.getOrganization().getId(),
                taskCount,
                activeTaskCount,
                doneTaskCount
        );
    }
}
