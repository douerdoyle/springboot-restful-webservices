# Spring Boot Account Management REST API

This repository is an example of integrating a Spring Boot service with Docker.

## Spring Boot service overview

* Controllers forward work to services and repositories
* CRUD operations rely on Spring Data JPA and MySQL
* DTO classes keep the HTTP surface separate from the database model
* Lombok keeps the DTO and entity classes readable
* A global exception handler formats validation errors

## Tech stack

| Component | Version or detail |
| --- | --- |
| Java | 21 (`pom.xml:<java.version>`) |
| Spring Boot | 4.0.1 (`spring-boot-starter-parent`) |
| Spring Data JPA and Spring MVC | Pulled from the Spring Boot BOM |
| Database driver | MySQL Connector J 9.5.0 |
| Tooling | Lombok 1.18.34, Bean Validation, JUnit 5 |

## Local setup

**Prerequisites**

* JDK 21 and Maven 3.9 (or the wrapper)
* Docker if you want to run the compose stack

## Run with Docker Compose

`docker-compose.yml` starts both the API and MySQL containers. The project directory is mounted into the container so you can edit code on the host and restart inside Docker without rebuilding.

### Configure the Basic Auth users first

1. Copy `.env.example` to `.env`
2. Configure at least one admin by setting `APP_SECURITY_ADMINS_0_USERNAME` and `_PASSWORD`. Add more admins by increasing the index, such as `APP_SECURITY_ADMINS_1_USERNAME` and `APP_SECURITY_ADMINS_2_USERNAME`
3. Optionally define any number of viewers with the same pattern, for example `APP_SECURITY_VIEWERS_0_USERNAME`, `APP_SECURITY_VIEWERS_1_USERNAME`, and so on
4. Continue with the compose commands below

Once `.env` exists:

1. Bring everything up from the repo root
   ```bash
   docker compose up --build -d
   ```
   The first run downloads the Maven and MySQL images and resolves dependencies.
2. Tail the logs with `docker compose logs -f app`
3. Shut everything down with `docker compose down -v`
4. Remove the MySQL data directory with `rm -rf ${HOME}/dev/container/mysql`

Feel free to adjust the environment variables inside `docker-compose.yml` if you want to use different ports or credentials.

### Actuator exposure notes

Actuator is exposed on `http://localhost:8081`. Only the admin user from `.env` can access the exposed endpoints.

| Endpoint | Purpose |
| --- | --- |
| `/actuator/health` | Basic health probe (admin Basic Auth required) |
| `/actuator/metrics` | Metrics snapshot (admin Basic Auth required) |
| `/actuator/info` | Application metadata (admin Basic Auth required) |
| `/actuator/env` | Disabled |
| `/actuator/configprops` | Disabled |
| `/actuator/shutdown` | Disabled |

### API documentation (Swagger and OpenAPI)

Swagger UI is exposed at `http://localhost:8080/swagger-ui.html`.
Sign in with the admin credentials from `.env`

| Endpoint | Purpose                                                                   |
| --- |---------------------------------------------------------------------------|
| `/swagger-ui.html` | The Swagger UI (admin Basic Auth required)                                |
| `/v3/api-docs` | JSON OpenAPI document that can be imported into tools like Postman (admin Basic Auth required) |
| `/v3/api-docs.yaml` | YAML version of the spec (admin Basic Auth required)                     |

If you expose this service outside localhost, keep these docs behind the same protections as Actuator.

### API versioning

Version 1 remains open at `/api/v1`. Version 2 uses Basic Auth through the usernames and passwords defined in `.env`. Any number of admin accounts can be configured (at least one is required) and viewer accounts are optional.

## API surface

**Version 1 (open access)**

| Method | Endpoint | Description | curl example |
| --- | --- | --- | --- |
| POST | `/api/v1/accounts` | Create an account from an `AccountRequest` body | `curl -X POST http://localhost:8080/api/v1/accounts -H 'Content-Type: application/json' -d '{"firstName":"Jane","lastName":"Doe","email":"jane@example.com"}'` |
| GET | `/api/v1/accounts/{id}` | Fetch an account by id | `curl http://localhost:8080/api/v1/accounts/1` |
| GET | `/api/v1/accounts` | Fetch every account | `curl http://localhost:8080/api/v1/accounts` |
| PUT | `/api/v1/accounts/{id}` | Replace an account | `curl -X PUT http://localhost:8080/api/v1/accounts/1 -H 'Content-Type: application/json' -d '{"firstName":"Janet","lastName":"Doe","email":"jdoe@example.com"}'` |
| DELETE | `/api/v1/accounts/{id}` | Remove an account | `curl -X DELETE http://localhost:8080/api/v1/accounts/1 -i` |

**Version 2 (Basic Auth protected)**

Admins can call every API. Viewer accounts can only call the GET API.

| Method | Endpoint | Description | curl example |
| --- | --- | --- | --- |
| POST | `/api/v2/accounts` | Create an account (admin) | `curl -u admin:password -X POST http://localhost:8080/api/v2/accounts -H 'Content-Type: application/json' -d '{"firstName":"Jane","lastName":"Doe","email":"jane@example.com"}'` |
| GET | `/api/v2/accounts/{id}` | Fetch an account (admin or viewer) | `curl -u viewer1:password http://localhost:8080/api/v2/accounts/1` |
| GET | `/api/v2/accounts` | Fetch every account (admin or viewer) | `curl -u admin:password http://localhost:8080/api/v2/accounts` |
| PUT | `/api/v2/accounts/{id}` | Replace an account (admin) | `curl -u admin:password -X PUT http://localhost:8080/api/v2/accounts/1 -H 'Content-Type: application/json' -d '{"firstName":"Janet","lastName":"Doe","email":"jdoe@example.com"}'` |
| DELETE | `/api/v2/accounts/{id}` | Remove an account (admin) | `curl -u admin:password -X DELETE http://localhost:8080/api/v2/accounts/1 -i` |

## Testing

Run the test suite with:
```bash
./mvnw test
```

If multiple JDKs are installed, set `JAVA_HOME` to version 21 before running tests:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw clean test
```

Security checks need to be verified manually with curl and the Swagger UI.

## Credits

This project began after I followed Ramesh Fadatare's lessons and the in28minutes Spring Boot microservices lessons, then I merged what I learned into a single codebase.
