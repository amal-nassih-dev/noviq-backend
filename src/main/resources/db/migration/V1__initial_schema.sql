-- =============================================
-- USERS
-- =============================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    role VARCHAR(255) NOT NULL DEFAULT 'USER'
);


-- =============================================
-- ORGANIZATIONS
-- =============================================

CREATE TABLE organizations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_url VARCHAR(500),
    description VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    owner_id BIGINT NOT NULL,

    CONSTRAINT fk_organization_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);


-- =============================================
-- ORGANIZATION MEMBERS
-- Many-to-many relationship between users and organizations
-- =============================================

CREATE TABLE organization_members (
    id BIGSERIAL PRIMARY KEY,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    user_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL DEFAULT 'MEMBER',

    CONSTRAINT fk_organization_member_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_organization_member_organization
        FOREIGN KEY (org_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_organization_member
        UNIQUE (user_id, org_id)
);


-- =============================================
-- PROJECTS
-- =============================================

CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    organization_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,

    CONSTRAINT fk_project_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_project_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_projects_organization
    ON projects(organization_id);

CREATE INDEX idx_projects_owner
    ON projects(owner_id);


-- =============================================
-- TASKS
-- =============================================

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,

    title VARCHAR(255) NOT NULL,
    description TEXT,

    status VARCHAR(50) NOT NULL DEFAULT 'BACKLOG',
    -- BACKLOG | TODO | IN_PROGRESS | REVIEW | DONE

    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    -- LOW | MEDIUM | HIGH

    position INTEGER NOT NULL DEFAULT 0,

    due_date DATE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    project_id BIGINT NOT NULL,
    assignee_id BIGINT,
    created_by BIGINT NOT NULL,

    CONSTRAINT fk_task_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_task_assignee
        FOREIGN KEY (assignee_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_task_creator
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_tasks_project
    ON tasks(project_id);

CREATE INDEX idx_tasks_status
    ON tasks(status);

CREATE INDEX idx_tasks_assignee
    ON tasks(assignee_id);


-- =============================================
-- TASK COMMENTS
-- =============================================

CREATE TABLE task_comments (
    id BIGSERIAL PRIMARY KEY,

    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    task_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,

    CONSTRAINT fk_comment_task
        FOREIGN KEY (task_id)
        REFERENCES tasks(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comment_author
        FOREIGN KEY (author_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);


-- =============================================
-- TASK ATTACHMENTS
-- =============================================

CREATE TABLE task_attachments (
    id BIGSERIAL PRIMARY KEY,

    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    task_id BIGINT NOT NULL,
    uploaded_by BIGINT NOT NULL,

    CONSTRAINT fk_attachment_task
        FOREIGN KEY (task_id)
        REFERENCES tasks(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_attachment_user
        FOREIGN KEY (uploaded_by)
        REFERENCES users(id)
        ON DELETE RESTRICT
);