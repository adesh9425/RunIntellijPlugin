print_colored_box() {
    local message="$1"
    local box_width="$2"

    # ANSI escape codes for colors
    red_color="\033[0;31m"
    blue_color="\033[0;34m"
    reset_color="\033[0m"

    # Calculate the padding for centering the message
    padding=$(( (box_width - ${#message} - 4) / 2 ))

    # Print the top border in red
    printf "${red_color}+%*s+${reset_color}\n" "$box_width" ""

    # Print the message with borders in blue
    printf "${red_color}|${blue_color}%*s %s %*s${red_color}|${reset_color}\n" "$padding" "" "$message" "$padding" ""

    # Print the bottom border in red
    printf "${red_color}+%*s+${reset_color}\n" "$box_width" ""
}

root_directory="$(pwd)"

declare scripts
while IFS= read -r -d '' directory; do
  scripts=("$directory")
done < <(find "$root_directory" -type d -name "scripts" -print0)

print_colored_box "Initiating pull process  " 100
sleep 3

  
 "$scripts/pull.sh"

print_colored_box "The pull process is finished " 100
sleep 3
########################################
print_colored_box "Initiating build process " 100
sleep 3

 "$scripts/build.sh"

print_colored_box "The build process is finished " 100
sleep 3
###########################

print_colored_box "Initiating run process" 100
sleep 3

 "$scripts/run.sh"

print_colored_box "The run process is finished " 100
sleep 3