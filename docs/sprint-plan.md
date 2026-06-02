# MVP Sprint Plan

## Sprint 1: Foundation

- Monorepo setup.
- Docker Compose for Postgres, Redis, MinIO.
- Auth, Candidate, Job, Matching service skeletons.
- Candidate and employer web shells.

## Sprint 2: Candidate Profile

- Candidate signup/login.
- Resume upload to object storage.
- Resume parser prompt integration.
- Candidate profile and skill persistence.

## Sprint 3: Jobs

- Employer company setup.
- Job CRUD.
- Job parser prompt integration.
- Job embedding persistence.

## Sprint 4: Matching

- Candidate-to-job recommendations.
- Job-to-candidate ranked candidates.
- Match explanation prompt.
- Skill gap and resume improvement suggestions.

## Sprint 5: Applications and Notifications

- Candidate apply flow.
- Applicant statuses.
- Employer applicant list.
- Email notifications.

## DevOps And Observability Sprint Add-On

- Add GitHub Actions and Jenkinsfile CI paths.
- Add backend Actuator Prometheus metrics.
- Add Prometheus and Grafana to local Docker Compose.
- Provision a default Grafana dashboard.
- Add alerts later for service down, high error rate, high latency, and high JVM
  memory usage.
