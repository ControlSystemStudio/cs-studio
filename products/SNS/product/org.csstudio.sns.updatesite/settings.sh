# Headless build settings
#
# Kay Kasemir

# Version to build
export VERSION=4.0.0

if [ `hostname` = 'ics-web4.sns.ornl.gov' ]
then
   # Must use Java 7
   export JAVA_HOME=/usr/local/java/jdk1.7.0_55

   # Top of repository tree
   export TOP=/usr/local/hudson/config/jobs/CSS4/workspace

   # Workspace that might have 'local' sources beyond repository
   export WORKSPACE=/home/kasemir/Eclipse/LocalCSS

   # Location of Eclipse and Delta pack, the 'target' platform
   export ECLIPSE=/home/kasemir/Eclipse/4.4.0/rcp/
   # Deltapack can list several sites: delta pack;PyDev;...
   export DELTAPACK="/home/kasemir/Eclipse/4.4.0/delta:/home/kasemir/Eclipse/CSS_Additions/PyDev2.6.0"

   # Output directory. Must NOT include symbolic link (like /tmp on Mac OS X)
   export BUILDDIR=/tmp/css_build

   export JRE_Win64=/home/kasemir/Eclipse/CSS_Additions/Win64/jre/
else
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_65.jdk/Contents/Home
   export TOP=/Users/ky9/git/cs-studio_4
   export WORKSPACE=/Users/ky9/Eclipse/Workspace4
   export ECLIPSE=/Users/ky9/Eclipse/4.4.0/rcp
   export DELTAPACK="/Users/ky9/Eclipse/4.4.0/delta:/Users/ky9/Eclipse/CSS_Additions/PyDev2.6.0"
   export BUILDDIR=/Kram/build
   export JRE_Win64=/Users/ky9/Eclipse/CSS_Additions/Win64/jre
fi

export PATH=$JAVA_HOME/bin:$PATH

# Use only the date as qualifier?
# With default, the time is included and then the same plugin for
# 'basic' and 'SNS' is generated twice
QUALIFIER=`date "+%Y%m%d"`

# This can be empty unless you have multiple version of
# org.eclipse.pde.build_*, as can happen after installing updates
#PDE_VER=3.8
PDE_VER=

# Ant with eclipse tasks
export ECLIPSE_ANT="java -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner"

# Could use standalone ant, or the one built into Eclipse
export ANT=ant
