package com.noviq.backend.tasks.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.noviq.backend.projects.entity.Project;
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

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Column(nullable = false)
    private Integer position = 0;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="project_id", nullable = false)
    private Project project;


    public Task(){
    }

    public Task(String title, String description, TaskStatus status, TaskPriority priority, LocalDate dueDate, User assignee, User createdBy, Project project, Integer position)
    {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.assignee = assignee;
        this.createdBy = createdBy;
        this.project = project;
        this.position = position;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskPriority getPriority() {
        return priority;
    }
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    public User getCreatedBy() {
        return createdBy;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }
    public User getAssignee() {
        return assignee;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    public Project getProject() {
        return project;
    }

     @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", position=" + position +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", assigneeId=" +
                (assignee != null ? assignee.getId() : null) +
                ", createdById=" +
                (createdBy != null ? createdBy.getId() : null) +
                ", projectId=" +
                (project != null ? project.getId() : null) +
                '}';
    }

}
