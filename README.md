# AI-Powered Job Portal MVP

AI-powered job portal MVP for candidates and employers.

Candidates can upload resumes, receive an AI-generated profile summary, see
recommended jobs, and get resume improvement suggestions. Employers can post
jobs, review applicants, and use AI-assisted ranking and explanations to
understand why candidates may be strong matches.

This README is the combined project and backend guide. It covers repository
setup, local development, backend microservices, frontend apps, CI pipelines,
Prometheus, Grafana, and component-by-component startup from a fresh machine.

## Project Location

Local folder:

```bash
cd /Users/diemn/Desktop/1WorkSpace/ai-job-search-platform
```

Recommended free hosted repository:

- GitHub public repository for free hosted GitHub Actions standard runners.
- GitHub private repository is also possible, but hosted Actions minutes/storage
  use the account's included quota.
- Jenkins is included as a free self-hosted CI alternative.

## Architecture Summary

The backend is microservice-style. Each business domain has its own Spring Boot
service module, configuration, Dockerfile, and default port.

| Area          | Technology                                                      |
| ------------- | --------------------------------------------------------------- |
| Candidate web | Next.js, React, TypeScript, Tailwind CSS, Axios, TanStack Query |
| Employer web  | Next.js, React, TypeScript, Tailwind CSS, Axios, TanStack Query |
| Backend       | Java 25, Spring Boot 4.0.6, Spring Security, Spring Data JPA    |
| Data          | PostgreSQL, pgvector, Redis, MinIO                              |
| AI            | Prompt files, provider interfaces for OpenAI / Vertex AI Gemini |
| DevOps        | GitHub Actions, Jenkinsfile, Docker Compose                     |
| Observability | Spring Boot Actuator, Micrometer, Prometheus, Grafana           |
| Future deploy | Cloud Run first, GKE/Kubernetes when needed                     |

## Repository Setup From Scratch

### 1. Create A Free GitHub Repository

Create a new repository in GitHub:

1. Go to `https://github.com/new`.
2. Name the repository, for example `ai-job-search-platform`.
3. Choose `Public` if you want free standard GitHub-hosted Actions runners.
4. Do not initialize with a README if you are pushing this existing project.
5. Create the repository.

### 2. Add This Local Project To Git

Optional GitHub CLI shortcut after `gh auth login`:

```bash
cd /Users/diemn/Desktop/1WorkSpace/ai-job-search-platform
gh repo create ai-job-search-platform --public --source=. --remote=origin --push
```

Manual Git flow from the project folder:

```bash
cd /Users/diemn/Desktop/1WorkSpace/ai-job-search-platform
git init
git add .
git commit -m "Initial AI job portal MVP scaffold"
git branch -M main
git remote add origin https://github.com/<your-user-or-org>/ai-job-search-platform.git
git push -u origin main
```

If the folder is already a Git repo, check the remote first:

```bash
git remote -v
git status
```

### 3. Clone The Repository On A New Machine

```bash
git clone https://github.com/<your-user-or-org>/ai-job-search-platform.git
cd ai-job-search-platform
```

### 4. Check Out A Feature Branch

```bash
git checkout main
git pull origin main
git checkout -b feature/resume-upload
```

### 5. Check In Changes

```bash
git status
git add README.md backend/README.md
git commit -m "Document full setup workflow"
git push -u origin feature/resume-upload
```

### 6. Merge Changes

Recommended team flow:

1. Push your feature branch.
2. Open a GitHub pull request into `main`.
3. Wait for GitHub Actions to pass.
4. Review the diff.
5. Merge the pull request in GitHub.
6. Update local `main`:

```bash
git checkout main
git pull origin main
git branch -d feature/resume-upload
```

Local-only merge option:

```bash
git checkout main
git pull origin main
git merge feature/resume-upload
git push origin main
```

Use pull requests for normal team work because they preserve review history and
show CI results before merge.

## Install Tools From Scratch

The commands below are macOS-friendly because this project is currently under a
`/Users/...` path. On Windows or Linux, install the same tools using the official
installer for that platform.

### Required For Local Development

Install Homebrew if needed:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

Install core CLI tools:

```bash
brew install git node maven
```

Install Java 25 with an official JDK distribution. Homebrew may provide a versioned Temurin cask:

