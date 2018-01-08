/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.platform.libs.epics;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;

/** The main plugin class to be used in the desktop.
 *  @author Original author unknown
 *  @author Kay Kasemir
 *  @author Sergei Chevtsov
 */
@SuppressWarnings("nls")
public class EpicsPlugin extends Plugin
{
    public static final String ID = "org.csstudio.platform.libs.epics";

    /** Singleton instance */
    private static EpicsPlugin plugin;

    /** Use CAJ or JNI ? */
    private boolean use_pure_java;
    
	private boolean dbe_property_supported;
	private boolean honor_zero_precision;
	private boolean rtyp_value_only;
	private Boolean var_array_supported;

    /** How should subscriptions be established? */
    public enum MonitorMask
    {
        /** Listen to changes in value beyond 'MDEL' threshold or alarm state*/
        VALUE(1 | 4),

        /** Listen to changes in value beyond 'ADEL' archive limit */
        ARCHIVE(2),

        /** Listen to changes in alarm state */
        ALARM(4);

        final private int mask;

        private MonitorMask(final int mask)
        {
            this.mask = mask;
        }

        /** @return Mask bits used in underlying CA call */
        public int getMask()
        {
            return mask;
        }
    }

    private MonitorMask monitor_mask = MonitorMask.VALUE;

    /** The constructor. */
    public EpicsPlugin()
    {
        setPlugin(this);
    }

    /** @return Logger for plugin ID */
    private Logger getLogger()
    {
        return Logger.getLogger(ID);
    }

    /** Set static plugin via static function to avoid FindBugs warning
     *  about access to static var from non-static code
     */
    private static void setPlugin(final EpicsPlugin the_plugin)
    {
        EpicsPlugin.plugin = the_plugin;
    }

    /** @return <code>true</code> if preferences suggest the use
     *  of pure java CA.
     */
    public boolean usePureJava()
    {   return use_pure_java; }

    /** @return Mask used to create CA monitors (subscriptions) */
    public MonitorMask getMonitorMask()
    {   return monitor_mask; }
    
    /** @return whether metadata updates are enabled */
    public boolean isDbePropertySupported() {
		return dbe_property_supported;
	}

    /** @return whether zero precision in numeric metadata should be honored */
	public boolean isHonorZeroPrecision() {
		return honor_zero_precision;
	}

	/** @return whether one should request value only for RTYP fields */ 
	public boolean isRtypValueOnly() {
		return rtyp_value_only;
	}

	/** @return whether variable array should be supported */
	public Boolean getVarArraySupported() {
		return var_array_supported;
	}

	/** @return Returns the shared instance. */
    public static EpicsPlugin getDefault()
    {   return plugin;    }

    /** Set preferences, load JNI libraries as needed
     *  {@inheritDoc}
     */
    @Override
    public final void start(final BundleContext context) throws Exception {
        super.start(context);

        //If it is in rap, set server preference in lookup order.
        if(Platform.getBundle("org.eclipse.rap.ui") != null) //$NON-NLS-1$
        	Platform.getPreferencesService().setDefaultLookupOrder(ID, null, new String[] { //
				InstanceScope.SCOPE, //
				ConfigurationScope.SCOPE, //
				"server", //$NON-NLS-1$
				DefaultScope.SCOPE});
        
        installPreferences();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /** Update the CAJ settings with the data from the
     *  preference page.
     *  <p>
     *  Unfortunately this only takes effect after a restart,
     *  the current setup seems to remain unaffected.
     */
    public void installPreferences()
    {
        try
        {
            final IPreferencesService prefs = Platform.getPreferencesService();
            use_pure_java =
                prefs.getBoolean(ID, PreferenceConstants.PURE_JAVA, true, null);
            monitor_mask = MonitorMask.valueOf(
                prefs.getString(ID, PreferenceConstants.MONITOR, "VALUE", null));

        	dbe_property_supported =
                    prefs.getBoolean(ID, PreferenceConstants.DBE_PROPERTY_SUPPORTED, false, null);
        	honor_zero_precision = 
                    prefs.getBoolean(ID, PreferenceConstants.HONOR_ZERO_PRECISION, true, null);
        	rtyp_value_only =
        			prefs.getBoolean(ID, PreferenceConstants.RTYP_VALUE_ONLY, false, null);
        	String varArraySupported = 
        			prefs.getString(ID, PreferenceConstants.VAR_ARRAY_SUPPORT, "Auto", null);
        	var_array_supported = null;
        	if ("Enabled".equals(varArraySupported)) {
        		var_array_supported = true;
        	}
        	if ("Disabled".equals(varArraySupported)) {
        		var_array_supported = false;
        	}
            
            /*
             * selects common Executor in EPICSPlug (if true) for all PropertyProxyImpls or
             * (if false) individual Executors for every PropertyProxyImpl
             */
            setSystemProperty("EPICSPlug.property.use_common_executor", Boolean.toString(true));
            // sets the number of core threads in the selected Executor
            setSystemProperty("EPICSPlug.property.core_threads", Integer.toString(5));
            // sets the maximum number of threads in the selected Executor
            setSystemProperty("EPICSPlug.property.max_threads", Integer.toString(20));
            setSystemProperty("EPICSPlug.default_pendIO_timeout", Integer.toString(1));

            // Set the 'CAJ' copies of the settings
            setSystemProperty("com.cosylab.epics.caj.CAJContext.use_pure_java", Boolean.toString(use_pure_java));
            final String addr_list =
                prefs.getString(ID, PreferenceConstants.ADDR_LIST, null, null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.addr_list",
                              addr_list);

            final String auto_addr = Boolean.toString(
                prefs.getBoolean(ID, PreferenceConstants.AUTO_ADDR_LIST, true, null));
            setSystemProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
                              auto_addr);

            final String timeout =
                prefs.getString(ID, PreferenceConstants.TIMEOUT, "30.0", null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.connection_timeout",
                            timeout);

            final String beacon_period =
                prefs.getString(ID, PreferenceConstants.BEACON_PERIOD, "15.0", null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.beacon_period",
                            beacon_period);

            final String repeater_port =
                prefs.getString(ID, PreferenceConstants.REPEATER_PORT, "5065", null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.repeater_port",
                            repeater_port);

            final String server_port =
                prefs.getString(ID, PreferenceConstants.SERVER_PORT, "5064", null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.server_port",
                            server_port);

            final String max_array_bytes =
                prefs.getString(ID, PreferenceConstants.MAX_ARRAY_BYTES, "16384", null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes",
                            max_array_bytes);

        }
        catch (Exception ex)
        {
            getLogger().log(Level.SEVERE, "Preferences Error", ex);
        }
    }

    /** Sets property from preferences to System properties only if property
     *  value is not null or empty string.
     *  @param prop System property name
     *  @param value CSS preference name
     */
    private void setSystemProperty(final String prop, final String value)
    {
        if (value == null  ||  value.length()<=0)
            return;

        getLogger().log(Level.FINE, "{0} = {1}", new Object[] { prop, value });

        System.setProperty(prop, value);
    }
}
