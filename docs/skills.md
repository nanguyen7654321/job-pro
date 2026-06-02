# Skills Needed To Build And Operate This MVP

This document explains the practical skills a developer or team needs to move
the AI-powered job portal from scaffold to production-ready MVP.

## Product And Domain Skills

### Recruiting Workflow Knowledge

You need to understand how candidates, recruiters, and employer admins move
through a hiring funnel. This includes resume intake, job posting, applicant
review, shortlist/reject decisions, interviews, offers, and hiring outcomes.

Why it matters:

- The product is only useful if AI outputs fit real recruiting decisions.
- Application statuses must match recruiter workflows.
- Explanations must be useful to both candidates and employers.

### Data Privacy And Compliance Awareness

The platform handles resumes, contact details, employment history, and hiring
decisions. Developers should understand privacy-by-design basics.

Important habits:

- Avoid logging resumes, passwords, tokens, or raw AI prompts with personal data.
- Use explicit consent for AI processing.
- Keep candidate and employer access separated by role.
- Add retention and deletion policies before launch.

## Frontend Skills

### React And Next.js

The web apps use Next.js with React. Developers should know App Router layout,
client components, server rendering tradeoffs, and how to organize feature UI.

Used in this project:

- `frontend/candidate-web` for resume and job recommendation flows.
- `frontend/employer-web` for job and applicant review flows.

### Tailwind CSS And Component Design

The UI uses Tailwind CSS with small shadcn-style primitives. Developers should
know utility-first styling, responsive layout, accessible button and card
patterns, and how to keep repeated UI in components.

### API State Management

TanStack React Query is used for API state and caching. Developers should know
query keys, stale time, loading states, invalidation, and optimistic updates for
application status changes.

## Backend Skills

### Java And Spring Boot

The backend is a Java/Spring Boot multi-module system. Developers should know
controllers, services, dependency injection, validation, configuration, JPA, and
security filters.

Used in this project:

- `backend/common-lib` for shared response, role, and status types.
- Service modules for auth, candidate, employer, job, application, matching, and
  notification domains.

### API Design

The system uses JSON REST APIs with JWT authentication. Developers should know
OpenAPI, request validation, error response design, pagination, correlation IDs,
and backward-compatible endpoint evolution.

### Persistence Modeling

PostgreSQL is the primary data store. Developers should know relational schema
design, indexes, migrations, JSON columns, and transaction boundaries.

## AI And Search Skills

### Prompt Engineering

Prompt files are version-controlled in `ai/prompts`. Developers should know how
to write prompts that request structured JSON, avoid hallucinations, and handle
ambiguous resumes or job descriptions.

### Embeddings And Vector Search

The MVP stores embeddings with pgvector. Developers should understand embedding
models, cosine similarity, vector dimensions, re-embedding strategy, and how to
combine vector similarity with business rules.

### AI Evaluation

The `ai/evaluation` folder is for repeatable quality checks. Developers should
build sample resume/job pairs, expected match behavior, and regression checks for
prompt and scoring changes.

## Infrastructure Skills

### Docker And Docker Compose

Docker Compose runs local Postgres, Redis, and MinIO. Developers should know
container health checks, volume persistence, environment files, and local service
ports.

### Cloud Deployment Basics

The architecture leaves room for Cloud Run or GKE. Developers should understand
container image builds, secrets, service-to-service auth, horizontal scaling,
logs, metrics, and rollback strategy.

### Terraform

Terraform placeholders are included for dev/prod. Use Terraform after the cloud
shape is stable enough to codify. Early over-automation can slow MVP learning.

## Recommended Learning Order

1. Run Docker Compose and understand the local data services.
2. Run one frontend app and inspect the UI flow.
3. Read `docs/api-contracts.md` and map endpoints to services.
4. Read candidate-service because it touches profile, resume, AI, storage, and
   embeddings.
5. Read matching-service and `docs/ai-matching-design.md`.
6. Add one vertical feature end-to-end, such as candidate resume upload or job
   posting.

## DevOps And Observability Skills

### CI/CD Pipeline Skills

Developers should understand pipeline stages, branch and pull-request triggers,
build caching, environment variables, and secret handling. This project includes
GitHub Actions for hosted CI and a Jenkinsfile for self-hosted CI.

Important habits:

- Keep pipeline steps deterministic.
- Run typecheck/build/verify before deployment.
- Never print secrets in logs.
- Keep GitLab CI separate unless the team explicitly chooses GitLab hosting.

### Prometheus Skills

Prometheus scrapes metrics from backend `/actuator/prometheus` endpoints.
Developers should understand scrape targets, labels, `up`, counters, gauges,
histograms, and PromQL basics.

### Grafana Skills

Grafana reads Prometheus data and displays dashboards. Developers should know how
to use provisioned datasources, dashboard JSON, panels, variables, and basic
PromQL queries.
