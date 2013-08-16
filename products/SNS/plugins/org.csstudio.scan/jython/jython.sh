#!/bin/sh
#
# Invoke jython
#
# Kay Kasemir

# Configure this --------------------------------------------

# Jython
JYTHON=/home/controls/css/CSS/plugins/org.python_*/jython.jar

# Scan client
export SCAN_CLIENT=/home/controls/css/scan.client.jar

# Directory that contains scan_client.py, scan_ui.py
# from the org.csstudio.scan/jython directory
export JYTHONPATH="/home/controls/share/scan"
export JYTHONPATH="/SNS/users/ky9/my_bl7/applications/bl7-ScanSupport/scan"


# -----------------------------------------------------------
# During development, allow usage within the source tree,
# overriding the above
if [ -r ../../../../../applications/plugins/org.python/jython.jar ]
then
    JYTHON=../../../../../applications/plugins/org.python/jython.jar
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
java -cp $JYTHON org.python.util.jython "$@"
