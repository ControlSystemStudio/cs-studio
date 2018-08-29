/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.jca;

import static org.csstudio.vtype.pv.PV.logger;

import java.util.logging.Level;

import org.csstudio.diirt.util.core.preferences.DIIRTPreferences;
import org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.MonitorMask;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.VariableArraySupport;
import org.csstudio.vtype.pv.PVPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preferences for JCA
 *
 *  <p>Based on code that was in the org.csstudio.platform.libs.epics.EpicsPlugin,
 *  Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton.
 *  When checking its license link, HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM,
 *  on 2016-08-18, it listed http://www.eclipse.org/org/documents/epl-v10.php,
 *
 *  @author Original author unknown
 *  @author Sergei Chevtsov - Contributed to EpicsPlugin
 *  @author Gabriele Carcassi - Contributed to EpicsPlugin
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JCA_Preferences
{
    private static final JCA_Preferences instance = new JCA_Preferences();

    /** Use CAJ or JNI ? */
    private boolean use_pure_java = true;

    private int monitor_mask = 5; // VALUE | ALARM

    private boolean dbe_property_supported = false;

    private VariableArraySupport var_array_supported = VariableArraySupport.FALSE;

    private int large_array_threshold = 100000;

    /** Initialize */
    private JCA_Preferences()
    {
        installPreferences();
        if (! use_pure_java)
            loadJCA();
    }

    /** Update the JCA/CAJ related properties from preferences */
    public void installPreferences()
    {
        try
        {
            final IPreferencesService prefs = Platform.getPreferencesService();
            if (prefs == null)
                return;

            final DIIRTPreferences dp = DIIRTPreferences.get();

            //  JCA no more available since DIIRT 3.1.7 ---------------------------
            use_pure_java = true;    //  dp.getBoolean(ChannelAccess.PREF_PURE_JAVA);
            //  -------------------------------------------------------------------

            final DataSourceOptions.MonitorMask mask = DataSourceOptions.MonitorMask.fromString(dp.getString(ChannelAccess.PREF_MONITOR_MASK));
            if (mask == MonitorMask.CUSTOM)
                monitor_mask = dp.getInteger(ChannelAccess.PREF_CUSTOM_MASK);
            else
                monitor_mask = mask.mask();

            dbe_property_supported = dp.getBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED);

            var_array_supported = VariableArraySupport.fromString(dp.getString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY));

            large_array_threshold = prefs.getInt(PVPlugin.ID, "large_array_threshold", large_array_threshold, null);

            // Set the 'CAJ' and 'JNI' copies of the settings
            setSystemProperty("com.cosylab.epics.caj.CAJContext.use_pure_java", Boolean.toString(use_pure_java));
            final String addr_list = dp.getString(ChannelAccess.PREF_ADDR_LIST);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.addr_list", addr_list);
            setSystemProperty("gov.aps.jca.jni.JNIContext.addr_list", addr_list);

            final String auto_addr = dp.getString(ChannelAccess.PREF_AUTO_ADDR_LIST);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", auto_addr);
            setSystemProperty("gov.aps.jca.jni.JNIContext.auto_addr_list", auto_addr);

            final String timeout = dp.getString(ChannelAccess.PREF_CONNECTION_TIMEOUT);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.connection_timeout", timeout);
            setSystemProperty("gov.aps.jca.jni.JNIContext.connection_timeout", timeout);

            final String beacon_period = dp.getString(ChannelAccess.PREF_BEACON_PERIOD);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.beacon_period", beacon_period);
            setSystemProperty("gov.aps.jca.jni.JNIContext.beacon_period", beacon_period);

            final String repeater_port = dp.getString(ChannelAccess.PREF_REPEATER_PORT);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.repeater_port", repeater_port);
            setSystemProperty("gov.aps.jca.jni.JNIContext.repeater_port", repeater_port);

            final String server_port = dp.getString(ChannelAccess.PREF_SERVER_PORT);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.server_port", server_port);
            setSystemProperty("gov.aps.jca.jni.JNIContext.server_port", server_port);

            final String max_array_bytes = dp.getString(ChannelAccess.PREF_MAX_ARRAY_SIZE);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", max_array_bytes);
            setSystemProperty("gov.aps.jca.jni.JNIContext.max_array_bytes", max_array_bytes);

            // gov.aps.jca.event.QueuedEventDispatcher avoids
            // deadlocks when calling JCA while receiving JCA callbacks.
            // But JCA_PV avoids deadlocks, and QueuedEventDispatcher is faster
            setSystemProperty("gov.aps.jca.jni.ThreadSafeContext.event_dispatcher",
                              "gov.aps.jca.event.DirectEventDispatcher");
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Preferences Error", ex);
        }
    }

    /** Sets property from preferences to System properties only if property
     *  value is not null or empty string.
     *  @param prop System property name
     *  @param value CSS preference name
     */
    private void setSystemProperty(final String prop, final String value)
    {
        if (value == null  ||  value.isEmpty())
            return;

        logger.log(Level.FINE, "{0} = {1}", new Object[] { prop, value });

        System.setProperty(prop, value);
    }

    // Get target arch as used to be done in JCA's JNITargetArch
    private String getTargetArch()
    {
        final String osname = System.getProperty("os.name", "");
        String osarch = System.getProperty("os.arch", "");

        if (osarch.equals("i386") ||
            osarch.equals("i486") ||
            osarch.equals("i586"))
            osarch = "x86";

        if (osname.equals("Mac OS X"))
            return "darwin-x86";
        else if (osname.equals("Linux"))
        {
            if (osarch.equals("x86"))
                return "linux-x86";
            else if (osarch.equals("x86_64") || osarch.equals("amd64"))
                return "linux-x86_64";
        }
        else if (osname.startsWith("Win"))
            return "win32-x86";
        return "unknown";
    }

    private void loadJCA()
    {
        final String jni_target = getTargetArch();
        // this property must be unset, because JCA might mistakenly use it
        final String path = "gov.aps.jca.jni.epics.".concat(jni_target).concat(".library.path");
        System.setProperty(path, "");

        // In case we have a dependency to Com and ca,
        // try to load those.
        // (On Windows, Com.dll and ca.dll need to be loaded
        //  before loading jca.dll)
        // Strictly speaking, loadLibray() might only apply to
        // real JNI libs, and not ordinary shared libs.
        // So the preferred method is to build a JCA JNILIB
        // without further dependencies, in which case it's
        // OK for the following two calls to fail:
        Throwable com_ca_exception = null;
        try
        {
            System.loadLibrary("Com");
            System.loadLibrary("ca");
        }
        catch (Throwable ex)
        {
            // Remember the error because it might explain a follow-up
            // jca load error.
            // On the other hand, if jca loads OK, we can ignore this one.
            com_ca_exception = ex;
        }
        // Load the JCA library.
        // This better works out OK.
        try
        {
            System.loadLibrary("jca");
        }
        catch (Throwable ex)
        {
            if (com_ca_exception != null)
                logger.log(Level.CONFIG, "Cannot load Com and ca libraries. " +
                                         "Could be a problem if JCA binary depends on them", com_ca_exception);
            // This is an error for sure:
            logger.log(Level.SEVERE, "Cannot load JCA binary", ex);
        }
    }

    /** @return Singleton instance */
    public static JCA_Preferences getInstance()
    {
        return instance;
    }

    /** @return <code>true</code> if preferences suggest the use
     *  of pure java CA.
     */
    public boolean usePureJava()
    {
        return use_pure_java;
    }

    /** @return Mask used to create CA monitors (subscriptions) */
    public int getMonitorMask()
    {
        return monitor_mask;
    }

    /** @return whether metadata updates are enabled */
    public boolean isDbePropertySupported()
    {
        return dbe_property_supported;
    }

    /** @return whether variable array should be supported (true/false), or auto-detect */
    public VariableArraySupport isVarArraySupported()
    {
        return var_array_supported;
    }

    public int largeArrayThreshold()
    {
        return large_array_threshold;
    }
}
