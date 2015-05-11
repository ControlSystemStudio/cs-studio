package org.csstudio.logbook.midas;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class LogbookMidasPreferences {
    public static final String HOST = "host";
    public static final String PORT = "port";

    /** @return HOST of Elog server */
    public static String getHOST()
    {
        String default_value = "";
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            default_value = prefs.getString(Activator.ID, HOST, default_value, null);
        return default_value.trim();
    }

    /** @return PORT of Elog server */
    public static String getPORT() throws Exception
    {
        String default_value = "";
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            default_value = prefs.getString(Activator.ID, PORT, default_value, null);
        return default_value.trim();
    }

}
