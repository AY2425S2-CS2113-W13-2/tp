#!/usr/bin/env bash

# change to the directory containing the script
cd "$(dirname "$0")"

# Store the project root directory
PROJECT_ROOT="$(cd .. && pwd)"

# Clean and build the project
cd "$PROJECT_ROOT"
./gradlew clean shadowJar

# Ensure data directory exists
mkdir -p data

# Create EventSync.txt if it doesn't exist
touch data/EventSync.txt

# Change to text-ui-test directory
cd text-ui-test

# Find the jar file (use full path)
JAR_PATH=$(find "$PROJECT_ROOT/build/libs" -mindepth 1 -print -quit)

# Run the jar and compare outputs
java -jar "$JAR_PATH" < input.txt > ACTUAL.TXT

# Normalize line endings (remove Windows-style line endings)
tr -d '\r' < EXPECTED.TXT > EXPECTED-UNIX.TXT
tr -d '\r' < ACTUAL.TXT > ACTUAL-UNIX.TXT

# Compare files
diff EXPECTED-UNIX.TXT ACTUAL-UNIX.TXT

# Check the result
if [ $? -eq 0 ]
then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi