#!/bin/bash
set -e

function doCompile {
  ./build/clean-and-build-tests.sh
}

function doCompileWithDeploy {
  echo "<settings><servers><server><id>s3.site</id><username>\${env.S3USER}</username><password>\${env.S3PASS}</password></server></servers></settings>" > ~/settings.xml
  ./build/clean-and-build-tests-upload.sh
}

function catTests {
  cat *.log
}

REPO=`git config remote.origin.url`
SSH_REPO=${REPO/https:\/\/github.com\//git@github.com:}
REPO_ORG_GIT=${REPO/https:\/\/github.com\//}
REPO_ORG=${REPO_ORG_GIT/\/cs-studio\.git/}
SHA=`git rev-parse --verify HEAD`
CORE=false
APPLICATIONS=false

echo $REPO
echo $REPO_ORG
echo $SHA

# Pull requests and commits to other branches shouldn't try to deploy, just build to verify
if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$REPO_ORG" == "ControlSystemStudio" ] && ([[ "$TRAVIS_BRANCH" =~ ^[0-9]+\.[0-9]+\.x ]] || [ "$TRAVIS_BRANCH" == "master" ]) && [ -n "$TRAVIS_TAG" ]; then
    echo "Deploying"
    doCompileWithDeploy
    catTests
else
    echo "Skipping deploy; just doing a build."
    doCompile
    catTests
fi

exit 0
