package com.noviq.backend.organizations.service;

import com.noviq.backend.organizations.dto.OrganizationMembersAddRequest;
import com.noviq.backend.organizations.dto.OrganizationMembersResponse;
import com.noviq.backend.organizations.dto.OrganizationRequest;
import com.noviq.backend.organizations.dto.OrganizationResponse;
import com.noviq.backend.organizations.dto.UpdateOrganizationMemberRequest;
import com.noviq.backend.users.User;
import java.util.List;

public interface OrganizationService {

  public OrganizationResponse create(OrganizationRequest request, User currentUser);
  public List<OrganizationResponse> findAll(User currentUser);
  public OrganizationResponse findById(Long id, User currentUser);
  public void delete(Long id, User currentUser);
  public OrganizationResponse update(Long id, OrganizationRequest request, User currentUser);
  
  public List<OrganizationMembersResponse> findMembers(Long organizationId, User currentUser);
  public OrganizationMembersResponse addMember(Long organizationId , OrganizationMembersAddRequest req, User currentUser);
  public OrganizationMembersResponse updateMemberRole(Long organizationId, Long userId, UpdateOrganizationMemberRequest request, User currentUser);
  public void removeMember(Long organizationId, Long userId, User currentUser);

}
