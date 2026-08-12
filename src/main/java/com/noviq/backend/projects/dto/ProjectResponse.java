package com.noviq.backend.projects.dto;

import java.time.Instant;

public record ProjectResponse(
    Long id,
    String name,
    String description,
    Instant createdAt,
    Long ownerId,
    Long organizationId,
    Long taskCount,
    Long activeTaskCount,
    Long doneTaskCount
) {
    
}
