#!/bin/sh
#
# Invoke jython
#
# Kay Kasemir

# Configure this --------------------------------------------

# Absolute path of jython.jar
JYTHON=/home/css/CSS3.1.1/plugins/org.python_*/jython.jar

# Directory that contains scan_client.py, scan*.py
export JYTHONPATH="/home/css/Share/scan:../jython"

# -----------------------------------------------------------

if [ -r ../../../../../applications/plugins/org.python/jython.jar ]
then
    # When run from within the source tree,
    # org.python would be under "applications"
    JYTHON=../../../../../applications/plugins/org.python/jython.jar
fi

# When running jython as a JAR like this...
#  java -jar ../../yabes.client/lib/jython.jar "$@"
# one cannot add more to the classpath.
# So we put jython on the class path
# and specify the main class to run.

java -cp $JYTHON org.python.util.jython "$@"
