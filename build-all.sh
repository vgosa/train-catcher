#!/bin/bash

# Define the list of sub-project directories
projects=(booking notification payment ticket trainOperator trainSearch user)

# Loop over each project and run the Gradle commands
for project in "${projects[@]}"; do
  echo "Building project: $project"
  cd "$project" || { echo "Directory $project not found"; exit 1; }
  ./gradlew clean bootJar || { echo "Build failed for $project"; exit 1; }
  cd ..
done

echo "All projects built successfully."
