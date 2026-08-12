package com.noviq.backend.tasks.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.noviq.backend.common.exceptions.OrganizationMemberNotFoundException;
import com.noviq.backend.common.exceptions.OrganizationNotFoundException;
import com.noviq.backend.common.exceptions.ProjectNotFoundException;
import com.noviq.backend.common.exceptions.TaskNotFoundException;
import com.noviq.backend.common.exceptions.UserNotFoundException;
import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.organizations.entity.OrganizationMember;
import com.noviq.backend.organizations.entity.OrganizationRoleMember;
import com.noviq.backend.organizations.repository.OrganizationMemberRepository;
import com.noviq.backend.organizations.repository.OrganizationRepository;
import com.noviq.backend.projects.entity.Project;
import com.noviq.backend.projects.repository.ProjectRepository;
import com.noviq.backend.tasks.dto.TaskRequest;
import com.noviq.backend.tasks.dto.TaskResponse;
import com.noviq.backend.tasks.entity.Task;
import com.noviq.backend.tasks.mapper.TaskMapper;
import com.noviq.backend.tasks.repository.TaskRepository;
import com.noviq.backend.users.User;
import com.noviq.backend.users.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

    private final OrganizationMemberRepository organizationMemberRepo;
    private final OrganizationRepository organizationRepository;
    private final TaskMapper mapper;
    private final UserRepository userRepo;
    private final ProjectRepository projectRepo;
    private final TaskRepository taskRepo;

    public TaskServiceImpl(OrganizationMemberRepository organizationMemberRepo, OrganizationRepository organizationRepository, TaskMapper mapper, UserRepository userRepo,  ProjectRepository projectRepo, TaskRepository taskRepo){
        this.mapper = mapper;
        this.organizationMemberRepo = organizationMemberRepo;
        this.organizationRepository = organizationRepository;
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
        this.taskRepo = taskRepo;
    }

    @Override
    public List<TaskResponse> fetchAll(Long orgId, Long projectId, User currentUser) {
       Organization org = getOrganization(orgId);
       
       getMembership(org, currentUser);

       Project project = getProject(org, projectId);

       return taskRepo.findByProject(project).stream().map(mapper::toResponse).toList();
    }

    @Override
    public TaskResponse fetchById(Long orgId, Long projectId, Long taskId, User currentUser) {
        Organization org = getOrganization(orgId);
       
        getMembership(org, currentUser);

       Project project = getProject(org, projectId);
       Task task = taskRepo.findByIdAndProject(taskId ,project).orElseThrow(
         ()-> new TaskNotFoundException(taskId)
       );

       return mapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse create(TaskRequest req, Long orgId, Long projectId, User currentUser) {
        Organization org = getOrganization(orgId);
       
        getMembership(org, currentUser);

        Project project = getProject(org, projectId);

         User assignee = null;

        if (req.assigneeId() != null) {
            assignee = userRepo.findById(req.assigneeId())
                    .orElseThrow(() ->
                            new UserNotFoundException(
                                    "Assignee not found."
                            ));

            // Assignee must belong to the organization
            getMembership(org, assignee);
        }

        Integer position =
                taskRepo.findMaxPositionByProject(project) + 1;

        Task task = mapper.toEntity(req, currentUser, currentUser, project, position);
        Task saved = taskRepo.save(task);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TaskResponse update(TaskRequest req, Long orgId, Long projectId,Long taskId ,User currentUser) {
       Organization org = getOrganization(orgId);
       
       OrganizationMember member = getMembership(org, currentUser);

       requireAdminOrOwner(member);
       Project project = getProject(org, projectId);

       Task task = taskRepo.findByIdAndProject(
                taskId,
                project
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Task not found."
                ));

        User assignee = null;

        if (req.assigneeId() != null) {
            assignee = userRepo.findById(req.assigneeId())
                    .orElseThrow(() ->
                            new UserNotFoundException(
                                    "Assignee not found."
                            ));

            getMembership(org, assignee);
        }


        task.setTitle(req.title());
        task.setDescription(req.description());
        task.setStatus(req.status());
        task.setPriority(req.priority());
        task.setDueDate(req.dueDate());
        task.setAssignee(assignee);

        Task updated = taskRepo.save(task);
        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long orgId, Long projectId, Long taskId, User currentUser) {
       Organization org = getOrganization(orgId);
       
       OrganizationMember member = getMembership(org, currentUser);

       requireAdminOrOwner(member);
       Project project = getProject(org, projectId);

       Task task = taskRepo.findByIdAndProject(
                taskId,
                project
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Task not found."
                ));
        taskRepo.delete(task);
    }


    //////////////////////////////
    /// Helpers
    /////////////////////////////
    
    private Organization getOrganization(Long orgId){
      return organizationRepository.findById(orgId).orElseThrow(
       () -> new OrganizationNotFoundException(orgId)
      );
    }

    private OrganizationMember getMembership(Organization org, User currentUser) {
        return organizationMemberRepo.findByOrgAndUser(org, currentUser).orElseThrow(
            ()-> new AccessDeniedException("You are not a member of this organization.")
        );
    }

    private Project getProject(Organization org, Long projectId){
       return projectRepo.findByIdAndOrganization(projectId, org).orElseThrow(
            ()-> new ProjectNotFoundException(projectId)
        );
    }

    private void requireAdminOrOwner(OrganizationMember membership) {
        if (membership.getRole() != OrganizationRoleMember.OWNER
                && membership.getRole() != OrganizationRoleMember.ADMIN) {

            throw new AccessDeniedException(
                    "You do not have permission to delete tasks."
            );
        }
    }
    
}
