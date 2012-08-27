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
 */
package org.csstudio.alarm.dal2jms;

import java.io.IOException;
import java.net.URL;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.servicelocator.ServiceLocatorFactory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author bknerr
 * @since 02.06.2010
 */
public class Dal2JmsActivator extends Plugin {
    private static final Logger LOG = LoggerFactory.getLogger(Dal2JmsActivator.class);
    
    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.alarm.dal2jms";
    
    // The shared instance
    private static Dal2JmsActivator INSTANCE;
    
    private BundleContext _bundleContext;
    
    /**
     * The constructor
     */
    public Dal2JmsActivator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }
    
    @Nonnull
    public final String getPluginId() {
        return PLUGIN_ID;
    }
    
    @Nonnull
    public final BundleContext getBundleContext() {
        return _bundleContext;
    }
    
    @Override
    public final void start(@Nullable final BundleContext context) throws Exception {
        super.start(context);
        _bundleContext = context;
        LOG.info("Dal2Jms activator starts");
        
        setupShutdownHook();
        setupLogging();
        ServiceLocator.registerServiceTracker(ISessionService.class, ServiceLocatorFactory
                .createServiceTracker(context, ISessionService.class));
    }
    
    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread("Dal2Jms Shutdown") {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                LOG.info("Shutdown-Hook will execute stop now");
                StopCommand stopCommand = tryToGetStopCommand();
                if (stopCommand != null) {
                    stopCommand.execute(null);
                }
            }
        });
    }
    
    @CheckForNull
    protected StopCommand tryToGetStopCommand() {
        StopCommand result = null;
        IConfigurationElement[] config = null;
        try {
            config = Platform.getExtensionRegistry()
                    .getConfigurationElementsFor("org.csstudio.platform.managementCommands");
            for (IConfigurationElement e : config) {
                final Object o = e.createExecutableExtension("class");
                if (o instanceof StopCommand) {
                    result = (StopCommand) o;
                }
            }
        } catch (Exception e) {
            LOG.error("Execption while retrieving stop command", e);
            // error message is given here and not from caller because internal details may help
            if (config != null) {
                LOG.error("Could not find stop command. Found the following management commands registered: "
                        + config);
            }
        }
        
        return result;
    }
    
    private void setupLogging() {
        // We use SLF4J over Log4J configured with log4j.properties file
        // found by Log4j-Framework implicitly
        LOG.info("Initializing slf4j over log4j logging");
        // disable dal-internal configuration of logging
        System.setProperty("dal.logging", "false"); // dal.logging is defined as Plugs.PLUGS_LOGGING

        try {
            String filePath = getFilePath();
            PropertyConfigurator.configureAndWatch(filePath);
            LOG.info("Watching log4j properties in file " + filePath);
            SLF4JBridgeHandler.install();
            LOG.info("Installed bridge from java.util.logging to sl4j");
        } catch (IOException e) {
            LOG.error("Watching log4j properties failed, file could not be found");
        }
    }
    
    @Nonnull
    private String getFilePath() throws IOException {
        // take log4j properties from the org.apache.log4j-fragment
        Bundle bundle = Platform.getBundle("org.csstudio.alarm.dal2jms.log4j");
        Path path = new Path("log4j.properties");
        URL url = FileLocator.find(bundle, path, null);
        String result = FileLocator.toFileURL(url).getPath();
        return result;
        //        return "D:\\Data\\development\\repo\\cs-studio\\products\\DESY\\plugins\\org.csstudio.alarm.dal2jms.log4j\\log4j.properties";
    }
    
    @Override
    public final void stop(@Nullable final BundleContext context) throws Exception {
        // terminate the log-properties-watch thread
        LOG.info("Dal2Jms activator stops, will shutdown the log manager");
        LogManager.shutdown();
        
        INSTANCE = null;
        super.stop(context);
    }
    
    /**
     * @return the shared instance
     */
    @Nonnull
    public static Dal2JmsActivator getDefault() {
        return INSTANCE;
    }
    
}
