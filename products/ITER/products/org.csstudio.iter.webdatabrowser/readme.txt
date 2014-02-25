## WebDataBrowser product
# webdatabrowser.warproduct.path file contains the path to webdatabrowser.warproduct

The main plugin of webdatabrowser is org.csstudio.trends.databrowser2.rap in applications/plugins/ directory.

To build webdatabrowser.war run the following command from build driectory:
./build_web.sh ITER ../products/ITER/products/org.csstudio.iter.webdatabrowser/webdatabrowser.warproduct.path

To deploy webdatabrowser, put webdatabrowser.war file in tomcat/webapps directory.
You should also create/modify a css_rap.ini in tomcat/conf directory with custom preferences.




