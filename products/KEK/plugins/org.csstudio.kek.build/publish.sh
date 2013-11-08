#!/bin/bash
#
# Script publishes sources
#
# Note: May have to remove a previously copied repository on the web server
# before copying new files on top of existing ones.
#
# Runs on Linux
#
# Author:
#  - Kay Kasemir
#  - Takashi Nakamoto (Cosylab)
#

SCRIPTDIR=$(cd $(dirname $0) && pwd)

source settings.sh

# Platforms
PLATFORMS="LINUX_X64 LINUX_X86 WIN_X64 WIN_X86 MACOSX_X64 MACOSX_X86"

DATE=$(date +'%Y%m%d_%H%M%S')

# Destination of index.html and produced zip files.
#DEST=/mnt/linac-misc/httpd/home/cont/css
DEST=${HOME}/tmp/css

LINUX_X64_NAME="Linux (64 bit)"
LINUX_X64_PREF="linux.gtk.x86_64"

LINUX_X86_NAME="Linux (32 bit)"
LINUX_X86_PREF="linux.gtk.x86"

WIN_X64_NAME="Windows (64 bit)"
WIN_X64_PREF="win32.win32.x86_64"

WIN_X86_NAME="Windows (32 bit)"
WIN_X86_PREF="win32.win32.x86"

MACOSX_X64_NAME="Mac OS X (64 bit)"
MACOSX_X64_PREF="macosx.cocoa.x86_64"

MACOSX_X86_NAME="Mac OS X (32 bit)"
MACOSX_X86_PREF="macosx.cocoa.x86"

MACOSX_PPC_NAME="Mac OS X (PPC)"
MACOSX_PPC_PREF="macosx.cocoa.ppc"

