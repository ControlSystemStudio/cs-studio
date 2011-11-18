# =========================================================================
# Output sequential numbers, from 1 to the given number, to the standard
# output.
#
# Parameters
#  1: Last number
#  OS: "MACOSX" in case of Mac OS X. Something else in case of Linux.
# =========================================================================
function num_seq {
    if [ "${OS}" = "MACOSX" ]; then
        jot -s " " $1
    else
        seq $1
    fi
}

# =========================================================================
# Append the given URLs to ARCHIVE_URLS.
# In ARCHIVE_URLS, URLs wil be separated by '*'.
#
# Parameters
#  1: URLs separated by a white space
# =========================================================================
function append_urls {
    local URLS=$(echo "$1" | tr ' ' '*')
    if [ -z "${ARCHIVE_URLS}" ]; then
        ARCHIVE_URLS="${URLS}"
    else
        ARCHIVE_URLS="${ARCHIVE_URLS}*${URLS}"
    fi
}

# =========================================================================
# Prepend the givne URLs to ARCHIVE_URLS.
# In ARCHIVE_URLS, URLs wil be separated by '*'.
#
# Parameters
#  1: URLs separated by a white space
# =========================================================================
function prepend_urls {
    local URLS=$(echo "$1" | tr ' ' '*')
    if [ -z "${ARCHIVE_URLS}" ]; then
        ARCHIVE_URLS="${URLS}"
    else
        ARCHIVE_URLS="${URLS}*${ARCHIVE_URLS}"
    fi
}

# =========================================================================
# Remove duplicated URLs from ARCHIVE_URLS.
#
# Parameters
#  ARCHIVE_URLS: URLs separated by '*'
# =========================================================================
function unique_urls {
    local URLS=$(echo "${ARCHIVE_URLS}" | tr '*' ' ')
    local UNIQUE_URLS=""

    for URL1 in ${URLS}; do
        DUPLICATED=0

        for URL2 in ${UNIQUE_URLS}; do
            if [ "${URL1}" = "${URL2}" ]; then
                DUPLICATED=1
                break
            fi
        done

        if [ ${DUPLICATED} -eq 0 ]; then
            if [ -z "${UNIQUE_URLS}" ]; then
                UNIQUE_URLS="${URL1}"
            else
                UNIQUE_URLS="${UNIQUE_URLS} ${URL1}"
            fi
        fi
    done

    ARCHIVE_URLS=$(echo "${UNIQUE_URLS}" | tr ' ' '*')
}

# =========================================================================
# Append sub archive names with the given URL to SUB_ARCHIVES.
# SUB_ARCHIVES will finally be a string in the following form:
#  NAME1|1|URL*NAME2|2|URL*NAME3|3|URL*...
# This string can be passed to org.csstudio.trends.databrowser2/archives
# property of CSS.
#
# Parameters
#  1: Archive URL
#  2: Sub archive names separated by a white space
# =========================================================================
function append_sub_archives {
    local URL=$1
    local NAMES=$2
    local i=1
    local SUB_ARCHIVE=

    for NAME in ${NAMES}; do
        SUB_ARCHIVE="${NAME}|$i|${URL}"

        if [ -z "${SUB_ARCHIVES}" ]; then
            SUB_ARCHIVES="${SUB_ARCHIVE}"
        else
            SUB_ARCHIVES="${SUB_ARCHIVES}*${SUB_ARCHIVE}"
        fi
        i=$(expr $i + 1)
    done
}

# =========================================================================
# Set ADDR_LIST, ARCHIVE_URLS and SUB_ARCHIVES
# =========================================================================
function css_kek_settings {
    valid=0
    ARCHIVE_URLS=""
    SUB_ARCHIVES=""

    for a in $VALID_ACCS; do
        URLS=$(eval 'echo $'$a'_ARCHIVE_URLS')

        if [ "$a" = "$ACC" ]; then
        # Set address list for channel access
            ADDR_LIST=$(eval 'echo $'$a'_ADDR_LIST')
            
        # Set archiver URLs
            prepend_urls "${URLS}"

        # Set sub archive names for each archiver URL
            i=1
            for URL in ${URLS}; do
                NAMES=$(eval 'echo $'$a'_ARCHIVE_NAMES_'$i)
                append_sub_archives "${URL}" "${NAMES}"
                i=$(expr $i + 1)
            done

            valid=1
        else
        # Set archiver URLs
            append_urls "${URLS}"
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

    # Remove duplicated archiver URLs
    unique_urls
}
