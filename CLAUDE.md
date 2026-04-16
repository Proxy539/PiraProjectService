# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Start the PostgreSQL database (required before running the app)
docker-compose up -d

# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.proxy.pira.service.ProjectServiceTest"

# Run a single test method
./gradlew test --tests "com.proxy.pira.service.ProjectServiceTest.givenProjectsInDatabaseWhenFindAllThenReturnProjects"

# Generate JaCoCo coverage report
./gradlew jacocoTestReport
```

## Architecture

This is a Spring Boot 4.0 REST API (Java 25) for managing projects, backed by PostgreSQL.

**Request flow:** `ProjectController` → `ProjectService` (interface) → `ProjectServiceImpl` → `ProjectRepository` (JPA)

**DTO separation:** Three distinct DTOs exist for different operations:
- `SaveProjectDto` — used for POST (create), requires `@NotBlank` on `title` and `description`
- `UpdateProjectDto` — used for PUT (update), same validation; optional `id` field — if `id` is null, creates new; if present, fetches and patches the existing entity
- `ProjectDto` — response shape returned to the client

**Mapping:** MapStruct (`ProjectMapper`) handles all entity↔DTO conversions. The `updateProject` method uses `@MappingTarget` to patch an existing entity and explicitly ignores `id`.

**Error handling:** `ControllerAdvice` (`@RestControllerAdvice`) handles two cases:
- `ResourceNotFoundException` → 404 with `ErrorResponseDTO`
- `MethodArgumentNotValidException` → 400 with field-level validation errors in `ErrorResponseDTO`

**Database:** PostgreSQL on `localhost:5432`, database `piradb`, user `pira`. Hibernate `ddl-auto: update` manages schema. Start with `docker-compose up -d`.

**Tests:** Two test slices — `@ExtendWith(MockitoExtension.class)` for service tests (mocking repo + mapper), `@WebMvcTest` for controller tests (mocking service, using MockMvc). Test utilities live in `ProjectUtils`.

## GitHub Bot Instructions

After completing all code changes, always create a pull request automatically using `gh pr create`. Do not just provide a link to create a PR — submit it yourself. Link the PR to the issue it resolves.
