package org.csstudio.diag.interconnectionServer;

/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton,
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

import java.io.IOException;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.csstudio.diag.interconnectionServer.server.IInterconnectionServer;
import org.csstudio.diag.interconnectionServer.server.IIocConnectionManager;
import org.csstudio.diag.interconnectionServer.server.ILdapServiceFacade;
import org.csstudio.diag.interconnectionServer.server.InterconnectionServer;
import org.csstudio.diag.interconnectionServer.server.IocConnectionManager;
import org.csstudio.diag.interconnectionServer.server.LdapServiceFacadeImpl;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.LdapServiceImplActivator;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceTracker;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {
    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
    
    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.diag.interconnectionServer";
    
    // The shared instance
    private static Activator INSTANCE;
    
    private LdapServiceTracker _ldapServiceTracker;
    
    private GenericServiceTracker<ISessionService> _genericServiceTracker;
    
    public Activator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }
    
    public static Activator getDefault() {
        return INSTANCE;
    }
    
    @Override
    public final void start(@Nullable final BundleContext context) throws Exception {
        super.start(context);
        
        LOG.info("ICS activator starts");
        
        setupLogging();
        setupLocalServices();
        
        //		final IIocConnectionReporter reporter = new IocConnectionReporter();
        //		final Dictionary<String, Object> props = new Hashtable<String, Object>();
        //		props.put("org.csstudio.management.remoteservice", Boolean.TRUE);
        //		context.registerService(IIocConnectionReporter.class.getName(),
        //				reporter, props);
        
        LdapServiceImplActivator.getDefault();
        
        _ldapServiceTracker = new LdapServiceTracker(context);
        _ldapServiceTracker.open();
        ServiceLocator.registerServiceTracker(ILdapService.class, _ldapServiceTracker);
        
        _genericServiceTracker = new GenericServiceTracker<ISessionService>(context, ISessionService.class);
        _genericServiceTracker.open();
    }
    
    private void setupLogging() {
        // We use SLF4J over Log4J configured with log4j.properties file
        // found by Log4j-Framework implicitly
        LOG.info("Initializing slf4j over log4j logging");
        // disable dal-internal configuration of logging (but, we do not sit on top of dal, do we?)
        System.setProperty("dal.logging", "false"); // dal.logging is defined as Plugs.PLUGS_LOGGING
        
        try {
            String filePath = getFilePath();
            PropertyConfigurator.configureAndWatch(filePath);
            LOG.info("Watching log4j properties in file " + filePath);
        } catch (IOException e) {
            LOG.error("Watching log4j properties failed, file could not be found");
        }
        SLF4JBridgeHandler.install();
        LOG.info("Installed bridge from java.util.logging to sl4j");
    }
    
    private void setupLocalServices() {
        ServiceLocator.registerService(ILdapServiceFacade.class, new LdapServiceFacadeImpl());
        LOG.info("Registered local service implementation for " + ILdapServiceFacade.class.getSimpleName());
        ServiceLocator.registerService(IIocConnectionManager.class, new IocConnectionManager());
        LOG.info("Registered local service implementation for " + IIocConnectionManager.class.getSimpleName());
        ServiceLocator.registerService(IInterconnectionServer.class, new InterconnectionServer());
        LOG.info("Registered local service implementation for " + IInterconnectionServer.class.getSimpleName());
    }
    
    @Nonnull
    private String getFilePath() throws IOException {
        // take log4j properties from the org.apache.log4j-fragment
        Bundle bundle = Platform.getBundle("org.csstudio.diag.interconnectionserver.log4j");
        Path path = new Path("log4j.properties");
        URL url = FileLocator.find(bundle, path, null);
        String result = FileLocator.toFileURL(url).getPath();
        return result;
    }
    
    @Override
    public final void stop(@Nullable final BundleContext context) throws Exception {
        _ldapServiceTracker.close();
        _genericServiceTracker.close();
        
        // terminate the log-properties-watch thread
        LOG.info("ICS activator stops, will shutdown the log manager");
        LogManager.shutdown();
        
        INSTANCE = null;
        super.stop(context);
    }
    
    @Nonnull
    public String getPluginId() {
        return PLUGIN_ID;
    }
    
    public void addSessionServiceListener(IGenericServiceListener<ISessionService> sessionServiceListener) {
        _genericServiceTracker.addServiceListener(sessionServiceListener);
    }
}
