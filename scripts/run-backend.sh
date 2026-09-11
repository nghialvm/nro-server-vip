#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd -- "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_DIR}"
JAVA_OPTS=${JAVA_OPTS:-"-Xms128m -Xmx3g -Xss256k"}
exec java ${JAVA_OPTS} -Dfile.encoding=UTF-8 -jar "${PROJECT_DIR}/backend/target/game-backend.jar" "$@"
