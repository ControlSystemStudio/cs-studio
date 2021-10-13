# cs-studio [![Build Status](https://travis-ci.com/ControlSystemStudio/cs-studio.svg?branch=master)](https://travis-ci.com/ControlSystemStudio/cs-studio) [![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/541/badge)](https://bestpractices.coreinfrastructure.org/projects/541)

Control System Studio is a collections of tools to monitor and operate large scale control systems, such as the ones in the accelerator community.

This repository is for the original, Eclipse-based implementation of CS-Studio.
A newer development provides most of the key CS-Studio components
without any Eclipse dependencies, see https://github.com/ControlSystemStudio/phoebus
It generally offers faster startup, and also dramatically simplifies the
build process.


* [Webpage](http://controlsystemstudio.github.io/) - general information
* [Github wiki](https://github.com/ControlSystemStudio/cs-studio/wiki/) - notes for developers
* [Tech-talk](http://aps.anl.gov/epics/tech-talk/index.php) (the EPICS mailing list) - for user queries and discussion
* [Download page](http://controlsystemstudio.org/download.html)

### How to contribute

User feedback, feature requests and code contributions are all welcome.

To get started, see the following Wiki pages:

* [How to contribute](https://github.com/ControlSystemStudio/cs-studio/wiki/HowToContribute)
* [Coding guidelines](https://github.com/ControlSystemStudio/cs-studio/wiki/CodingGuidelines)


### Building CS-Studio

This repository contains all the code for building CS-Studio. However, there is a two-step
build process.

```
# Build maven-osgi-bundles to ensure all bundles are available for Tycho resolution.
mvn -f maven-osgi-bundles/pom.xml clean verify
# Build everything else.
mvn clean verify -Dcsstudio.composite.repo=$(pwd)/p2repo
```

You can use `-DskipTests=true` to speed up the build process.

