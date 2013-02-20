# Headless build settings
#
# Kay Kasemir

# Version to build
export VERSION=3.2.0I

# Must use Java 7
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_13.jdk/Contents/Home

if [ ! -d $JAVA_HOME/Classes ]
then
   echo "On OS X, to make new Oracle JDK look like old Apple JDK"
   echo "that RCP headless build still expects, do this:"
   echo "ln -s $JAVA_HOME/jre/lib $JAVA_HOME/Classes"
   exit -1
fi

export PATH=$JAVA_HOME/bin:$PATH

# Top of repository tree
export TOP=/Users/ky9/git/cs-studio

# Workspace that might have 'local' sources beyond repository
export WORKSPACE=/Kram/Eclipse/Workspace_cs-studio_4.2

# Location of Eclipse and Delta pack, the 'target' platform
export ECLIPSE_BASE=/Kram/Eclipse/3_7_2/rcp
# Deltapack can list several sites: delta pack;PyDev;...
export DELTAPACK="/Kram/Eclipse/3_7_2/rcp/delta/eclipse:/Kram/Eclipse/CSS_Additions/PyDev2.6.0"
export ECLIPSE=$ECLIPSE_BASE/eclipse

export JRE_Macosx64=/Library/Java/JavaVirtualMachines/1.7.0.jdk/Contents/Home/jre
export JRE_Win64=/Kram/Eclipse/CSS_Additions/Win64/jre

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
export ECLIPSE_ANT="java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"

# Could use standalone ant, or the one built into Eclipse
export ANT=ant
