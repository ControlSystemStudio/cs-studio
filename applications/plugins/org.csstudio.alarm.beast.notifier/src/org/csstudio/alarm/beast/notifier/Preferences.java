package org.csstudio.alarm.beast.notifier;

import org.csstudio.auth.security.SecureStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read preferences
 *  <p>
 *  See preferences.ini for explanation of supported preferences.
 *  @author Fred Arnaud (Sopra Group)
 */
@SuppressWarnings("nls")
public class Preferences {
    final public static String THRESHOLD = "threshold";
    final public static String SMTP_SENDER = "smtp_sender";
    final public static String SMS_URL = "sms_url";
    final public static String SMS_USER = "sms_user";
    final public static String SMS_PASSWORD = "sms_password";

	/**
	 * @param setting Preference identifier
	 * @return String from preference system, or <code>null</code>
	 */
	private static String getString(final String setting) {
		return getString(setting, null);
	}

	/**
	 * @param setting Preference identifier
	 * @param default_value Default value when preferences unavailable
	 * @return String from preference system, or <code>null</code>
	 */
	private static String getString(final String setting,
			final String default_value) {
		final IPreferencesService service = Platform.getPreferencesService();
		if (service == null)
			return default_value;
		return service.getString(Activator.ID, setting, default_value, null);
	}

	/** @return threshold for automated actions */
	public static int getThreshold() {
		final IPreferencesService service = Platform.getPreferencesService();
		if (service == null)
			return 100; // default
		return service.getInt(Activator.ID, THRESHOLD, 100, null);
	}

    /** @return SMTP Password */
    public static String getSMTP_Sender() {
        return getString(SMTP_SENDER, "");
    }

	/** @return SMS URL */
	public static String getSMS_URL() {
		return getString(SMS_URL);
	}

	/** @return SMS User name */
	public static String getSMS_User() {
		return getSecureString(SMS_USER);
	}

	/** @return SMS Password */
	public static String getSMS_Password() {
		return getSecureString(SMS_PASSWORD);
	}

	private static String getSecureString(final String setting) {
		String value = SecureStorage.retrieveSecureStorage(Activator.ID, setting);
		return value;
	}
}
