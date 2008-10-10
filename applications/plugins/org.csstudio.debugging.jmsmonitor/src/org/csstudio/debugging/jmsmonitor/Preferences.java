package org.csstudio.debugging.jmsmonitor;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Access to preferences for JMS Monitor
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    public static final String JMS_URL = "jms_url";

    /** @return URL of JMS server or <code>null</code> */
    public static String getJMS_URL()
    {
        final IPreferencesService preferences = Platform.getPreferencesService();
        return preferences.getString(Activator.ID, JMS_URL, null, null);
    }
}
