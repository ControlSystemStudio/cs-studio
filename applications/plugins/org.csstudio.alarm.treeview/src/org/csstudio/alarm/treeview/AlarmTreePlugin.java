/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.treeview;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceTracker;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class of the LdapTree-Plug-In. This manages the plug-in's lifecycle.
 *
 * @author Joerg Rathlev
 */
public final class AlarmTreePlugin extends AbstractCssUiPlugin {

    /**
     * The plug-in id.
     */
    public static final String PLUGIN_ID = "org.csstudio.alarm.treeview";

    /**
     * The service tracker reference for the LDAP service.
     */
    private ServiceTracker _ldapServiceTracker;

    /**
     * The alarm service
     */
    private IAlarmService _alarmService;

    /**
     * The alarm configuration service
     */
    private IAlarmConfigurationService _alarmConfigurationService;


    private static AlarmTreePlugin INSTANCE;


    /**
     * Returns the shared instance.
     *
     * @return the shared instance.
     */
    @Nonnull
    public static AlarmTreePlugin getDefault() {
        return INSTANCE;
    }

    /**
     * Don't instantiate.
     * Called by framework.
     */
    public AlarmTreePlugin() {
        if (INSTANCE != null) {
            throw new IllegalStateException("TreeModelActivator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart(@Nonnull final BundleContext context) throws Exception {

        _alarmService = getService(context, IAlarmService.class);
        _alarmConfigurationService = getService(context, IAlarmConfigurationService.class);

        _ldapServiceTracker = new LdapServiceTracker(context);
        _ldapServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop(@Nonnull final BundleContext context) throws Exception {
        _ldapServiceTracker.close();
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     *
     * @param path
     *            the path
     * @return the image descriptor
     */
    @CheckForNull
    public static ImageDescriptor getImageDescriptor(@Nonnull final String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * @return this plug-in's id.
     */
    @Override
    @Nonnull
    public String getPluginId() {
        return PLUGIN_ID;
    }

    /**
     * @return the alarm service or null
     */
    @CheckForNull
    public IAlarmService getAlarmService() {
        return _alarmService;
    }

    /**
     * @return the alarm configuration service or null
     */
    @CheckForNull
    public IAlarmConfigurationService getAlarmConfigurationService() {
        return _alarmConfigurationService;
    }

    /**
     * @return the LDAP service or null
     */
    @CheckForNull
    public ILdapService getLdapService() {
        return (ILdapService) _ldapServiceTracker.getService();
    }
}
