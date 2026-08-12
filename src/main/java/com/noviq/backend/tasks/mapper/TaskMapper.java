package com.noviq.backend.tasks.mapper;

import org.springframework.stereotype.Component;

import com.noviq.backend.projects.entity.Project;
import com.noviq.backend.tasks.dto.TaskRequest;
import com.noviq.backend.tasks.dto.TaskResponse;
import com.noviq.backend.tasks.entity.Task;
import com.noviq.backend.users.User;

@Component
public class TaskMapper {

    public Task toEntity(
            TaskRequest request,
            User createdBy,
            User assignee,
            Project project,
            Integer position
    ) {
        return new Task(
                request.title(),
                request.description(),
                request.status(),
                request.priority(),
                request.dueDate(),
                assignee,
                createdBy,
                project,
                position
        );
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getPosition(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getProject().getId(),
                task.getAssignee() != null
                        ? task.getAssignee().getId()
                        : null,
                task.getCreatedBy().getId()
        );
    }
}