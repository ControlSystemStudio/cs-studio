/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import org.csstudio.auth.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to RDB archive preferences
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchivePreferences
{
    public static final String URL = "url";
    public static final String SCHEMA = "schema";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String MAX_TEXT_SAMPLE_LENGTH = "max_text_sample_length";
	public static final String MIN_SAMPLE_PERIOD = "min_sample_period";
    public static final String SQL_TIMEOUT = "sql_timeout";

    /** @return URL of RDB archive server */
    public static String getURL()
    {
        return getString(URL);
    }

    /** @return Schema for RDB tables or <code>null</code> */
    public static String getSchema()
    {
        return getString(SCHEMA);
    }

    /** @return User name for RDB archive server */
    public static String getUser()
    {
        return getString(USER);
    }

    /** @return Password for RDB archive server */
    public static String getPassword()
    {
        // Try 'secure' preference file
        final String password = SecureStorage.retrieveSecureStorage(Activator.ID, PASSWORD);
        if (password != null)
            return password;
        // Fall back to plain prefs
        return getString(PASSWORD);
    }

    /** @return Maximum length of text samples written to SAMPLE.STR_VAL */
    public static int getMaxStringSampleLength()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 80;
        return prefs.getInt(Activator.ID, MAX_TEXT_SAMPLE_LENGTH, 80, null);
    }

    /** @return Minimum sample period in seconds */
    public static double getMinSamplePeriod()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 0.1;
        return prefs.getDouble(Activator.ID, MIN_SAMPLE_PERIOD, 0.1, null);
    }

	/** @return SQL Timeout in seconds */
    public static int getSQLTimeout()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 30*60;
        return prefs.getInt(Activator.ID, SQL_TIMEOUT, 30*60, null);
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
