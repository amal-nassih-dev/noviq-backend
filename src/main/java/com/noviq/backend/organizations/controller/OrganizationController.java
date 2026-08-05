package com.noviq.backend.organizations.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noviq.backend.organizations.dto.OrganizationRequest;
import com.noviq.backend.organizations.dto.OrganizationResponse;
import com.noviq.backend.organizations.service.OrganizationService;
import com.noviq.backend.users.User;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import java.util.List; 

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    

    public OrganizationController(
            OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public OrganizationResponse create(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid OrganizationRequest request) {

        return organizationService.create(request, user);
    }

    @GetMapping
    public List<OrganizationResponse> findAll(
            @AuthenticationPrincipal User user) {

        return organizationService.findAll(user);
    }

    @GetMapping("/{id}")
    public OrganizationResponse findById(
            @PathVariable Long id, @AuthenticationPrincipal User user) {

        return organizationService.findById(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id, @AuthenticationPrincipal User user) {

        organizationService.delete(id, user);
    }

    @PutMapping("/{id}")
    public OrganizationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request,
            @AuthenticationPrincipal User user) {

        return organizationService.update(id, request, user);
    }

}