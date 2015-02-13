package org.csstudio.askap.logviewer;


import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * 
 * @author Xinyu Wu
 */
public class Preferences {
	
	public static final String LOG_MESSAGE_TOPIC_NAME = "log_message_topic_name";
	public static final String LOG_QUERY_ADAPTOR_NAME = "log_query_adaptor_name";
	public static final String LOG_VIEW_MAX_MESSAGES= "max_num_messages";
	public static final String LOG_SUBSCRIBER_END_POINT_NAME = "log_subscriber_name";
	public static final String LOG_QUERY_MAX_MESSAGES_PER_QUERY = "log_query_max_messages_per_query";
	public static final String LOG_VIEW_REFRESH_PERIOD = "log_view_refresh_period";
	
	
	public static final String[] LOG_LEVELS = new String[]{"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"};

	public static final String[] SERVICE_NAMES = new String[]{};
	
	public static String getLogMessageTopicName() {
		return getString(LOG_MESSAGE_TOPIC_NAME, "");
	}
	public static String getLogQueryAdaptorName() {
		return getString(LOG_QUERY_ADAPTOR_NAME, "");
	}
	public static String getLogSubscriberEndPointName() {
		return getString(LOG_SUBSCRIBER_END_POINT_NAME, "");
	}
	
	public static int getMaxMessages() {
		return getInt(LOG_VIEW_MAX_MESSAGES, 1000);
	}
	public static int getLogQueryMessagesPerQuery() {
		return getInt(LOG_QUERY_MAX_MESSAGES_PER_QUERY, 1000);
	}
	public static int getLogViewRefreshPeriod() {
		return getInt(LOG_VIEW_REFRESH_PERIOD, 500);
	}
	
    /** Get long preference
     *  @param key Preference key
     *  @return long or <code>null</code>
     */
    private static int getInt(final String key, final int default_value)
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return default_value;
        
        return prefs.getInt(Activator.ID, key, default_value, null);
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
        return prefs.getString(Activator.ID, key, default_value, null);
    }
}
