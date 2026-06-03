# Phase 1: Backend Sprint 1 Readiness

This phase prepares backend developers to start Sprint 1 work from scratch.
It covers local backend environment setup, Codex AI support for vibe coding,
account/access setup, and the checks that prove development can begin.

## Phase 1 Summary

Phase 1 is not feature delivery yet. It is the readiness phase.

By the end of this phase:

- The backend developer can clone the repository.
- Required local tools are installed.
- Local infrastructure starts with Docker Compose.
- Backend Maven modules verify successfully.
- At least one backend service can run locally.
- Swagger UI, Actuator health, and Prometheus metrics are reachable.
- The developer has GitHub/repository access.
- The developer knows how to use Codex for vibe-coding support.

## User Stories

### Backend Environment Readiness

As a backend developer, when the BE environment is ready for Sprint 1, I want
to begin development so that I can work on the Sprint 1 deliverables.

### Codex AI Development Support

As a backend developer, when the Codex AI feature for vibe coding development
is identified, I want to use it so that development can start with the
required AI support.

### Account Access

As a backend developer, when an account is set up, I want to access it so
that I can begin development.

## Acceptance Criteria

| Acceptance Criteria                                         | How To Prove It                                                                                                         |
| ----------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| BE environment is set up for Sprint 1 readiness.            | Tool versions are installed, Docker Compose runs, Maven verify passes, at least one service starts.                     |
| Codex AI feature for vibe coding development is identified. | Developer can describe how Codex will be used for repo reading, code generation, validation, documentation, and review. |
| Account is set up for BE developers.                        | Developer can access GitHub repo, clone with SSH or HTTPS, and push a test branch.                                      |
| BE developers can begin development.                        | Developer can create a feature branch, run local backend checks, and use Swagger/Actuator endpoints.                    |

## Phase 1 Deliverables

- Local development machine ready.
- Repository cloned locally.
- `.env` created from `.env.example`.
- Docker Compose infrastructure running.
- Backend parent POM and `common-lib` installed locally.
- Backend `mvn verify` passes.
- At least one service runs locally.
- Codex usage workflow documented and understood.
- Backend developer account access verified.
- Phase 1 readiness checklist completed.

## Step 1: Confirm Required Accounts

### GitHub Account

You need a GitHub account with access to this repository:

```text
https://github.com/nanguyen7654321/job-pro
```

Ask the repository owner to add you as a collaborator with at least `Write`
access if you will push branches.

Verify access:

```bash
git ls-remote git@github.com:nanguyen7654321/job-pro.git
```

If you use HTTPS instead of SSH:

```bash
git ls-remote https://github.com/nanguyen7654321/job-pro.git
```

### SSH Key For GitHub

Check for an existing SSH key:

```bash
ls ~/.ssh
```

If you do not have one, create an Ed25519 key:

```bash
ssh-keygen -t ed25519 -C "your-email@example.com"
```

Print the public key:

```bash
cat ~/.ssh/id_ed25519.pub
```

Add it in GitHub:

```text
GitHub -> Settings -> SSH and GPG keys -> New SSH key
```

Test SSH:

```bash
ssh -T git@github.com
```

A successful GitHub SSH test usually says authentication succeeded, even if
shell access is not provided.

### Codex Account / Access

Confirm you can open Codex and point it at the local repository. In this
project, Codex is used as the AI coding assistant for vibe coding:

- Read project docs and source files.
- Explain architecture and backend flow.
- Generate service code and DTOs.
- Update docs when behavior changes.
- Run local validation commands.
- Review diffs before commits.
- Help create commit messages and pull request summaries.

Codex does not replace backend ownership. The backend developer still reviews
generated code, validates behavior, and decides what to commit.

### Optional External Accounts

These are not required to start Sprint 1 scaffold work, but they may be
needed later:

| Account                    | Needed For                            | Phase 1 Requirement                                 |
| -------------------------- | ------------------------------------- | --------------------------------------------------- |
| OpenAI or Vertex AI Gemini | Real LLM and embedding calls          | Optional; placeholder AI logic works without keys.  |
| Docker Desktop account     | Docker Desktop usage on some machines | Optional unless your Docker install asks for login. |
| Jenkins account            | Self-hosted CI access                 | Optional if using GitHub Actions first.             |
| Cloud account              | Cloud Run, GKE, Terraform later       | Not required for Sprint 1 local readiness.          |

