package com.noviq.backend.projects.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.noviq.backend.common.exceptions.OrganizationNotFoundException;
import com.noviq.backend.common.exceptions.ProjectNotFoundException;
import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.organizations.entity.OrganizationMember;
import com.noviq.backend.organizations.entity.OrganizationRoleMember;
import com.noviq.backend.organizations.repository.OrganizationMemberRepository;
import com.noviq.backend.organizations.repository.OrganizationRepository;
import com.noviq.backend.projects.dto.ProjectRequest;
import com.noviq.backend.projects.dto.ProjectResponse;
import com.noviq.backend.projects.entity.Project;
import com.noviq.backend.projects.mapper.ProjectMapper;
import com.noviq.backend.projects.repository.ProjectRepository;
import com.noviq.backend.tasks.entity.TaskStatus;
import com.noviq.backend.tasks.repository.TaskRepository;
import com.noviq.backend.users.User;
import org.springframework.security.access.AccessDeniedException;

import jakarta.transaction.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;
    private final ProjectMapper mapper;
    private final OrganizationRepository orgRepo;
    private final OrganizationMemberRepository orgMemberRepo;
    private final TaskRepository taskRepo;

    public ProjectServiceImpl(ProjectRepository projectRepo, ProjectMapper mapper, OrganizationRepository orgRepo, OrganizationMemberRepository orgMemberRepo,
        TaskRepository taskRepo
    ){
        this.mapper = mapper;
        this.projectRepo = projectRepo;
        this.orgRepo = orgRepo;
        this.orgMemberRepo = orgMemberRepo;
        this.taskRepo = taskRepo;
    }


    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request, Long orgId, User currentUser) {
        Organization org = getOrganization(orgId);
        OrganizationMember member = getMembership(org, currentUser);

        requireMember(member);

        Project project = mapper.toEntity(request, currentUser, org);

        Project saved = projectRepo.save(project);

        return toProjectResponse(saved);
    }

    @Override
    @Transactional
    public ProjectResponse update(ProjectRequest request, Long orgId, Long projectId, User currentUser) {
         Organization org = getOrganization(orgId);
         OrganizationMember member = getMembership(org, currentUser);
         requireAdminOrOwner(member);

         Project project = projectRepo.findByIdAndOrganization(projectId, org).orElseThrow(() ->
                        new ProjectNotFoundException(projectId)
         );

         project.setName(request.name());
         project.setDescription(request.description());
         Project updated = projectRepo.save(project);

         return toProjectResponse(updated);
    }

    @Override
    public List<ProjectResponse> findAll(Long orgId, User currentUser) {
        Organization org = getOrganization(orgId);
        getMembership(org, currentUser);

        return projectRepo.findByOrganization(org)
                .stream()
                .map(this::toProjectResponse)
                .toList();
    }

    @Override
    public ProjectResponse findById(Long orgId, Long projectId, User currentUser) {
       Organization organization = getOrganization(orgId);

        getMembership(organization, currentUser);

        Project project =
                projectRepo.findByIdAndOrganization(
                        projectId,
                        organization
                )
                .orElseThrow(() ->
                        new ProjectNotFoundException(projectId)
                );

        return toProjectResponse(project);
    }

    @Override
    @Transactional
    public void delete(Long orgId, Long projectId, User currentUser) {
        Organization organization = getOrganization(orgId);

        OrganizationMember membership =
                getMembership(organization, currentUser);

        requireAdminOrOwner(membership);

        Project project =
                projectRepo.findByIdAndOrganization(
                        projectId,
                        organization
                )
                .orElseThrow(() ->
                        new ProjectNotFoundException(projectId)
                );

        projectRepo.delete(project);
    }

     // ============================================================
    // HELPERS
    // ============================================================

    private Organization getOrganization(Long id) {
        return orgRepo.findById(id)
                .orElseThrow(() ->
                        new OrganizationNotFoundException(id));
    }

    private OrganizationMember getMembership(Organization organization,User user) {
        return orgMemberRepo
                .findByOrgAndUser(organization, user)
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "You are not a member of this organization."
                        ));
    }

     private void requireMember(OrganizationMember membership) {
        if (membership.getRole() == null) {
            throw new AccessDeniedException(
                    "You do not have permission to access this organization."
            );
        }
    }

    private void requireAdminOrOwner(OrganizationMember membership) {
        if (membership.getRole() != OrganizationRoleMember.OWNER
                && membership.getRole() != OrganizationRoleMember.ADMIN) {

            throw new AccessDeniedException(
                    "You do not have permission to manage projects."
            );
        }
    }

    private ProjectResponse toProjectResponse(Project project) {
        long taskCount = taskRepo.countByProject(project);

        // Active = everything that is not DONE
        long doneTaskCount = taskRepo.countByProjectAndStatus(project, TaskStatus.DONE);
        long activeTaskCount = taskCount - doneTaskCount;

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
