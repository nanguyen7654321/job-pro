# Requirement Traceability

Source requirement document:

```text
/Users/diemn/Downloads/SD-AI-Powered Job Portal MVP Architecture and Tech Stack-020626-190415.pdf
```

This document maps the PDF requirements to the local project files. The project
is an MVP scaffold: it follows the requested architecture and technology
direction, while several production behaviors remain intentionally marked as
future work.

## Coverage Summary

| Requirement Area                               | Status                                                    | Project Evidence                                                                           |
| ---------------------------------------------- | --------------------------------------------------------- | ------------------------------------------------------------------------------------------ |
| Candidate uploads resume                       | Scaffolded with persistence target                        | `backend/candidate-service`, `ResumeController`, `ResumeDocument`, `docs/api-contracts.md` |
| Candidate gets AI-generated profile            | Scaffolded                                                | `CandidateProfileService`, `ResumeParsingService`, `ai/prompts/resume-parser.prompt.md`    |
| Candidate sees best-matched jobs               | Scaffolded                                                | `frontend/candidate-web`, `MatchingController`, `docs/ai-matching-design.md`               |
| Candidate gets match explanation               | Implemented as deterministic scoring/explanation scaffold | `backend/matching-service`, `MatchScoreResponse`, `ai/prompts/match-explanation.prompt.md` |
| Candidate applies easily                       | Scaffolded                                                | `backend/application-service`, `POST /api/candidates/jobs/{jobId}/apply`                   |
| Employer posts jobs                            | Scaffolded                                                | `backend/job-service`, `POST /api/employers/jobs`                                          |
| Employer views applicants                      | Scaffolded                                                | `backend/application-service`, `GET /api/employers/jobs/{jobId}/applicants`                |
| Employer gets AI-ranked candidates             | Scaffolded                                                | `backend/matching-service`, `GET /api/matching/job/{jobId}/candidates`                     |
| Employer sees strengths, gaps, and match score | Scaffolded                                                | `MatchScoreResponse`, `MatchingService`, `ai/prompts/match-explanation.prompt.md`          |

## Technology Stack Traceability

| PDF Requirement                | Project Implementation                                                                                                                                                            |
| ------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Next.js / React                | `frontend/candidate-web`, `frontend/employer-web` use Next.js, React, and App Router.                                                                                             |
| Tailwind CSS                   | Both web apps include `tailwind.config.ts`, `postcss.config.mjs`, and `src/app/globals.css`.                                                                                      |
| Shadcn UI                      | Both web apps include `components.json`, Shadcn-style `Button` and `Card` primitives, `cn()` utilities, `class-variance-authority`, `clsx`, `tailwind-merge`, and `lucide-react`. |
| Axios / React Query            | Both web apps use `axios` and `@tanstack/react-query` with API clients and hooks.                                                                                                 |
| Java 25                        | `backend/pom.xml` sets `<java.version>25</java.version>`.                                                                                                                         |
| Spring Boot 4.x                | `backend/pom.xml` sets Spring Boot `4.0.6`.                                                                                                                                       |
| Spring Security                | Auth and gateway services include Spring Security configuration.                                                                                                                  |
| Spring Data JPA                | Candidate Service uses JPA entities and repositories; schema docs define persistence targets for the other services.                                                              |
| PostgreSQL                     | `docker-compose.yml`, `.env.example`, and `docs/database-schema.md`.                                                                                                              |
| Redis                          | `docker-compose.yml`, `.env.example`, architecture docs.                                                                                                                          |
| OpenAPI / Swagger              | `springdoc-openapi-starter-webmvc-ui` is added to service modules and documented in `docs/api-contracts.md`.                                                                      |
| Vertex AI Gemini / OpenAI      | Provider choice is represented in `.env.example`, `ai/`, and client interfaces.                                                                                                   |
| Embedding model                | `ai/embeddings/embedding-client.md`, `CandidateEmbeddingService`, matching docs.                                                                                                  |
| pgvector first, Pinecone later | `docker-compose.yml` uses `pgvector/pgvector:pg16`; docs explain Pinecone as later option.                                                                                        |
| Docker                         | Service Dockerfiles and `docker-compose.yml`.                                                                                                                                     |
| Docker Compose local           | `docker-compose.yml`, `scripts/start-local.sh`, `scripts/stop-local.sh`.                                                                                                          |
| GKE / Cloud Run deployment     | Architecture and technology docs describe Cloud Run first and GKE/Kubernetes when needed; starter manifests live in `infra/k8s`.                                                  |
| Terraform later                | `infra/terraform/dev/main.tf`, `infra/terraform/prod/main.tf`.                                                                                                                    |
| GitHub Actions / Jenkins       | `.github/workflows/ci.yml`, `Jenkinsfile`, `docs/devops-and-observability.md`.                                                                                                    |

## Service Traceability

| PDF Service          | Project Module                 | Notes                                                                      |
| -------------------- | ------------------------------ | -------------------------------------------------------------------------- |
| API Gateway          | `backend/api-gateway`          | Central Spring Boot entry point and route ownership map.                   |
| Auth Service         | `backend/auth-service`         | Signup/login DTOs, roles, MVP token scaffold, security config.             |
| Candidate Service    | `backend/candidate-service`    | Profile, resume upload, parser, embedding service, repositories, entities. |
| Job Service          | `backend/job-service`          | Employer job endpoints and job DTO scaffold.                               |
| Matching Service     | `backend/matching-service`     | Candidate-to-job/job-to-candidate endpoints and weighted score logic.      |
| Application Service  | `backend/application-service`  | Apply, applicant list, status update scaffold.                             |
| Notification Service | `backend/notification-service` | Email notification endpoint scaffold.                                      |
| Employer Service     | `backend/employer-service`     | Company profile endpoint scaffold.                                         |