## Step 2: Install Local Tools

On macOS, install Homebrew if needed:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

Install required command-line tools:

```bash
brew install git node maven
```

Install Docker Desktop:

```bash
brew install --cask docker
```

Install Java 25 from a trusted JDK provider. If available through Homebrew:

```bash
brew install --cask temurin@25
```

If that cask is unavailable, install Eclipse Temurin 25 manually from
Adoptium or another trusted vendor.

Verify versions:

```bash
git --version
node -v
npm -v
java -version
mvn -v
docker --version
docker compose version
```

Expected baseline:

| Tool           | Expected                     |
| -------------- | ---------------------------- |
| Git            | Installed                    |
| Node.js        | 20 or newer                  |
| npm            | 10 or newer                  |
| Java           | 25                           |
| Maven          | 3.9 or newer                 |
| Docker         | Docker Desktop running       |
| Docker Compose | Available through Docker CLI |

## Step 3: Clone The Project

Choose a workspace folder:

```bash
mkdir -p /Users/diemn/Desktop/1WorkSpace
cd /Users/diemn/Desktop/1WorkSpace
```

Clone with SSH:

```bash
git clone git@github.com:nanguyen7654321/job-pro.git ai-job-search-platform
cd ai-job-search-platform
```

Or clone with HTTPS:

```bash
git clone https://github.com/nanguyen7654321/job-pro.git ai-job-search-platform
cd ai-job-search-platform
```

Verify repository state:

```bash
git status --short --branch
git remote -v
```

Expected result:

```text
## main...origin/main
```

## Step 4: Create A Sprint 1 Branch

Keep `main` clean. Create a backend readiness or feature branch:

```bash
git checkout main
git pull origin main
git checkout -b feature/phase1-backend-readiness
```

Branch naming examples:

- `feature/auth-service-foundation`
- `feature/candidate-profile-foundation`
- `feature/matching-service-foundation`
- `chore/backend-readiness`

## Step 5: Create Local Environment File

Copy the sample environment file:

```bash
cp .env.example .env
```

Do not commit `.env`. It is ignored by Git.

For Phase 1, placeholder AI behavior works without real AI keys. You can leave
these empty at first:

```text
OPENAI_API_KEY=
VERTEX_PROJECT_ID=
```

Default local infrastructure values:

| Setting               | Value                   |
| --------------------- | ----------------------- |
| PostgreSQL DB         | `aijobs`                |
| PostgreSQL user       | `aijobs`                |
| PostgreSQL password   | `aijobs`                |
| Redis port            | `6379`                  |
| MinIO console         | `http://localhost:9001` |
| MinIO user            | `admin`                 |
| MinIO password        | `password123`           |
| Grafana URL           | `http://localhost:3002` |
| Grafana user/password | `admin` / `admin`       |

## Step 6: Start Local Infrastructure

Open Docker Desktop and wait until Docker is running.

Start infrastructure:

```bash
./scripts/start-local.sh
```

This starts:

- PostgreSQL with pgvector on `localhost:5432`
- Redis on `localhost:6379`
- MinIO on `localhost:9000`
- MinIO console on `localhost:9001`
- Prometheus on `localhost:9090`
- Grafana on `localhost:3002`

Verify containers:

```bash
docker compose ps
```

If Postgres was already initialized before the latest schema, run:

```bash
psql postgresql://aijobs:aijobs@localhost:5432/aijobs -f scripts/seed-data.sql
```

Verify tables:

```bash
psql postgresql://aijobs:aijobs@localhost:5432/aijobs -c "\dt"
```

If `psql` is not installed, this is not a blocker for Phase 1 as long as
Docker Compose starts and backend services can connect.

## Step 7: Install Frontend Dependencies

Even for backend work, install root npm dependencies because CI validates the
monorepo and frontend type checks.

```bash
npm install
```

Verify frontend checks:

```bash
npm run format:check
npm run typecheck:web
```

## Step 8: Verify Backend Build

From the project root:

```bash
cd backend
mvn clean verify
```

If individual service modules cannot resolve the parent POM or `common-lib`,
install them locally:

```bash
mvn install -N
mvn -pl common-lib install
```

Then retry:

```bash
mvn clean verify
```

Expected result:

```text
BUILD SUCCESS
```

## Step 9: Run A Backend Service

Start with Candidate Service because it has the first real JPA persistence
path:

