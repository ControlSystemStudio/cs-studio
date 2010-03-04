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
    // useAdvancedGraphics() is called from many drawing operations, so
    // only determine it once
    private static boolean use_advanced_graphics;

    static
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            use_advanced_graphics = true;
        else
            use_advanced_graphics = prefs.getBoolean(Activator.PLUGIN_ID, "use_advanced_graphics", true, null);
    }

    public static boolean useAdvancedGraphics()
    {
        return use_advanced_graphics;
    }
}
