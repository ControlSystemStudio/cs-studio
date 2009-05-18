package org.csstudio.display.pace;


import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read preference settings.
 *  Defaults for the application are provided in preferences.ini.
 *  Final product can override in plugin_preferences.ini.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    final public static String DEFAULT_LOGBOOK = "default_logbook";
    
    /** @return Name of default logbook
     */
    public static String getDefaultLogbook()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        return service.getString(Activator.ID, DEFAULT_LOGBOOK, null, null);
    }
  }
