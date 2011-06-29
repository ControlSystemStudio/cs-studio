# Headless build settings
#
# Kay Kasemir

# Settings for Windows with cygwin tools
export VERSION=3.0.0
export TOP=/CSS/Repo/cs-studio-3.0

export ECLIPSE_BASE=/CSS/Eclipse/RCP
export WORKSPACE=/CSS/RCPWorkspace

export JAVA_HOME="c:\Program Files\Java\jdk1.6.0_26"
export ANT=/cygdrive/c/CSS/Tools/apache-ant-1.8.2-bin/apache-ant-1.8.2/bin/ant.bat

# Use only the date as qualifier?
# With default, the time is included and then the same plugin for
# 'basic' and 'SNS' is generated twice
QUALIFIER=`date "+%Y%m%d"`

export ECLIPSE=$ECLIPSE_BASE/eclipse
export DELTAPACK=$ECLIPSE_BASE/eclipse-3.6.2-delta-pack/eclipse

export BUILDDIR=$TOP/products/KEK/plugins/org.csstudio.kek.build/build

# This can be empty unless you happen to have more than one version of
# org.eclipse.pde.build_*, as can happen after installing updates
#PDE_VER=*
PDE_VER=3.6.2.R36x_20110203