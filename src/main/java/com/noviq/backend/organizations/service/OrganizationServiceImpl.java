package com.noviq.backend.organizations.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.noviq.backend.common.exceptions.OrganizationAlreadyExistsException;
import com.noviq.backend.common.exceptions.OrganizationNotFoundException;
import com.noviq.backend.organizations.dto.OrganizationRequest;
import com.noviq.backend.organizations.dto.OrganizationResponse;
import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.organizations.mapper.OrganizationMapper;
import com.noviq.backend.organizations.repository.OrganizationRepository;
import com.noviq.backend.users.User;

@Service
public class OrganizationServiceImpl implements OrganizationService{

    private final OrganizationRepository organizationRepo;
    private final OrganizationMapper mapper;

    public OrganizationServiceImpl(OrganizationRepository organizationRepo, OrganizationMapper mapper){
        this.organizationRepo = organizationRepo;
        this.mapper = mapper;
    }

    @Override
    public OrganizationResponse create(OrganizationRequest request, User currentUser) {
        if (organizationRepo.existsByOwnerAndNameIgnoreCase(
                currentUser,
                request.name())) {

            throw new OrganizationAlreadyExistsException(
                    "You already have an organization named '" + request.name() + "'."
            );
        }
        Organization organization = mapper.toEntity(request, currentUser);
        Organization saved = organizationRepo.save(organization);
        return mapper.toResponse(saved);
    }

    @Override
    public List<OrganizationResponse> findAll(User currentUser) {
        return organizationRepo.findByOwner(currentUser)
                .stream().map(org -> {
                    return mapper.toResponse(org);
                }).toList();
    }

    @Override
    public OrganizationResponse findById(Long id, User currentUser) {
        Organization org = organizationRepo.findById(id)
            .orElseThrow(() ->
                    new OrganizationNotFoundException(id));
        if (!org.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to access this organization.");
        }
        return mapper.toResponse(org);
    }

    @Override
    public void delete(Long id, User currentUser) {

        Organization organization = organizationRepo.findById(id)
                .orElseThrow(() ->
                        new OrganizationNotFoundException(
                                id
                        ));

        if (!organization.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to delete this organization."
            );
        }

        organizationRepo.delete(organization);
    }

    @Override
    public OrganizationResponse update(
            Long id,
            OrganizationRequest request,
            User currentUser) {

        Organization organization =
                organizationRepo.findById(id)
                        .orElseThrow(() ->
                                new OrganizationNotFoundException(
                                        id
                                ));
        if (!organization.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to update this organization."
            );
        }

        boolean duplicate =
                organizationRepo.existsByOwnerAndNameIgnoreCase(
                        currentUser,
                        request.name());

        if (duplicate &&
            !organization.getName().equalsIgnoreCase(request.name())) {

            throw new OrganizationAlreadyExistsException(
                    "You already have an organization named '"
                            + request.name() + "'.");
        }

        organization.setName(request.name());
        organization.setDescription(request.description());

        Organization updated =
                organizationRepo.save(organization);

        return mapper.toResponse(updated);
    }
    
}
