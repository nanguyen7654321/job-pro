# DevOps Pipeline And Observability

This project includes a free DevOps and observability setup that is appropriate
for an MVP:

- GitHub Actions for hosted CI on public repositories and included/free-tier
  minutes depending on account plan.
- Jenkinsfile for teams that prefer a fully self-hosted, open-source CI server.
- Prometheus for metrics collection.
- Grafana OSS for dashboards.

The project intentionally does not add or edit `.gitlab-ci.yml` so it stays clear
of the earlier request not to change GitLab CI configuration.

## What The Pipeline Checks

The CI pipeline is designed to catch the highest-risk scaffold regressions first:

1. Install Node workspace dependencies.
2. Check formatting for markdown, config, and frontend files.
3. Typecheck both Next.js web apps.
4. Build both web apps.
5. Run Maven verify for all backend modules.
6. Validate Docker Compose configuration.

## GitHub Actions

Workflow file:

```text
.github/workflows/ci.yml
```

Why use it:

- It is simple to enable when the project is hosted on GitHub.
- Public repositories can use standard GitHub-hosted runners without consuming
  paid minutes under GitHub's current public-repository model.
- It requires no self-hosted CI server for an MVP.

How to use it:

1. Push this project to GitHub.
2. Open the repository's `Actions` tab.
3. Enable workflows if GitHub asks for confirmation.
4. Push a branch or open a pull request.

Manual run:

1. Open `Actions`.
2. Select `AI Job Platform CI`.
3. Choose `Run workflow`.

## Jenkins

Workflow file:

```text
Jenkinsfile
```

Why include it:

- Jenkins is open source and can run on a self-hosted machine.
- It is useful when a team wants full control over runners, network access, and
  secrets.
- It avoids hosted CI quotas.

Expected Jenkins agent tools:

- Node.js 22 or newer.
- npm 10 or newer.
- Java 25.
- Maven 3.9 or newer.
- Docker with Compose support.

How to use it:

1. Install Jenkins.
2. Create a Pipeline or Multibranch Pipeline job.
3. Point Jenkins at this repository.
4. Make sure the Jenkins agent has Node, Java, Maven, and Docker.
5. Run the job.

## Prometheus

Prometheus collects time-series metrics. In this project, it scrapes Spring Boot
Actuator metrics from backend services.

Config file:

```text
infra/observability/prometheus/prometheus.yml
```

Local URL:

```text
http://localhost:9090
```

Important endpoints:

- `http://localhost:8080/actuator/prometheus`
- `http://localhost:8081/actuator/prometheus`
- `http://localhost:8082/actuator/prometheus`
- `http://localhost:8086/actuator/prometheus`

The Prometheus container scrapes `host.docker.internal` because backend services
are expected to run from Maven on the host during local development.

## Grafana

Grafana visualizes metrics collected by Prometheus.

Local URL:

```text
http://localhost:3002
```

Default local credentials:

- User: `admin`
- Password: `admin`

Provisioned files:

- Datasource: `infra/observability/grafana/provisioning/datasources/prometheus.yml`
- Dashboard provider:
  `infra/observability/grafana/provisioning/dashboards/dashboards.yml`
- Dashboard:
  `infra/observability/grafana/dashboards/ai-job-platform-overview.json`

The dashboard shows backend target availability, JVM heap usage, and HTTP request
rate once backend services are running.

## Run Locally

Start infrastructure and observability:

```bash
./scripts/start-local.sh
```

Open:

- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3002`
- MinIO console: `http://localhost:9001`

Run a backend service so Prometheus has metrics:

```bash
cd backend
mvn -pl candidate-service spring-boot:run
```

Then open Prometheus and query:

```text
up
```

## Troubleshooting

### Grafana Shows No Data

Check Prometheus targets:

```text
http://localhost:9090/targets
```

If targets are down, start the backend services on ports `8080` through `8087`.

### Prometheus Cannot Reach Host Services

Docker Desktop usually supports `host.docker.internal`. On Linux, Compose also
sets `host.docker.internal:host-gateway` for the Prometheus service.

### Jenkins Cannot Run Docker Compose

Make sure the Jenkins agent user has permission to access Docker. Avoid mounting
the host Docker socket into untrusted Jenkins jobs.

## Interview Questions And Answers

**Q: Why add CI/CD to an MVP?**

A: CI catches integration problems early. Even a simple pipeline that typechecks,
builds, verifies backend modules, and validates Docker Compose protects the
project from broken commits.

**Q: Why GitHub Actions instead of GitLab CI?**

A: This project uses GitHub Actions because it avoids editing `.gitlab-ci.yml`
and is easy to enable for a hosted repository. GitLab CI is also a good option if
the project is hosted in GitLab.

**Q: Why include Jenkins if GitHub Actions exists?**

A: Jenkins is a free self-hosted alternative. It is useful when a team needs
private network access, custom runners, or no dependency on hosted CI minutes.

**Q: Why Prometheus?**

A: Prometheus is a standard open-source metrics system. It scrapes service
endpoints, stores time-series metrics, and works well with Spring Boot Actuator.

**Q: Why Grafana?**

A: Grafana turns metrics into dashboards. It helps developers and operators see
service availability, memory usage, request rate, latency, and error trends.

## Official References

- GitHub Actions billing:
  https://docs.github.com/en/billing/concepts/product-billing/github-actions
- Jenkins Pipeline:
  https://www.jenkins.io/doc/book/pipeline/
- Prometheus getting started:
  https://prometheus.io/docs/prometheus/latest/getting_started/
- Grafana provisioning:
  https://grafana.com/docs/grafana/latest/administration/provisioning/

## Microservices, Docker, And Kubernetes

The backend can be microservice-style without making Kubernetes mandatory.
Microservices define service boundaries. Docker packages services and local
dependencies into containers. Docker Compose runs multiple containers locally.
Kubernetes orchestrates many containers across a cluster.

This MVP uses Docker and Docker Compose now. Kubernetes starter manifests are
included for a future GKE path, but Kubernetes is deferred as the default runtime
until scale, networking, or platform requirements justify the added complexity.
