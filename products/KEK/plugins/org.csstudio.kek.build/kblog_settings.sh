PATH_TO_KBLOGRD=/usr/local/bin/kblogrd
KBLOG_REL_PATH_TO_SUBARCHIVE_LIST=SYS/KEKBLog.list
KBLOG_REL_PATH_TO_LCF_DIR=SYS/LCF
KBLOG_REDUCE_DATA=false

# =========================================================================
# Output KBLog sub archive names separated by a white space to the
# standard output
#
# Parameter:
#  1: URL of KBLog archive (e.g. kblog:///KEKBLog)
# =========================================================================
function kblog_sub_archives {
    URL=$1

    if [ "${OS}" == "WIN" ]; then
        # Windows is not supported
        return
    fi

    PREFIX=$(echo "${URL}" | cut -c 1-8)
    if [ "${PREFIX}" != "kblog://" ]; then
        # Invalid URL
        return
    fi

    ROOT=$(echo "${URL}" | cut -c 9-)

    # Absolute path to KEKBLog.list
    PATH_TO_SUBARCHIVE_LIST=${ROOT}/${KBLOG_REL_PATH_TO_SUBARCHIVE_LIST}
    if [ -f "${PATH_TO_SUBARCHIVE_LIST}" ]; then
            cat ${PATH_TO_SUBARCHIVE_LIST} | tr ' ' '/' | tr '\n' ' '
    fi
}
