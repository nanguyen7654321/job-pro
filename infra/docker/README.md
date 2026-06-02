# Docker Notes

Use the root `docker-compose.yml` for local infrastructure. Service Dockerfiles
live next to each backend module so each service can be built independently.

## Observability Containers

The root Compose file also starts:

- Prometheus on `http://localhost:9090`
- Grafana on `http://localhost:3002`

The configuration is stored under `infra/observability` so dashboards and
datasources are version-controlled.
