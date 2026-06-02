# Observability

Local observability uses Prometheus and Grafana.

- Prometheus config: `infra/observability/prometheus/prometheus.yml`
- Grafana datasource provisioning: `infra/observability/grafana/provisioning/datasources/prometheus.yml`
- Grafana dashboard provisioning: `infra/observability/grafana/provisioning/dashboards/dashboards.yml`
- Dashboard JSON: `infra/observability/grafana/dashboards/ai-job-platform-overview.json`

Run:

```bash
docker compose up -d prometheus grafana
```

Open:

- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3002`

Default local Grafana credentials come from `.env.example`:

- User: `admin`
- Password: `admin`

Prometheus scrapes backend Spring Boot Actuator metrics at
`/actuator/prometheus`. Backend services should be running on ports `8080`
through `8087` for all targets to show as up.
