package org.csstudio.swt.xygraph;

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
    public static boolean useAdvancedGraphics()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getBoolean(Activator.PLUGIN_ID, "use_advanced_graphics", true, null);
    }
}
