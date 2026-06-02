#!/usr/bin/env bash
set -euo pipefail

# Starts local infrastructure, observability, and dashboard dependencies. App
# services are run separately so developers can iterate on one frontend or
# backend service at a time.
docker compose up -d postgres redis minio prometheus grafana
