#!/bin/sh
#
# Invoke jython with NumJy for tests
#
# See org.csstudio.scan/jython/jython.sh for
# example that includes NumJy as well as scan support
#
# Kay Kasemir

# When run with an exported product,
# jython would be one of the other plugins of the product
PLUGINS=../..

if [ -r $PLUGINS/org.python_*/jython.jar ]
then
    JYTHON=$PLUGINS/org.python_*/jython.jar
elif [ -r ../../org.python/jython.jar ]
then
    # When run from within the IDE setup,
    # org.python would be under "applications"
    # parallel to this plugin
    JYTHON=../../org.python/jython.jar
else
    echo "Cannot locate jython.jar"
fi

# When running jython as a JAR like this...
#  java -jar jython.jar "$@"
# one cannot add more to the classpath.
# So we put jython on the class path
# and specify the main class to run.

java -cp $JYTHON org.python.util.jython "$@"
