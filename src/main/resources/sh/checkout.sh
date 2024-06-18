#!/bin/bash

# Prompt user for project name
read -p "Enter username name: " username
read -p "Enter project name: " project_name

eval "mkdir -p $project_name"
eval "cd $project_name"
eval "mkdir -p git"
cd "git"


eval "mkdir -p $project_name"
eval "cd $project_name"
eval "git clone git@bitbucket.org:avizva-products/avizva-hc-$project_name.git"
eval "cd avizva-hc-$project_name"
eval "git fetch origin"
eval "git checkout -b sandbox-$username-integration --track origin/integration"

eval "cd .."
eval "git clone git@bitbucket.org:avizva-products/avizva-hc-$project_name-assets.git"
eval "cd avizva-hc-$project_name-assets"
eval "git fetch origin"
eval "git checkout -b sandbox-$username-integration --track origin/integration"

eval "cd .."
eval "git clone git@bitbucket.org:avizva-products/avizva-hc-$project_name-database.git"
eval "cd avizva-hc-$project_name-database"
eval "git fetch origin"
eval "git checkout -b sandbox-$username-integration --track origin/integration"

eval "cd .."
eval "git clone git@bitbucket.org:avizva-products/avizva-hc-$project_name-usecases.git"
eval "cd avizva-hc-$project_name-usecases"
eval "git fetch origin"
eval "git checkout -b sandbox-$username-master --track origin/master"

eval "cd .."
eval "git clone git@bitbucket.org:avizva-products/avizva-hc-$project_name-machinesetup.git"
eval "cd avizva-hc-$project_name-machinesetup"
eval "git fetch origin"
eval "git checkout -b sandbox-$username-master --track origin/master"

eval "cd .."
eval "git clone git@bitbucket.org:avizva-products/avizva-hc-common.git"
eval "cd avizva-hc-common"
eval "git fetch origin"
eval "git checkout -b sandbox-$username-integration --track origin/integration"