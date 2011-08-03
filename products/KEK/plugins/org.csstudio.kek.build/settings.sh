# Headless build settings
#
# Kay Kasemir

# Settings for Windows with cygwin tools
export VERSION=3.0.0
export TOP=/CSS/Repo/cs-studio-3.0

export BUILDDIR=/CSS/build

export ECLIPSE_BASE=/CSS/Eclipse/RCP
export WORKSPACE=/CSS/RCPWorkspace

export JAVA_HOME="c:\Program Files\Java\jdk1.6.0_26"

export ECLIPSE=$ECLIPSE_BASE/eclipse

# Could use standalone ant, or the one built into Eclipse
export ANT="java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"

# Ant with eclipse tasks
export ECLIPSE_ANT="java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"


# Use only the date as qualifier?
# With default, the time is included and then the same plugin for
# 'basic' and 'SNS' is generated twice
QUALIFIER=`date "+%Y%m%d"`

export DELTAPACK=$ECLIPSE_BASE/eclipse-3.6.2-delta-pack/eclipse

# This can be empty unless you happen to have more than one version of
# org.eclipse.pde.build_*, as can happen after installing updates
#PDE_VER=*
PDE_VER=3.6.2.R36x_20110203