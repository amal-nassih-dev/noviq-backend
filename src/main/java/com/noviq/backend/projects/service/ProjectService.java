package com.noviq.backend.projects.service;

import java.util.List;

import com.noviq.backend.projects.dto.ProjectRequest;
import com.noviq.backend.projects.dto.ProjectResponse;
import com.noviq.backend.users.User;

public interface ProjectService {
    public ProjectResponse create(ProjectRequest request, Long orgId, User currentUser);
    public ProjectResponse update(ProjectRequest request, Long orgId, Long projectId,  User currentUser);
    public List<ProjectResponse> findAll(Long orgId, User currentUser);
    public ProjectResponse findById(Long orgId,Long projectId, User currentUser);
    public void delete(Long orgId,Long projectId, User currentUser);
}
