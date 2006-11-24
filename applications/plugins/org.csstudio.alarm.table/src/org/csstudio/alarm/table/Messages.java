package org.csstudio.alarm.table;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.alarm.table.messages"; //$NON-NLS-1$

	public static String AlarmViewerPreferencePage_column;

	public static String AlarmViewerPreferencePage_columnNamesMessageKeys;

	public static String AlarmViewerPreferencePage_enterColumnName;

	public static String column;

	public static String columnNamesMessageKeys;

	public static String ExpertSearchDialog_expertButton;

	public static String ExpertSearchDialog_end;

	public static String ExpertSearchDialog_endTime;

	public static String ExpertSearchDialog_search;

	public static String ExpertSearchDialog_start;

	public static String ExpertSearchDialog_startEndMessage;

	public static String ExpertSearchDialog_startTime;

	public static String JmsLogPreferencePage_color;

	public static String JmsLogPreferencePage_key;

	public static String JmsLogPreferencePage_severityKeys;

	public static String JmsLogPreferencePage_value;

	public static String LogArchiveViewerPreferencePage_column;

	public static String LogArchiveViewerPreferencePage_columnNamesMessageKeys;

	public static String LogArchiveViewerPreferencePage_dateFormat;

	public static String LogArchiveViewerPreferencePage_javaDateFormat;

	public static String LogArchiveViewerPreferencePage_newColumnName;

	public static String LogViewArchive_3days;

	public static String LogViewArchive_day;

	public static String LogViewArchive_expert;

	public static String LogViewArchive_from;

	public static String LogViewArchive_period;

	public static String LogViewArchive_to;

	public static String LogViewArchive_user;

	public static String LogViewArchive_week;

	public static String newColumnName;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * The localzation messages ressource bundle.
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	
	private Messages() {
	}
	
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
