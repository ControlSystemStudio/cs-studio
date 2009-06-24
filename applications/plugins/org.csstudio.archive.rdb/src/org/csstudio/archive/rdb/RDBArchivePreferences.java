package org.csstudio.archive.rdb;

import org.csstudio.platform.security.SecureStorage;
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
