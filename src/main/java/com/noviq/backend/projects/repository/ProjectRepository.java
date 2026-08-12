package com.noviq.backend.projects.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.projects.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>{
    
    public List<Project> findByOrganization(Organization organization);
    public Optional<Project> findByIdAndOrganization(Long id, Organization organization);
    public boolean existsByIdAndOrganization(Long id, Organization organization);
    public long countByOrganization(Organization organization);
}
