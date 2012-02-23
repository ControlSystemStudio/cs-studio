#!/bin/sh
#
# Invoke jython with classpath that includes the client code

# When run as a JAR like this, one cannot add
# more to the classpath:
#java -jar ../../yabes.client/lib/jython.jar "$@"

# When listing jython.jar on the classpath,
# need to specify the main class to run
PLUGINS=../..
JYTHON=$PLUGINS/org.csstudio.scan.client_*/lib/jython.jar

# java -cp $JYTHON:$CLIENT:$COMMON org.python.util.jython "$@"
java -cp $JYTHON org.python.util.jython "$@"

