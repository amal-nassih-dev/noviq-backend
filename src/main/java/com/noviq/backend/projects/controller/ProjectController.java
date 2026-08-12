package com.noviq.backend.projects.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noviq.backend.projects.dto.ProjectRequest;
import com.noviq.backend.projects.dto.ProjectResponse;
import com.noviq.backend.projects.service.ProjectService;
import com.noviq.backend.users.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/organizations/{organizationId}/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectResponse create(
            @PathVariable Long organizationId,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal User currentUser) {

        return projectService.create(
                request,
                organizationId,
                currentUser
        );
    }

    @GetMapping
    public List<ProjectResponse> findAll(
            @PathVariable Long organizationId,
            @AuthenticationPrincipal User currentUser) {

        return projectService.findAll(
                organizationId,
                currentUser
        );
    }

    @GetMapping("/{projectId}")
    public ProjectResponse findById(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @AuthenticationPrincipal User currentUser) {

        return projectService.findById(
                organizationId,
                projectId,
                currentUser
        );
    }

    @PutMapping("/{projectId}")
    public ProjectResponse update(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal User currentUser) {

        return projectService.update(
                request,
                organizationId,
                projectId,
                currentUser
        );
    }

    @DeleteMapping("/{projectId}")
    public void delete(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @AuthenticationPrincipal User currentUser) {

        projectService.delete(
                organizationId,
                projectId,
                currentUser
        );
    }
}
