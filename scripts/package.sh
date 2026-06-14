#!/usr/bin/env bash
# Package PayrollPal with jpackage: a self-contained portable app (bundled Java runtime).
set -euo pipefail
cd "$(dirname "$0")/.."
JAR=PayrollSystem-1.0-SNAPSHOT.jar
MAIN=org.springframework.boot.loader.launch.JarLauncher

[ -f "target/$JAR" ] || { echo "Build first: ./scripts/build.sh"; exit 1; }

echo "Staging jar..."
rm -rf build/jpackage-input && mkdir -p build/jpackage-input
cp "target/$JAR" build/jpackage-input/

echo "Building portable app-image..."
rm -rf dist/PayrollPal && mkdir -p dist
jpackage --type app-image --name PayrollPal --app-version 1.0 \
  --input build/jpackage-input --main-jar "$JAR" --main-class "$MAIN" \
  --java-options "-Dfile.encoding=UTF-8" --dest dist

echo
echo "Portable app ready: dist/PayrollPal  (run it, then open http://localhost:8080)"
echo
echo "For a native installer on this OS, use --type deb (Linux), rpm, or dmg (macOS):"
echo "  jpackage --type deb --name PayrollPal --app-version 1.0 --input build/jpackage-input --main-jar $JAR --main-class $MAIN --dest dist"
