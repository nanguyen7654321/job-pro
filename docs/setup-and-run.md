# Setup And Run Guide

This guide gets the project running locally from a fresh machine.

## 1. Prerequisites

Install:

- Node.js 20 or newer
- npm 10 or newer
- Docker Desktop
- Java 25 JDK
- Maven 3.9 or newer
- Git

Spring Boot 4.0.6 officially supports Java 17 through Java 26. This project
sets the compiler target to Java 25 to match the architecture document.

Optional but useful:

- psql client
- Redis CLI
- MinIO client

## 2. Clone Or Open The Project

Project path:

```bash
cd /Users/diemn/Desktop/1WorkSpace/ai-job-search-platform
```

## 3. Create Local Environment File

```bash
cp .env.example .env
```

Edit `.env` and fill AI provider keys when you are ready to call real AI APIs.
The app scaffold works without real AI keys because current AI clients use MVP
placeholder logic.

## 4. Start Local Infrastructure

```bash
./scripts/start-local.sh
```

This starts:

- PostgreSQL with pgvector on `localhost:5432`
- Redis on `localhost:6379`
- MinIO API on `localhost:9000`
- MinIO console on `http://localhost:9001`
- Prometheus on `http://localhost:9090`
- Grafana on `http://localhost:3002`

The first time Postgres creates its named Docker volume, it runs
`scripts/seed-data.sql` automatically. If you already had the volume before a
schema change, re-run the schema manually:

```bash
psql postgresql://aijobs:aijobs@localhost:5432/aijobs -f scripts/seed-data.sql
```

Default local credentials:

- PostgreSQL database: `aijobs`
- PostgreSQL user: `aijobs`
- PostgreSQL password: `aijobs`
- MinIO user: `admin`
- MinIO password: `password123`
- Grafana user: `admin`
- Grafana password: `admin`

To stop local infrastructure:

```bash
./scripts/stop-local.sh
```

## 5. Install Frontend Dependencies

From the project root:

```bash
npm install
```

The root `package.json` uses npm workspaces for both web apps.

## 6. Run Candidate Web

```bash
npm run dev:candidate
```

Open:

```text
http://localhost:3000
```

The current frontend uses local fallback data for the first visual pass. It
shows:

- +- Resume upload action

* AI profile summary panel
* Recommended jobs
* Match explanations
* Resume improvement suggestions

## 7. Run Employer Web

In a second terminal:

```bash
npm run dev:employer
```

Open:

```text
http://localhost:3001
```

The current employer app also uses local fallback data until backend API hooks
are connected. It shows:

- +- Active job context

* Applicant metrics
* AI-ranked candidates
* Recruiter summaries

## 8. View Local Observability

After local infrastructure starts, open:

```text
http://localhost:9090
http://localhost:3002
```

Prometheus targets show as down until matching backend services are running on
ports `8080` through `8087`. Start a service, then open Prometheus targets:

```text
http://localhost:9090/targets
```

Grafana is provisioned with a Prometheus datasource and an `AI Job Platform
Overview` dashboard.

## 9. Build Or Check Frontend

```bash
npm run typecheck:web
npm run build:web
```

If dependencies are not installed yet, run `npm install` first.

## 10. Build Backend

```bash
cd backend
mvn clean verify
```

If `mvn` is not found, install Maven first. If Candidate Service fails with a
schema validation error, confirm `scripts/seed-data.sql` has run against the
local Postgres database.

## 11. Run A Backend Service

Example for Candidate Service:

```bash
cd backend
mvn -pl candidate-service spring-boot:run
```

Default service ports:

- API Gateway: `8080`
- Auth Service: `8081`
- Candidate Service: `8082`
- Employer Service: `8083`
- Job Service: `8084`
- Application Service: `8085`
- Matching Service: `8086`
- Notification Service: `8087`

## 12. Try Example API Calls

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

## 13. Common Troubleshooting

### Docker Services Do Not Start

Check whether the ports are already in use:

```bash
lsof -i :5432
lsof -i :6379
lsof -i :9000
```

Stop conflicting local services or change ports in `docker-compose.yml`.

### Frontend Cannot Reach Backend

Set:

```bash
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

The current UI uses fallback data, so backend connectivity is not required for
the first visual check.

### Backend Cannot Connect To Database

Verify Docker Compose is running:

```bash
docker compose ps
```

Then confirm `.env` matches the datasource values in service config.

## 13. Recommended First Development Task

Build candidate resume upload end to end:

1. Upload file from candidate-web.
2. Store file in MinIO.
3. Extract resume text.
4. Call resume parser prompt through AI provider.
5. Save parsed JSON and AI summary.
6. Generate embedding and store it in pgvector.
7. Show updated profile in candidate-web.

## 14. Run CI Locally

The hosted CI pipeline installs dependencies first. To run the same core commands locally, run `npm install` once, then:

```bash
npm run ci:web
npm run ci:backend
npm run compose:config
```

`npm run ci:backend` requires Maven. `npm run compose:config` requires Docker.