```bash
brew install --cask temurin@25
```

If that cask name is unavailable on your machine, install Eclipse Temurin 25 from Adoptium or another trusted JDK vendor, then verify with:

```bash
java -version
mvn -v
node -v
npm -v
git --version
```

Install Docker Desktop:

```bash
brew install --cask docker
```

Then open Docker Desktop once and wait until it says Docker is running.

Verify Docker:

```bash
docker --version
docker compose version
```

### Optional But Useful

```bash
brew install postgresql@16 redis
```

Optional GitHub CLI:

```bash
brew install gh
gh auth login
```

Optional Jenkins:

```bash
brew install jenkins-lts
brew services start jenkins-lts
```

Jenkins defaults to `http://localhost:8080`, which conflicts with this
project's API Gateway. If you run Jenkins locally, use Jenkins when the API
Gateway is stopped or configure Jenkins to run on another port such as `8088`.

## Environment Setup

Create the local environment file:

```bash
cp .env.example .env
```

Important local defaults:

| Component        | URL / Value             |
| ---------------- | ----------------------- |
| PostgreSQL       | `localhost:5432`        |
| Redis            | `localhost:6379`        |
| MinIO API        | `http://localhost:9000` |
| MinIO console    | `http://localhost:9001` |
| Prometheus       | `http://localhost:9090` |
| Grafana          | `http://localhost:3002` |
| Grafana user     | `admin`                 |
| Grafana password | `admin`                 |

AI keys are optional for the scaffold because AI clients currently use
placeholder logic. Add real provider keys only when you wire real AI calls:

```text
AI_PROVIDER=openai
OPENAI_API_KEY=
VERTEX_PROJECT_ID=
VERTEX_LOCATION=us-central1
```

## Run The Project From Scratch

### 1. Start Infrastructure And Observability

```bash
./scripts/start-local.sh
```

This starts:

- PostgreSQL with pgvector
- Redis
- MinIO
- Prometheus
- Grafana

Equivalent direct command:

```bash
docker compose up -d postgres redis minio prometheus grafana
```

Check containers:

```bash
docker compose ps
```

Stop containers:

```bash
./scripts/stop-local.sh
```

### 2. Initialize Or Refresh Database Schema

The first time Docker creates the Postgres volume, it automatically runs:

```text
scripts/seed-data.sql
```

If you already had a Postgres volume and the schema changed, run:

```bash
psql postgresql://aijobs:aijobs@localhost:5432/aijobs -f scripts/seed-data.sql
```

If `psql` is not installed, use a database client or recreate the local Docker
volume only when you are comfortable losing local data.

### 3. Install Frontend Dependencies

```bash
npm install
```

This installs root workspace dependencies for:

- `frontend/candidate-web`
- `frontend/employer-web`

### 4. Run Candidate Web

```bash
npm run dev:candidate
```

Open:

```text
http://localhost:3000
```

### 5. Run Employer Web

In a second terminal:

```bash
npm run dev:employer
```

Open:

```text
http://localhost:3001
```

### 6. Build Or Typecheck Web Apps

```bash
npm run typecheck:web
npm run build:web
```

### 7. Build Backend

```bash
cd backend
mvn clean verify
```

If `mvn` is not found, install Maven. If Candidate Service fails with a schema
validation error, confirm Postgres is running and `scripts/seed-data.sql` has
run.

### 8. Run Backend Services

Each backend service is a Spring Boot module.

| Service              | Command                                        | Port   |
| -------------------- | ---------------------------------------------- | ------ |
| API Gateway          | `mvn -pl api-gateway spring-boot:run`          | `8080` |
| Auth Service         | `mvn -pl auth-service spring-boot:run`         | `8081` |
| Candidate Service    | `mvn -pl candidate-service spring-boot:run`    | `8082` |
| Employer Service     | `mvn -pl employer-service spring-boot:run`     | `8083` |
| Job Service          | `mvn -pl job-service spring-boot:run`          | `8084` |
| Application Service  | `mvn -pl application-service spring-boot:run`  | `8085` |
| Matching Service     | `mvn -pl matching-service spring-boot:run`     | `8086` |
| Notification Service | `mvn -pl notification-service spring-boot:run` | `8087` |

