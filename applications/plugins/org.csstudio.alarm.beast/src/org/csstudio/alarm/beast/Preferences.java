/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.auth.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read preference settings.
 *
 *  Defaults for the application are provided in preferences.ini, see there
 *  for more detailed explanations.
 *
 *  Final product can override in plugin_preferences.ini.
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
public class Preferences
{
	final public static String READONLY = "readonly";
    final public static String ALLOW_CONFIG_SELECTION = "allow_config_selection";
    final public static String AnonyACK = "allow_anonymous_acknowledge";
    final public static String RDB_URL = "rdb_url";
    final public static String RDB_USER = "rdb_user";
    final public static String RDB_PASSWORD = "rdb_password";
    final public static String RDB_SCHEMA = "rdb_schema";
    final public static String ROOT_COMPONENT = "root_component";
    final public static String GLOBAL_PREFIX = "global_prefix";
    final public static String JMS_URL = "jms_url";
    final public static String JMS_USER = "jms_user";
    final public static String JMS_PASSWORD = "jms_password";
    final public static String JMS_IDLE_TIMEOUT = "jms_idle_timeout";
    final public static String PV_START_DELAY = "pv_start_delay";
    final public static String CONNECTION_GRACE_PERIOD = "connection_grace_period";
    final public static String COMMAND_DIRECTORY = "command_directory";
    final public static String COMMAND_CHECK_TIME = "command_check_time";
    final public static String COLOR_OK = "color_ok";
    final public static String COLOR_MINOR_ACK = "color_minor_ack";
    final public static String COLOR_MAJOR_ACK = "color_major_ack";
    final public static String COLOR_INVALID_ACK = "color_invalid_ack";
    final public static String COLOR_MINOR = "color_minor";
    final public static String COLOR_MAJOR = "color_major";
    final public static String COLOR_INVALID = "color_invalid";
    final public static String MAX_CONTEXT_MENU_ENTRIES = "max_context_menu_entries";
    final public static String GUI_UPDATE_SUPPRESSION_MILLIS = "gui_update_suppression_millis";
    final public static String GUI_UPDATE_INITIAL_MILLIS = "gui_update_initial_millis";

    final private static String SERVER_SUFFIX = "_SERVER";
    final private static String CLIENT_SUFFIX = "_CLIENT";
    final private static String TALK_SUFFIX = "_TALK";


    /** @param setting Preference identifier
     *  @return String from preference system, or <code>null</code>
     */
    private static String getString(final String setting)
    {
        return getString(setting, null);
    }

