# Headless build settings
#
# Kay Kasemir

export VERSION=3.0.0

# Settings for Windows with cygwin tools
export TOP=/CSS/Repo/cs-studio-3.0

# On Windows w/ cygwin, this needs to be set.
# On Linux and OS X, it must be empty
export CYGDRIVE=/cygdrive/c

export BUILDDIR=/CSS/build

export ECLIPSE_BASE=/CSS/Eclipse/RCP
#export WORKSPACE=/CSS/RCPWorkspace

export JAVA_HOME="c:\Program Files\Java\jdk1.6.0_26"

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

export DELTAPACK=$ECLIPSE_BASE/eclipse-3.6.2-delta-pack/eclipse

# This can be empty unless you happen to have more than one version of
# org.eclipse.pde.build_*, as can happen after installing updates
#PDE_VER=*
PDE_VER=3.6.2.R36x_20110203


# Linux:
#export TOP=$HOME/CSS/cs-studio-3.0
#export CYGDRIVE=
#export BUILDDIR=$HOME/CSS/build
#export ECLIPSE_BASE=$HOME/CSS/Tools
#export JAVA_HOME=$HOME/jdk1.6.0_26
#export ECLIPSE=$ECLIPSE_BASE/eclipse
#export ECLIPSE_ANT="java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"
#export ANT=$ECLIPSE_ANT
#export DELTAPACK=$ECLIPSE

