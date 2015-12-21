#!/bin/bash
set -e
set +x

export token

while IFS='' read -r line || [[ -n "$line" ]]; do
    token=$line
done < "$1"

folderChangeSet=(`git diff-tree --name-only --no-commit-id -r HEAD | grep '\(core\|applications\)\/[[:alpha:]]' | cut -d/ -f1,2`)
sorted_unique=(`echo "${folderChangeSet[@]}" | tr ' ' '\n' | sort -u -r | tr '\n' ' '`)

for project in "${sorted_unique[@]}"
do
  arr=(${project//// })
  jenkinsName="cs-studio-${arr[0]}-${arr[1]}-${GIT_BRANCH#*/}"
  echo "triggering ${jenkinsName}"
  curl -X POST https://openepics.ci.cloudbees.com/job/${jenkinsName}/build --data token=${token}
done

set -x

