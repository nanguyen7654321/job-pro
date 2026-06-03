# API Contracts

For the data model map behind these APIs, see
[`docs/database-schema.md`](database-schema.md). That document separates the
database schema, implemented JPA entities, backend DTOs, and frontend view
models.

## Candidate APIs

- `POST /api/candidates/profile`
- `GET /api/candidates/profile/me`
- `PUT /api/candidates/profile/me`
- `POST /api/candidates/resume/upload`
- `GET /api/candidates/resume/latest`
- `GET /api/candidates/jobs/recommended`
- `GET /api/candidates/jobs/{jobId}/match`
- `POST /api/candidates/jobs/{jobId}/apply`

## Employer APIs

- `POST /api/employers/company`
- `GET /api/employers/company/me`
- `POST /api/employers/jobs`
- `GET /api/employers/jobs`
- `GET /api/employers/jobs/{jobId}`
- `PUT /api/employers/jobs/{jobId}`
- `DELETE /api/employers/jobs/{jobId}`
- `GET /api/employers/jobs/{jobId}/applicants`
- `GET /api/employers/jobs/{jobId}/ranked-candidates`
- `PUT /api/employers/applications/{applicationId}/status`

## Matching APIs

- `POST /api/matching/candidate/{candidateId}/refresh`
- `POST /api/matching/job/{jobId}/refresh`
- `GET /api/matching/candidate/{candidateId}/jobs`
- `GET /api/matching/job/{jobId}/candidates`
- `POST /api/matching/explain`
- `GET /api/matching/explain`

## Operational Endpoints

Spring Boot services expose operational endpoints for local monitoring:

- `GET /actuator/health`
- `GET /actuator/info`
- `GET /actuator/prometheus`

Prometheus scrapes `/actuator/prometheus`; application clients should not depend
on actuator endpoints for product workflows.

## OpenAPI And Swagger UI

Each Spring Boot service generates an OpenAPI document and serves Swagger UI.
Replace `<service-port>` with the service port, such as `8086` for
`matching-service`:

- `GET /v3/api-docs`
- `GET /v3/api-docs.yaml`
- `GET /swagger-ui.html`

Example:

```text
http://localhost:8086/swagger-ui.html
http://localhost:8086/v3/api-docs
```

## API Standards

- JSON request and response bodies.
- JWT bearer authentication.
- OpenAPI is generated per service with Swagger UI for local exploration.
- Correlation ID propagated with `X-Request-Id`.
- Validation errors use field-level details.
