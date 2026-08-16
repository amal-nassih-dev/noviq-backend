# Noviq Backend

**Noviq Backend** is the REST API powering the Noviq project and task management application.

It is built with **Spring Boot** and provides authentication, authorization, organization management, project management, and task management through a structured REST API.

The backend uses **PostgreSQL** for persistence, **Flyway** for database migrations, **Spring Security + JWT** for authentication, and **Docker** for local database development.

## ✨ Features

* 🔐 **Authentication & Authorization**

  * User registration and login
  * JWT-based authentication
  * Password handling
  * Protected API endpoints
  * Role-based authorization

* 🏢 **Organization Management**

  * Create organizations
  * Manage organization members
  * Add and remove members
  * Assign organization roles
  * Organization-level access control

* 📁 **Project Management**

  * Create projects
  * Retrieve projects
  * Update projects
  * Delete projects
  * Projects scoped to organizations

* ✅ **Task Management**

  * Create tasks
  * Update tasks
  * Delete tasks
  * Task status and priority
  * Due dates
  * Task ordering/positioning
  * Project-based task organization

* 🗄️ **Database Management**

  * PostgreSQL
  * Dockerized local database
  * Flyway database migrations
  * Version-controlled schema

## 🛠️ Tech Stack

| Technology          | Purpose                        |
| ------------------- | ------------------------------ |
| **Java 21**         | Backend development            |
| **Spring Boot**     | REST API framework             |
| **Spring Security** | Authentication & authorization |
| **JWT**             | Stateless authentication       |
| **Spring Data JPA** | Database access                |
| **PostgreSQL**      | Relational database            |
| **Flyway**          | Database migrations            |
| **Maven**           | Dependency management & build  |
| **Docker**          | Local PostgreSQL environment   |

## 🏗️ Architecture

The application follows a layered architecture with a clear separation between controllers, services, repositories, entities, and DTOs.

```text
src/
└── main/
    ├── java/
    │   └── .../
    │       ├── config/
    │       ├── controller/
    │       ├── dto/
    │       ├── entity/
    │       ├── exception/
    │       ├── repository/
    │       ├── security/
    │       └── service/
    │
    └── resources/
        ├── sql/
        │   └── migration/
        └── application.properties
```

### Controllers

Expose the REST API endpoints and handle HTTP requests.

### Services

Contain the application's business logic and coordinate operations between controllers and repositories.

### Repositories

Use Spring Data JPA to interact with PostgreSQL.

### DTOs

Define the data exchanged through the API while keeping API contracts separate from database entities.

### Security

Handles authentication, JWT processing, authorization, and protected endpoints.

### Database Migrations

Flyway manages database schema changes through versioned SQL migration files.

---

# 🚀 Getting Started

## Prerequisites

Make sure you have installed:

* Java 21
* Maven
* Docker
* Docker Compose

Check your Java version:

```bash
java --version
```

Check Docker:

```bash
docker --version
docker compose version
```

## 1. Clone the Repository

```bash
git clone https://github.com/amal-nassih-dev/noviq-backend.git
cd noviq-backend
```

## 2. Start PostgreSQL

Noviq uses Docker Compose to run PostgreSQL locally.

Start Docker Desktop first, then configure the PostgreSQL service in:

```text
docker-compose.yml
```

Start the database:

```bash
docker compose up -d
```

Verify that the container is running:

```bash
docker compose ps
```

---

# 🗄️ Database Setup

Noviq uses **PostgreSQL + Flyway** to manage the database schema.

### Database Configuration

The PostgreSQL service should be configured in `docker-compose.yml`.

The corresponding database properties must then be configured in:

```text
src/main/resources/application.properties
```

For example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/noviq
spring.datasource.username=noviq
spring.datasource.password=noviq
```

> Keep credentials and other environment-specific secrets out of source control when working with real deployments.

## Flyway Migrations

Database migrations are stored under:

```text
src/main/resources/sql/migration/
```

The initial schema should be defined in the first migration, for example:

```text
V1__initial_schema.sql
```

Flyway executes migrations in version order when the application starts.

```text
V1__initial_schema.sql
        ↓
