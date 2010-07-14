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
package org.csstudio.platform.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Test data provider that yields access to test configuration files that are
 * specific for different test sites (e.g. SNS, DESY, ITER).
 *
 * Specify your site in your eclipse launch configuration in the VM arguments list with e.g.:
 * -DsiteId=DESY
 *
 * In your plugin create an ini file, e.g. desyTestConfiguration.ini, whereas by convention the file
 * name prefix is the lowercase string of your SiteKey enum (DESY->desy) and the file name main part
 * is TestConfiguration.ini
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.07.2010
 */
public class TestDataProvider {
    private final Properties _props;


    /**
     * Constructor.
     * @param pluginId id of the plugin in which the tests and their config file reside
     * @param testConfigFileName name of the dedicated test config file
     * @throws IOException either file not found or stream couldn't be opened
     */
    public TestDataProvider(final String pluginId, final String testConfigFileName) throws IOException {

        // We'd really love to use annoations @Nonnull, @Nullable, @CheckForNull etc.
        if ((pluginId == null) || (testConfigFileName == null)) {
            throw new IllegalArgumentException("Parameters pluginId and testConfigFileName must not be null.");
        }
        InputStream openStream = null;
        try {
            final Bundle bundle = Platform.getBundle(pluginId);
            final URL resource = bundle.getResource(testConfigFileName);

            if (resource == null) {
                throw new FileNotFoundException("Test configuration file for plugin " + pluginId +
                                                " and file name " + testConfigFileName +
                " does not exist");
            }
            _props = new Properties();
            openStream =  resource.openStream();
            _props.load(openStream);
        } finally {
            if (openStream != null) {
                openStream.close();
            }
        }
    }

    /**
     * Retrieve test config property from file
     * @param key
     * @return
     */
    public Object get(final String key) {
        return _props.get(key);
    }

    public static TestDataProvider loadTestProperties(final String pluginId)
        throws IOException, IllegalArgumentException {

        SiteKey site;
        String siteProp = "";
        try {
            siteProp = System.getProperty("siteId");
            if (siteProp == null) {
                site = SiteKey.SNS; // TODO (bknerr) : contact all site's main responsible developers how to handle the default
            } else {
                site = SiteKey.valueOf(siteProp);
            }
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("The site enum type for vm arg -DsiteId="+ siteProp +" is unknown. ", e);
        }

        final String testConfigFileName = site.getPrefix() + "TestConfiguration.ini";
        return new TestDataProvider(pluginId, testConfigFileName);
    }
}
