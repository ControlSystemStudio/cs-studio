package org.csstudio.alarm.beast.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read preference settings.
*
*  Defaults for the application are provided in preferences.ini, see there
*  for more detailed explanations.
*
*  Final product can override in plugin_preferences.ini.
*  @author Fred Arnaud
*/
@SuppressWarnings("nls")
public class Preferences 
{
	final public static String DEFAULT_EMAIL_SENDER = "default_email_sender";

	public static String getDefaultEmailSender() {
	    String sender = "alarms@css";
		final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs == null)
			return sender;
		return prefs.getString(Activator.ID, DEFAULT_EMAIL_SENDER, sender, null);
	}
}
