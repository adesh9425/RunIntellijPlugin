#!/bin/bash


declare -a web_path


for path in "${web_path[@]}"; do
(cd "$path"  && git stash -m"master.sh has made this")&
done

wait

for path in "${web_path[@]}"; do
(cd "$path"  && git pull )&
done
wait


for path in "${web_path[@]}"; do
(cd "$path"  && git stash apply stash@{0} )&
done
wait