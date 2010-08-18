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

exit 1

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
    
    # * Mark launchers as executable
    # With a headless build in 3.5, the OS X and Linux launchers were not
    # marked executable. https://bugs.eclipse.org/bugs/show_bug.cgi?id=260844 ?
    # With 3.6, this seems no longer necessary, but it can't hurt, either.
    # -> chmod +x
    
    # * Create dropins directory
    # Done via p2.inf

    # * Enable dropins directory
    # In configuration/org.eclipse.equinox.simpleconfigurator/bundles.info,
    # the org.eclipse.equinox.p2.reconciler.dropins must be set to
    # start automatically.
    # Unclear how to do that in p2.inf, so using perl
    
    ## Basic EPICS
    # OS X
    unzip -q build/I.epics_css_$VERSION/epics_css_$VERSION-macosx.carbon.x86.zip
    chmod +x CSS_EPICS_$VERSION/css.app/Contents/MacOS/css
    perl -p -i -e 's/(org\.eclipse\.equinox\.p2\.reconciler\.dropins,.+),false/\1,true/;' CSS_EPICS_$VERSION/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info    
    rm -f apps/epics_css_$VERSION-macosx.carbon.x86.zip
    zip -qr apps/epics_css_$VERSION-macosx.carbon.x86.zip CSS_EPICS_$VERSION
    rm -rf CSS_EPICS_$VERSION
    rm build/I.epics_css_$VERSION/epics_css_$VERSION-macosx.carbon.x86.zip

    # Linux
    unzip -q build/I.epics_css_$VERSION/epics_css_$VERSION-linux.gtk.x86.zip
    chmod +x CSS_EPICS_$VERSION/css
    perl -p -i -e 's/(org\.eclipse\.equinox\.p2\.reconciler\.dropins,.+),false/\1,true/;' CSS_EPICS_$VERSION/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info    
    rm -f apps/epics_css_$VERSION-linux.gtk.x86.zip
    zip -qr apps/epics_css_$VERSION-linux.gtk.x86.zip CSS_EPICS_$VERSION
    rm -rf CSS_EPICS_$VERSION
    rm build/I.epics_css_$VERSION/epics_css_$VERSION-linux.gtk.x86.zip
    
    # Windows
    unzip build/I.epics_css_$VERSION/epics_css_$VERSION-win32.win32.x86.zip
    perl -p -i -e 's/(org\.eclipse\.equinox\.p2\.reconciler\.dropins,.+),false/\1,true/;' CSS_EPICS_$VERSION/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info    
    rm -f apps/epics_css_$VERSION-win32.win32.x86.zip
    zip -qr apps/epics_css_$VERSION-win32.win32.x86.zip CSS_EPICS_$VERSION
    rm -rf CSS_EPICS_$VERSION
	rm build/I.epics_css_$VERSION/epics_css_$VERSION-win32.win32.x86.zip

    ## SNS CSS
    # OS X
    unzip -q build/I.sns_css_$VERSION/sns_css_$VERSION-macosx.carbon.x86.zip
    chmod +x CSS_$VERSION/css.app/Contents/MacOS/css
    perl -p -i -e 's/(org\.eclipse\.equinox\.p2\.reconciler\.dropins,.+),false/\1,true/;' CSS_$VERSION/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info    
    rm -f apps/sns_css_$VERSION-macosx.carbon.x86.zip
    zip -qr apps/sns_css_$VERSION-macosx.carbon.x86.zip CSS_$VERSION
    rm -rf CSS_$VERSION
    rm build/I.sns_css_$VERSION/sns_css_$VERSION-macosx.carbon.x86.zip

    # Linux
    unzip -q build/I.sns_css_$VERSION/sns_css_$VERSION-linux.gtk.x86.zip
    chmod +x CSS_$VERSION/css
    perl -p -i -e 's/(org\.eclipse\.equinox\.p2\.reconciler\.dropins,.+),false/\1,true/;' CSS_$VERSION/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info    
    rm -f apps/sns_css_$VERSION-linux.gtk.x86.zip
    zip -qr apps/sns_css_$VERSION-linux.gtk.x86.zip CSS_$VERSION
    rm -rf CSS_$VERSION
    rm build/I.sns_css_$VERSION/sns_css_$VERSION-linux.gtk.x86.zip

    # Windows
    unzip -q build/I.sns_css_$VERSION/sns_css_$VERSION-win32.win32.x86.zip
    perl -p -i -e 's/(org\.eclipse\.equinox\.p2\.reconciler\.dropins,.+),false/\1,true/;' CSS_$VERSION/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info    
    rm -f apps/sns_css_$VERSION-win32.win32.x86.zip
    zip -qr apps/sns_css_$VERSION-win32.win32.x86.zip CSS_$VERSION
    rm -rf CSS_$VERSION
    rm build/I.sns_css_$VERSION/sns_css_$VERSION-win32.win32.x86.zip

    ## Optional feature is already in buildRepo

    ## Source code
    ant zip_sources
fi