    /** @param setting Preference identifier
     *  @param default_value Default value when preferences unavailable
     *  @return String from preference system, or <code>null</code>
     */
    private static String getString(final String setting, final String default_value)
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return default_value;
        return service.getString(Activator.ID, setting, default_value, null);
    }

    /** @return <code>true</code> for read-only operation */
    public static boolean isReadOnly()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return true;
        return service.getBoolean(Activator.ID, READONLY, true, null);
    }

    /** @return <code>true</code> if selection of a different alarm configuration is allowed */
    public static boolean isConfigSelectionAllowed()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getBoolean(Activator.ID, ALLOW_CONFIG_SELECTION, true, null);
    }

    /** @return <code>true</code> for allow_anonymous_acknowledge operation */
    public static boolean getAllowAnonyACK()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getBoolean(Activator.ID, AnonyACK, false, null);
    }

    /** @return RDB URL */
    public static String getRDB_Url()
    {
        return getString(RDB_URL);
    }

    /** @return RDB User name */
    public static String getRDB_User()
    {
    	return getSecureString(RDB_USER);
    }

	/** @return RDB Password */
    public static String getRDB_Password()
    {
        return getSecureString(RDB_PASSWORD);
    }

	/** @return RDB schema */
    public static String getRDB_Schema()
    {
        final String schema = getString(RDB_SCHEMA);
        if (schema.endsWith("."))
            return schema.substring(0, schema.length()-1);
        return schema;
    }

	/** @return Configuration Name, i.e. name of alarm tree root component */
    public static String getAlarmTreeRoot()
    {
        return getString(ROOT_COMPONENT);
    }

    /** @return JMS URL */
    public static String getJMS_URL()
    {
    	return getString(JMS_URL, "tcp://localhost:61616");
    }

    /** @return JMS User name */
    public static String getJMS_User()
    {
       return getSecureString(JMS_USER);
    }

    /** @return JMS Password */
    public static String getJMS_Password()
    {
    	return getSecureString(JMS_PASSWORD);
    }

    /** @param config Alarm configuration name (root)
     *  @return JMS topic used for alarm messages from server
     */
    public static String getJMS_AlarmServerTopic(final String config)
    {
        return config + SERVER_SUFFIX;
    }

    /** @param config Alarm configuration name (root)
     *  @return JMS topic used for alarm messages form clients
     */
    public static String getJMS_AlarmClientTopic(final String config)
    {
        return config + CLIENT_SUFFIX;
    }

    /** @param config Alarm configuration name (root)
     *  @return JMS topic used to annunciate alarm messages
     */
    public static String getJMS_TalkTopic(final String config)
    {
        return config + TALK_SUFFIX;
    }

    /**  @return JMS topic used for 'global' alarm messages from servers */
    public static String getJMS_GlobalServerTopic()
    {
        return getString(GLOBAL_PREFIX, "GLOBAL") + SERVER_SUFFIX;
    }

    /** @return Delay in seconds between expected idle messages */
    public static long getJMS_IdleTimeout()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getLong(Activator.ID, JMS_IDLE_TIMEOUT, 10, null);
    }

    /** @return Delay in ms between PV startups */
    public static long getPVStartDelay()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getLong(Activator.ID, PV_START_DELAY, 0, null);
    }

    /** @return Grace period in seconds for PVs to connect */
    public static long getConnectionGracePeriod()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getLong(Activator.ID, CONNECTION_GRACE_PERIOD, 30, null);
    }

    /** @return Directory where to run commands */
    public static String getCommandDirectory() throws Exception
    {
        try
        {
            return replaceProperties(getString(COMMAND_DIRECTORY));
        }
        catch (Exception ex)
        {
            throw new Exception(
                    "Error in command_directory preference setting, "
                    + ex.getMessage());
        }
    }

    /** @return Time in seconds to wait for OK or error from command */
    public static int getCommandCheckTime()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getInt(Activator.ID, COMMAND_CHECK_TIME, 10, null);
    }

    /** @param value Value that might contain "$(prop)"
     *  @return Value where "$(prop)" is replaced by Java system property "prop"
     *  @throws Exception on error
     */
    private static String replaceProperties(final String value) throws Exception
    {
        final Matcher matcher = Pattern.compile("\\$\\((.*)\\)").matcher(value);
        if (matcher.matches())
        {
            final String prop_name = matcher.group(1);
            final String prop = System.getProperty(prop_name);
            if (prop == null)
                throw new Exception("Property '" + prop_name + "' is not defined");
            return prop;
        }
        // Return as is
        return value;
    }

    /** Get RGB values for color.
     *  In case of errors, it won't throw an exception but log error.
     *  Somewhat unfortunate but handled this way because of the way
     *  this is invoked from SeverityLevel initializer.
     *  @param color_pref_name Name of preference key
     *  @return RGB ints (0...255)
     */
    public static int[] getColor(final String color_pref_name)
    {
        final int rgb[] = new int[] { 0, 0, 0 };
        final IPreferencesService service = Platform.getPreferencesService();
        // Allow execution from JUnit test without platform support
        if (service == null)
            return rgb;
        final String color = service.getString(Activator.ID, color_pref_name, "0,0,0", null);
        final String[] rgbvals = color.split(", *");
        if (rgbvals.length != 3)
        {
            Activator.getLogger().log(Level.WARNING, "Cannot decode RGB settings for {0} from {1}",
                    new Object[] { color_pref_name, color });
            return rgb;
        }
        try
        {
            for (int i=0; i<3; ++i)
                rgb[i] = Integer.parseInt(rgbvals[i]);
        }
        catch (Throwable ex)
        {
            Activator.getLogger().log(Level.WARNING, "Error parsing RGB settings for {0} from {1}",
                    new Object[] { color_pref_name, color });
        }
        return rgb;
    }

    /** @return Delay in millisecs for the initial update after trigger */
    public static long getGuiThrottleInitialMillis()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getLong(Activator.ID, GUI_UPDATE_INITIAL_MILLIS, 100, null);
    }

    /** @return Delay in millisecs for the suppression of a burst of events */
    public static long getGuiThrottleSuppressionMillis()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getLong(Activator.ID, GUI_UPDATE_SUPPRESSION_MILLIS, 1000, null);
    }

    /** @return Maximum number of context menu entries before summarizing them */
    public static int getMaxContextMenuEntries()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getInt(Activator.ID, MAX_CONTEXT_MENU_ENTRIES, 10, null);
    }

    private static String getSecureString(final String setting) {
    	String value = SecureStorage.retrieveSecureStorage(Activator.ID, setting);
        return value;
    }
}
