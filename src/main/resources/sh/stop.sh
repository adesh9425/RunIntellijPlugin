#!/bin/bash

# Read ports from environment variables
ports=($PORTS)

# Loop through each port and check for running processes
for port in "${ports[@]}"; do
    # Check if the port is in use
    if lsof -i :$port > /dev/null; then
        echo "Process running on port $port. Killing the process..."
        # Find and kill the process using the specified port
        lsof -ti :$port | xargs kill -9
        echo "Process killed on port $port."
    else
        echo "No process found on port $port."
    fi
done