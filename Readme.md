Product Release
----------------
To make a release to s3, a release branch (and pull request) is made in which the release happens, and then the branch is merged to its target

Update submodules:
```git submodule foreach git pull origin```

change to the product plugin directory:
```cd org.csstudio.product```

Update versions of the plugins and splash screens (optional, add information to product changelog), and commit and tag with version:
```./prepare-release.sh 3.3.1 "https://github.com/ControlSystemStudio/cs-studio/wiki/Compatibility" "https://github.com/ControlSystemStudio/cs-studio/issues?milestone=10&page=1&state=closed" "First release of single product"```

or

```./prepare-release 3.3.1```

then, push to release branch:
```git push origin master:release_3.3.1```

make pull request from the github webpage to master or frozen branch (ie. 3.3.x).  Don't merge yet.

make sure passes all tests, then push tag (which will start release/upload to s3, even though pull request has not been merged)
```git push origin CSS-3.3.1```

after release, add snapshot versions, and push to pull request
```./prepare-next-release 3.3.1```
```git push origin master:release_3.3.1```

when all tests pass, merge pull request to target branch
