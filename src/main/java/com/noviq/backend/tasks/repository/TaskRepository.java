package com.noviq.backend.tasks.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.projects.entity.Project;
import com.noviq.backend.tasks.entity.Task;
import com.noviq.backend.tasks.entity.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long>{

    public List<Task> findByProject(Project project);
    public Optional<Task> findByIdAndProject(Long id, Project project);
    public boolean existsByIdAndProject(Long id, Project project);
    @Query("""
        SELECT COALESCE(MAX(t.position), -1)
        FROM Task t
        WHERE t.project = :project
    """) // this is for the position column it controls the position for the task in the kanban board
    public Integer findMaxPositionByProject(Project project);
    public long countByProjectOrganization(Organization organization);
    public long countByProjectOrganizationAndStatus(Organization organization, TaskStatus status);
    public long countByProject(Project project);
    public long countByProjectAndStatus(Project project, TaskStatus status);
}
