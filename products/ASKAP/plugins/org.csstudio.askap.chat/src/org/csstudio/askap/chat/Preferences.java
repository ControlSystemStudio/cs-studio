package org.csstudio.askap.chat;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class Preferences {
	
	public static final String CHAT_MESSAGE_TIME_TO_LIVE = "chat_message_time_to_live";
	public static final String CHAT_JMS_SERVER_URL = "chat_jms_server_url";
	public static final String CHAT_JMS_MESSAGE_TOPIC = "chat_jms_msg_topic";
	public static final String CHAT_JMS_HEART_BEAT_TOPIC = "chat_jms_heart_beat_topic";
	
	// a thread wakes up every JMSChatHeartBeatPeriod to send heart beats and check all the users have sent heartbeats
	public static final String CHAT_HEART_BEAT_PERIOD = "chat_heart_beat_period";
	
	// if a heart beat has not been received in the last JMSChatHeartBeatMinPeriod, the user is considered offline
	public static final String CHAT_HEART_BEAT_MIN_PERIOD = "chat_heart_beat_min_period";
	
	public static String getServerURL() {
		return getString(CHAT_JMS_SERVER_URL, "");

	}

	public static String getMessageTopicName() {
		return getString(CHAT_JMS_MESSAGE_TOPIC, "ASKAP_Chat");
	}
	
	public static String getHeasrtBeatTopicName() {
		return getString(CHAT_JMS_HEART_BEAT_TOPIC, "ASKAP_Chat");
	}
	
	public static long getTimeToLive() {
		return getLong(CHAT_MESSAGE_TIME_TO_LIVE, 1000000);
	}

	public static long getHeartBeatPeriod() {
		return getLong(CHAT_HEART_BEAT_PERIOD, 60000);
	}
	
	public static long getHeartBeatMinPeriod() {
		return getLong(CHAT_HEART_BEAT_MIN_PERIOD, 30000);
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
