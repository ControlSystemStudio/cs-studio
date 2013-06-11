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
    local URLS=($(echo "$1"))
    local BASE_NAME="$2"
    for i in $(seq 1 ${#URLS[@]}); do
        URL=${URLS[$i-1]}
        ALIAS=$(eval 'echo $'${BASE_NAME}${i})
        if [ -z "${ARCHIVE_URLS}" ]; then
            ARCHIVE_URLS="${URL}|${ALIAS}"
        else
            ARCHIVE_URLS="${ARCHIVE_URLS}*${URL}|${ALIAS}"
        fi
    done
}

# =========================================================================
# Prepend the givne URLs to ARCHIVE_URLS.
# In ARCHIVE_URLS, URLs wil be separated by '*'.
#
# Parameters
#  1: URLs separated by a white space
# =========================================================================
function prepend_urls {
    local URLS=($(echo "$1"))
    local BASE_NAME="$2"
    for i in $(seq 1 ${#URLS[@]}); do
        URL=${URLS[$i-1]}
        ALIAS=$(eval 'echo $'${BASE_NAME}${i})
        if [ -z "${ARCHIVE_URLS}" ]; then
            ARCHIVE_URLS="${URL}|${ALIAS}"
        else
            ARCHIVE_URLS="${URL}|${ALIAS}*${ARCHIVE_URLS}"
        fi
    done
}

# =========================================================================
# Remove duplicated URLs from ARCHIVE_URLS.
#
# Parameters
#  ARCHIVE_URLS: URLs separated by '*'
# =========================================================================
function unique_urls {
    local OIFS=${IFS}
    IFS="*"
    local UNIQUE_URLS=""

    for URL1 in ${ARCHIVE_URLS}; do
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
                UNIQUE_URLS="${UNIQUE_URLS}*${URL1}"
            fi
        fi
    done

    ARCHIVE_URLS=${UNIQUE_URLS}
    IFS=${OIFS}
}

# =========================================================================
# Append sub archive name with the given URL to SUB_ARCHIVES.
# SUB_ARCHIVES will finally be a string in the following form:
#  NAME1|1|URL1*NAME2|1|URL2*NAME3|1|URL3*...
# This string can be passed to org.csstudio.trends.databrowser2/archives
# property of CSS.
#
# Parameters
#  1: Archive URL
#  2: Sub archive names separated by white spaces
# =========================================================================
function append_sub_archives {
    local URL=$1
    local NAMES=$2
    local i=1
    local SUB_ARCHIVE=
    local TAIL=
    local NAME=
    local POS=

    for S in ${NAMES}; do
        TAIL=$(echo ${S} | cut -c ${#S})
        if [ "${TAIL}" = "\\" ]; then
            echo "OK!!!"
            POS=$(expr ${#S} - 1)
            NAME="${NAME}"$(echo ${S} | cut -c 1-${POS})" "
        else
            NAME="${NAME}${S}"
            SUB_ARCHIVE="${NAME}|$i|${URL}"
            if [ -z "${SUB_ARCHIVES}" ]; then
                SUB_ARCHIVES="${SUB_ARCHIVE}"
            else
                SUB_ARCHIVES="${SUB_ARCHIVES}*${SUB_ARCHIVE}"
            fi

            NAME=""
            i=$(expr $i + 1)
        fi
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

	    # Set font.def and color.def
            COLOR_DEF=$(eval 'echo $'$a'_COLOR_DEF')
            FONT_DEF=$(eval 'echo $'$a'_FONT_DEF')

	    # Set -share_link parameters
            SHARE_LINK=""
            SHARE_LINK_WIN=""
            for i in $(seq 3); do
	        SHARE_LINK_SRC_WIN=$(eval 'echo $'$a'_SHARE_LINK_SRC_WIN_'$i)
	        SHARE_LINK_SRC=$(eval 'echo $'$a'_SHARE_LINK_SRC_'$i)
	        SHARE_LINK_DEST=$(eval 'echo $'$a'_SHARE_LINK_DEST_'$i)
                if [ -n "${SHARE_LINK_SRC}" -a -n "${SHARE_LINK_DEST}" ]; then
                    if [ -z "${SHARE_LINK}" ]; then
                        SHARE_LINK="${SHARE_LINK_SRC}=${SHARE_LINK_DEST}"
                    else
                        SHARE_LINK="${SHARE_LINK},${SHARE_LINK_SRC}=${SHARE_LINK_DEST}"
                    fi
                fi

                if [ -n "${SHARE_LINK_SRC_WIN}" -a -n "${SHARE_LINK_DEST}" ]; then
                    if [ -z "${SHARE_LINK_WIN}" ]; then
                        SHARE_LINK_WIN="${SHARE_LINK_SRC_WIN}=${SHARE_LINK_DEST}"
                    else
                        SHARE_LINK_WIN="${SHARE_LINK_WIN},${SHARE_LINK_SRC_WIN}=${SHARE_LINK_DEST}"
                    fi
                fi
            done
            
        # Set archiver URLs
            prepend_urls "${URLS}" "${a}_ARCHIVE_ALIAS_"

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
            append_urls "${URLS}" "${a}_ARCHIVE_ALIAS_"
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
