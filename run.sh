#!/usr/bin/env bash
set -e

if [ ! -f minibank.jar ]; then
    echo "minibank.jar not found. Running build first..."
    ./build.sh
fi

java -jar minibank.jar
