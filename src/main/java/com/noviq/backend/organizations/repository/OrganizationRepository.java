package com.noviq.backend.organizations.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.users.User;
import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long>{
    List<Organization> findByOwner(User user);
    Optional<Organization> findById(Long id);
    boolean existsByOwnerAndNameIgnoreCase(User owner, String name);
}
