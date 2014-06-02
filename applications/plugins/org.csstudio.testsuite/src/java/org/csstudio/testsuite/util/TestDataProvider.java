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
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;


import org.csstudio.domain.common.SiteId;
import org.csstudio.domain.common.resource.CssResourceLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;


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


    public static final TestDataProvider EMPTY_PROVIDER = new TestDataProvider();

    private static final Logger LOG = LoggerFactory.getLogger(TestDataProvider.class);

    private static final String HOST_PROPERTIES_FILE_NAME = "host.properties";
    private static final String HOST_PROPERTIES_ROOT_NS = "root";
    private static final String HOST_PROPERTIES_NS_SEP = ".";

    private static final String CONFIGURATION_FILE_SUFFIX = "TestConfiguration.ini";

    private static final String SENSITIVE_FILE_KEY = "sensitiveConfigFilePath";

    private static final String EMPTY_PROVIDER_PLUGIN = "<emptyProvider>";

    private static TestDataProvider INSTANCE;
    private static Properties PROPERTIES = new Properties();
    private final String _pluginId;

    /**
     * Constructor.
     */
    private TestDataProvider() {
        _pluginId = EMPTY_PROVIDER_PLUGIN;
    }

    /**
     * Constructor.
     */
    private TestDataProvider(final String pluginId) {
        _pluginId = pluginId;
    }

    private static void loadProperties(final String pluginId)
        throws IOException, BundleException {

        findAndLoadGeneralProperties(pluginId);
        findAndLoadGeneralSecretProperties(pluginId);
        findAndLoadHostSpecificProperties();
    }

    private static void findAndLoadGeneralProperties(final String pluginId)
                                                     throws IOException, BundleException {
        final String testConfigFileName = createSiteSpecificName();
        final URL resource = locateResource(pluginId, testConfigFileName);
        if (resource != null) {
            openStreamAndLoadProps(resource);
        }
    }

    private static void findAndLoadGeneralSecretProperties(final String pluginId)
                                                           throws IOException, BundleException {
        final String secretFile = findSensitiveDataFile();
        if (secretFile != null) {
            final URL resource = locateResource(pluginId, secretFile);
            if (resource != null) {
                openStreamAndLoadProps(resource);
            }
        }
    }

    private static void findAndLoadHostSpecificProperties() {
        try {
            final File configFile =
                CssResourceLocator.locateSiteSpecificResource(HOST_PROPERTIES_FILE_NAME);
            final URL resource = configFile.toURI().toURL();
            openStreamAndLoadProps(resource);
        } catch (final IOException e) {
            LOG.warn("Host specific properties file could not be found: ", e);
        }
    }

    private static void openStreamAndLoadProps(final URL resource) throws IOException {
        InputStream openStream = null;
        try {
            openStream =  resource.openStream();
            PROPERTIES.load(openStream);
        } finally {
            if (openStream != null) {
                openStream.close();
            }
        }
    }

    private static String findSensitiveDataFile() {

        if (PROPERTIES != null) {
            final String secretFilePath = (String) PROPERTIES.get(SENSITIVE_FILE_KEY);
            if (secretFilePath != null) {
                return secretFilePath;
            }
        }
        return null;
    }

    private static URL locateResource(final String pluginId,
                                      final String testConfigFileName) throws MalformedURLException,
                                                                                       BundleException {
        Bundle bundle = Platform.getBundle(pluginId);
        URL resource = null;
        if (bundle == null) {
            LOG.warn("Bundle could not be located. Try to find config file via current working dir.");

            final String curDir = System.getProperty("user.dir");
            final File configFile = new File(curDir + File.separator + testConfigFileName);
            resource = configFile.exists() ? configFile.toURI().toURL() : null;
        }  else {
            bundle = whenFragmentReturnHostBundle(bundle);
            resource = bundle.getResource(testConfigFileName);
        }

        if (resource == null) {
            LOG.warn("Test configuration file for plugin " + pluginId +
                     " and file name " + testConfigFileName +
                     " does not exist");
        }
        return resource;
    }

    public static Bundle whenFragmentReturnHostBundle(final Bundle bundle) throws BundleException {
        String host =
            (String) bundle.getHeaders().get(org.osgi.framework.Constants.FRAGMENT_HOST);
        if (!Strings.isNullOrEmpty(host)) {
            final String[] hostAndVersion = host.split(";");
            if (hostAndVersion.length > 0) {
                host = hostAndVersion[0];
            }
            if (!Strings.isNullOrEmpty(host)) {
                final Bundle hostBundle = Platform.getBundle(host);
                if (hostBundle == null) {
                    throw new BundleException("Host bundle for " + bundle.getSymbolicName() + " could not be found.");
                }
                return hostBundle;
            }
            throw new BundleException("Host bundle for " + bundle.getSymbolicName() + " could not be found.");
        }
        return bundle;
    }


    /**
     * Retrieve test config property from file
     * @param key
     * @return the property object
     */
    public Object get(final String key) {
        return PROPERTIES.get(key);
    }

    /**
     * Retrieve host specific property from file.
     * Searches for keys with namespaces from {@link InetAddress#getHostName()} or if empty for
     * {@link InetAddress#getHostAddress()} in host.properties file. If both are not present
     * the host property with namespace {@link HOST_PROPERTIES_ROOT_NS} is checked as default.
     *
     * @param key the describing key for the host specific property
     * @return the property object the property under namespace + "/" + key
     * @throws UnknownHostException
     */
    public Object getHostProperty(final String key) {

        InetAddress localHost;
        try {
            localHost = InetAddress.getLocalHost();
            Object object = PROPERTIES.get(localHost.getHostName() + HOST_PROPERTIES_NS_SEP + key);
            if (object == null) {
                object = PROPERTIES.get(localHost.getHostAddress() + HOST_PROPERTIES_NS_SEP + key);
            }
            if (object == null) {
                object = PROPERTIES.get(HOST_PROPERTIES_ROOT_NS + HOST_PROPERTIES_NS_SEP + key);
            }
            return object;
        } catch (final UnknownHostException e) {
            LOG.warn("Local host could not be resolved for host property retrieval.", e);
        }
        return null;
    }

    /**
     * Returns the lazily created instance of the test data provider.
     * @param pluginId id of the plugin in which the tests and their config file reside
     * @return the instance of the data provider
     * @throws TestProviderException
     */
    public static TestDataProvider getInstance(final String pluginId)
                                               throws TestProviderException {
        try {
            synchronized (TestDataProvider.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TestDataProvider(pluginId);
                    loadProperties(pluginId);
                } else if (!pluginId.equals(INSTANCE._pluginId)) {
                    PROPERTIES.clear();
                    loadProperties(pluginId);
                }
            }
            return INSTANCE;

        } catch (final IOException e) {
            throw new TestProviderException("Test config file for plugin " + pluginId + " couldn't be found or opened.", e);
        } catch (final BundleException e) {
            throw new TestProviderException("Bundle or host bundle for " + pluginId + " couldn't be found or opened.", e);
        }
    }

    private static String createSiteSpecificName() {

        final String siteProp = System.getProperty(SiteId.JVM_ARG_KEY);
        if (siteProp == null) {
            throw new IllegalArgumentException("There isn't any jvm arg -D" + SiteId.JVM_ARG_KEY + "=xxx configured. Please do so in your launch configuration.");
        }

        SiteId site;
        try {
            site = SiteId.valueOf(siteProp);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("The site for jvm arg -D" + SiteId.JVM_ARG_KEY + "="+ siteProp +" is not present in " + SiteId.class.getName(), e);
        }

        final String testConfigFileName = site.getPrefix() + CONFIGURATION_FILE_SUFFIX;
        return testConfigFileName;
    }
}
