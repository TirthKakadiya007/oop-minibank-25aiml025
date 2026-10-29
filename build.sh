#!/usr/bin/env bash
set -e

echo "========================================="
echo "   Building MiniBank Project (JDK 17+)   "
echo "========================================="

# Clean build directory
rm -rf bin
mkdir -p bin

# Compile all source files
echo "[1/2] Compiling Java source files..."
javac -d bin $(find src -name "*.java")

# Package into runnable JAR with Manifest
echo "[2/2] Packaging runnable minibank.jar..."
jar --create --file minibank.jar --main-class minibank.MiniBank -C bin .

echo "========================================="
echo "   BUILD SUCCESSFUL: minibank.jar created"
echo "   Run with: ./run.sh or java -jar minibank.jar"
echo "========================================="
