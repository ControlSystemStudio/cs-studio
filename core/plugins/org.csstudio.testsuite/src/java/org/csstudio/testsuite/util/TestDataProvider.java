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
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
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

    private static final String CONFIGURATION_REPO_PATH_KEY = "configRepoPath";


    private static final Logger LOG = LoggerFactory.getLogger(TestDataProvider.class);
    
 
    private static final String CONFIGURATION_FILE_SUFFIX = "TestConfiguration.ini";

    private static final String SENSITIVE_FILE_KEY = "sensitiveConfigFilePath";
    private static final String HOST_PROPERTIES_FILE_NAME = "host.properties";

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

    private static void loadProperties(@Nonnull final String pluginId)
        throws IOException {

        findAndLoadGeneralProperties(pluginId);
        
        findAndLoadGeneralSecretProperties(pluginId);
        
        findAndLoadHostSpecificProperties();
    }

    private static void findAndLoadGeneralProperties(@Nonnull final String pluginId) 
                                                     throws IOException {
        String testConfigFileName = createSiteSpecificName();
        final URL resource = locateResource(pluginId, testConfigFileName);
        openStreamAndLoadProps(resource);
    }

    private static void findAndLoadGeneralSecretProperties(@Nonnull final String pluginId) 
                                                           throws IOException {
        final String secretFile = findSensitiveDataFile();
        if (secretFile != null) {
            final URL resource = locateResource(pluginId, secretFile);
            openStreamAndLoadProps(resource);
        }
    }
    
    private static void findAndLoadHostSpecificProperties() throws IOException {
        final String configRepoPath = System.getProperty(CONFIGURATION_REPO_PATH_KEY);
        final String siteId = System.getProperty(SiteId.JVM_ARG_KEY);
        if (!Strings.isNullOrEmpty(configRepoPath) && !Strings.isNullOrEmpty(siteId)) {
            final String pathname = configRepoPath + File.separator + siteId + File.separator + HOST_PROPERTIES_FILE_NAME;
            try {
                
                final File configFile = new File(pathname);
                URL resource = configFile.toURI().toURL();
                openStreamAndLoadProps(resource);
            } catch (final IOException e) {
                LOG.warn("Host specific properties file could not be found: " + pathname);
            }
        } else {
            LOG.warn("No " + CONFIGURATION_REPO_PATH_KEY + " or " + 
                     SiteId.JVM_ARG_KEY + " specified as current jre's jvm arg.");
        }
        
    }

    private static void openStreamAndLoadProps(@Nonnull final URL resource) throws IOException {
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
            resource = configFile.toURI().toURL();
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
     * Retrieve host specific property from file.
     * Searches for keys with namespaces from {@link InetAddress#getHostName()} or if empty for  
     * {@link InetAddress#getHostAddress()} in host.properties file.
     * 
     * @param key the describing key for the host specific property
     * @return the property object the property under namespace + "/" + key
     * @throws UnknownHostException 
     */
    @CheckForNull
    public Object getHostProperty(@Nonnull final String key) {
        
        InetAddress localHost;
        try {
            localHost = InetAddress.getLocalHost();
            Object object = PROPERTIES.get(localHost.getHostName() + "/" + key);
            if (object == null) {
                object = PROPERTIES.get(localHost.getHostAddress() + "/" + key);
            }
            return object;
        } catch (UnknownHostException e) {
            LOG.warn("Local host could not be resolved for host property retrieval.", e);
        }
        return null;
    }

    /**
     *
     * @param pluginId id of the plugin in which the tests and their config file reside
     * @return the instance of the data provider
     * @throws TestProviderException
     */
    @Nonnull
    public static TestDataProvider getInstance(@Nonnull final String pluginId)
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
        }
    }

    @Nonnull
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
