/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id$
 */
package org.csstudio.testsuite.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test data provider that yields access to test configuration files that are
 * specific for different test sites (e.g. SNS, DESY, ITER).
 *
 * Specify your site in your eclipse launch configuration in the VM arguments list with e.g.:
 * -DsiteId=DESY
 *
 * In your plugin create an ini file, e.g. desyTestConfiguration.ini, where the file
 * name prefix is the configured in the SiteId configuration and the file name main part
 * is TestConfiguration.ini
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.07.2010
 */
public final class TestDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TestDataProvider.class);
    
    private static final String CONFIGURATION_FILE_SUFFIX = "TestConfiguration.ini";

    private static final String SENSITIVE_FILE_KEY = "sensitiveConfigFilePath";

    public static final TestDataProvider EMPTY_PROVIDER = new TestDataProvider();

    private static TestDataProvider INSTANCE;

    private static Properties PROPERTIES = new Properties();

    private final String _pluginId;

    /**
     * Constructor.
     */
    private TestDataProvider() {
        _pluginId = "<emptyProvider>";
    }
    
    /**
     * Constructor.
     */
    private TestDataProvider(@Nonnull final String pluginId) {
        _pluginId = pluginId;
    }

    /**
     * @param pluginId
     * @param testConfigFileName
     * @throws IOException
     */
    private static void loadProperties(@Nonnull final String pluginId,
                                       @Nonnull final String testConfigFileName)
        throws IOException {

        openStreamAndLoadProps(pluginId, testConfigFileName);
        final String secretFile = findSensitiveDataFile();
        if (secretFile != null) {
            openStreamAndLoadProps(pluginId, secretFile);
        }
    }

    /**
     * @param pluginId
     * @param fileName
     * @return
     */
    @CheckForNull
    private static String findSensitiveDataFile() {

        if (PROPERTIES != null) {
            final String secretFilePath = (String) PROPERTIES.get(SENSITIVE_FILE_KEY);
            if (secretFilePath != null) {
                return secretFilePath;
            }
        }
        return null;
    }

    /**
     * @param pluginId
     * @param testConfigFileName
     * @throws IOException
     */
    private static void openStreamAndLoadProps(@Nonnull final String pluginId,
                                               @Nonnull final String testConfigFileName)
        throws IOException {

        InputStream openStream = null;
        try {
            final URL resource = locateResource(pluginId, testConfigFileName);
            openStream =  resource.openStream();
            PROPERTIES.load(openStream);
        } finally {
            if (openStream != null) {
                openStream.close();
            }
        }
    }

    /**
     * @param pluginId
     * @param testConfigFileName
     * @return
     * @throws MalformedURLException
     * @throws FileNotFoundException
     */
    @Nonnull
    private static URL locateResource(@Nonnull final String pluginId,
                                      @Nonnull final String testConfigFileName) throws MalformedURLException,
                                                                                       FileNotFoundException {
        final Bundle bundle = Platform.getBundle(pluginId);
        URL resource = null;
        if (bundle == null) {
            LOG.warn("Bundle could not be located. Try to find config file via current working dir.");

            final String curDir = System.getProperty("user.dir");
            final File configFile = new File(curDir + File.separator + testConfigFileName);
            resource = configFile.toURL();
        } else {
            resource = bundle.getResource(testConfigFileName);
        }

        if (resource == null) {
            throw new FileNotFoundException("Test configuration file for plugin " + pluginId +
                                            " and file name " + testConfigFileName +
            " does not exist");
        }
        return resource;
    }

    /**
     * Retrieve test config property from file
     * @param key
     * @return the property object
     */
    @CheckForNull
    public Object get(@Nonnull final String key) {
        return PROPERTIES.get(key);
    }

    /**

     * @param pluginId id of the plugin in which the tests and their config file reside
     * @return the instance of the data provider
     * @throws TestProviderException
     */
    @Nonnull
    public static TestDataProvider getInstance(@Nonnull final String pluginId)
        throws TestProviderException {

        String testConfigFileName = "";
        try {
            synchronized (TestDataProvider.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TestDataProvider(pluginId);
                    testConfigFileName = createSiteSpecificName();
                    loadProperties(pluginId, testConfigFileName);
                }
            }

            if (!INSTANCE._pluginId.equals(pluginId)) {
                TestDataProvider.PROPERTIES.clear();
                testConfigFileName = createSiteSpecificName();
                loadProperties(pluginId, testConfigFileName);
            }
            return INSTANCE;

        } catch (final IOException e) {
            throw new TestProviderException("Test config file " + testConfigFileName + " couldn't be found or opened.", e);
        }
    }

    @Nonnull
    private static String createSiteSpecificName() {

        final String siteProp = System.getProperty("siteId");
        if (siteProp == null) {
            throw new IllegalArgumentException("There isn't any jvm arg -DsiteId=xxx configured. Please do so in your launch configuration.");
        }

        SiteId site;
        try {
            site = SiteId.valueOf(siteProp);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("The site enum type for jvm arg -DsiteId="+ siteProp +" is unknown. ", e);
        }

        final String testConfigFileName = site.getPrefix() + CONFIGURATION_FILE_SUFFIX;
        return testConfigFileName;
    }
}
