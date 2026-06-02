# Backend

The detailed backend setup is now combined into the root
[README.md](../README.md). Start there for from-scratch setup, Git repository
workflow, frontend startup, backend startup, CI, Prometheus, and Grafana.

## Backend Summary

The backend is organized as microservice-style Spring Boot modules. Each service
has its own Maven module, Spring Boot application entry point, `application.yml`,
Dockerfile, and default local port.

| Service                | Purpose                                        | Port   |
| ---------------------- | ---------------------------------------------- | ------ |
| `api-gateway`          | central entry point and route boundary         | `8080` |
| `auth-service`         | signup, login, roles, future JWT issue/refresh | `8081` |
| `candidate-service`    | profiles, resume metadata, parsing, embeddings | `8082` |
| `employer-service`     | company profile and employer workflow          | `8083` |
| `job-service`          | job posting and future job parsing/embedding   | `8084` |
| `application-service`  | applications and recruiter status changes      | `8085` |
| `matching-service`     | weighted matching and recommendations          | `8086` |
| `notification-service` | email first, later SMS/WhatsApp/push           | `8087` |

Candidate Service currently has the first real JPA persistence path. The other
services expose placeholder contract flows until repositories and integrations
are added.

## Backend Commands

From the project root:

```bash
./scripts/start-local.sh
cd backend
mvn clean verify
mvn -pl candidate-service spring-boot:run
```

Run another service by replacing `candidate-service` with the module name.

## Metrics

All Spring Boot service modules include Actuator and Micrometer Prometheus
registry dependencies. When a service is running, Prometheus can scrape:

```text
http://localhost:<service-port>/actuator/prometheus
```

Prometheus:

```text
http://localhost:9090
```

Grafana:

```text
http://localhost:3002
```

## Microservices, Docker, And Kubernetes

- Microservices are the backend architecture style.
- Docker packages/runs services and dependencies as containers.
- Docker Compose runs the local infrastructure stack.
- Kubernetes is optional for later cluster orchestration and is not required for
  the MVP local workflow.
