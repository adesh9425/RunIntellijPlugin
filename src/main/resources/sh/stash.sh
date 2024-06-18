#!/bin/bash

# Specify the root directory to start searching
root_directory="$(pwd)"
echo "inside pull.sh" $root_directory
# Declare an array to store directory paths

declare -a web_path
# Use find to get a list of directories named "shub" and populate the array
while IFS= read -r -d '' directory; do
  web_path+=("$directory")
done < <(find "$root_directory" -type d -name "avizva-hc-common" -print0)

while IFS= read -r -d '' directory; do
  web_path+=("$directory")
done < <(find "$root_directory" -type d -name "avizva-hc-*-web" -print0)

while IFS= read -r -d '' directory; do
  web_path+=("$directory")
done < <(find "$root_directory" -type d -name "avizva-hc-*-usecases" -print0)

for path in "${web_path[@]}"; do
(cd "$path"  && git stash apply)&
done

wait



