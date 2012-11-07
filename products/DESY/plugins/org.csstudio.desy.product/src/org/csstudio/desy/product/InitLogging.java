/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchroton,
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
 */
package org.csstudio.desy.product;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Logging is configured here: Install watch for log4j configuration file, install bridge for jul. 
 * 
 * @author jpenning
 * @since 21.03.2012
 */
public class InitLogging  {

    private static final Logger LOG = LoggerFactory.getLogger(InitLogging.class);

    public static void setupLogging() {
        // We use SLF4J over Log4J configured with log4j.properties file
        // found by Log4j-Framework implicitly
        LOG.info("Initializing slf4j over log4j logging");
        // disable dal-internal configuration of logging
        System.setProperty("dal.logging", "false"); // dal.logging is defined as Plugs.PLUGS_LOGGING
        
        try {
            String filePath = getFilePath();
            PropertyConfigurator.configureAndWatch(filePath);
            LOG.info("Watching log4j properties in file " + filePath);
        } catch (IOException e) {
            LOG.error("Watching log4j properties failed, file could not be found");
        }
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getLogger("").setLevel(Level.ALL);
        LOG.info("Installed bridge from java.util.logging to sl4j");
    }
    
    @Nonnull
    private static String getFilePath() throws IOException {
        // actually the log4j properties are found inside the org.apache.log4j fragment
        // take care that there is exactly one fragment with log4j properties defined in the launcher
        Bundle bundle = Platform.getBundle("org.apache.log4j");
        Path path = new Path("log4j.properties");
        URL url = FileLocator.find(bundle, path, null);
        String result = FileLocator.toFileURL(url).getPath();
        return result;
    }
    

}
