#!/usr/bin/env bash
set -e

echo "========================================="
echo "   Running MiniBank JUnit 5 Test Suite   "
echo "========================================="

# Ensure source is compiled
if [ ! -d bin ]; then
    ./build.sh
fi

rm -rf bin-test
mkdir -p bin-test

# Compile test sources
javac -cp "bin:lib/junit-platform-console-standalone.jar" -d bin-test $(find test -name "*.java")

# Run JUnit 5 platform console launcher
java -jar lib/junit-platform-console-standalone.jar execute -cp "bin:bin-test" --select-class minibank.MiniBankTest
