#!/usr/bin/env bash
# Build the full single-artifact jar (React UI bundled into Spring Boot).
set -euo pipefail
cd "$(dirname "$0")/.."

echo "[1/2] Building frontend (bun)..."
( cd frontend && bun install && bun run build )

echo "[2/2] Building backend jar (bundles the UI under /static)..."
mvn -q clean package -DskipTests

echo
echo "Done: target/PayrollSystem-1.0-SNAPSHOT.jar"
echo "Run it with: ./scripts/run.sh"
