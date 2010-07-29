# Headless build settings
#
# Kay Kasemir

export VERSION=2.2.0

# Use only the date as qualifier?
# With default, the time is included and then the same plugin for
# 'basic' and 'SNS' is generated twice
QUALIFIER=`date "+%Y%m%d"`

# OS X
export ECLIPSE_BASE=/Kram/Eclipse/3_5_2/rcp
export WORKSPACE=/Kram/Eclipse/Workspace

export ECLIPSE=$ECLIPSE_BASE/eclipse
export DELTAPACK=$ECLIPSE_BASE/delta/eclipse
export BUILDDIR=$WORKSPACE/org.csstudio.sns.updatesite/build

# This can be empty unless you happen to have more than one version of
# org.eclipse.pde.build_*, as can happen after installing updates
#PDE_VER=3.5.2
PDE_VER=