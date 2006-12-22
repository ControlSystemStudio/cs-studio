package org.csstudio.utility.nameSpaceSearch;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.csstudio.platform.logging.CentralLogger;
//import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.utility.nameSpaceSearch.messages"; //$NON-NLS-1$

	public static String MainView_Controller;

	public static String MainView_ecom;

	public static String MainView_facility;

	public static String MainView_Record;

	public static String MainView_searchButton;

	public static String PreferencePage_DN;

	public static String PreferencePage_LDAP;

	public static String PreferencePage_PASS;

	public static String PreferencePage_URL;

	/**
	 * The localzation messages ressource bundle.
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
	public static String getString(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			CentralLogger.getInstance().error(Messages.class, e);
			return '!' + key + '!';
		}
	}
}
