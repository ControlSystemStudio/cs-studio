/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb.raw;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to preferences for the InfluxDB archive reader.
 *
 *  <p>See preferences.ini for explanation of settings.
 *
 *  <p>Note that most InfluxDB archive settings are in the
 *  plugin org.csstudio.archive.influxdb.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    final public static String CHUNK_SIZE = "chunk_size";
    // final public static String STORED_PROCEDURE = "use_stored_procedure";
    // final public static String STARTTIME_FUNCTION = "use_starttime_function";
    final public static String USE_STD_DEV = "use_std_dev";

    public static int getChunkSize()
    {
        int chunk_size = 10000;
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return chunk_size;
        return prefs.getInt(Activator.ID, CHUNK_SIZE, chunk_size, null);
    }

    // public static String getStoredProcedure()
    // {
    // return getString(STORED_PROCEDURE, "");
    // }
    //
    // public static String getStarttimeFunction()
    // {
    // return getString(STARTTIME_FUNCTION, "");
    // }

    // /** Get string preference
    // * @param key Preference key
    // * @return String or <code>null</code>
    // */
    // private static String getString(final String key, final String
    // default_value)
    // {
    // final IPreferencesService prefs = Platform.getPreferencesService();
    // if (prefs == null)
    // return default_value;
    // return prefs.getString(Activator.ID, key, default_value, null);
    // }

    public static boolean getUseStdDev()
    {
        boolean use_std_dev = false;
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return use_std_dev;
        return prefs.getBoolean(Activator.ID, USE_STD_DEV, use_std_dev, null);
    }
}
