## WebOPI product
# webopi.warproduct.path file contains the path to webopi.warproduct

The main plugin of webopi is org.csstudio.opibuilder.rap in applications/plugins/ directory.

To build webopi.war run the following command from build directory:
./build_web.sh ITER ../products/ITER/products/org.csstudio.iter.webopi/webopi.warproduct.path

To deploy webopi, put webopi.war file in tomcat/webapps directory.
You should also create/modify a css_rap.ini in tomcat/conf directory with custom preferences.

