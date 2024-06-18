#!/bin/bash



root_directory="$(pwd)"

declare -a web_path
declare -a ports

while IFS= read -r -d '' directory; do
  web_path+=("$directory")
done < <(find "$root_directory" -type d -name "*-*web" -print0)




IFS=$'\n' web_path=($(sort <<<"${web_path[*]}"))
unset IFS

for project_path in "${web_path[@]}"; do
    echo "Running project in directory: $project_path"
    cd "$project_path/src/main/resources" || exit 1

    YAML_FILE="local.yml"

    # Check if the YAML file exists
    if [ -f "$YAML_FILE" ]; then
        # Extract the port values manually
        debug=$(awk '/^ *debug:/ {found=1} found && /^ *port:/ {gsub(/[^0-9]/, "", $2); print $2; found=0}' "$YAML_FILE")
        render=$(awk '/^ *render:/ {found=1} found && /^ *port:/ {gsub(/[^0-9]/, "", $2); print $2; found=0}' "$YAML_FILE")
        server=$(awk '/^ *server:/ {found=1} found && /^ *port:/ {gsub(/[^0-9]/, "", $2); print $2; found=0}' "$YAML_FILE")

        # Add port values to the port array
        ports+=("$debug")
        ports+=("$render")
        ports+=("$server")

        # Print the server port value
        echo "Server Port: $server"
    fi
done


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


#####################################################################################
idea_path=$(find /Applications -maxdepth 1 -type d -name "IntelliJ IDEA*" -print -quit)
export PATH=$idea_path'/Contents/plugins/maven/lib/maven3/bin':$PATH

for project_path in "${web_path[@]}"; do
  	echo "Running project in directory: $project_path"
  	cd "$project_path"
  	cd "$project_path/src/main/resources" || exit 1

      YAML_FILE="local.yml"

      # Check if the YAML file exists
      if [ -f "$YAML_FILE" ]; then
        # Extract the port value manually
        debug_args=$(awk '/^ *debug:/ {found=1} found && /^ *port:/ {gsub(/[^0-9]/, "", $2); print $2; found=0}' "$YAML_FILE")
	  else
	  	debug_args=""
	fi
	cd "$project_path"
	mvn spring-boot:run -Dspring-boot.run.fork=false -Dfork=false "-Dspring-boot.run.jvmArguments=-Xdebug -Xrunjdwp:server=y,transport=dt_socket,suspend=n,address="$debug_args &
   # Adjust the sleep duration according to your needs
done