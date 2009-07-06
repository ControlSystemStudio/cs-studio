package org.csstudio.utility.pv;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preferences for Utility.PV
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    /** Preference ID of the default PV type */
    final public static String DEFAULT_TYPE = "default_type";
    
    /** @return Default PV type from preferences */
    public static String getDefaultType()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getString(Plugin.ID, DEFAULT_TYPE, "ca", null);
    }
}
