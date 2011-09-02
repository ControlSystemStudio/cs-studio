# Headless build settings
#
# Kay Kasemir

export VERSION=3.0.3
export TOP=/Kram/MerurialRepos/cs-studio-3.0.0

export ECLIPSE_BASE=/Kram/Eclipse/3_7/rcp
export WORKSPACE=/Kram/Eclipse/Workspace3.0
export DELTAPACK=/Kram/Eclipse/3_7/delta/eclipse

# Mac OS X window system
OSWIN=cocoa
# OSWIN=carbon

# Use only the date as qualifier?
# With default, the time is included and then the same plugin for
# 'basic' and 'SNS' is generated twice
QUALIFIER=`date "+%Y%m%d"`

export ECLIPSE=$ECLIPSE_BASE/eclipse

export BUILDDIR=$TOP/products/SNS/product/org.csstudio.sns.updatesite/build

# This can be empty unless you happen to have more than one version of
# org.eclipse.pde.build_*, as can happen after installing updates
#PDE_VER=3.6.0
PDE_VER=