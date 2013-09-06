#!/bin/sh
#
# Invoke jython with scan support
#
# Kay Kasemir

# Configure this --------------------------------------------

# Jython
JYTHON=`echo /home/controls/css/CSS/plugins/org.python_*/jython.jar`

# Scan client
export SCAN_CLIENT=/home/controls/css/scan.client.jar

# Directory that contains scan_client.py, scan_ui.py
# from the org.csstudio.scan/jython directory
# and maybe other local scan related python scripts
export JYTHONPATH="/home/controls/share/scan"

# -----------------------------------------------------------
# During development, allow usage within the source tree,
# overriding the above.
if [ -r ../../../../../applications/plugins/org.python/jython.jar ]
then
    JYTHON=../../../../../applications/plugins/org.python/jython.jar
fi

if [ -r ../../org.csstudio.scan.client/scan.client.jar ]
then
   export SCAN_CLIENT=../../org.csstudio.scan.client/scan.client.jar
fi

if [ -f ../jython/scan_client.py ]
then
    export JYTHONPATH=".:../jython"
fi

# When running jython as a JAR like this...
#  java -jar ../../jython.jar "$@"
# one cannot extend the classpath; only the jar is used.
# So we put jython on the class path
# and specify the main class to run.
java -cp "$JYTHON:$SCAN_CLIENT" org.python.util.jython "$@"
