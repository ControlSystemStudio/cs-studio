package org.csstudio.askap.chat;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class Preferences {
	
	public static final String CHAT_MESSAGE_TIME_TO_LIVE = "chat_message_time_to_live";
	public static final String CHAT_JMS_SERVER_URL = "chat_jms_server_url";
	public static final String CHAT_JMS_TOPIC = "chat_jms_topic";
	
	
	public static String getServerURL() {
		return getString(CHAT_JMS_SERVER_URL, "");

	}

	public static String getTopicName() {
		return getString(CHAT_JMS_TOPIC, "ASKAP_Chat");
	}
	
	public static long getTimeToLive() {
		return getLong(CHAT_MESSAGE_TIME_TO_LIVE, 1000000);
	}

    /** Get long preference
     *  @param key Preference key
     *  @return long or <code>null</code>
     */
    private static long getLong(final String key, final long default_value)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return default_value;
        
        return prefs.getLong(Activator.PLUGIN_ID, key, default_value, null);
    }
    

    /** Get string preference
     *  @param key Preference key
     *  @return String or <code>null</code>
     */
    private static String getString(final String key, final String default_value)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return default_value;
        return prefs.getString(Activator.PLUGIN_ID, key, default_value, null);
    }

}
