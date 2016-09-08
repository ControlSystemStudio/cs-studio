#!/bin/bash
set -e

function doCompile {
  mvn clean verify -fcore/pom.xml -Declipse.p2.mirrors=false -Dtycho.localArtifacts=ignore -Pcheckstyle
  mvn clean verify -fapplications/pom.xml -Declipse.p2.mirrors=false -Dtycho.localArtifacts=ignore -Dcsstudio.composite.repo=core/p2repo -Pcheckstyle
}

function doCompileWithDeploy {
  echo "<settings><servers><server><id>s3.site</id><username>\${env.S3USER}</username><password>\${env.S3PASS}</password></server></servers></settings>" > ~/settings.xml
  mvn clean verify -fcore/pom.xml --settings ~/settings.xml -Declipse.p2.mirrors=false -Dtycho.localArtifacts=ignore -PuploadRepo
  mvn clean verify -fapplications/pom.xml --settings ~/settings.xml -Declipse.p2.mirrors=false -Dtycho.localArtifacts=ignore -Dcsstudio.composite.repo=core/p2repo -PuploadRepo
}

function catTests {
  find ./ -type d -name "surefire-reports" -print0 | xargs -0 -I {} find {} -iname "*.txt" -type f | xargs cat
}

REPO=`git config remote.origin.url`
SSH_REPO=${REPO/https:\/\/github.com\//git@github.com:}
REPO_ORG_GIT=${REPO/https:\/\/github.com\//}
REPO_ORG=${REPO_ORG_GIT/\/cs-studio\.git/}
SHA=`git rev-parse --verify HEAD`

echo $REPO
echo $REPO_ORG
echo $SHA

# Pull requests and commits to other branches shouldn't try to deploy, just build to verify
if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$REPO_ORG" == "ControlSystemStudio" ] && ([[ "$TRAVIS_BRANCH" =~ ^[0-9]+\.[0-9]+\.x ]] || [ "$TRAVIS_BRANCH" == "master" ]); then
    echo "Deploying"
    doCompileWithDeploy
    catTests
else
    echo "Skipping deploy; just doing a build."
    doCompile
    catTests
fi

exit 0
