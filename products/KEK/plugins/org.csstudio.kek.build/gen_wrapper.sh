#!/bin/sh
#
# CSS Launcher Generator
#
# Supported OS:
#  Linux
#  Mac
#  Windows
#
# Author
#  - Takashi Nakamoto
#

SCRIPTDIR=$(cd $(dirname $0) && pwd)

ACC=$(echo "$1" | tr "[a-z]" "[A-Z]")
OS=$(echo "$2" | tr "[a-z]" "[A-Z]")

source ${SCRIPTDIR}/acc_settings.sh

valid=0
ADDR_LIST=""
ARCHIVE_URLS=()
ARCHIVE_NAMES=()

for a in ${VALID_ACCS}; do
    URLS=($(eval 'echo $'$a'_ARCHIVE_URLS'))
    NAMES=($(eval 'echo $'$a'_ARCHIVE_NAMES'))

    if [ "$a" = "$ACC" ]; then
	for i in $(seq ${#URLS[@]}); do
		j=$(expr $i - 1)
		ARCHIVE_URLS=(${URLS[$j]} ${ARCHIVE_URLS[@]})
		ARCHIVE_NAMES=(${NAMES[$j]} ${ARCHIVE_NAMES[@]})
		
		ADDR_LIST=$(eval 'echo $'$a'_ADDR_LIST')
	done
	valid=1
    else
	for i in $(seq ${#URLS[@]}); do
		j=$(expr $i - 1)
		ARCHIVE_URLS=(${ARCHIVE_URLS[@]} ${URLS[$j]})
		ARCHIVE_NAMES=(${ARCHIVE_NAMES[@]} ${NAMES[$j]})
	done
    fi
done

if [ $valid -ne 1 ]; then
    echo "Invalid accelerator name: $ACC" >&2
    echo "" >&2
    echo "Valid accelerator names are:" >&2
    for a in $VALID_ACCS; do
	echo " - $a" >&2
    done
    exit 1
fi

for i in $(seq ${#ARCHIVE_URLS[@]}); do
    j=$(expr $i - 1)
    DATABROWSER_URLS="${DATABROWSER_URLS}*${ARCHIVE_URLS[$j]}"
    DATABROWSER_ARCHIVES="${DATABROWSER_ARCHIVES}*${ARCHIVE_NAMES[$j]}|$i|${ARCHIVE_URLS[$j]}"
done

FIRST_CHAR=$(echo "${DATABROWSER_URLS}" | cut -c 1-1)
if [ "${FIRST_CHAR}" = "*" ]; then
    DATABROWSER_URLS=$(echo "${DATABROWSER_URLS}" | cut -c 2-)
fi

FIRST_CHAR=$(echo "${DATABROWSER_ARCHIVES}" | cut -c 1-1)
if [ "${FIRST_CHAR}" = "*" ]; then
    DATABROWSER_ARCHIVES=$(echo "${DATABROWSER_ARCHIVES}" | cut -c 2-)
fi

if [ "${OS}" = "WIN" ]; then
cat <<EOF
cd %~dp0

@echo org.csstudio.platform.libs.epics/addr_list=${ADDR_LIST} > %TEMP%\plugin_customization_${ACC}.ini
@echo org.csstudio.trends.databrowser2/archives=${DATABROWSER_ARCHIVES//|/^|} >> %TEMP%\plugin_customization_${ACC}.ini
@echo org.csstudio.trends.databrowser2/urls=${DATABROWSER_URLS//|/^|} >> %TEMP%\plugin_customization_${ACC}.ini

css.exe -pluginCustomization %TEMP%\plugin_customization_${ACC}.ini
EOF

else

cat <<EOF
SCRIPTDIR=\$(cd \$(dirname \$0) && pwd)
TMP_INI=/tmp/plugin_customization_\$\$.ini
CSS=\${SCRIPTDIR}/css

cat <<EOS
org.csstudio.platform.libs.epics/addr_list=${ADDR_LIST}

org.csstudio.trends.databrowser2/archives=${DATABROWSER_ARCHIVES}
org.csstudio.trends.databrowser2/urls=${DATABROWSER_URLS}
EOS

\${CSS} -pluginCustomization \${TMP_INI}
EOF

fi