```bash
cd backend
mvn install -N
mvn -pl common-lib install
mvn -pl candidate-service spring-boot:run
```

Candidate Service runs on:

```text
http://localhost:8082
```

Verify health:

```bash
curl http://localhost:8082/actuator/health
```

Verify metrics:

```bash
curl http://localhost:8082/actuator/prometheus
```

Open Swagger UI:

```text
http://localhost:8082/swagger-ui.html
```

## Step 10: Run Sprint 1 Backend Services As Needed

The backend is scaffolded as microservice-style Spring Boot modules. Sprint 1
can start with one service at a time, but the local project already defines
the full backend service map:

| Service              | Module                 | Port   | Start Command From `backend/`                  |
| -------------------- | ---------------------- | ------ | ---------------------------------------------- |
| API Gateway          | `api-gateway`          | `8080` | `mvn -pl api-gateway spring-boot:run`          |
| Auth Service         | `auth-service`         | `8081` | `mvn -pl auth-service spring-boot:run`         |
| Candidate Service    | `candidate-service`    | `8082` | `mvn -pl candidate-service spring-boot:run`    |
| Employer Service     | `employer-service`     | `8083` | `mvn -pl employer-service spring-boot:run`     |
| Job Service          | `job-service`          | `8084` | `mvn -pl job-service spring-boot:run`          |
| Application Service  | `application-service`  | `8085` | `mvn -pl application-service spring-boot:run`  |
| Matching Service     | `matching-service`     | `8086` | `mvn -pl matching-service spring-boot:run`     |
| Notification Service | `notification-service` | `8087` | `mvn -pl notification-service spring-boot:run` |

Run one service per terminal window. If port `8080` is already used by
Jenkins, stop Jenkins or move either Jenkins or API Gateway to a different
port.

## Step 11: Try Backend API Smoke Tests

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

Candidate health:

```bash
curl http://localhost:8082/actuator/health
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

## Step 12: Verify Observability

Open Prometheus:

```text
http://localhost:9090
```

Open Prometheus targets:

```text
http://localhost:9090/targets
```

Prometheus is configured to scrape Spring Boot Actuator metrics from
`host.docker.internal` on ports `8080` through `8087`. A backend service target
is `UP` only when that matching service is running locally.

Open Grafana:

```text
http://localhost:3002
```

Login:

```text
admin / admin
```

Open the dashboard:

```text
AI Job Platform Overview
```

## Step 13: Identify The Codex AI Vibe-Coding Feature

For this project, the Codex AI feature is the coding-agent workflow inside
the repository. Use it as a paired backend development assistant.

Codex should be used for:

| Activity            | How To Use Codex                                                                                                         |
| ------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| Requirement reading | Ask Codex to read `docs/requirement-traceability.md`, `docs/architecture.md`, and `docs/api-contracts.md` before coding. |
| Code discovery      | Ask Codex to find service modules, controllers, DTOs, entities, repositories, and configs.                               |
| Implementation      | Ask Codex to add or update focused backend code for one vertical story at a time.                                        |
| Validation          | Ask Codex to run Maven, npm, Docker Compose config, curl smoke checks, and formatting checks.                            |
| Documentation       | Ask Codex to update API contracts, architecture notes, setup docs, and troubleshooting when behavior changes.            |
| Review              | Ask Codex to review diffs for bugs, missing tests, API drift, and risky assumptions.                                     |
| Git workflow        | Ask Codex to stage, commit, and push only after validation passes.                                                       |

Codex prompt template for Sprint 1 backend work:

```text
Read docs/requirement-traceability.md, docs/architecture.md, and
docs/api-contracts.md first.

