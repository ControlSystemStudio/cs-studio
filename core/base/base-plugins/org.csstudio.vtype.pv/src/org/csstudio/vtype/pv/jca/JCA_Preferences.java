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

import org.csstudio.vtype.pv.internal.Activator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;

import gov.aps.jca.jni.JNITargetArch;

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

    /** Use pure java or not? Values "true", "false" */
    final public static String PURE_JAVA = "use_pure_java";

    /** How to monitor (subscribe): Values "VALUE", "ARCHIVE", "ALARM" */
    final public static String MONITOR = "monitor";

    /** List of IP addresses, separated by space */
    final public static String ADDR_LIST = "addr_list";

    /** Add automatic IP entries? Values "true", "false" */
    final public static String AUTO_ADDR_LIST = "auto_addr_list";

    /** Should we use metadata update? Values "true", "false" */
    final public static String DBE_PROPERTY_SUPPORTED = "dbe_property_supported";

    /** Should we enable variable array support? Values "Auto", "Enabled", "Disabled" */
    final public static String VAR_ARRAY_SUPPORT = "var_array_support";

    // See Channel Access docu for rest
    final public static String TIMEOUT = "conn_tmo";
    final public static String BEACON_PERIOD = "beacon_period";
    final public static String REPEATER_PORT = "repeater_port";
    final public static String SERVER_PORT = "server_port";
    final public static String MAX_ARRAY_BYTES = "max_array_bytes";




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

    private boolean dbe_property_supported = false;

    private Boolean var_array_supported = Boolean.TRUE;

    private int large_array_threshold = 100000;

    /** Use CAJ or JNI ? */
    private boolean use_pure_java = true;

    /** Initialize */
    private JCA_Preferences()
    {
        //If it is in rap, set server preference in lookup order.
        if(Platform.getBundle("org.eclipse.rap.ui") != null)
            Platform.getPreferencesService().setDefaultLookupOrder(Activator.ID, null,
                new String[]
                {
                    InstanceScope.SCOPE,
                    ConfigurationScope.SCOPE,
                    "server",
                    DefaultScope.SCOPE
                });

        installPreferences();
        if (! use_pure_java)
            loadJCA();
    }

    /** Update the JCA/CAJ related properties from preferences */
    private void installPreferences()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return;
        try
        {
            use_pure_java = prefs.getBoolean(Activator.ID, PURE_JAVA, use_pure_java, null);
            monitor_mask = MonitorMask.valueOf(prefs.getString(Activator.ID, MONITOR, monitor_mask.name(), null));
            dbe_property_supported = prefs.getBoolean(Activator.ID, DBE_PROPERTY_SUPPORTED, dbe_property_supported, null);

            String var_array_config = prefs.getString(Activator.ID, VAR_ARRAY_SUPPORT, "Auto", null);
            var_array_supported = null;
            if ("enabled".equalsIgnoreCase(var_array_config)  || "true".equalsIgnoreCase(var_array_config))
                var_array_supported = true;
            if ("disabled".equalsIgnoreCase(var_array_config) || "false".equalsIgnoreCase(var_array_config))
                var_array_supported = false;

            large_array_threshold = prefs.getInt(Activator.ID, "large_array_threshold", large_array_threshold, null);

            // Set the 'CAJ' and 'JNI' copies of the settings
            setSystemProperty("com.cosylab.epics.caj.CAJContext.use_pure_java", Boolean.toString(use_pure_java));
            final String addr_list = prefs.getString(Activator.ID, ADDR_LIST, null, null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.addr_list", addr_list);
            setSystemProperty("gov.aps.jca.jni.JNIContext.addr_list", addr_list);

            final String auto_addr = Boolean.toString(prefs.getBoolean(Activator.ID, AUTO_ADDR_LIST, true, null));
            setSystemProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", auto_addr);
            setSystemProperty("gov.aps.jca.jni.JNIContext.auto_addr_list", auto_addr);

            final String timeout = prefs.getString(Activator.ID, TIMEOUT, "30.0", null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.connection_timeout", timeout);
            setSystemProperty("gov.aps.jca.jni.JNIContext.connection_timeout", timeout);

            final String beacon_period = prefs.getString(Activator.ID, BEACON_PERIOD, "15.0", null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.beacon_period", beacon_period);
            setSystemProperty("gov.aps.jca.jni.JNIContext.beacon_period", beacon_period);

            final String repeater_port = prefs.getString(Activator.ID, REPEATER_PORT, "5065", null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.repeater_port", repeater_port);
            setSystemProperty("gov.aps.jca.jni.JNIContext.repeater_port", repeater_port);

            final String server_port = prefs.getString(Activator.ID, SERVER_PORT, "5064", null);
            setSystemProperty("com.cosylab.epics.caj.CAJContext.server_port", server_port);
            setSystemProperty("gov.aps.jca.jni.JNIContext.server_port", server_port);

            final String max_array_bytes = prefs.getString(Activator.ID, MAX_ARRAY_BYTES, "16384", null);
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

    private void loadJCA()
    {
        final String jni_target = JNITargetArch.getTargetArch();
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
    public MonitorMask getMonitorMask()
    {
        return monitor_mask;
    }

    /** @return whether metadata updates are enabled */
    public boolean isDbePropertySupported()
    {
        return dbe_property_supported;
    }

    /** @return whether variable array should be supported (true/false), or auto-detect (null) */
    public Boolean isVarArraySupported()
    {
        return var_array_supported;
    }

    public int largeArrayThreshold()
    {
        return large_array_threshold;
    }
}
