#!/bin/sh
#
# Invoke jython with classpath that includes the client code
#
# Kay Kasemir

PLUGINS=../..
JYTHON=$PLUGINS/org.csstudio.scan.client*/lib/jython.jar

# When running jython as a JAR like this...
#  java -jar ../../yabes.client/lib/jython.jar "$@"
# one cannot add more to the classpath.
# So we put jython on the class path
# and specify the main class to run.

java -cp $JYTHON org.python.util.jython "$@"