V2__add_project_fields.sql
        ↓
V3__add_task_priority.sql
        ↓
...
```

### Changing the Initial Schema During Development

If you are still developing locally and modify the initial database schema, you may need to completely recreate the database.

Stop the containers and remove their volumes:

```bash
docker compose down -v
```

Then start PostgreSQL again:

```bash
docker compose up -d
```

Restart the Spring Boot application so Flyway can create the database from the migrations again.

> **Important:** In a real/shared environment, do not modify an already-applied migration. Create a new versioned migration instead.

For example:

```text
V1__initial_schema.sql
V2__add_task_priority.sql
```

This allows Flyway to track and apply schema changes safely.

---

# 🔎 Accessing PostgreSQL Directly

You can inspect the database directly from the PostgreSQL container.

Open the PostgreSQL container in **Docker Desktop** and open its terminal.

Connect to the database:

```bash
psql -U noviq -d noviq
```

Once connected, you can execute PostgreSQL commands directly.

### List Tables

```sql
\dt
```

### Describe a Table

```sql
\d users
```

### Run a Query

```sql
SELECT * FROM users;
```

### Exit PostgreSQL

```sql
\q
```

---

# 🔐 Security Configuration

Noviq uses **JWT** to authenticate users and secure API requests.

The JWT signing secret should be provided through the `JWT_SECRET` environment variable.

### Set JWT Secret

On macOS/Linux:

```bash
export JWT_SECRET="THIS_IS_MY_SUPER_SECRET_KEY_12345678901234567890"
```

The application reads this value when generating and validating JWTs.

You can then start the application normally:

```bash
./mvnw spring-boot:run
```

or:

```bash
mvn spring-boot:run
```

### JWT Secret

The secret should:

* Be sufficiently long
* Be kept private
* Not be committed to Git
* Be different between development and production environments

For local development, the application currently has a fallback value if `JWT_SECRET` is not configured.

**For production, always provide a secure secret through the environment rather than relying on the fallback.**

---

# ▶️ Running the Application

After PostgreSQL is running and the environment is configured:

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8081
```

API endpoints are exposed under:

```text
/api
```

---

# 🔄 Application Flow

The main application flow is:

```text
                    ┌──────────────┐
                    │     User     │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │ Register /   │
                    │    Login     │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │     JWT      │
                    │ Authentication│
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │ Organization │
                    └──────┬───────┘
                           │
                  ┌────────┴────────┐
                  ▼                 ▼
             ┌─────────┐       ┌──────────┐
             │ Members │       │ Projects │
             └─────────┘       └────┬─────┘
                                    │
                                    ▼
                               ┌─────────┐
                               │  Tasks  │
                               └─────────┘
```

---

# 📡 API Structure

The backend exposes REST endpoints organized around the application's main resources.

```text
/api/auth
    ├── /register
    └── /login

/api/organizations
    ├── /{organizationId}
    ├── /{organizationId}/members
    └── /{organizationId}/projects

/api/organizations/{organizationId}/projects
    └── /{projectId}/tasks
```

Authentication is required for protected endpoints.

---

# 🧪 Testing

Run the test suite with:

```bash
./mvnw test
```

Or:

```bash
mvn test
```

---

# 📦 Build

Create a production JAR:

```bash
./mvnw clean package
```

The generated artifact will be available under:

```text
target/
```

Run the packaged application with:

```bash
java -jar target/<application-name>.jar
```

---

# 🔗 Frontend

Noviq Backend is designed to work with the **Noviq Angular frontend**.

**Frontend repository:** https://github.com/amal-nassih-dev/noviq

The frontend communicates with this backend through the REST API and uses the JWT returned during authentication to access protected resources.

---

# 🎯 Project Goals

Noviq Backend was built to demonstrate practical backend engineering skills, including:

* REST API design
* Spring Boot application development
* JWT authentication
* Authorization and access control
* Layered architecture
* DTO-based API contracts
* Relational database design
* JPA/Hibernate
* Database migrations with Flyway
* PostgreSQL
* Docker-based development
* Exception handling and validation
* Secure API development

---

# 👩‍💻 Author

**Amal Nassih**

Software Engineer
