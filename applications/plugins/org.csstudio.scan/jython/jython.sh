#!/bin/sh
#
# Invoke jython with scan support
#
# Kay Kasemir

# Configure this --------------------------------------------

# Jython
JYTHON=`echo /home/controls/css/CSS/plugins/org.python_*/jython.jar`

# Scan client: Actual scan client and NumJy
export SCAN_CLIENT=/home/controls/css/scan.client.jar

# Directory that contains the content of
# the org.csstudio.scan/jython 
# and org.csstudio.numjy/jython directories
# and maybe other local scan-related python scripts
export JYTHONPATH="/home/controls/share/scan"

# -----------------------------------------------------------
# During development, assuming cs-studio/applications have been
# built once from the maven command line,
# allow usage within the source tree, overriding the above
if [ -r ../../../repository/target/repository/plugins/org.python.jython*.jar     \
  -a -d ../../org.csstudio.numjy/jython \
  -a -r ../../org.csstudio.scan.client/scan.client.jar                \
  -a -f ../jython/scan_client.py ]
then
    JYTHON=`echo ../../../repository/target/repository/plugins/org.python.jython*.jar`
    export SCAN_CLIENT=../../org.csstudio.scan.client/scan.client.jar
    export JYTHONPATH=".:../jython:../../org.csstudio.numjy/jython"
fi

# When running jython as a JAR like this...
#  java -jar ../../jython.jar "$@"
# one cannot extend the classpath; only the jar is used.
# So we put jython on the class path
# and specify the main class to run.
java -cp "$JYTHON:$SCAN_CLIENT" org.python.util.jython "$@"
