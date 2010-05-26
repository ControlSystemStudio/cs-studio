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

import java.util.Dictionary;
import java.util.Hashtable;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.service.internal.AlarmConfigurationServiceImpl;
import org.csstudio.alarm.service.internal.AlarmServiceDALImpl;
import org.csstudio.alarm.service.internal.AlarmServiceJMSImpl;
import org.csstudio.alarm.service.preferences.AlarmServicePreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.csstudio.utility.ldap.service.ILdapService;
import org.osgi.framework.BundleContext;

/**
 * The activator decides which implementation is used for the alarm service.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 26.04.2010
 */
public class AlarmServiceActivator extends AbstractCssUiPlugin {
    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(AlarmServiceActivator.class);

    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.alarm.service"; //$NON-NLS-1$

    // The shared instance.
    private static AlarmServiceActivator plugin;

    /**
     * The constructor.
     */
    public AlarmServiceActivator() {
        if (plugin != null) {
            throw new IllegalStateException("Attempt to call plugin constructor more than once.");
        }
        plugin = this;
    }

    /**
     * Returns the shared instance.
     */
    public static AlarmServiceActivator getDefault() {
        return plugin;
    }

    @Override
    protected void doStart(final BundleContext context) throws Exception {
        LOG.debug("Starting AlarmService");

        registerAlarmConfigurationService(context, getService(context, ILdapService.class));

        // Provide implementation for alarm service
        boolean isDAL = getPreferenceStore()
                .getBoolean(AlarmServicePreferenceConstants.ALARMSERVICE_DAL);

        if (isDAL) {
            registerDALService(context, getService(context, IAlarmConfigurationService.class));
        } else {
            registerJMSService(context);
        }
    }

    @Override
    protected void doStop(final BundleContext context) throws Exception {
        LOG.debug("Stopping AlarmService");
        plugin = null;
    }

    /**
     * @param context
     * @param iLdapService
     */
    private void registerAlarmConfigurationService(@Nonnull final BundleContext context,
                                                   @Nonnull final ILdapService ldapService) {
        final Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("service.vendor", "DESY");
        properties.put("service.description", "Alarm configuration service implementation.");

        context.registerService(IAlarmConfigurationService.class.getName(),
                                new AlarmConfigurationServiceImpl(ldapService),
                                properties);

    }

    private void registerJMSService(@Nonnull final BundleContext context) {
        LOG.debug("Registering JMS implementation for the alarm service");

        final Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("service.vendor", "DESY");
        properties.put("service.description", "JMS implementation of the alarm service");

        context.registerService(IAlarmService.class.getName(),
                                new AlarmServiceJMSImpl(),
                                properties);
    }

    private void registerDALService(@Nonnull final BundleContext context,
                                    @Nonnull final IAlarmConfigurationService alarmConfigService) {
        LOG.debug("Registering DAL implementation for the alarm service");

        final Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("service.vendor", "DESY");
        properties.put("service.description", "DAL implementation of the alarm service");

        context.registerService(IAlarmService.class.getName(),
                                new AlarmServiceDALImpl(alarmConfigService),
                                properties);
    }

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

}