# Hack the windows file permissions for cygwin
chmod 664 ${CYGDRIVE}${BUILDDIR}/I.css_kek_$VERSION/*.zip ${CYGDRIVE}${BUILDDIR}/css_kek_$VERSION-src.zip
chmod -R 777 ${CYGDRIVE}${BUILDDIR}/buildRepo

DOWNLOAD_LINKS=""
PREV_DIR=$(pwd)

# Copy CSS binaries
mkdir -p ${DEST}/apps
ACCS="pfar pf linac superkekb cerl pf_office linac_office superkekb_office jparc jparc_office"

for PLATFORM in ${PLATFORMS}; do
    echo "#####################################################"
    echo " Publishing a binary for ${PLATFORM}"
    echo "#####################################################"

    rm -rf ${CYGDRIVE}${BUILDDIR}/css_${VERSION}
    mkdir -p ${CYGDRIVE}${BUILDDIR}/css_${VERSION}

    PREF=$(eval 'echo $'$PLATFORM'_PREF')
    NAME=$(eval 'echo $'$PLATFORM'_NAME')
    OS=$(echo "${PLATFORM}" | cut -f1 -d'_')
    ZIP_BINARY=${CYGDRIVE}${BUILDDIR}/I.css_kek_${VERSION}/css_kek_${VERSION}-${PREF}_${DATE}.zip

    cp ${CYGDRIVE}${BUILDDIR}/I.css_kek_${VERSION}/css_kek_${VERSION}-${PREF}.zip ${ZIP_BINARY}

    # Adding launcher files to the archive.
    if [ "${OS}" = "LINUX" ]; then
        cd ${CYGDRIVE}${BUILDDIR}
        
        # Include common scripts required to launch CSS.
        cp ${SCRIPTDIR}/css_kek.sh css_${VERSION}/
        cp ${SCRIPTDIR}/css_kek_functions.sh css_${VERSION}/
        cp ${SCRIPTDIR}/acc_settings.sh css_${VERSION}/
        cp ${SCRIPTDIR}/kblog_settings.sh css_${VERSION}/

	ADD_FILE_LIST="css_${VERSION}/css_kek.sh css_${VERSION}/css_kek_functions.sh css_${VERSION}/acc_settings.sh css_${VERSION}/kblog_settings.sh"

        for acc in ${ACCS}; do
            # Generate a launcher script and include it into the zip.
            echo "#!/bin/sh" > css_${VERSION}/css_kek_${acc}
            echo "SCRIPTDIR=\$(cd \$(dirname \$0) && pwd)" >> css_${VERSION}/css_kek_${acc}
            echo "sh \${SCRIPTDIR}/css_kek.sh $acc LINUX \"\$@\"" >> css_${VERSION}/css_kek_${acc}

            chmod a+x css_${VERSION}/css_kek_${acc}
	    ADD_FILE_LIST="${ADD_FILE_LIST} css_${VERSION}/css_kek_${acc}"
        done

        zip ${ZIP_BINARY} ${ADD_FILE_LIST}

    elif [ "${OS}" = "WIN" ]; then
        cd ${CYGDRIVE}${BUILDDIR}

	ADD_FILE_LIST=

        for acc in ${ACCS}; do
            # Generate a launcher batch file and include it into the zip.
            sh ${SCRIPTDIR}/gen_wrapper.sh ${acc} WIN | perl -pe 's/\n/\r\n/' > css_${VERSION}/css_kek_${acc}.bat
            chmod a+x css_${VERSION}/css_kek_${acc}.bat
	    ADD_FILE_LIST="${ADD_FILE_LIST} css_${VERSION}/css_kek_${acc}.bat"
        done

	zip ${ZIP_BINARY} ${ADD_FILE_LIST}

    elif [ "${OS}" = "MACOSX" ]; then
        cd ${CYGDRIVE}${BUILDDIR}

        unzip ${ZIP_BINARY} "css_${VERSION}/css.app/*"

	if [ ! -e css_${VERSION}/css.app/Contents/Info.plist ]; then
	    # If Info.plist is missing from .app directory, create it from 
	    # Info.plits in the script directory. This is an ad-hoc solution.
	    mkdir -p css_${VERSION}/css.app/Contents
	    sed -e "s|_CSS_VERSION_|${VERSION}|g" ${SCRIPTDIR}/Info.plist > css_${VERSION}/css.app/Contents/Info.plist
	fi

	if [ ! -e css_${VERSION}/css.app/Contents/Resources/css.icns ]; then
	    # If css.icns is missing from .app directory for some reason,
	    # copy css.icns in source directory. This is an ad-hoc solution. 
	    mkdir -p css_${VERSION}/css.app/Contents/Resources
	    cp -f ${CYGDRIVE}${BUILDDIR}/plugins/org.csstudio.utility.product/icons/css.icns css_${VERSION}/css.app/Contents/Resources/css.icns
	    chmod 644 css_${VERSION}/css.app/Contents/Resources/css.icns
	fi

        for acc in ${ACCS}; do
            # Generate an application bundle based on CSS's and include it
            # into the zip.
            cp -r css_${VERSION}/css.app css_${VERSION}/css_kek_${acc}.app
            sed -i -e "s|<string>css</string>|<string>css_kek_${acc}</string>|" css_${VERSION}/css_kek_${acc}.app/Contents/Info.plist

            cp ${SCRIPTDIR}/css_kek.sh css_${VERSION}/css_kek_${acc}.app/Contents/MacOS
            cp ${SCRIPTDIR}/css_kek_functions.sh css_${VERSION}/css_kek_${acc}.app/Contents/MacOS
            cp ${SCRIPTDIR}/acc_settings.sh css_${VERSION}/css_kek_${acc}.app/Contents/MacOS
            cp ${SCRIPTDIR}/kblog_settings.sh css_${VERSION}/css_kek_${acc}.app/Contents/MacOS

            echo "#!/bin/sh" > css_${VERSION}/css_kek_${acc}.app/Contents/MacOS/css_kek_${acc}
            echo "SCRIPTDIR=\$(cd \$(dirname \$0) && pwd)" >> css_${VERSION}/css_kek_${acc}.app/Contents/MacOS/css_kek_${acc}
            echo "sh \${SCRIPTDIR}/css_kek.sh $acc MACOSX \"\$@\"" >> css_${VERSION}/css_kek_${acc}.app/Contents/MacOS/css_kek_${acc}

            chmod a+x css_${VERSION}/css_kek_${acc}.app/Contents/MacOS/css_kek_${acc}
            
            zip -r ${ZIP_BINARY} css_${VERSION}/css_kek_${acc}.app
        done
    fi

    # Append date and time to the zip file name and copy it to the destination.
    echo "Copying css_kek_$VERSION-$PREF.zip to ${DEST}/apps/css_kek_${VERSION}-${PREF}_${DATE}.zip"
    cp ${ZIP_BINARY} ${DEST}/apps/
    DOWNLOAD_LINKS="$DOWNLOAD_LINKS<li><a href=\"apps/css_kek_${VERSION}-${PREF}_${DATE}.zip\">$NAME</a></li>\n"

    # Clean.
    rm -rf ${CYGDRIVE}${BUILDDIR}/css_${VERSION}
done

cd ${PREV_DIR}

# Copy CSS source
echo "#####################################################"
echo " Publishing the source"
echo "#####################################################"
echo "Copying css_kek_$VERSION-src.zip to ${DEST}/apps"

ZIPBINARY=${DEST}/apps/css_kek_${VERSION}-src_${DATE}.zip
cp ${CYGDRIVE}${BUILDDIR}/css_kek_$VERSION-src.zip ${ZIPBINARY}

# Add README file to source tar ball.
cd ${SCRIPTDIR}/source
zip ${ZIPBINARY} README
cd ${PREV_DIR}

DOWNLOAD_LINKS="$DOWNLOAD_LINKS<li><a href=\"apps/css_kek_$VERSION-src_${DATE}.zip\">Source</a></li>\n"

# Archive built plugins
echo "#####################################################"
echo " Publishing built plugins for online update"
echo "#####################################################"
echo "Copying built plugins to ${DEST}/updates-3.2/repo${VERSION}_${DATE}"
mkdir -p ${DEST}/updates-3.2/repo${VERSION}_${DATE}
cp -r ${CYGDRIVE}${BUILDDIR}/buildRepo/* ${DEST}/updates-3.2/repo${VERSION}_${DATE}

sh ${SCRIPTDIR}/mirror.sh ${DEST}/updates-3.2/repo${VERSION}_${DATE} ${DEST}/updates-3.2