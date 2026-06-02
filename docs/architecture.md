# MVP Architecture

## Runtime Shape

The MVP uses a monorepo with separate web apps and microservice-style Spring
Boot services.
PostgreSQL with pgvector stores operational data and vector embeddings. Redis is
reserved for short-lived cache, rate limiting, and async coordination. MinIO is
used locally for resume object storage. Prometheus scrapes Spring Boot Actuator
metrics, and Grafana displays service health and JVM/application dashboards.

## Whole System Architecture Diagram

This diagram shows the full MVP runtime from users to frontend apps, gateway,
domain services, AI integrations, storage, DevOps, and observability. The arrows
show the normal request/data direction. Services remain in one monorepo for MVP
speed, but each backend module has a clear boundary so it can later be deployed
independently.

```mermaid
flowchart TB
  candidate["Candidate User"]
  employer["Employer / Recruiter User"]

  subgraph web["Frontend Apps - Next.js / React"]
    candidateWeb["Candidate Web<br/>Resume upload, job recommendations, profile"]
    employerWeb["Employer Web<br/>Job posting, applicant review, AI ranking"]
  end

  subgraph edge["Backend Entry"]
    gateway["API Gateway<br/>Routing, JWT checks, rate-limit hook"]
  end

  subgraph services["Spring Boot Domain Services"]
    auth["Auth Service<br/>Signup, login, token issue"]
    candidateSvc["Candidate Service<br/>Profiles, resumes, parsed skills, embeddings"]
    employerSvc["Employer Service<br/>Companies and employer users"]
    jobSvc["Job Service<br/>Job posts, search, job embeddings"]
    applicationSvc["Application Service<br/>Applications, statuses, recruiter workflow"]
    matchingSvc["Matching Service<br/>Similarity search, score, explanations"]
    notificationSvc["Notification Service<br/>Email now, SMS/push later"]
    commonLib["common-lib<br/>Shared DTOs, roles, errors"]
  end

  subgraph data["Data And Storage"]
    postgres[("PostgreSQL + pgvector<br/>Relational data and vector embeddings")]
    redis[("Redis<br/>Cache, rate limits, async coordination")]
    minio[("MinIO<br/>Local resume object storage")]
  end

  subgraph ai["AI Provider Layer"]
    prompts["Versioned prompts<br/>ai/prompts"]
    llm["OpenAI or Vertex AI Gemini<br/>Resume parsing, job parsing, explanations"]
    embeddings["Embedding Model<br/>Resume/job vector creation"]
  end

  subgraph devops["Free DevOps And Observability"]
    githubActions["GitHub Actions<br/>Hosted CI"]
    jenkins["Jenkins Pipeline<br/>Self-hosted CI option"]
    dockerCompose["Docker Compose<br/>Local dependencies"]
    prometheus["Prometheus<br/>Metrics scrape :9090"]
    grafana["Grafana OSS<br/>Dashboards :3002"]
  end

  candidate --> candidateWeb
  employer --> employerWeb
  candidateWeb --> gateway
  employerWeb --> gateway

  gateway --> auth
  gateway --> candidateSvc
  gateway --> employerSvc
  gateway --> jobSvc
  gateway --> applicationSvc
  gateway --> matchingSvc
  gateway --> notificationSvc

  auth --> postgres
  candidateSvc --> postgres
  employerSvc --> postgres
  jobSvc --> postgres
  applicationSvc --> postgres
  matchingSvc --> postgres

  gateway --> redis
  candidateSvc --> minio
  candidateSvc --> prompts
  jobSvc --> prompts
  matchingSvc --> prompts
  candidateSvc --> llm
  jobSvc --> llm
  matchingSvc --> llm
  candidateSvc --> embeddings
  jobSvc --> embeddings
  embeddings --> postgres

  applicationSvc --> notificationSvc

  services -. "shared models" .-> commonLib
  dockerCompose -. "runs local dependencies" .-> data
  prometheus -. "scrapes /actuator/prometheus" .-> services
  grafana -. "reads metrics" .-> prometheus
  githubActions -. "builds and verifies" .-> web
  githubActions -. "builds and verifies" .-> services
  jenkins -. "alternative CI" .-> web
  jenkins -. "alternative CI" .-> services
```

## Sequence Diagrams

### Candidate Signup, Resume Upload, And Job Recommendations

This is the most important candidate MVP flow. The fake MVP token in the current
scaffold should be replaced with real JWT signing/validation before production.

