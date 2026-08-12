package com.noviq.backend.organizations.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noviq.backend.organizations.dto.OrganizationMembersAddRequest;
import com.noviq.backend.organizations.dto.OrganizationMembersResponse;
import com.noviq.backend.organizations.dto.UpdateOrganizationMemberRequest;
import com.noviq.backend.organizations.service.OrganizationService;
import com.noviq.backend.users.User;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/organizations/{orgId}/members")
public class OrganizationMemberController {
    private final OrganizationService orgService;
    
    public OrganizationMemberController(OrganizationService orgService){
        this.orgService = orgService;
    }

    @GetMapping
    public List<OrganizationMembersResponse> findMembers(@PathVariable Long orgId, @AuthenticationPrincipal User currentUser){
       return orgService.findMembers(orgId, currentUser);
    }

    @PostMapping
    public OrganizationMembersResponse addMembers(@PathVariable Long orgId, @Valid @RequestBody OrganizationMembersAddRequest req,@AuthenticationPrincipal User currentUser){
       return orgService.addMember(orgId, req, currentUser);
    }

    @PutMapping("/{userId}")
    public OrganizationMembersResponse updateMember(@PathVariable Long orgId, @PathVariable Long userId,@Valid @RequestBody UpdateOrganizationMemberRequest req,@AuthenticationPrincipal User currentUser){
       return orgService.updateMemberRole(orgId, userId, req, currentUser);
    }

    @DeleteMapping("/{userId}")
    public void removeMember(@PathVariable Long orgId, @PathVariable Long userId,@AuthenticationPrincipal User currentUser){
       orgService.removeMember(orgId,userId,currentUser);
    }
    
}
