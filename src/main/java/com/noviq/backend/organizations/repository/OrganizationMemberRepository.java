package com.noviq.backend.organizations.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.organizations.entity.OrganizationMember;
import com.noviq.backend.users.User;

import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember,Long>{

    public List<OrganizationMember> findByOrg(Organization org);
    public List<OrganizationMember> findByUser(User user);
    public Optional<OrganizationMember> findByOrgAndUser(Organization org, User user);
    public boolean existsByOrgAndUser(Organization org, User user);
    public long countByOrg(Organization organization);
    public void deleteByOrgAndUser(Organization org, User user);
    
}
