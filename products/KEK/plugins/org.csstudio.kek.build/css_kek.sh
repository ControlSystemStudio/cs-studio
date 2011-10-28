#!/bin/sh
#
# CSS Launcher for KEK
#
# Supported OS:
#  Linux
#  Mac
#
# Author
#  - Takashi Nakamoto
#

SCRIPTDIR=$(cd $(dirname $0) && pwd)

ACC=$(echo "$1" | tr "[a-z]" "[A-Z]")
OS=$(echo "$2" | tr "[a-z]" "[A-Z]")
TMP_INI=/tmp/plugin_customization_$$.ini
CSS=${SCRIPTDIR}/css

function num_seq {
    if [ "${OS}" = "MACOSX" ]; then
	jot -s " " $1
    else
	seq $1
    fi
}

source ${SCRIPTDIR}/acc_settings.sh

valid=0
ADDR_LIST=""
ARCHIVE_URLS=()

for a in $VALID_ACCS; do
    URLS=($(eval 'echo $'$a'_ARCHIVE_URLS'))
    NAMES=($(eval 'echo $'$a'_ARCHIVE_NAMES'))

    if [ ${#URLS[@]} -ne 0 ]; then
		for i in $(num_seq ${#URLS[@]}); do
			j=$(expr $i - 1)
			if [ "$a" = "$ACC" ]; then
			    
			    ARCHIVE_URLS=(${URLS[$j]} ${ARCHIVE_URLS[@]})
                            DATABROWSER_ARCHIVES="${NAMES[$j]}|1|${URLS[$j]}*${DATABROWSER_ARCHIVES}"
			    
			    ADDR_LIST=$(eval 'echo $'$a'_ADDR_LIST')
			    valid=1
			else
			    ARCHIVE_URLS=(${ARCHIVE_URLS[@]} ${URLS[$j]})
			fi
		done
    fi
done

if [ $valid -ne 1 ]; then
    echo "Invalid accelerator name: $ACC"
    echo ""
    echo "Valid accelerator names are:"
    for a in $VALID_ACCS; do
	echo " - $a"
    done
    exit 1
fi

for i in $(num_seq ${#ARCHIVE_URLS[@]}); do
    j=$(expr $i - 1)
    DATABROWSER_URLS="${DATABROWSER_URLS}*${ARCHIVE_URLS[$j]}"
done

FIRST_CHAR=$(echo "${DATABROWSER_URLS}" | cut -c 1-1)
if [ "${FIRST_CHAR}" = "*" ]; then
    DATABROWSER_URLS=$(echo "${DATABROWSER_URLS}" | cut -c 2-)
fi

LAST_CHAR=$(echo "${DATABROWSER_ARCHIVES}" | rev | cut -c 1-1)
if [ "${LAST_CHAR}" = "*" ]; then
    DATABROWSER_ARCHIVES=$(echo "${DATABROWSER_ARCHIVES}" | rev | cut -c 2- | rev )
fi

cat > ${TMP_INI} <<EOF
org.csstudio.platform.libs.epics/addr_list=${ADDR_LIST}

org.csstudio.trends.databrowser2/archives=${DATABROWSER_ARCHIVES}
org.csstudio.trends.databrowser2/urls=${DATABROWSER_URLS}
EOF

${CSS} -pluginCustomization ${TMP_INI}