```mermaid
sequenceDiagram
  autonumber
  actor Candidate
  participant CandidateWeb as Candidate Web
  participant Gateway as API Gateway
  participant Auth as Auth Service
  participant CandidateSvc as Candidate Service
  participant MinIO as MinIO
  participant AI as AI Provider
  participant DB as PostgreSQL + pgvector
  participant Matching as Matching Service

  Candidate->>CandidateWeb: Sign up or log in
  CandidateWeb->>Gateway: POST /api/auth/login
  Gateway->>Auth: Forward auth request
  Auth->>DB: Validate or create user
  Auth-->>CandidateWeb: Return access token

  Candidate->>CandidateWeb: Upload resume
  CandidateWeb->>Gateway: POST /api/candidates/resume/upload
  Gateway->>CandidateSvc: Forward multipart resume upload
  CandidateSvc->>MinIO: Store original resume file
  CandidateSvc->>AI: Extract structured skills, experience, education
  AI-->>CandidateSvc: Parsed resume JSON
  CandidateSvc->>AI: Create candidate embedding
  AI-->>CandidateSvc: Candidate vector
  CandidateSvc->>DB: Save profile, resume metadata, embedding
  CandidateSvc-->>CandidateWeb: Resume upload and profile response

  Candidate->>CandidateWeb: Open recommended jobs
  CandidateWeb->>Gateway: GET /api/matching/candidate/{candidateId}/jobs
  Gateway->>Matching: Forward recommendation request
  Matching->>DB: Vector search candidate embedding against job embeddings
  Matching->>AI: Generate explanation and improvement suggestions
  AI-->>Matching: Match explanation
  Matching-->>CandidateWeb: Ranked job recommendations
```

### Employer Job Posting And Candidate Ranking

This flow explains how an employer creates a job and later reviews AI-ranked
candidate matches.

```mermaid
sequenceDiagram
  autonumber
  actor Recruiter
  participant EmployerWeb as Employer Web
  participant Gateway as API Gateway
  participant Auth as Auth Service
  participant EmployerSvc as Employer Service
  participant JobSvc as Job Service
  participant AI as AI Provider
  participant DB as PostgreSQL + pgvector
  participant Matching as Matching Service

  Recruiter->>EmployerWeb: Log in
  EmployerWeb->>Gateway: POST /api/auth/login
  Gateway->>Auth: Forward auth request
  Auth->>DB: Validate employer user and role
  Auth-->>EmployerWeb: Return access token

  Recruiter->>EmployerWeb: Create or update company profile
  EmployerWeb->>Gateway: POST /api/employers/company
  Gateway->>EmployerSvc: Forward company request
  EmployerSvc->>DB: Save company profile
  EmployerSvc-->>EmployerWeb: Company response

  Recruiter->>EmployerWeb: Publish job
  EmployerWeb->>Gateway: POST /api/employers/jobs
  Gateway->>JobSvc: Forward job request
  JobSvc->>AI: Parse job requirements and seniority
  AI-->>JobSvc: Structured job JSON
  JobSvc->>AI: Create job embedding
  AI-->>JobSvc: Job vector
  JobSvc->>DB: Save job post and embedding
  JobSvc-->>EmployerWeb: Job response

  Recruiter->>EmployerWeb: Review ranked candidates
  EmployerWeb->>Gateway: GET /api/matching/job/{jobId}/candidates
  Gateway->>Matching: Forward ranking request
  Matching->>DB: Vector search job embedding against candidate embeddings
  Matching->>AI: Explain strengths, gaps, and fit
  AI-->>Matching: Ranking explanation
  Matching-->>EmployerWeb: Ranked candidates with explanations
```

### Candidate Application And Notification

This flow shows how the portal handles an application after the candidate chooses
a job.

```mermaid
sequenceDiagram
  autonumber
  actor Candidate
  participant CandidateWeb as Candidate Web
  participant Gateway as API Gateway
  participant ApplicationSvc as Application Service
  participant JobSvc as Job Service
  participant DB as PostgreSQL
  participant NotificationSvc as Notification Service
  participant Email as Email Provider
  participant EmployerWeb as Employer Web

  Candidate->>CandidateWeb: Apply to a job
  CandidateWeb->>Gateway: POST /api/candidates/jobs/{jobId}/apply
  Gateway->>ApplicationSvc: Forward application request
  ApplicationSvc->>JobSvc: Verify job exists and is open
  JobSvc-->>ApplicationSvc: Job is open
  ApplicationSvc->>DB: Save application with APPLIED status
  ApplicationSvc->>NotificationSvc: Request confirmation email
  NotificationSvc->>Email: Send application confirmation
  Email-->>NotificationSvc: Accepted for delivery
  ApplicationSvc-->>CandidateWeb: Application response

  EmployerWeb->>Gateway: GET /api/employers/jobs/{jobId}/applicants
  Gateway->>ApplicationSvc: Request applicant list
  ApplicationSvc->>DB: Load applications for job
  ApplicationSvc-->>EmployerWeb: Applicant list and statuses
```

### Observability And CI Feedback Loop

This diagram shows how code changes become verified builds and how local runtime
health becomes visible in Prometheus and Grafana.

```mermaid
sequenceDiagram
  autonumber
  actor Developer
  participant GitHub as GitHub Repository
  participant Actions as GitHub Actions
  participant Jenkins as Jenkins Pipeline
  participant Docker as Docker Compose
  participant Services as Spring Boot Services
  participant Prometheus as Prometheus
  participant Grafana as Grafana

  Developer->>GitHub: Push branch or main
  GitHub->>Actions: Trigger hosted CI workflow
  Actions->>Actions: Run frontend checks and backend Maven verify
  Actions->>Actions: Validate Docker Compose configuration
  Actions-->>Developer: Pass/fail result in GitHub

  Developer->>Jenkins: Optional self-hosted build
  Jenkins->>Jenkins: Run same build and validation stages
  Jenkins-->>Developer: Pass/fail result in Jenkins

  Developer->>Docker: docker compose up
  Docker->>Services: Start local services and dependencies
  Prometheus->>Services: Scrape /actuator/prometheus
  Grafana->>Prometheus: Query metrics for dashboards
  Grafana-->>Developer: Service and JVM health dashboards
```

