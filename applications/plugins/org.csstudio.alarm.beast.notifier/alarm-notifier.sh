#!/bin/bash

CODAC_ROOT=$(readlink -f $0 | sed -n -e 's|^\(/[^/]*/[^/]*\)/.*|\1|p')
. ${CODAC_ROOT}/bin/codacenv

# This script combines the default preferences that depends on environmental
# variables with the preference file specified by a user. Finally Alarm
# Notifier will launch with the combined preferences.
#
# The same command line arguments that you used to give to Alarm Notifier
# can be used when you run this script. All the options except
# "-pluginCustomization <location>" will be passed to the Alarm Notifier
# at the end.

# Parse command line arguments
#
# If "-pluginCustomization <location>" option is specified more than once,
# the last one will be considered as the only valid option, and the others
# will be discarded.
#
# All the other options will be given to Alarm Notifier.
while [ -n "$1" ]; do
    case $1 in
	-pluginCustomization) USER_PREF_FILE=$2; shift 2;;
	*) USER_ARGS="${USER_ARGS} \"$1\""; shift 1;;
    esac
done

TMP_PREF_FILE=/tmp/plugin_customization.ini.$$

# Trap EXIT signal to ensure that the temporary preference file is
# removed when this script eixts or interrupted with "Ctrl+C".
trap "rm -f \"${TMP_PREF_FILE}\"" EXIT

# Before starting to parse and concatenate preference files, make sure
# to remove the file which has the same name 
if [ -e "${TMP_PREF_FILE}" ]; then
    rm -f "${TMP_PREF_FILE}" || echo "ERROR: Failed to remove ${TMP_PREF_FILE}. Try again." >&2
fi

# Load CODAC server configuration in ${CODAC_CONF}/default/servers
# 
# Usually, this configuration file is edited by "codac-server" command.
# This wrapper script loads configuration as shell script varaibles,
# which will be used in the last half of this script to set servers'
# host name or IP address with CSS preference mechanism so that the
# users do not have to manually change the server settings in "Preference"
# window.
[ -r "${CODAC_CONF}/default/servers" ] && . "${CODAC_CONF}/default/servers"

# Load CSS database names in ${CODAC_CONF}/default/css-db
[ -r "${CODAC_CONF}/default/css-db" ] && . "${CODAC_CONF}/default/css-db"


cat > "${TMP_PREF_FILE}" <<EOF
org.csstudio.alarm.beast/rdb_url=jdbc:postgresql://${CODAC_ALARM_DB_SERVER:-localhost}/${CODAC_CSS_ALARM_DBNAME:-css_alarm}
org.csstudio.alarm.beast/jms_url=failover:(tcp://${CODAC_CSS_LOG_JMS_SERVER:-localhost}:61616)
org.csstudio.logging/jms_url=failover:(tcp://${CODAC_CSS_LOG_JMS_SERVER:-localhost}:61616)?randomize=false
EOF


if [ -n "${USER_PREF_FILE}" ]; then
    cat "${USER_PREF_FILE}" >> "${TMP_PREF_FILE}"
fi

# Put all the arguments together.
ARGS="${USER_ARGS} -pluginCustomization \"${TMP_PREF_FILE}\""

# Run Alarm Notifier.
eval "${CODAC_ROOT}/css/alarm-notifier/alarm-notifier ${ARGS}"
