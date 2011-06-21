/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import org.csstudio.archive.rdb.RDBArchivePreferences;
import org.csstudio.auth.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to RDB archive preferences
 *  <p>
 *  See preferences.ini for explanation of settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    final public static String USER = "user";
    final public static String PASSWORD = "password";
    final public static String SCHEMA = "schema";
    final public static String STORED_PROCEDURE = "use_stored_procedure";
    final public static String TIMEOUT_SECS = "timeout_secs";

    public static String getUser()
    {
        return getString(USER, RDBArchivePreferences.getUser());
    }
    
    public static String getPassword()
    {
        // Must use SecureStorage for password because preference page
        // uses PasswordFieldEditor 
        String password = SecureStorage.retrieveSecureStorage(Activator.ID, PASSWORD);
        if (password == null)
        	password = RDBArchivePreferences.getPassword();
        return password;
    }
    
    public static String getSchema()
    {
        return getString(SCHEMA, RDBArchivePreferences.getSchema());
    }

    public static String getStoredProcedure()
    {
        return getString(STORED_PROCEDURE, "");
    }

    public static int getTimeoutSecs()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return 120;
        return prefs.getInt(Activator.ID, TIMEOUT_SECS, 120, null);
    }
    
    /** Get string preference
     *  @param key Preference key
     *  @return String or <code>null</code>
     */
    private static String getString(final String key, final String default_value)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return default_value;
        return prefs.getString(Activator.ID, key, default_value, null);
    }
}
