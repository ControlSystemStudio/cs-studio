# Headless build settings
#
# Author
#  - Kay Kasemir
#  - Takashi Nakamoto (Cosylab)
#

# Version number of CSS
export VERSION=3.2.1

# Build host name (lcba03, abco4, lcba07 or JCSL_WS001).
# Current official build machine is lcba07.
#
# If you would like to use other machines, comment out the following line,
# and set TOP, CYGDRIVE, BUILDDIR, ECLIPSE_BASE, DELTAPACK and JAVA_HOME
# manually. See the following lines for more details.
BUILD_HOST=lcba07

# Root of CSS source tree.
#export TOP=${HOME}/work/cs-studio

# Root directory of the drive on cygwin.
#
# On Windows with cygwin, this needs to be set to something like /cygdrive/c.
# On Linux, this needs to be empty.
#export CYGDRIVE=

# Directory where the built CSS and temporary files will be stored.
#export BUILDDIR=${HOME}/work/CSSBuild

# Directory which contains the root of Eclipse RCP.
#export ECLIPSE_BASE=${HOME}/work

# Root directory of Delta pack
#export DELTAPACK=${HOME}/work/delta/eclipse

# Workspace that might have 'local' sources beyond repository

# Root directory of JDK SE 7.
#
# On Windows with cygwin, this needs to be set in the form like
# "c:\Program Files\Java\jdk1.7.0_21".
#export JAVA_HOME=${HOME}/work/jdk1.7.0_21

case "${BUILD_HOST}" in
    lcba03 | abco4 | lcba07)
        export TOP=${HOME}/work/cs-studio
        export CYGDRIVE=
        export BUILDDIR=${HOME}/work/CSSBuild
        export ECLIPSE_BASE=${HOME}/work
        export DELTAPACK=${HOME}/work/delta/eclipse:${HOME}/work/rap/rap1.4/eclipse
        export WORKSPACE=${HOME}/work/Workspace_cs-studio-3-1_3.7
        export JAVA_HOME=${HOME}/work/jdk1.7.0_21
        PDE_VER=*
        ;;
    JCSL_WS001)
        export TOP='/work/cs-studio'
        export CYGDRIVE=/cygdrive/c
        export BUILDDIR='/work/CSSBuild/BuildDir'
        export ECLIPSE_BASE='/work/CSSBuild'
        export DELTAPACK='/work/CSSBuild/delta/eclipse:/work/CSSBuild/rap/rap1.4/eclipse'
        export WORKSPACE='/work/CSSBuild/Workspace_cs-studio-3-1_3.7'
        export JAVA_HOME='c:\Program Files\Java\jdk1.7.0_21'
        PDE_VER=3.7.0.v20111116-2009
        ;;
esac

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
#PDE_VER=*
#PDE_VER=3.7.0.v20111116-2009

