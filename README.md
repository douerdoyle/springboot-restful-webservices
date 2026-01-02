# Spring Boot Account Management REST API

A Spring Boot REST API demonstrating layered architecture, DTO-based contracts, validation, and operational tooling.
The Spring Boot API from my other project: https://github.com/douerdoyle/springboot-restful-webservices/tree/main 

## Project Overview

- This is a general Spring Boot project that follows controller, service, and repository layers.
- This project demonstrates how to run a Spring Boot RESTful API service alongside MySQL. The Spring Boot service performs CRUD operations against the MySQL database.
- DTOs handle requests and responses so the service won't expose its database table structure.
- Database access is implemented with Spring Data JPA.
- The entity, DTOs, and service classes are integrated with Lombok.
- A global exception handler also exists for handling errors.

## Tech Stack & Versions

| Component | Version / Details |
| --- | --- |
| Java | 21 (`pom.xml:<java.version>`) |
| Spring Boot | 4.0.1 (`spring-boot-starter-parent`) |
| Spring Data JPA / Spring Web | Pulled from the Spring Boot BOM |
| Persistence | MySQL Connector/J 9.5.0 (default datasource) |
| Alt. Database | H2 2.2.224 dependency available for in-memory runs |
| Build | Maven 3.9+ or the bundled `mvnw` wrapper |
| Tooling | Lombok 1.18.34, Bean Validation (`spring-boot-starter-validation`), JUnit 5 |

## Local Setup

**Prerequisites**

- Install JDK 21 and Maven 3.9+ (or rely on `./mvnw`).
- Make sure Docker is installed in your environment.

## Run with Docker Compose

There is a file named `docker-compose.yml` in the project root that allows the service and the MySQL database to be run as containers.
The project directory is mounted into the `app` container so code edits on the host instantly apply to the service running inside Docker, and the MySQL service also mounts the volume from the host.

1. Launch the full stack from the repository root:
   ```bash
   docker compose up --build -d
   ```
   The first run downloads the Maven and MySQL images and resolves project dependencies. Omit `--build` for subsequent starts if no code changes were made.
   The services usually take a few minutes to download the required images and libraries and then start.
2. Follow the logs with `docker compose logs -f app`.
3. Delete everything with `docker compose down -v`. It will also delete `${HOME}/dev/container/mysql`, which stores the MySQL data directory.

You may also revise the environment variables in `docker-compose.yml` if you want a different schema/user/password or different service ports.

### Actuator exposure notes

Spring Boot Actuator endpoints are intended for internal diagnostics.
They are usually kept behind an API gateway or a restricted network after deployment.
Actuator's URL is `http://localhost:8081`.

| Endpoint | Purpose / Notes |
| --- | --- |
| `/actuator/health` | Health check endpoint (exposed). |
| `/actuator/metrics` | Application metrics (exposed). |
| `/actuator/env` | Not exposed by default (security reasons). |
| `/actuator/configprops` | Not exposed by default (security reasons). |
| `/actuator/shutdown` | Disabled by default. |

### API documentation (Swagger / OpenAPI)

Swagger UI is available at:
- `http://localhost:8080/swagger-ui.html`

| Endpoint | Purpose / Notes |
| --- | --- |
| `/swagger-ui.html` | Interactive Swagger UI for testing and exploring the REST endpoints. |
| `/v3/api-docs` | JSON OpenAPI document that can be imported into Postman, API Gateway definitions, etc. |
| `/v3/api-docs.yaml` | YAML variant of the spec (served automatically by Springdoc). |

Protect these documentation endpoints the same way you protect Actuator if the service is exposed outside trusted networks.

## API Surface

| Method | Endpoint | Description                                                                                                        | curl example |
| --- | --- |--------------------------------------------------------------------------------------------------------------------| --- |
| POST | `/api/accounts` | Create an account using the validated `AccountRequest` payload.                                                    | `curl -X POST http://localhost:8080/api/accounts -H 'Content-Type: application/json' -d '{"firstName":"Jane","lastName":"Doe","email":"jane@example.com"}'` |
| GET | `/api/accounts/{id}` | Fetch a single account by ID. Missing IDs return HTTP 404.                                                         | `curl http://localhost:8080/api/accounts/1` |
| GET | `/api/accounts` | Return all accounts.                                                                                               | `curl http://localhost:8080/api/accounts` |
| PUT | `/api/accounts/{id}` | Replace the identified account's fields with a validated request body. Returns HTTP 404 if the account is missing. | `curl -X PUT http://localhost:8080/api/accounts/1 -H 'Content-Type: application/json' -d '{"firstName":"Janet","lastName":"Doe","email":"jdoe@example.com"}'` |
| DELETE | `/api/accounts/{id}` | Remove the account and respond with HTTP 204 (no body), returning HTTP 404 if the ID does not exist.             | `curl -X DELETE http://localhost:8080/api/accounts/1 -i` |

## Testing

Execute the Spring Boot test suite:
```bash
./mvnw test
```

The test suite focuses on unit tests for the service and controller layers.
Integration tests with a real database are intentionally omitted to keep the project lightweight and fast to run.

### Testing prerequisites

If your environment has multiple JDKs installed, set Maven to Java 21 before running the tests:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw clean test
```

## Note

This project was inspired by Ramesh Fadatare's Spring Boot tutorials.
I extended it with testing, Docker support, and operational tooling.
