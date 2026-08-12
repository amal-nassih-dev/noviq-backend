package com.noviq.backend.projects.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.noviq.backend.organizations.entity.Organization;
import com.noviq.backend.tasks.entity.Task;
import com.noviq.backend.users.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * 
 * Project represents a project inside an organization.
 */
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="owner_id" , nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="organization_id" , nullable = false)
    private Organization organization;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> tasks;

    public Project(){}

    public Project(String name, String description, User owner, Organization organization){
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.organization = organization;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
    public User getOwner() {
        return owner;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
    public Organization getOrganization() {
        return organization;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    public List<Task> getTasks() {
        return tasks;
    }

     @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", ownerId=" + (owner != null ? owner.getId() : null) +
                ", organizationId=" +
                (organization != null ? organization.getId() : null) +
                '}';
    }
    
}
