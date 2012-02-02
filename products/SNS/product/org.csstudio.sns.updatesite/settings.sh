# Headless build settings
#
# Kay Kasemir

# Version to build
export VERSION=3.1.0

# Top of repository tree
export TOP=/Kram/MerurialRepos/cs-studio

# Location of Eclipse and Delta pacj
export ECLIPSE_BASE=/Kram/Eclipse/3_7_1/rcp
export DELTAPACK=/Kram/Eclipse/3_7_1/delta/eclipse

# Workspace that might have 'local' sources beyond repository
export WORKSPACE=/Kram/Eclipse/Workspace_cs-studio_3.7.1

# Output directory. Must NOT include symbolic link (like /tmp on Mac OS X)
export BUILDDIR=/Kram/build

# Mac OS X window system
OSWIN=cocoa
# OSWIN=carbon

# Use only the date as qualifier?
# With default, the time is included and then the same plugin for
# 'basic' and 'SNS' is generated twice
QUALIFIER=`date "+%Y%m%d"`

export ECLIPSE=$ECLIPSE_BASE/eclipse


# This can be empty unless you happen to have more than one version of
# org.eclipse.pde.build_*, as can happen after installing updates
#PDE_VER=3.6.0
PDE_VER=