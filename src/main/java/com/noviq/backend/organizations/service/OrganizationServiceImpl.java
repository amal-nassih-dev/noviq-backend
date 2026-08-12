package com.noviq.backend.organizations.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.noviq.backend.common.exceptions.OrganizationAlreadyExistsException;
import com.noviq.backend.common.exceptions.OrganizationMemberAlreadyExistsException;
import com.noviq.backend.common.exceptions.OrganizationMemberNotFoundException;
import com.noviq.backend.common.exceptions.OrganizationNotFoundException;
import com.noviq.backend.common.exceptions.UserNotFoundException;
import com.noviq.backend.organizations.dto.OrganizationMembersAddRequest;
import com.noviq.backend.organizations.dto.OrganizationMembersResponse;
import com.noviq.backend.organizations.dto.OrganizationRequest;
import com.noviq.backend.organizations.dto.OrganizationResponse;
import com.noviq.backend.organizations.dto.UpdateOrganizationMemberRequest;
import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.organizations.entity.OrganizationMember;
import com.noviq.backend.organizations.entity.OrganizationRoleMember;
import com.noviq.backend.organizations.mapper.OrganizationMapper;
import com.noviq.backend.organizations.mapper.OrganizationMemberMapper;
import com.noviq.backend.organizations.repository.OrganizationMemberRepository;
import com.noviq.backend.organizations.repository.OrganizationRepository;
import com.noviq.backend.projects.repository.ProjectRepository;
import com.noviq.backend.tasks.entity.TaskStatus;
import com.noviq.backend.tasks.repository.TaskRepository;
import com.noviq.backend.users.User;
import com.noviq.backend.users.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class OrganizationServiceImpl implements OrganizationService{

    private final OrganizationRepository organizationRepo;
    private final OrganizationMapper mapper;
    private final OrganizationMemberRepository orgMemberRepo;
    private final UserRepository userRepo;
    private final OrganizationMemberMapper memberMapper;
    private final ProjectRepository projectRepo;
    private final TaskRepository taskRepo;

    public OrganizationServiceImpl(OrganizationRepository organizationRepo, OrganizationMapper mapper, OrganizationMemberRepository orgMemberRepo, UserRepository userRepo,
        OrganizationMemberMapper memberMapper, ProjectRepository projectRepo, TaskRepository taskRepo
    ){
        this.organizationRepo = organizationRepo;
        this.mapper = mapper;
        this.orgMemberRepo = orgMemberRepo;
        this.userRepo = userRepo;
        this.memberMapper = memberMapper;
        this.projectRepo = projectRepo;
        this.taskRepo = taskRepo;
    }

    @Override
    @Transactional // because we have two operations, we want if one fails the other also fail and rollback happens
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
         // The creator automatically becomes the OWNER
        OrganizationMember ownerMember = new OrganizationMember(currentUser, saved, OrganizationRoleMember.OWNER);
        orgMemberRepo.save(ownerMember);
        return toResponseWithStats(saved);
    }

    @Override
    public List<OrganizationResponse> findAll(User currentUser) {
        return orgMemberRepo.findByUser(currentUser)
            .stream()
            .map(OrganizationMember::getOrg)
            .map(this::toResponseWithStats)
            .toList();
    }

    @Override
    public OrganizationResponse findById(Long id, User currentUser) {
        Organization org = organizationRepo.findById(id)
            .orElseThrow(() ->
                    new OrganizationNotFoundException(id));
        // User must belong to the organization
        getMembership(
                org,
                currentUser
        );
        return toResponseWithStats(org);
    }

    @Override
    public void delete(Long id, User currentUser) {

        Organization organization = organizationRepo.findById(id)
                .orElseThrow(() ->
                        new OrganizationNotFoundException(
                                id
                        ));

         // Only the owner can delete the organization
        if (!organization.getOwner()
                .getId()
                .equals(currentUser.getId())) {

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
        // Currently only owner can update
        if (!organization.getOwner()
                .getId()
                .equals(currentUser.getId())) {

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

        return toResponseWithStats(updated);
    }

    // ============================================================
    // MEMBERS
    // ============================================================

    @Override
    public List<OrganizationMembersResponse> findMembers(Long organizationId, User currentUser) {
        Organization org = getOrganization(organizationId);

        getMembership(org, currentUser); // Anyone who is a member can view the members
         
        return orgMemberRepo.findByOrg(org).stream().map(memberMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public OrganizationMembersResponse addMember(Long organizationId, OrganizationMembersAddRequest req,
            User currentUser) {
        Organization org = getOrganization(organizationId);

        OrganizationMember currentMembership = getMembership(org, currentUser);
        requireAdminOrOwner(currentMembership);
        
        User user = userRepo.findByEmailIgnoreCase(req.email()).orElseThrow(() ->
                                new IllegalArgumentException( "No user exists with email '" + req.email() + "'."));

        if(orgMemberRepo.existsByOrgAndUser(org, user)){ // already a member
           throw new OrganizationMemberAlreadyExistsException(
                    "This user is already a member of the organization."
            );
        }

         if (req.role() == OrganizationRoleMember.OWNER) {
            throw new IllegalArgumentException(
                    "The OWNER role cannot be assigned to a new member."
            );
        }
        OrganizationMember member = new OrganizationMember(user, org, req.role());
        OrganizationMember saved = orgMemberRepo.save(member);

        return memberMapper.toResponse(saved);
    }



    @Override
    @Transactional
    public OrganizationMembersResponse updateMemberRole(Long organizationId, Long userId,
            UpdateOrganizationMemberRequest request, User currentUser) {

        Organization org = getOrganization(organizationId);

        OrganizationMember currentMembership = getMembership(org, currentUser);
        requireAdminOrOwner(currentMembership);
        
        User user = userRepo.findById(userId)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "User not found."
                                ));

        
        // Find target user's membership
        OrganizationMember member =
                orgMemberRepo.findByOrgAndUser(org,user).orElseThrow(() ->
                        new OrganizationMemberNotFoundException(
                                "User is not a member of this organization."
                        ));
        
        if (member.getRole()
                == OrganizationRoleMember.OWNER) {

            throw new AccessDeniedException(
                    "The organization owner's role cannot be changed."
            );
        }
        
        if (request.role()
                == OrganizationRoleMember.OWNER) {

            throw new IllegalArgumentException(
                    "The OWNER role cannot be assigned."
            );
        }

        member.setRole(request.role());
        
        OrganizationMember saved = orgMemberRepo.save(member);

        return memberMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void removeMember(Long organizationId, Long userId, User currentUser) {
         Organization org = getOrganization(organizationId);

        OrganizationMember currentMembership = getMembership(org, currentUser);
        //requireAdminOrOwner(currentMembership);
        
        User user = userRepo.findById(userId).orElseThrow(() ->
                                new UserNotFoundException( "User not found"));

        

        OrganizationMember member =
                orgMemberRepo.findByOrgAndUser(org, user).orElseThrow(() ->
                        new OrganizationMemberNotFoundException(
                                "User is not a member of this organization."
                        ));


        orgMemberRepo.delete(member);
    }

    private Organization getOrganization(Long id) {
        return organizationRepo.findById(id)
                .orElseThrow(() ->
                        new OrganizationNotFoundException(id));
    }

    private OrganizationMember getMembership(Organization organization, User user) {
    return orgMemberRepo
            .findByOrgAndUser(organization, user)
            .orElseThrow(() ->
                    new OrganizationMemberNotFoundException(
                            "You are not a member of this organization."
                    ));
    }

    private void requireAdminOrOwner(OrganizationMember member) {
        if (member.getRole() != OrganizationRoleMember.OWNER
                && member.getRole() != OrganizationRoleMember.ADMIN) {
            throw new AccessDeniedException(
                    "You do not have permission to manage organization members."
            );
        }
    }


    private OrganizationResponse toResponseWithStats(Organization org) {
        long memberCount = orgMemberRepo.countByOrg(org);
        long projectCount = projectRepo.countByOrganization(org);
        long taskCount = taskRepo.countByProjectOrganization(org);
        long doneTaskCount = taskRepo.countByProjectOrganizationAndStatus(org, TaskStatus.DONE);

        return mapper.toResponse(
                org,
                memberCount,
                projectCount,
                taskCount,
                doneTaskCount
        );
    }
    
    
}
