# API Contracts

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

## API Standards

- JSON request and response bodies.
- JWT bearer authentication.
- OpenAPI should be generated per service once endpoint shapes stabilize;
  this markdown file is the current scaffold contract.
- Correlation ID propagated with `X-Request-Id`.
- Validation errors use field-level details.
