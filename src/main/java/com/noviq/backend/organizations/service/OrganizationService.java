package com.noviq.backend.organizations.service;

import com.noviq.backend.organizations.dto.OrganizationRequest;
import com.noviq.backend.organizations.dto.OrganizationResponse;
import com.noviq.backend.users.User;
import java.util.List;

public interface OrganizationService {

  public OrganizationResponse create(OrganizationRequest request, User currentUser);
  public List<OrganizationResponse> findAll(User currentUser);
  public OrganizationResponse findById(Long id, User currentUser);
  public void delete(Long id, User currentUser);
  public OrganizationResponse update(Long id, OrganizationRequest request, User currentUser);
  
}
