#!/bin/sh
#
# Invoke jython
#
# Kay Kasemir

PLUGINS=../..

if [ -r ../../../../../applications/plugins/org.python/jython.jar ]
then
    # When run from within the source tree,
    # org.python would be under "applications"
    JYTHON=../../../../../applications/plugins/org.python/jython.jar
elif [ -r $PLUGINS/org.python_*/jython.jar ]
then
    # When run with an exported product,
    # jython would be one of the other plugins of the product
    JYTHON=$PLUGINS/org.python_*/jython.jar
else
    # TODO: Set to the absolute path of jython.jar
    JYTHON=/home/css/CSS3.1.0/plugins/org.python_*/jython.jar
    echo "Cannot locate jython.jar"
fi

# When running jython as a JAR like this...
#  java -jar ../../yabes.client/lib/jython.jar "$@"
# one cannot add more to the classpath.
# So we put jython on the class path
# and specify the main class to run.

java -cp $JYTHON org.python.util.jython "$@"
