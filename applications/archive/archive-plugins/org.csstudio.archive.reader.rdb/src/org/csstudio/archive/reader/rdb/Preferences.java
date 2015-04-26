/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to preferences for the RDB archive reader.
 *
 *  <p>See preferences.ini for explanation of settings.
 *
 *  <p>Note that most RDB archive settings are in the
 *  plugin org.csstudio.archive.rdb.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    final public static String FETCH_SIZE = "fetch_size";
    final public static String STORED_PROCEDURE = "use_stored_procedure";

    public static int getFetchSize()
    {
        int fetch_size = 10;
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return fetch_size;
        return prefs.getInt(Activator.ID, FETCH_SIZE, fetch_size, null);
    }
    
    public static String getStoredProcedure()
    {
        return getString(STORED_PROCEDURE, "");
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