Example:

```bash
cd backend
mvn -pl candidate-service spring-boot:run
```

### 9. Test Example APIs

Auth signup:

```bash
curl -X POST http://localhost:8081/api/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "candidate@example.com",
    "password": "password123",
    "role": "CANDIDATE"
  }'
```

Matching explanation:

```bash
curl -X POST http://localhost:8086/api/matching/explain \
  -H 'Content-Type: application/json' \
  -d '{
    "candidateId": "00000000-0000-0000-0000-000000000001",
    "jobId": "00000000-0000-0000-0000-000000000002",
    "skillsScore": 90,
    "experienceScore": 85,
    "titleScore": 80,
    "locationScore": 100,
    "aiReasoningScore": 88
  }'
```

## Component Setup Guide

### Candidate Web

Location:

```text
frontend/candidate-web
```

Run:

```bash
npm run dev:candidate
```

Purpose:

- candidate dashboard;
- resume upload action;
- profile summary;
- recommended jobs;
- resume improvement suggestions.

### Employer Web

Location:

```text
frontend/employer-web
```

Run:

```bash
npm run dev:employer
```

Purpose:

- employer dashboard;
- applicant metrics;
- AI-ranked candidate list;
- recruiter-facing summaries.

### Backend Microservices

Location:

```text
backend
```

The backend is organized as microservice-style Spring Boot services:

- `api-gateway`
- `auth-service`
- `candidate-service`
- `employer-service`
- `job-service`
- `application-service`
- `matching-service`
- `notification-service`

Candidate Service currently has the first real JPA persistence path. Other
services expose contract-shaped placeholder flows until repositories and
integrations are added.

### Database

Location:

```text
scripts/seed-data.sql
```

Run:

```bash
docker compose up -d postgres
```

Connect:

```bash
psql postgresql://aijobs:aijobs@localhost:5432/aijobs
```

### Redis

Run:

```bash
docker compose up -d redis
```

Purpose:

- short-lived cache;
- rate limits;
- future background coordination.

### MinIO

Run:

```bash
docker compose up -d minio
```

Open:

```text
http://localhost:9001
```

Purpose:

- local S3-compatible resume file storage.

### Prometheus

Run:

```bash
docker compose up -d prometheus
```

Open:

```text
http://localhost:9090
```

Prometheus scrapes backend metrics from:

```text
http://localhost:<service-port>/actuator/prometheus
```

Check targets:

```text
http://localhost:9090/targets
```

Targets show as down until the matching backend services are running.

### Grafana

Run:

```bash
docker compose up -d grafana
```

Open:

```text
http://localhost:3002
```

Login:

- user: `admin`
- password: `admin`

Grafana is provisioned from:

```text
infra/observability/grafana/provisioning
infra/observability/grafana/dashboards
```

Dashboard:

```text
AI Job Platform Overview
```

## DevOps Pipeline

### GitHub Actions

Workflow:

```text
.github/workflows/ci.yml
```

Runs on:

- push to `main`;
- push to `develop`;
- pull request;
- manual workflow dispatch.

Pipeline jobs:

1. Install Node dependencies.
2. Check formatting.
3. Typecheck web apps.
4. Build web apps.
5. Run backend Maven verify.
6. Validate Docker Compose configuration.

To use it:

1. Push the repository to GitHub.
2. Open the `Actions` tab.
3. Enable workflows if prompted.
4. Push a branch or open a pull request.

### Jenkins

Workflow:

```text
Jenkinsfile
```

Jenkins agent requirements:

- Git
- Node.js 22 or newer
- npm 10 or newer
- Java 25
- Maven 3.9 or newer
- Docker with Compose support

Create a Jenkins Pipeline job:

1. Open Jenkins.
2. Create `New Item`.
3. Choose `Pipeline` or `Multibranch Pipeline`.
4. Point it at the GitHub repository.
5. Make sure the Jenkins agent can run `npm`, `mvn`, and `docker compose`.
6. Run the pipeline.

### Run CI Commands Locally

Run once first:

```bash
npm install
```

Then:

```bash
npm run format:check
npm run ci:web
npm run ci:backend
npm run compose:config
```

Notes:

