#!/bin/bash
set -e

function doCompile {
  ./build/clean-and-build-tests.sh
}

function doCompileWithDeploy {
  ./build/clean-and-build-tests-upload.sh
}

function catTests {
  cat *.log
}

REPO=`git config remote.origin.url`
SSH_REPO=${REPO/https:\/\/github.com\//git@github.com:}
REPO_ORG_GIT=${REPO/https:\/\/github.com\//}
REPO_ORG=${REPO_ORG_GIT/\/org\.csstudio\.product\.git/}
SHA=`git rev-parse --verify HEAD`
CORE=false
APPLICATIONS=false

echo "Tag: $TRAVIS_TAG"
echo $REPO
echo $REPO_ORG
echo $SHA

# Pull requests and commits to other branches shouldn't try to deploy, just build to verify
if [ -n "$TRAVIS_TAG" ]; then
    echo "Depolying"
    doCompileWithDeploy
else
    if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$REPO_ORG" == "ControlSystemStudio" ] && ([[ "$TRAVIS_BRANCH" =~ ^[0-9]+\.[0-9]+\.x ]] || [ "$TRAVIS_BRANCH" == "master" ]); then
        echo "Skipping deploying on target branch, deploy runs on tag"
        doCompile
    else
        echo "Skipping deploy; just doing a build."
        doCompile
    fi
fi
exit 0
