#!/bin/bash

# Load .env.dev file and export variables
if [ -f .env.dev ]; then
    echo "Loading environment variables from .env.dev..."
    export $(cat .env.dev | grep -v '^#' | xargs)
else
    echo "Warning: .env.dev file not found!"
fi

# Run the application
echo "Starting application..."
mvn spring-boot:run