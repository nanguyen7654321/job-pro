# API Contracts

For the data model map behind these APIs, see
[`docs/database-schema.md`](database-schema.md). That document separates the
database schema, implemented JPA entities, backend DTOs, and frontend view
models.

## Response Envelope

Successful API responses use the shared `ApiResponse<T>` envelope:

```json
{
  "data": {},
  "message": "ok",
  "timestamp": "2026-06-03T12:00:00Z"
}
```

`data` changes by endpoint. The examples below describe the `data` object or
array inside that envelope.

## Auth Service

Runs on port `8081` locally.

| Method | Path | What It Does | Input | Output `data` |
| --- | --- | --- | --- | --- |
| `POST` | `/api/auth/signup` | Issues an MVP bearer token for signup. The current implementation does not persist users or hash passwords yet. | JSON body: `email` string email format, `password` required string, `role` one of `CANDIDATE`, `EMPLOYER_ADMIN`, `RECRUITER`, `ADMIN`. | `AuthResponse`: `accessToken`, `tokenType`, `role`. |
| `POST` | `/api/auth/login` | Issues an MVP bearer token for login. Uses the same placeholder token logic as signup. | JSON body: `email` string email format, `password` required string, `role` one of `CANDIDATE`, `EMPLOYER_ADMIN`, `RECRUITER`, `ADMIN`. | `AuthResponse`: `accessToken`, `tokenType`, `role`. |

Example input:

```json
{
  "email": "candidate@example.com",
  "password": "secret123",
  "role": "CANDIDATE"
}
```

## Candidate Service

Runs on port `8082` locally.

| Method | Path | What It Does | Input | Output `data` |
| --- | --- | --- | --- | --- |
| `POST` | `/api/candidates/profile` | Creates or updates the candidate profile for the supplied `userId`. It also generates and stores an AI summary. | JSON body: `userId` required UUID, `fullName` required string, `headline`, `location`, `totalExperienceYears`, `currentTitle`, `desiredTitle`, `preferredLocation`, `openToRemote`. | `CandidateProfileResponse`: `id`, `userId`, `fullName`, `headline`, `location`, `totalExperienceYears`, `currentTitle`, `desiredTitle`, `preferredLocation`, `openToRemote`, `aiSummary`. |
| `GET` | `/api/candidates/profile/me` | Reads the candidate profile for the user id in the request header. | Header: `X-User-Id` UUID. No body. | `CandidateProfileResponse`. |
| `PUT` | `/api/candidates/profile/me` | Updates or creates a candidate profile. Despite the `/me` path, the current implementation uses `userId` from the JSON body. | Same JSON body as `POST /api/candidates/profile`. | `CandidateProfileResponse`. |
| `POST` | `/api/candidates/resume/upload` | Creates a resume record, stores a generated object-storage URL, parses supplied text, refreshes the candidate embedding, and stores AI parse metadata. This scaffold accepts text params instead of a real multipart file. | Header: `X-Candidate-Id` UUID. Query/form params: `fileName` required string, `extractedText` optional string. | `ResumeUploadResponse`: `resumeId`, `candidateId`, `fileUrl`, `aiSummary`. |
| `GET` | `/api/candidates/resume/latest` | Returns the latest uploaded resume for the candidate. | Header: `X-Candidate-Id` UUID. No body. | `ResumeUploadResponse`. |

Example profile input:

```json
{
  "userId": "11111111-1111-1111-1111-111111111111",
  "fullName": "Diem Nguyen",
  "headline": "Backend engineer",
  "location": "San Jose, CA",
  "totalExperienceYears": 5,
  "currentTitle": "Software Engineer",
  "desiredTitle": "Senior Software Engineer",
  "preferredLocation": "Remote",
  "openToRemote": true
}
```

## Employer Service

Runs on port `8083` locally.

| Method | Path | What It Does | Input | Output `data` |
| --- | --- | --- | --- | --- |
| `POST` | `/api/employers/company` | Creates a company response with a generated UUID. The current implementation does not persist companies yet. | JSON body: `name` required string, `website`, `industry`, `size`. | `CompanyResponse`: `id`, `name`, `website`, `industry`, `size`. |
| `GET` | `/api/employers/company/me` | Returns a demo company placeholder. | No body. | `CompanyResponse` with `name` set to `Demo Company` and most other fields null. |

Example input:

```json
{
  "name": "Acme AI",
  "website": "https://example.com",
  "industry": "Software",
  "size": "51-200"
}
```

## Job Service

Runs on port `8084` locally.

| Method | Path | What It Does | Input | Output `data` |
| --- | --- | --- | --- | --- |
| `POST` | `/api/employers/jobs` | Creates a job response with a generated UUID and `DRAFT` status. The current implementation does not persist jobs yet. | JSON body: `companyId` UUID, `title` required string, `description` required string, `location`, `employmentType`, `experienceMin`, `experienceMax`, `salaryMin`, `salaryMax`. | `JobResponse`: `id`, `companyId`, `title`, `description`, `location`, `employmentType`, `experienceMin`, `experienceMax`, `salaryMin`, `salaryMax`, `status`. |
| `GET` | `/api/employers/jobs` | Lists employer jobs. Current implementation returns an empty list. | No body. | Array of `JobResponse`. |
| `GET` | `/api/employers/jobs/{jobId}` | Gets one job by id. Current implementation returns a demo job using the path `jobId`. | Path: `jobId` UUID. No body. | `JobResponse` with `title` set to `Demo Job`, `status` set to `PUBLISHED`, and several fields null or empty. |
| `PUT` | `/api/employers/jobs/{jobId}` | Updates a job. Current implementation reuses create logic and returns a new generated UUID instead of the path id. | Path: `jobId` UUID. JSON body same as create. | `JobResponse` with `DRAFT` status. |
| `DELETE` | `/api/employers/jobs/{jobId}` | Deletes a job. Current implementation returns success without persistence. | Path: `jobId` UUID. No body. | `null`. |

