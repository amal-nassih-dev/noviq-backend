package com.noviq.backend.tasks.controller;

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

import com.noviq.backend.tasks.dto.TaskRequest;
import com.noviq.backend.tasks.dto.TaskResponse;
import com.noviq.backend.tasks.service.TaskService;
import com.noviq.backend.users.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping(
        "/api/organizations/{organizationId}/projects/{projectId}/tasks"
)
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponse create(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User currentUser) {

        return taskService.create(
                request,
                organizationId,
                projectId,
                currentUser
        );
    }

    @GetMapping
    public List<TaskResponse> findAll(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @AuthenticationPrincipal User currentUser) {

        return taskService.fetchAll(
                organizationId,
                projectId,
                currentUser
        );
    }

    @GetMapping("/{taskId}")
    public TaskResponse findById(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser) {

        return taskService.fetchById(
                organizationId,
                projectId,
                taskId,
                currentUser
        );
    }

    @PutMapping("/{taskId}")
    public TaskResponse update(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User currentUser) {

        return taskService.update(
                request,
                organizationId,
                projectId,
                taskId,
                currentUser
        );
    }

    @DeleteMapping("/{taskId}")
    public void delete(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser) {

        taskService.delete(
                organizationId,
                projectId,
                taskId,
                currentUser
        );
    }
}
