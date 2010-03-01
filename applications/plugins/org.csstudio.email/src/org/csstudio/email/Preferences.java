package org.csstudio.email;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to preference settings.
 * 
 *  See preferences.ini for details on the available settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    final public static String SMTP_HOST = "smtp_host";
    
    public static String getSMTP_Host()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getString(Activator.ID, SMTP_HOST, "undefined.host", null);
    }
}
