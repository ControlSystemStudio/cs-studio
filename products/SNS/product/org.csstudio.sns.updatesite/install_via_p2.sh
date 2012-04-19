# Example of installing CSS from P2 repository
#
# Kay Kasemir

ECLIPSE=/Kram/Eclipse/3_7_2/rcp/eclipse
REPO=file:/Kram/build/buildRepo
DEST=/tmp/SNS_CSS
ARCH=-p2.os linux -p2.ws gtk -p2.arch x86 
#ARCH=-p2.os macosx -p2.ws cocoa -p2.arch x86


rm -rf $DEST

# -installIU: a comma separated list of IUs to install.
# Each entry in the list is in the form <id> [ '/' <version> ].
#
# Note that "org.csstudio.sns.product" would only install the
# product plugin itself with dependencies.
# org.csstudio.sns.product.product installs the complete product
# with launcher, features, ...
#
# Similar, the identifier of a feature has be suffixed with ".feature.group".
#
# The scan.ui.feature includes PyDev, which has a self-signed
# certificate, and the P2 command-line installer refuses to
# use it because it cannot prompt "Accept unknown ...?".
#
# Workaround:
# Download http://pydev.org/pydev_certificate.cer
# and install in JDK's cacerts
java -Declipse.p2.mirrors=false \
   -jar $ECLIPSE/plugins/org.eclipse.equinox.launcher_*.jar \
   -application org.eclipse.equinox.p2.director \
   -repository $REPO \
   -installIU org.csstudio.sns.product.product,org.csstudio.scan.ui.feature.feature.group \
   -tag InitialState \
   -destination $DEST \
   -profile SDKProfile \
   -profileProperties org.eclipse.update.install.features=true \
   -bundlepool $DEST \
   $ARCH \
   -roaming

