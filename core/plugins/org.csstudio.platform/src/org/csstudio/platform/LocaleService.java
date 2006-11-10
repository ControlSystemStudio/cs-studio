package org.csstudio.platform;

import java.util.Locale;

/**
 * 
 * @author awill
 * 
 */
public final class LocaleService {
	/**
	 * Property ID for the locale setting.
	 */
	public static final String PROP_LOCALE = "locale"; //$NON-NLS-1$

	/**
	 * This class can not be instantiated.
	 */
	private LocaleService() {
	}	
	
	/**
	 * Set the system's default locate according to the CSS settings.
	 * 
	 * @param locale
	 *            The system locale.
	 * 
	 */
	public static void setSystemLocale(final String locale) {
		if (locale != null) {
			String[] elements = locale.split("[_]"); //$NON-NLS-1$
			String language = ""; //$NON-NLS-1$
			String country = ""; //$NON-NLS-1$

			if (elements != null) {
				if (elements.length > 0) {
					language = elements[0];
				}

				if (elements.length > 1) {
					country = elements[1];
				}
			}

			if (language.length() > 0) {
				Locale.setDefault(new Locale(language, country, "")); //$NON-NLS-1$
			}
		}
	}
}
