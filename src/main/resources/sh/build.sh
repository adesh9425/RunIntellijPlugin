#!/bin/bash
idea_path=$(find /Applications -maxdepth 1 -type d -name "IntelliJ IDEA*" -print -quit)
export PATH="$idea_path/Contents/plugins/maven/lib/maven3/bin:$PATH"

# Specify the root directory to start searching
root_directory="$(pwd)"

# Declare an array to store directory paths
declare -a common_path
declare -a web_path

# Use find to get a list of directories named "avizva-hc-common" and populate the array
while IFS= read -r -d '' directory; do
  common_path+=("$directory")
done < <(find "$root_directory" -type d -name "*-common" -print0)

# Use find to get a list of directories named "avizva-hc-*ex" and populate the array
while IFS= read -r -d '' directory; do
  web_path+=("$directory")
done < <(find "$root_directory" -type d -name "*-*ex" -print0)

# Use find to get a list of directories named "avizva-hc-*app" and populate the array
while IFS= read -r -d '' directory; do
  web_path+=("$directory")
done < <(find "$root_directory" -type d -name "*-*app" -print0)



read -p "Enter yes to build common: " weather
if [ "$weather" == "yes" ]; then
  for path in "${common_path[@]}"; do
    (cd "$path" && mvn  -T 10 -U  clean install) &
  done

fi
wait

# Print the elements of the array (optional)
for path in "${web_path[@]}"; do
  (cd "$path" && mvn -T 10 -U clean install )  &
done
wait

