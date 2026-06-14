#!/usr/bin/env bash
# Run the single-artifact jar. UI + API both served on http://localhost:8080
set -euo pipefail
cd "$(dirname "$0")/.."

if [ ! -f target/PayrollSystem-1.0-SNAPSHOT.jar ]; then
  echo "Jar not found. Build first: ./scripts/build.sh"
  exit 1
fi

echo "PayrollPal running at http://localhost:8080  (Ctrl+C to stop)"
java -jar target/PayrollSystem-1.0-SNAPSHOT.jar
