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
    public static final String SQL_TIMEOUT = "sql_timeout";
    public static final String USE_ARRAY_BLOB = "use_array_blob";

    /** @return URL of RDB archive server */
    public static String getURL()
    {
        return getString(URL);
    }

    /** @return Schema for RDB tables or <code>null</code> */
    public static String getSchema()
    {
        final String schema = getString(SCHEMA);
        if (schema.endsWith("."))
            return schema.substring(0, schema.length()-1);
        return schema;
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

	/** @return SQL Timeout in seconds */
    public static int getSQLTimeoutSecs()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 0;
        return prefs.getInt(Activator.ID, SQL_TIMEOUT, 0, null);
    }

    /** @return <code>true</code> if a BLOB should be used for array samples */
    public static boolean useArrayBlob()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return true;
        return prefs.getBoolean(Activator.ID, USE_ARRAY_BLOB, true, null);
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
