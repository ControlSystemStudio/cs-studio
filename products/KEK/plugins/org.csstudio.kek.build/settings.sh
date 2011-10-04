# Headless build settings
#
# Author
#  - Kay Kasemir
#  - Takashi Nakamoto
#

# Version number of CSS
export VERSION=3.0.0

# Root of CSS source tree.
export TOP=/CSS/Repo/cs-studio-3.0

# On Windows with cygwin, this needs to be set.
export CYGDRIVE=/cygdrive/c

# Directory where the built CSS and temporary files will be stored.
export BUILDDIR=/CSS/build

# Root directory of Eclipse.
export ECLIPSE_BASE=/CSS/Eclipse/RCP

# Root directory of Delta pack
export DELTAPACK=$ECLIPSE_BASE/eclipse-3.6.2-delta-pack/eclipse

# Root directory of JDK SE 6.
export JAVA_HOME="c:\Program Files\Java\jdk1.6.0_26"

# Path to the Eclipse executable.
export ECLIPSE=$ECLIPSE_BASE/eclipse

# Could use standalone ant, or the one built into Eclipse
export ANT="java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"

# Ant with eclipse tasks
export ECLIPSE_ANT="java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"

# Use only the date as qualifier/
# With default, the time is included and then the same plugin
# will be created several times for the various tools that
# we compile
QUALIFIER=`date "+%Y%m%d"`

# This can be empty unless you happen to have more than one version of
# org.eclipse.pde.build_*, as can happen after installing updates
#PDE_VER=3.6.2.R36x_20110203
PDE_VER=

# Linux
# export TOP=$HOME/cs-studio-3.0
# export CYGDRIVE=
# export BUILDDIR=$HOME/CSS/build
# export ECLIPSE_BASE=$HOME
# export JAVA_HOME=/usr/new/pkg/jdk1.6.0_21
# export ECLIPSE=$ECLIPSE_BASE/eclipse
# export ECLIPSE_ANT="$JAVA_HOME/bin/java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"
# export ANT=$ECLIPSE_ANT
# export DELTAPACK=$ECLIPSE/delta


