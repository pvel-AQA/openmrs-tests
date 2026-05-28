#!/bin/bash

IMAGE_NAME=openmrs-tests
TEST_PROFILE=${1:-api,ui}
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP

echo ">>> Building tests"
docker build -t $IMAGE_NAME .

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

echo ">>> Tests are started"
docker run --rm  \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL=http://putYourIp/openmrs/ \
  -e UIBASEURL=http://putYourIp/openmrs/spa \
  -e BROWSERREMOTE=http://putYourIp:4444/wd/hub \
$IMAGE_NAME

echo ">>> Tests are finished"
echo "Log files: $TEST_OUTPUT_DIR/logs/run.log"
echo "Test results: $TEST_OUTPUT_DIR/results"
echo "Report: $TEST_OUTPUT_DIR/report"
