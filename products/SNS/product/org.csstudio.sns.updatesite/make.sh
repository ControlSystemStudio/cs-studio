#!/bin/sh
#
# Script that triggers a build of "everything"
#
# In principle, this should be an ANT build.xml,
# but the ant copy tasks were really slow compared to rsync.
# Maybe use a 'system' ant task to call rsync?
# In any case, this one is good enough for now.
#
# Kay Kasemir

cd /Kram/MerurialRepos/cs-studio/products/SNS/product/org.csstudio.sns.updatesite

source settings.sh

# Fetch new copy of sources
ant clean

echo Fetching sources
ant get_sources

# Build products and optional feature
for prod in config_build_Basic_CSS config_build_SNS_CSS config_build_optional
do
    echo $prod
    (cd $prod; sh build.sh)
    echo Done with $prod
done

OK=1
# Each build log contains 2(!) "BUILD SUCCESSFUL" lines
for prod in config_build_Basic_CSS config_build_SNS_CSS config_build_optional
do
    if [ `cat $prod/build.log | grep -c "BUILD SUCCESSFUL"` -eq 2 ]
    then
        echo OK: $prod
    else
        echo Build failed: $prod
        OK=0
    fi
done

# On success, fetch/patch the zip files
if [ $OK = 1 ]
then
    echo Collecting ZIP files
    mkdir -p apps
    
    # When exporting the product from the IDE, all is OK.
    # With a headless build, the OS X and Linux launchers are not
    # marked executable.
    # https://bugs.eclipse.org/bugs/show_bug.cgi?id=260844 ?
    
    ## Basic EPICS
    # OS X
    unzip -q build/I.epics_css_$VERSION/epics_css_$VERSION-macosx.carbon.x86.zip
    chmod +x CSS_EPICS_$VERSION/css.app/Contents/MacOS/css
    rm -f apps/epics_css_$VERSION-macosx.carbon.x86.zip
    zip -qr apps/epics_css_$VERSION-macosx.carbon.x86.zip CSS_EPICS_$VERSION
    rm -rf CSS_EPICS_$VERSION
    rm build/I.epics_css_$VERSION/epics_css_$VERSION-macosx.carbon.x86.zip

    # Linux
    unzip -q build/I.epics_css_$VERSION/epics_css_$VERSION-linux.gtk.x86.zip
    chmod +x CSS_EPICS_$VERSION/css
    rm -f apps/epics_css_$VERSION-linux.gtk.x86.zip
    zip -qr apps/epics_css_$VERSION-linux.gtk.x86.zip CSS_EPICS_$VERSION
    rm -rf CSS_EPICS_$VERSION
    rm build/I.epics_css_$VERSION/epics_css_$VERSION-linux.gtk.x86.zip
    
    # Windows
    mv build/I.epics_css_$VERSION/epics_css_$VERSION-win32.win32.x86.zip apps

    ## SNS CSS
    # OS X
    unzip -q build/I.sns_css_$VERSION/sns_css_$VERSION-macosx.carbon.x86.zip
    chmod +x CSS_$VERSION/css.app/Contents/MacOS/css
    rm -f apps/sns_css_$VERSION-macosx.carbon.x86.zip
    zip -qr apps/sns_css_$VERSION-macosx.carbon.x86.zip CSS_$VERSION
    rm -rf CSS_$VERSION
    rm build/I.sns_css_$VERSION/sns_css_$VERSION-macosx.carbon.x86.zip    

    # Linux
    unzip -q build/I.sns_css_$VERSION/sns_css_$VERSION-linux.gtk.x86.zip
    chmod +x CSS_$VERSION/css
    rm -f apps/sns_css_$VERSION-linux.gtk.x86.zip
    zip -qr apps/sns_css_$VERSION-linux.gtk.x86.zip CSS_$VERSION
    rm -rf CSS_$VERSION
    rm build/I.sns_css_$VERSION/sns_css_$VERSION-linux.gtk.x86.zip

    # Windows
    mv build/I.sns_css_$VERSION/sns_css_$VERSION-win32.win32.x86.zip apps

    ## Optional feature is already in buildRepo

    ## Source code
    ant zip_sources
fi  


