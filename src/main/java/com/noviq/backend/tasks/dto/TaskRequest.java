package com.noviq.backend.tasks.dto;

import java.time.LocalDate;

import com.noviq.backend.tasks.entity.TaskPriority;
import com.noviq.backend.tasks.entity.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequest(
    @NotBlank
    @Size(max = 255)
    String title,

    String description,

    @NotNull
    TaskStatus status,

    @NotNull
    TaskPriority priority,

    Integer position,

    LocalDate dueDate,

    Long assigneeId
) {
    
}
