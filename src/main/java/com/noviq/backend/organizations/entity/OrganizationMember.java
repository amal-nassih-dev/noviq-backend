package com.noviq.backend.organizations.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.noviq.backend.users.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * 
 * OrganizationMember represents the membership relationship between a User and an Organization.
 */
@Entity
@Table(
    name = "organization_members",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_organization_member",
            columnNames = {"user_id", "org_id"}
        )
    }
)
public class OrganizationMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization org;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrganizationRoleMember role;

    public OrganizationMember(){

    }

    public OrganizationMember(User user, Organization org,  OrganizationRoleMember role){
       this.user = user;
       this.org = org;
       this.role = role;
    }
 
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setOrg(Organization org) {
        this.org = org;
    }

    public Organization getOrg() {
        return org;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setRole(OrganizationRoleMember role) {
        this.role = role;
    }

    public OrganizationRoleMember getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "OrganizationMember{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", organizationId=" + (org != null ? org.getId() : null) +
                ", joinedAt=" + joinedAt +
                ", role=" + role +
                '}';
    }

}
