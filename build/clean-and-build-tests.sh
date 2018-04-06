#!/bin/bash
#


echo ""
echo "===="
echo "==== JDK used: " $JAVA_HOME
echo "===="

START=$(date +%s)

# To start fresh, clean your local repository
# If you have accidentally invoked
#   mvn install
# or want to assert that you start over fresh,
# delete the Maven repository:
# rm -rf $HOME/.m2/repository
# rm -rf $HOME/.m2/repository/p2/bundle/osgi/org.csstudio.*
# rm -rf $HOME/.m2/repository/p2/bundle/osgi/org.diirt.*
rm -f ?_*.log

# To reduce maven verbosity
# MAVEN_OPTS = $MAVEN_OPTS -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
# MVNOPT="-P !cs-studio-sites,!eclipse-sites -B -DlocalArtifacts=ignore"
MVNOPT="-B -P css-settings"

echo ""
echo "===="
echo "==== BUILDING maven-osgi-bundles"
echo "===="
(time mvn $MVNOPT -f maven-osgi-bundles/pom.xml --settings maven/settings.xml clean verify) | tee 0_maven-osgi-bundles.log

echo ""
echo "===="
echo "==== BUILDING cs-studio-thirdparty"
echo "===="
(time mvn $MVNOPT -f cs-studio-thirdparty/pom.xml --settings maven/settings.xml clean verify) | tee 1_cs-studio-thirdparty.log

#echo ""
#echo "===="
#echo "==== BUILDING diirt"
#echo "===="
#(cd diirt; time mvn $MVNOPT --settings ../maven/settings.xml clean verify) | tee 2_diirt.log

echo ""
echo "===="
echo "==== BUILDING cs-studio/core"
echo "===="
(time mvn $MVNOPT -f cs-studio/core/pom.xml --settings maven/settings.xml clean verify) | tee 3_cs-studio-core.log

echo ""
echo "===="
echo "==== BUILDING cs-studio/applications"
echo "===="
(time mvn $MVNOPT -f cs-studio/applications/pom.xml --settings maven/settings.xml clean verify) | tee 4_cs-studio-applications.log

echo ""
echo "===="
echo "==== BUILDING org.csstudio.product"
echo "===="
(time mvn $MVNOPT -f org.csstudio.product/pom.xml --settings maven/settings.xml clean verify) | tee 6_org.csstudio.product.log

echo ""
tail ?_*.log
echo ""

# Displaying execution time
DUR=$(echo "$(date +%s) - $START" | bc)
MDUR=`expr $DUR / 60`; \
SDUR=`expr $DUR - 60 \* $MDUR`; \
echo "===="
echo "==== Building took $MDUR minutes and $SDUR seconds."
echo "===="
