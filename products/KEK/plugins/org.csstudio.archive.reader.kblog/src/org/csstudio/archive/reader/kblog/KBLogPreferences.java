package org.csstudio.archive.reader.kblog;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Access to KBLog preferences
 * 
 * @author Takashi Nakamoto
 */
@SuppressWarnings("nls")
public class KBLogPreferences {
	final public static String PATH_TO_KBLOGRD = "path_to_kblogrd";
	final public static String PATH_TO_KBLOGRD_DEFAULT = "/usr/local/bin/kblogrd";

	final public static String REDUCE_DATA = "reduce_data";
	final public static boolean REDUCE_DATA_DEFAULT = false;
	
	public static String getPathToKBLogRD() {
		return getString(PATH_TO_KBLOGRD, PATH_TO_KBLOGRD_DEFAULT);
	}
	
	public static boolean getReduceData() {
		return getBoolean(REDUCE_DATA, REDUCE_DATA_DEFAULT);
	}
	
	/**
	 * Get boolean preference
	 * @param key Preference key
	 * @param default_value Default value if the preference is not set by the user
	 * @return boolean
	 */
	private static boolean getBoolean(final String key, final boolean default_value)
	{
		final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs == null)
			return default_value;
		return prefs.getBoolean(Activator.ID, key, default_value, null);
	}

	/**
	 * Get string preference
	 * @param key Preference key
	 * @param default_value Default value if the preference is not set by the user
	 * @return String or <code>null</code>
	 */
	private static String getString(final String key, final String default_value)
	{
		final IPreferencesService prefs = Platform.getPreferencesService();
		if (prefs == null)
			return default_value;
		return prefs.getString(Activator.ID, key, default_value, null);
	}
}
