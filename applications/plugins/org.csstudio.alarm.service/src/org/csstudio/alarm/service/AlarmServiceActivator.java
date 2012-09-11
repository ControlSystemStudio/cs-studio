/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: AlarmServiceActivator.java,v 1.2
 * 2010/04/26 09:35:22 jpenning Exp $
 */
package org.csstudio.alarm.service;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAcknowledgeService;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.service.declaration.IRemoteAcknowledgeService;
import org.csstudio.alarm.service.declaration.TimeService;
import org.csstudio.alarm.service.internal.AcknowledgeServiceImpl;
import org.csstudio.alarm.service.internal.AlarmConfigurationServiceImpl;
import org.csstudio.alarm.service.internal.AlarmServiceDalImpl;
import org.csstudio.alarm.service.internal.AlarmServiceJmsImpl;
import org.csstudio.servicelocator.ServiceLocatorFactory;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator decides which implementation is used for the alarm service.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 26.04.2010
 */
public class AlarmServiceActivator extends AbstractUIPlugin {
	private static final Logger LOG = LoggerFactory.getLogger(AlarmServiceActivator.class);    
    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.alarm.service"; //$NON-NLS-1$
    
    // The shared instance.
    private static AlarmServiceActivator PLUGIN;
    
    /**
     * The constructor.
     */
    public AlarmServiceActivator() {
        if (PLUGIN != null) {
            throw new IllegalStateException("Attempt to call plugin constructor more than once.");
        }
        PLUGIN = this;
    }
    
    /**
     * Returns the shared instance.
     */
    @Nonnull
    public static AlarmServiceActivator getDefault() {
        return PLUGIN;
    }
    
    @Override
    public void start(@Nullable final BundleContext context) throws Exception {
    	super.start(context);
        LOG.debug("Starting AlarmService");
        
        if (context == null) {
            throw new IllegalArgumentException("Bundle context is null in doStart method.");
        }
        
        registerAlarmConfigurationService(context);
        registerAcknowledgeService(context);
        
        // Provide implementation for alarm service
        final boolean isDAL = AlarmPreference.ALARMSERVICE_IS_DAL_IMPL.getValue();
        if (isDAL) {
            registerDALService(context);
        } else {
            registerJMSService(context);
        }
    }
    
    @Override
    public void stop(@Nullable final BundleContext context) throws Exception {
    	super.stop(context);
        LOG.debug("Stopping AlarmService");
        PLUGIN = null;
    }
    
    private void registerAcknowledgeService(@Nonnull final BundleContext context) throws RemoteException,
                                                                                 NotBoundException {
        if (AlarmPreference.ALARMSERVICE_RUNS_AS_SERVER.getValue()) {
            LOG.debug("Registering acknowledge service implementation");
            AcknowledgeServiceImpl ackService = new AcknowledgeServiceImpl(new TimeService());
            ServiceLocatorFactory.registerServiceWithTracker("Acknowledge service implementation.",
                                                             context,
                                                             IRemoteAcknowledgeService.class,
                                                             ackService);
            ServiceLocatorFactory.registerServiceWithTracker("Acknowledge connection service implementation.",
                                                             context,
                                                             IAcknowledgeService.class,
                                                             ackService);
        } else if (AlarmPreference.ALARMSERVICE_LISTENS_TO_ALARMSERVER.getValue()) {
            LOG.debug("Registering remote acknowledge service implementation");
            ServiceLocatorFactory.registerRemoteService("Acknowledge service implementation",
                                           AlarmPreference.ALARMSERVICE_RMI_REGISTRY_SERVER
                                                   .getValue(),
                                           AlarmPreference.ALARMSERVICE_RMI_REGISTRY_PORT
                                                   .getValue(),
                                                   IRemoteAcknowledgeService.class);
        }
    }
    
    private void registerAlarmConfigurationService(@Nonnull final BundleContext context) {
        LOG.debug("Registering Alarm configuration service implementation");
        
        ServiceLocatorFactory
                .registerServiceWithTracker("Alarm configuration service implementation.",
                                            context,
                                            IAlarmConfigurationService.class,
                                            new AlarmConfigurationServiceImpl());
    }
    
    private void registerJMSService(@Nonnull final BundleContext context) {
        LOG.debug("Registering JMS implementation for the alarm service");
        
        ServiceLocatorFactory
                .registerServiceWithTracker("JMS implementation of the alarm service.",
                                            context,
                                            IAlarmService.class,
                                            new AlarmServiceJmsImpl());
    }
    
    private void registerDALService(@Nonnull final BundleContext context) {
        LOG.debug("Registering DAL implementation for the alarm service");
        
        ServiceLocatorFactory
                .registerServiceWithTracker("DAL implementation of the alarm service.",
                                            context,
                                            IAlarmService.class,
                                            new AlarmServiceDalImpl());
    }
}
