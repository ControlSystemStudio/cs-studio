/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.influxdb;

import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to InfluxDB archive preferences
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class InfluxDBArchivePreferences
{
    public static final String URL = "url";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String TIMEOUT = "timeout_secs";

    //TODO: Multiple databases?
    // public static final String DFLT_DBNAME = "channel_data";
    // public static final String DFLT_METADBNAME = "channel_meta";

    public static final String DFLT_DB = "default_db";
    public static final String DFLT_META_DB = "default_meta_db";
    public static final String DB_PREFIX = "db_prefix";

    public static String getDBName() {
        final String ret = getString(DFLT_DB);
        if (ret == null)
            return "channel_data";
        return ret;
    }

    public static String getMetaDBName() {
        final String ret = getString(DFLT_META_DB);
        if (ret == null)
            return "channel_meta";
        return ret;
    }

    public static String getDBPrefix() {
        final String ret = getString(DB_PREFIX);
        if (ret == null)
            return "";
        return ret;
    }

    /** @return URL of InfluxDB archive server */
    public static String getURL()
    {
        return getString(URL);
    }

    /** @return User name for InfluxDB archive server */
    public static String getUser()
    {
        return getString(USER);
    }

    /** @return Password for InfluxDB archive server */
    public static String getPassword()
    {
        return SecurePreferences.get(Activator.ID, PASSWORD, null);
    }

    /** @return Timeout in seconds */
    public static int getChunkTimeoutSecs()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 10;
        return prefs.getInt(Activator.ID, TIMEOUT, 0, null);
    }

    /** Get string preference
     *  @param key Preference key
     *  @return String or <code>null</code>
     */
    private static String getString(final String key)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return null;
        return prefs.getString(Activator.ID, key, null, null);
    }
}