- `npm run ci:backend` requires Maven.
- `npm run compose:config` requires Docker.
- If Docker or Maven is not installed, those commands fail locally but still run
  in GitHub Actions or a properly configured Jenkins agent.

## Microservices, Docker, And Kubernetes

These are related but different technologies:

| Technology     | Meaning                                                | Project Decision                                                               |
| -------------- | ------------------------------------------------------ | ------------------------------------------------------------------------------ |
| Microservices  | Backend architecture split by domain and API boundary. | Used now through Spring Boot service modules.                                  |
| Docker         | Packages and runs services as containers.              | Used now through backend Dockerfiles and Docker Compose.                       |
| Docker Compose | Runs multiple local containers from one YAML file.     | Used now for local infrastructure and observability.                           |
| Kubernetes     | Orchestrates containers across a cluster.              | Starter manifests exist under `infra/k8s`, but it is deferred for MVP runtime. |

Why not Kubernetes first:

- Docker Compose is simpler for local development.
- Cloud Run is simpler for early stateless service deployment.
- Kubernetes adds cluster, ingress, networking, secrets, scaling, monitoring, and
  upgrade overhead.
- Move to GKE/Kubernetes when scale, service mesh, workload identity, or
  advanced orchestration requirements justify it.

## Useful File Map

```text
.github/workflows/ci.yml                       GitHub Actions CI
Jenkinsfile                                    Jenkins pipeline
docker-compose.yml                             local infra + observability
scripts/start-local.sh                         starts local dependencies
scripts/stop-local.sh                          stops local dependencies
scripts/seed-data.sql                          local Postgres schema
infra/observability/prometheus/prometheus.yml  Prometheus scrape config
infra/observability/grafana/provisioning       Grafana datasource/dashboard provisioning
infra/observability/grafana/dashboards         Grafana dashboard JSON
backend/pom.xml                                backend Maven parent
frontend/candidate-web                         candidate Next.js app
frontend/employer-web                          employer Next.js app
```

## Troubleshooting

### Docker Is Not Running

```bash
docker --version
docker compose version
docker compose ps
```

Open Docker Desktop and wait until it is running.

### Maven Is Missing

```bash
brew install maven
mvn -v
```

### Java Version Is Wrong

```bash
java -version
```

Install Java 25 and make sure `JAVA_HOME` points to it.

### Grafana Shows No Data

Open:

```text
http://localhost:9090/targets
```

Start at least one backend service. Prometheus cannot scrape a service that is
not running.

### Port Conflict

Common ports:

- Candidate web: `3000`
- Employer web: `3001`
- Grafana: `3002`
- API Gateway: `8080`
- Jenkins default: `8080`
- Prometheus: `9090`

If Jenkins and API Gateway both need port `8080`, run only one at a time or move
Jenkins to another port.

## More Documentation

- [Architecture](docs/architecture.md)
- [API Contracts](docs/api-contracts.md)
- [Database Schema](docs/database-schema.md)
- [AI Matching Design](docs/ai-matching-design.md)
- [Skills Needed](docs/skills.md)
- [Technology Decisions](docs/technology-decisions.md)
- [Setup And Run Guide](docs/setup-and-run.md)
- [DevOps And Observability](docs/devops-and-observability.md)
- [Sprint Plan](docs/sprint-plan.md)

## References

- GitHub repository quickstart:
  https://docs.github.com/en/repositories/creating-and-managing-repositories/quickstart-for-repositories
- Git clone guide: https://github.com/git-guides/git-clone
- GitHub pull requests:
  https://docs.github.com/articles/creating-a-pull-request
- GitHub Actions billing:
  https://docs.github.com/en/billing/concepts/product-billing/github-actions
- Jenkins Pipeline: https://www.jenkins.io/doc/book/pipeline/
- Jenkins macOS install: https://www.jenkins.io/download/lts/macos
- Jenkins Docker install: https://www.jenkins.io/doc/book/installing/docker
- Prometheus getting started:
  https://prometheus.io/docs/prometheus/latest/getting_started/
- Grafana provisioning:
  https://grafana.com/docs/grafana/latest/administration/provisioning/
- Docker Compose: https://docs.docker.com/compose/
- Kubernetes overview: https://kubernetes.io/docs/concepts/overview/