Implement <specific backend task> in <service module>.
Keep the change focused.
Do not touch .gitlab-ci.yml.
Preserve any user changes already in the worktree.
Run backend validation and update docs if API behavior changes.
Summarize files changed and test results.
```

Good Phase 1 Codex tasks:

- Explain how Auth Service is structured.
- Add real persistence to Employer Service.
- Add a repository/entity for Job Service.
- Add validation to Matching Service DTOs.
- Update `docs/api-contracts.md` after endpoint behavior changes.
- Review whether a service matches the architecture document.

Do not ask Codex to blindly generate a large feature across all services at
once. Keep each task small enough to review.

## Step 14: Confirm Backend Developer Account Access

The developer is ready when all access checks pass:

```bash
git remote -v
git fetch origin
git checkout -b chore/access-check
git status --short --branch
git push -u origin chore/access-check
```

If this branch is only an access test, delete it afterward:

```bash
git checkout main
git push origin --delete chore/access-check
git branch -D chore/access-check
```

If branch protection prevents direct push to `main`, that is expected. Use
feature branches and pull requests.

## Step 15: Phase 1 Exit Checklist

Complete this checklist before starting Sprint 1 deliverables:

| Check                           | Command / Evidence                                                                 | Done |
| ------------------------------- | ---------------------------------------------------------------------------------- | ---- |
| GitHub account can access repo  | `git fetch origin`                                                                 | [ ]  |
| SSH or HTTPS auth works         | `git ls-remote origin`                                                             | [ ]  |
| Local repo cloned               | `git status --short --branch`                                                      | [ ]  |
| Docker Desktop running          | `docker compose version`                                                           | [ ]  |
| `.env` created                  | `test -f .env`                                                                     | [ ]  |
| Local infrastructure starts     | `./scripts/start-local.sh` then `docker compose ps`                                | [ ]  |
| npm dependencies installed      | `npm install`                                                                      | [ ]  |
| Frontend typecheck passes       | `npm run typecheck:web`                                                            | [ ]  |
| Backend Maven verify passes     | `cd backend && mvn clean verify`                                                   | [ ]  |
| Parent/common modules installed | `mvn install -N` and `mvn -pl common-lib install`                                  | [ ]  |
| Backend service starts          | `mvn -pl candidate-service spring-boot:run`                                        | [ ]  |
| Health endpoint works           | `curl http://localhost:8082/actuator/health`                                       | [ ]  |
| Swagger UI works                | `http://localhost:8082/swagger-ui.html`                                            | [ ]  |
| Prometheus is reachable         | `http://localhost:9090`                                                            | [ ]  |
| Grafana is reachable            | `http://localhost:3002`                                                            | [ ]  |
| Codex workflow understood       | Developer can explain how Codex will be used for coding, validation, docs, review. | [ ]  |
| Feature branch can be pushed    | `git push -u origin <branch>`                                                      | [ ]  |

## Phase 1 Definition Of Done

Phase 1 is done when:

- All Phase 1 acceptance criteria are satisfied.
- The developer can run local infrastructure.
- The developer can run backend Maven verification.
- The developer can start at least one backend service.
- The developer can access API docs and health endpoints.
- The developer can use Codex for a focused backend task.
- The developer can push a branch or open a pull request.

## Recommended First Sprint 1 Backend Tasks

After Phase 1 readiness is complete, start Sprint 1 with small backend
foundation tasks:

1. Replace Auth Service placeholder token with a real signed JWT scaffold.
2. Add persistence for Employer Service company records.
3. Add persistence for Job Service job records.
4. Add database migration tooling.
5. Add backend unit tests for Matching Service score calculation.
6. Add request/response examples to OpenAPI or API docs.

Start with one task, create one branch, and keep the pull request small.

## Troubleshooting

### Docker Is Not Running

Symptom:

```text
Cannot connect to the Docker daemon
```

Fix:

- Open Docker Desktop.
- Wait until Docker says it is running.
- Retry `docker compose ps`.

### Port Is Already In Use

Check the port:

```bash
lsof -i :8080
```

Common conflict:

- Jenkins often uses `8080`.
- API Gateway also uses `8080`.

Stop the conflicting service or move one service to a different port.

### Candidate Service Cannot Connect To Postgres

Check Postgres:

```bash
docker compose ps postgres
```

Re-run seed SQL:

```bash
psql postgresql://aijobs:aijobs@localhost:5432/aijobs -f scripts/seed-data.sql
```

### Maven Cannot Resolve `common-lib`

From `backend`:

```bash
mvn install -N
mvn -pl common-lib install
```

Then retry the service command.

### Swagger UI Does Not Open

Check the service is running:

```bash
curl http://localhost:8082/actuator/health
```

Then open:

```text
http://localhost:8082/swagger-ui.html
```

### Prometheus Target Is Down

A target is down when the matching backend service is not running.

Start the service, then refresh:

```text
http://localhost:9090/targets
```

### Git Push Is Rejected

Pull latest `main`, recreate or rebase your branch, then push again:

```bash
git checkout main
git pull origin main
git checkout <your-branch>
git rebase main
git push
```

If this is a protected branch issue, push a feature branch and open a pull
request.
