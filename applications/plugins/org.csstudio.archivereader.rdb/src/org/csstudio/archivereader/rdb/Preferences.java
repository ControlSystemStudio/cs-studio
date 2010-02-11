package org.csstudio.archivereader.rdb;

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
    final public static String SCHEMA = "schema";
    final public static String USER = "user";
    final public static String PASSWORD = "password";
    final public static String STORED_PROCEDURE = "use_stored_procedure";

    public static String getSchema()
    {
        return getString(SCHEMA, "chan_arch");
    }

    public static String getUser()
    {
        return getString(USER, "");
    }
    
    public static String getPassword()
    {
        return getString(PASSWORD, "");
    }
    
    public static boolean useStoredProcedure()
    {
        return Boolean.parseBoolean(getString(STORED_PROCEDURE, "true"));
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
