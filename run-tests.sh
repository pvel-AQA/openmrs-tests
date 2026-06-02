#!/bin/bash

# ===== Настройки =====
IMAGE_NAME=openmrs-tests
TEST_PROFILE=${1:-api}
TIMESTAMP=$(date +"%Y%m%d_%H%M")

# ===== FIX: абсолютный путь для Docker (Windows-safe) =====
BASE_DIR="$(pwd -W 2>/dev/null || pwd)"
TEST_OUTPUT_DIR="$BASE_DIR/test-output/$TIMESTAMP"

echo ">>> BASE_DIR=$BASE_DIR"
echo ">>> TEST_OUTPUT_DIR=$TEST_OUTPUT_DIR"

# ===== Создание папок =====
mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

# ===== Сборка образа =====
echo ">>> Building Docker image"
docker build -t $IMAGE_NAME .

# ===== Запуск тестов =====
echo ">>> Running tests with profile: $TEST_PROFILE"

docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs:/app/logs" \
  -v "$TEST_OUTPUT_DIR/results:/app/target/surefire-reports" \
  -v "$TEST_OUTPUT_DIR/report:/app/target/site" \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e BASEAPIURL=http://putYourApi/openmrs/ \
  -e BASEUIURL=http://putYourIp/openmrs/spa \
  -e BROWSERREMOTE=http://host.docker.internal:4444/wd/hub \
  $IMAGE_NAME

# ===== Итоги =====
echo ">>> Tests finished"
echo "Logs: $TEST_OUTPUT_DIR/logs/run.log"
echo "Results: $TEST_OUTPUT_DIR/results"
echo "Report: $TEST_OUTPUT_DIR/report"