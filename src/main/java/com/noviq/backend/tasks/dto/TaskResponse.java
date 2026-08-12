package com.noviq.backend.tasks.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.noviq.backend.tasks.entity.TaskPriority;
import com.noviq.backend.tasks.entity.TaskStatus;

public record TaskResponse(

    Long id,
    String title,
    String description,
    TaskStatus status,
    TaskPriority priority,
    Integer position,
    LocalDate dueDate,
    Instant createdAt,
    Instant updatedAt,
    Long projectId,
    Long assigneeId,
    Long createdById
) {
    
}
