# Headless build settings
#
# Kay Kasemir

# Version to build

export VERSION=3.1.6

export PATH=/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/bin:$PATH

# Top of repository tree
export TOP=/Users/ky9/git/cs-studio_SNS_3.1.x/

# Workspace that might have 'local' sources beyond repository
export WORKSPACE=/Users/ky9/Eclipse/Workspace_SNS_3.1.x

# Location of Eclipse and Delta pack, the 'target' platform
export ECLIPSE_BASE=/Users/ky9/Eclipse/3.7.2/rcp
# Deltapack can list several sites: delta pack;PyDev
export DELTAPACK="/Users/ky9/Eclipse/3.7.2/delta:/Users/ky9/Eclipse/CSS_Additions/PyDev2.6.0"

export JRE_Win64=/Users/ky9/Eclipse/CSS_Additions/Win64/jre

# Output directory. Must NOT include symbolic link (like /tmp on Mac OS X)
export BUILDDIR=/Kram/build

# Use only the date as qualifier?
# With default, the time is included and then the same plugin for
# 'basic' and 'SNS' is generated twice
QUALIFIER=`date "+%Y%m%d"`

# This can be empty unless you happen to have more than one version of
# org.eclipse.pde.build_*, as can happen after installing updates
#PDE_VER=3.6.0
PDE_VER=

# Ant with eclipse tasks
export ECLIPSE_ANT="java -jar $ECLIPSE_BASE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"

# Could use standalone ant, or the one built into Eclipse
export ANT=ant
