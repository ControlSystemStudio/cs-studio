# Headless build settings
#
# Author
#  - Kay Kasemir
#  - Takashi Nakamoto
#

# Version number of CSS
export VERSION=3.0.0

# Root of CSS source tree.
export TOP=${HOME}/work/cs-studio-3.0

# Root directory of the drive on cygwin.
#
# On Windows with cygwin, this needs to be set to something like /cygdrive/c.
# On Linux, this needs to be empty.
export CYGDRIVE=

# Directory where the built CSS and temporary files will be stored.
export BUILDDIR=${HOME}/work/CSSBuild

# Directory which contains the root of Eclipse RCP.
export ECLIPSE_BASE=${HOME}/work

# Root directory of Delta pack
export DELTAPACK=${HOME}/work/delta/eclipse

# Root directory of JDK SE 6.
#
# On Windows with cygwin, this needs to be set in the form like
# "c:\Program Files\Java\jdk1.6.0_26".
export JAVA_HOME=${HOME}/work/jdk1.6.0_27

###################################################################
# Following parts should NOT be edited without a particuar reason.
###################################################################

# Path to the Eclipse executable.
export ECLIPSE=${ECLIPSE_BASE}/eclipse

# Could use standalone ant, or the one built into Eclipse
export ANT="java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"

# Ant with eclipse tasks
export ECLIPSE_ANT="java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"

# Use only the date as qualifier/
# With default, the time is included and then the same plugin
# will be created several times for the various tools that
# we compile
QUALIFIER=`date "+%Y%m%d"`

# This can be '*' unless you happen to have more than one version of
# org.eclipse.pde.build_*, as can happen after installing updates
PDE_VER=*
#PDE_VER=3.6.2.R36x_20110203

