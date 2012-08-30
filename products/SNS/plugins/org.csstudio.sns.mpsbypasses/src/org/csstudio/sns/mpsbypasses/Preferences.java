package org.csstudio.sns.mpsbypasses;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preference settings
 *
 *  <p>See <code>preferences.ini</code> for details
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    final private static String DEFAULT_RDB_URL = "jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=OFF)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=snsapp1a.sns.ornl.gov)(PORT=1610))(ADDRESS=(PROTOCOL=TCP)(HOST=snsapp1b.sns.ornl.gov)(PORT=1610))(CONNECT_DATA=(SERVICE_NAME=ics_prod_lba)))";
    final private static String DEFAULT_RDB_USER = "sns_reports";
    final private static String DEFAULT_RDB_PASSWORD = "sns";
    final private static String DEFAULT_WEB_URL = "http://snsapp1.sns.ornl.gov";

    public static String getRDB_URL()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return DEFAULT_RDB_URL;
        return prefs.getString(Plugin.ID, "rdb_url", DEFAULT_RDB_URL, null);
    }

    public static String getRDB_User()
    {
        return DEFAULT_RDB_USER;
    }

    public static String getRDB_Password()
    {
        return DEFAULT_RDB_PASSWORD;
    }

    public static String getEnterBypassURL()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return DEFAULT_WEB_URL;
        return prefs.getString(Plugin.ID, "url_enter_bypass", DEFAULT_WEB_URL, null);
    }

    public static String getViewBypassURL()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return DEFAULT_WEB_URL;
        return prefs.getString(Plugin.ID, "url_view_bypass", DEFAULT_WEB_URL, null);
    }
}
