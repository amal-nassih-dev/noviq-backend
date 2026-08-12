package com.noviq.backend.organizations.dto;

import java.time.Instant;

public record OrganizationResponse(
    Long id,
    String name,
    String description,
    String logoUrl,
    Long ownerId,
    Instant createdAt,
    // stats
    long memberCount,
    long projectCount,
    long taskCount,
    long doneTaskCount
){
    
}
