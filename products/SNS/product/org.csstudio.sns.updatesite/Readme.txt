** Build Process for SNS

The 'Basic EPICS' and 'SNS' versions as well as optional plugins are built
'headless' into a product and a P2 repository.

** Create new version
- IN MANY PLACES! UPDATE VERSION NUMBERS IN ALL THESE:
  * settings.sh
  * org.csstudio.sns.product/plugin.xml
  * org.csstudio.sns.product/SNS_CSS.product
  * org.csstudio.basic.epics.product/SNS_CSS.product
  * orc.csstudio.sns.feature.*/feature.xml
- Whenever a plugin changes "enough", increment its version number.
- For deployment, inc. the *sns.feature* versions
- The product file has the feature versions hardcoded. Before Eclipse 3.5.1,
  I used the text editor to check/update it to version "0.0.0" which matched
  all versions but then there came https://bugs.eclipse.org/bugs/show_bug.cgi?id=279480

** Prerequisites
- Install Eclipse and the matching 'RCP delta pack', obtained by looking
  for Eclipse, Downloads by topic, RCP, correct version, "Delta Pack".
  Check settings.sh for the required versions.
  On my first attempt, versions didn't match and voila, it didn't work.
  The delta pack is needed for the headless build as well as cross-builds.
  1. Extract the delta pack archive into its own directory on disk.
  2. In the IDE, open the Target Platform preferences (Window -> Preferences -> Plug-in Development -> Target Platform).
  3. Edit the active target, add the delta pack directory (top level that contains features, plugins).
  4. For headless build, see settings.sh DELTAPACK
      
** Headless build
  # Edit the version number in settings.sh(!!), then run
  sh make.sh
  
  The result is
  1) build/buildRepo
     A repository with the new binaries.
     Copied to update site web server as ..../repo$VERSION,
     then see mk_repo.sh and mirror_repo.sh for combining such repos
     from various versions into one big repo.
  2) apps/* application binaries and source snapshot
     Also copied to update site.

push_web.sh will copy the apps and the repo for this version
to the web server.

** Check List
- make.sh finishes without errors
- Check from direct build output, or later from
  https://ics-web.sns.ornl.gov/css/updates/apps:
  epics_css_*.zip and sns_css_*.zip unpack OK
  and run on Linux, OS X, Windows, 32 and 64 bit
  - Icon
  - Welcome page
  - Data Browser:
    SNS version can search for PVs "DTL_LLRF*Load"
    and plot some of them w/ historic data.
    Basic EPICS version can plot sim://sine
  - BOY:
    Install OPI Examples, run main.opi, edit main.opi
    
- Download an older version from web page.
  When started for the first time, it may prompt for
  an update to the latest version in the repository.
  Cancel that.
  Use menu Help, Install New Software..., Available Software Sites...
  to edit the SNS update site.
  Change it from
    http://ics-web.sns.ornl.gov/css/updates/
  to
    http://ics-web.sns.ornl.gov/css/updates/repo3.1.0
  meaning: Point it to _only_ the repo for the version just built.
  Now restart the product, and it should prompt for an update
  to the version just built.
  Updated product should be OK.
  
- Use update site to install "Optional SNS Control Room Tools"
  into SNS CSS product. Should then have alarm tools in menu.

Once all looks good, update the web site:
Html pages link to the current app ZIP files,
overall combined repo includes the latest repo.
  

** Example for manual build in IDE: Build Basic EPICS
Open org.csstudio.basic.epics.product/CSS.product.

Adjust versions of CSS.product and org.csstudio.basic.epics.product.

Export product:
Root dir: CSS_EPICS
Destination directory: /Kram/Eclipse/Workspace/org.csstudio.sns.updatesite/apps,
do not include source code,
check the "generate metadata" box,
export for Linux GTK/x86, macos x86, win32 x86.



* Setup on SNS Ctrls net
Download CSS Zip, or install from repository (see install.sh)

Use online update to add Optional SNS tools (alarm, MPS, ...)
 
- Add a startup file like below,
  soft-link it to /usr/local/bin/css
 
- Run once, enter the alarm RDB password via preference GUI
  
#!/bin/sh
#
# Start CSS
#
# kasemirk@ornl.gov

CSS=/usr/local/css/CSS_2.x.x
# Use the latest product (last by version number)
PROD=`ls -d $CSS/plugins/org.csstudio.sns.product_* | tail -1`
INI=$PROD/SNS_CCR.ini
OPTIONS=$PROD/debug_options.txt

LOGDIR=/usr/local/css/log/`hostname`
LOG=$LOGDIR/css.$$

# Limit PATH to minimum
export PATH="/usr/local/java/jdk1.6.0_21/bin:/usr/local/css/alarm_scripts:/bin:/usr/bin:/usr/local/bin"
# Don't 'inherid' any Java or LD_..PATH
export CLASSPATH=""
export LD_LIBRARY_PATH=""

# Assert there's a log dir
if [ ! -d $LOGDIR ]
then
   mkdir $LOGDIR
   chmod 777 $LOGDIR
fi

# Files created by CSS should be shareable
umask 0

# Allow core files
ulimit -c unlimited

# Run in logdir, pipe console to log file
(
cd $LOGDIR ;
$CSS/css -debug $OPTIONS -workspace_prompt $HOME/CSS-Workspaces/`hostname` -consoleLog -share_link /ade/css/Share -pluginCustomization $INI "$@" 2>&1
) >$LOG &

