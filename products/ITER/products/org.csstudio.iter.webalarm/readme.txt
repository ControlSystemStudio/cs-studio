## WebAlarm product
# webalarm.warproduct.path file contains the path to webalarm.warproduct

The main plugin of webalarm is org.csstudio.alarm.beast.ui.rap in applications/plugins/ directory.

To build webalarm.war run the following command from build directory:
./build_web.sh ITER ../products/ITER/products/org.csstudio.iter.webalarm/webalarm.warproduct.path

To deploy webalarm, put webalarm.war file in tomcat/webapps directory.
You should also create/modify a css_rap.ini in tomcat/conf directory with custom preferences.

