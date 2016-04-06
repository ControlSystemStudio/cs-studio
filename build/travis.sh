#!/bin/bash
set -ev

folderChangeSet=(`git diff-tree --name-only --no-commit-id -r HEAD | grep '\(core\|applications\)\/[[:alpha:]]' | cut -d/ -f1,2`)
sorted_unique=(`echo "${folderChangeSet[@]}" | tr ' ' '\n' | sort -u -r | tr '\n' ' '`)
for project in "${sorted_unique[@]}"
do
  echo "${project}/pom.xml"
done