Example input:

```json
{
  "companyId": "22222222-2222-2222-2222-222222222222",
  "title": "Backend Engineer",
  "description": "Build APIs for the job platform.",
  "location": "Remote",
  "employmentType": "FULL_TIME",
  "experienceMin": 3,
  "experienceMax": 6,
  "salaryMin": 120000,
  "salaryMax": 160000
}
```

## Application Service

Runs on port `8085` locally.

| Method | Path | What It Does | Input | Output `data` |
| --- | --- | --- | --- | --- |
| `POST` | `/api/candidates/jobs/{jobId}/apply` | Creates an application response for a candidate applying to a job. Current implementation returns a generated id, `APPLIED` status, zero match score, and `AI review pending`. | Path: `jobId` UUID. JSON body: `candidateId` required UUID. The DTO also contains `jobId`, but the controller replaces it with the path value. | `ApplicationResponse`: `id`, `jobId`, `candidateId`, `status`, `matchScore`, `aiSummary`. |
| `GET` | `/api/employers/jobs/{jobId}/applicants` | Lists applicants for a job. Current implementation returns an empty list. | Path: `jobId` UUID. No body. | Array of `ApplicationResponse`. |
| `PUT` | `/api/employers/applications/{applicationId}/status` | Updates application status. Current implementation returns the path id and requested status only. | Path: `applicationId` UUID. Query param: `status` one of `APPLIED`, `AI_REVIEWED`, `SHORTLISTED`, `INTERVIEW`, `REJECTED`, `OFFERED`, `HIRED`. No body. | `ApplicationResponse` with `id` and `status`; other fields are null. |

Example apply input:

```json
{
  "candidateId": "11111111-1111-1111-1111-111111111111"
}
```

## Matching Service

Runs on port `8086` locally.

| Method | Path | What It Does | Input | Output `data` |
| --- | --- | --- | --- | --- |
| `POST` | `/api/matching/candidate/{candidateId}/refresh` | Queues or simulates a candidate embedding refresh. Current implementation returns a message string only. | Path: `candidateId` UUID. No body. | String: `candidate embedding refresh queued: {candidateId}`. |
| `POST` | `/api/matching/job/{jobId}/refresh` | Queues or simulates a job embedding refresh. Current implementation returns a message string only. | Path: `jobId` UUID. No body. | String: `job embedding refresh queued: {jobId}`. |
| `GET` | `/api/matching/candidate/{candidateId}/jobs` | Lists matched jobs for a candidate. Current implementation returns an empty list. | Path: `candidateId` UUID. No body. | Array of `MatchScoreResponse`. |
| `GET` | `/api/matching/job/{jobId}/candidates` | Lists matched candidates for a job. Current implementation returns an empty list. | Path: `jobId` UUID. No body. | Array of `MatchScoreResponse`. |
| `POST` | `/api/matching/explain` | Computes an explainable weighted match score from supplied component scores. | JSON body: `candidateId` required UUID, `jobId` required UUID, `skillsScore`, `experienceScore`, `titleScore`, `locationScore`, `aiReasoningScore`; each score must be 0 through 100. | `MatchScoreResponse`: `candidateId`, `jobId`, `overallScore`, component scores, `explanation`, `missingSkills`. |

Overall score formula:

```text
skillsScore * 0.40
+ experienceScore * 0.25
+ titleScore * 0.15
+ locationScore * 0.10
+ aiReasoningScore * 0.10
```

Example input:

```json
{
  "candidateId": "11111111-1111-1111-1111-111111111111",
  "jobId": "33333333-3333-3333-3333-333333333333",
  "skillsScore": 90,
  "experienceScore": 85,
  "titleScore": 80,
  "locationScore": 100,
  "aiReasoningScore": 88
}
```

## Notification Service

Runs on port `8087` locally.

| Method | Path | What It Does | Input | Output `data` |
| --- | --- | --- | --- | --- |
| `POST` | `/api/notifications/email` | Queues or simulates an email notification. Current implementation returns a message string only. | JSON body: `to` string email format, `subject` required string, `body` required string. | String: `queued email to {to}`. |

Example input:

```json
{
  "to": "candidate@example.com",
  "subject": "Application received",
  "body": "Thanks for applying."
}
```

## API Gateway

Runs on port `8080` locally.

| Method | Path | What It Does | Input | Output `data` |
| --- | --- | --- | --- | --- |
| `GET` | `/api/gateway/routes` | Returns the intended route ownership map while the MVP gateway is still a Spring Boot module. | No body. | Map of route groups to path patterns, including `auth`, `candidates`, `employers`, `jobs`, and `matching`. |

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
- JWT bearer authentication is the intended production standard. The current
  auth service still returns an MVP placeholder token.
- OpenAPI is generated per service with Swagger UI for local exploration.
- Correlation ID propagated with `X-Request-Id`.
- Validation errors use field-level details.