## Backend Microservices

Yes, the backend is intentionally split into microservice-style Spring Boot
modules. Each domain service can eventually be built, deployed, scaled, and owned
independently. For MVP speed, they live in one monorepo and share a parent Maven
build plus `common-lib`.

Current service modules:

| Module                 | Default Port | Primary Responsibility                                     |
| ---------------------- | ------------ | ---------------------------------------------------------- |
| `api-gateway`          | `8080`       | Single backend entry point and route/security boundary.    |
| `auth-service`         | `8081`       | Signup, login, token issue, and role-aware identity flows. |
| `candidate-service`    | `8082`       | Candidate profiles, resume uploads, parsing, embeddings.   |
| `employer-service`     | `8083`       | Company profiles and employer/recruiter workflows.         |
| `job-service`          | `8084`       | Job creation, publishing, search, parsing, embeddings.     |
| `application-service`  | `8085`       | Applications, applicant review, and status transitions.    |
| `matching-service`     | `8086`       | AI matching, scoring, vector search, explanations.         |
| `notification-service` | `8087`       | Email notifications first, other channels later.           |
| `common-lib`           | N/A          | Shared Java models, API wrappers, roles, and errors.       |

## Microservices Vs Docker Vs Kubernetes

These technologies solve different problems:

| Concept        | Primary Job                                                                                                     | Project Usage                                                                                                                       |
| -------------- | --------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| Microservices  | Split backend responsibilities by business domain and API boundary.                                             | Used now through separate Spring Boot modules for auth, candidate, employer, job, application, matching, notification, and gateway. |
| Docker         | Package an application and its runtime dependencies into a container image.                                     | Used now through service Dockerfiles and Docker Compose for local infrastructure.                                                   |
| Docker Compose | Run multiple local containers from one YAML file.                                                               | Used now for Postgres, Redis, MinIO, Prometheus, and Grafana.                                                                       |
| Kubernetes     | Orchestrate containerized workloads across a cluster with scheduling, scaling, service discovery, and rollouts. | Included as starter manifests, but not the default MVP path.                                                                        |

The MVP does use Docker. It does not make Kubernetes the first runtime because a
cluster adds operational overhead: nodes, ingress, service discovery, secrets,
resource limits, monitoring, deployments, and cluster upgrades. For early
validation, Docker Compose locally and Cloud Run later are enough. Move to GKE or
another Kubernetes platform when the product needs advanced orchestration,
custom networking, service mesh, workload identity, or high-control multi-service
operations.

## Services

- API Gateway: central entry point, JWT validation, rate-limit hook, route
  ownership boundary.
- Auth Service: candidate and employer signup/login, JWT issue and refresh,
  role model.
- Candidate Service: candidate profiles, resume uploads, resume parsing,
  profile summaries, candidate embeddings.
- Employer Service: company profile and employer user management.
- Job Service: job creation, publishing, search, job parsing, job embeddings.
- Matching Service: candidate-to-job and job-to-candidate matching, weighted
  score calculation, explanation generation, gap detection.
- Application Service: applications, statuses, applicant list, recruiter
  workflow, AI ranking integration.
- Notification Service: email notification first, later SMS, WhatsApp, and push.

## Roles

- CANDIDATE
- EMPLOYER_ADMIN
- RECRUITER
- ADMIN

## Current Scaffold Status

The project currently implements a production-shaped scaffold, not the complete
production system. Candidate Service has JPA entities and repositories for
profiles and resume metadata. Auth Service issues a fake MVP token. Employer,
job, application, matching, and notification services expose placeholder flows
that match the intended contracts but still need persistence, validation depth,
real AI provider calls, and workflow integrations.

## DevOps And Observability

- GitHub Actions runs hosted CI for frontend typecheck/build, backend Maven
  verify, and Docker Compose validation.
- `Jenkinsfile` provides the same free pipeline option for self-hosted Jenkins.
- Prometheus runs locally on port `9090` and scrapes backend
  `/actuator/prometheus` endpoints.
- Grafana OSS runs locally on port `3002` with a provisioned Prometheus data
  source and overview dashboard.

## Deployment Direction

Start with Docker Compose locally. Deploy services to Cloud Run for the fastest
MVP path or GKE when service mesh, workload identity, or deeper Kubernetes
control becomes necessary. Terraform placeholders are included but should stay
minimal until environments stabilize.

## References

- GitHub Actions billing:
  https://docs.github.com/en/billing/concepts/product-billing/github-actions
- Jenkins Pipeline: https://www.jenkins.io/doc/book/pipeline/
- Prometheus docs:
  https://prometheus.io/docs/prometheus/latest/getting_started/
- Grafana provisioning:
  https://grafana.com/docs/grafana/latest/administration/provisioning/
