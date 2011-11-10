#!/bin/bash
#
# CSS Launcher for KEK (Linux and Mac OS X)
#
# Usage:
#  css_kek.sh ACC OS
#
#  ACC - One of valid accelerators name. See VALID_ACC varible defined
#        in acc_settings.sh
#  OS - MACOSX or LINUX
#
# Author
#  - Takashi Nakamoto (Cosylab)
#

SCRIPTDIR=$(cd $(dirname $0) && pwd)

ACC=$(echo "$1" | tr "[a-z]" "[A-Z]")
OS=$(echo "$2" | tr "[a-z]" "[A-Z]")

# Temporary plugin customization file.
# This file must be writable by the user who run this script.
TMP_INI=/tmp/plugin_customization_$$.ini

# Path to the CSS executable.
CSS=${SCRIPTDIR}/css

# Read KBLog settings
source ${SCRIPTDIR}/kblog_settings.sh

# Read settings of each accelerator
source ${SCRIPTDIR}/acc_settings.sh

# Utility functions
source ${SCRIPTDIR}/css_kek_functions.sh

# Set basic parameters
css_kek_settings

# Output a temporary plugin customizaiton file
cat > ${TMP_INI} <<EOF
org.csstudio.platform.libs.epics/addr_list=${ADDR_LIST}

org.csstudio.trends.databrowser2/archives=${SUB_ARCHIVES}
org.csstudio.trends.databrowser2/urls=${ARCHIVE_URLS}

org.csstudio.archive.reader.kblog/path_to_kblogrd=${PATH_TO_KBLOGRD}
org.csstudio.archive.reader.kblog/rel_path_to_subarchive_list=${KBLOG_REL_PATH_TO_SUBARCHIVE_LIST}
org.csstudio.archive.reader.kblog/rel_path_to_lcf_dir=${KBLOG_REL_PATH_TO_LCF_DIR}
org.csstudio.archive.reader.kblog/reduce_data=${KBLOG_REDUCE_DATA}
EOF


# Launch CSS with the temporary plugin customization file
${CSS} -pluginCustomization ${TMP_INI}
