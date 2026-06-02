#!/usr/bin/env bash
set -euo pipefail

# Stops local dependencies while keeping named Docker volumes unless the
# developer explicitly removes them.
docker compose down