## Monorepo Structure Traceability

| PDF Folder               | Project Folder                                                                                                      |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------- |
| `docs/`                  | Architecture, API contracts, database schema, AI matching design, sprint plan, setup, skills, DevOps, traceability. |
| `frontend/candidate-web` | Candidate Next.js app with app, components, services, hooks, types.                                                 |
| `frontend/employer-web`  | Employer Next.js app with app, components, services, hooks, types.                                                  |
| `backend/common-lib`     | Shared DTO, error, role, and status models.                                                                         |
| `backend/*-service`      | Spring Boot service modules with `pom.xml`, Dockerfile, source packages, and application config.                    |
| `ai/prompts`             | Resume parser, job parser, match explanation, resume improvement prompts.                                           |
| `ai/embeddings`          | Embedding client design note.                                                                                       |
| `ai/evaluation`          | Sample resume/job placeholders and match score tests.                                                               |
| `infra/docker`           | Docker notes.                                                                                                       |
| `infra/k8s`              | Starter Kubernetes manifests for auth, candidate, job, and matching services.                                       |
| `infra/terraform`        | Dev/prod placeholders.                                                                                              |
| `scripts`                | Start, stop, and seed-data scripts.                                                                                 |

## Database Entity Traceability

| PDF Entity           | Project Evidence                                                    |
| -------------------- | ------------------------------------------------------------------- |
| `users`              | `docs/database-schema.md`, `scripts/seed-data.sql`                  |
| `candidate_profiles` | JPA entity `CandidateProfile`, repository, schema docs, seed SQL    |
| `candidate_skills`   | Schema docs and seed SQL                                            |
| `resumes`            | JPA entity `ResumeDocument`, repository, schema docs, seed SQL      |
| `companies`          | Schema docs and seed SQL                                            |
| `employer_users`     | Schema docs and seed SQL                                            |
| `jobs`               | Schema docs and seed SQL                                            |
| `applications`       | Schema docs and seed SQL                                            |
| `match_scores`       | Schema docs and seed SQL                                            |
| Embeddings           | `embeddings` table in schema docs and seed SQL for pgvector storage |

## API Contract Traceability

The PDF API paths are covered in `docs/api-contracts.md`. Current controller
coverage includes:

- Auth: `/api/auth/signup`, `/api/auth/login`
- Candidate: `/api/candidates/profile`, `/api/candidates/profile/me`,
  `/api/candidates/resume/upload`, `/api/candidates/resume/latest`
- Employer: `/api/employers/company`, `/api/employers/company/me`
- Jobs: `/api/employers/jobs`, `/api/employers/jobs/{jobId}`
- Applications: `/api/candidates/jobs/{jobId}/apply`,
  `/api/employers/jobs/{jobId}/applicants`,
  `/api/employers/applications/{applicationId}/status`
- Matching: `/api/matching/candidate/{candidateId}/refresh`,
  `/api/matching/job/{jobId}/refresh`,
  `/api/matching/candidate/{candidateId}/jobs`,
  `/api/matching/job/{jobId}/candidates`, `/api/matching/explain`
- Notification: `/api/notifications/email`

## AI Prompt And Matching Flow Traceability

| PDF Requirement               | Project Evidence                                                                               |
| ----------------------------- | ---------------------------------------------------------------------------------------------- |
| Resume parser prompt          | `ai/prompts/resume-parser.prompt.md`                                                           |
| Job parser prompt             | `ai/prompts/job-parser.prompt.md`                                                              |
| Match explanation prompt      | `ai/prompts/match-explanation.prompt.md`                                                       |
| Resume improvement prompt     | `ai/prompts/resume-improvement.prompt.md`                                                      |
| Candidate resume to embedding | `docs/ai-matching-design.md`, `CandidateEmbeddingService`, `ai/embeddings/embedding-client.md` |
| Job description to embedding  | `docs/ai-matching-design.md`, `JobService`, `ai/embeddings/embedding-client.md`                |
| Similarity search             | `docs/ai-matching-design.md`, `docs/database-schema.md`, pgvector Docker image                 |
| Weighted score                | `MatchingService`, `docs/ai-matching-design.md`, `ai/evaluation/match-score-tests.md`          |
| LLM explanation               | `MatchingService`, `ai/prompts/match-explanation.prompt.md`                                    |

## Known MVP Scaffold Gaps

The project follows the requirement document as an MVP architecture scaffold.
These items remain intentionally incomplete until product behavior stabilizes:

- Replace MVP placeholder token logic with signed JWT, refresh tokens, password
  hashing, and persistence.
- Wire real OpenAI or Vertex AI Gemini calls behind `AiClient` instead of
  deterministic placeholder parsing.
- Persist employer, job, application, matching, and notification workflows
  fully instead of returning scaffold DTO responses.
- Add database migrations and real foreign-key constraints.
- Add production deployment automation after the MVP local workflow is stable.
- Add end-to-end tests and integration tests around the primary candidate and
  employer flows.
