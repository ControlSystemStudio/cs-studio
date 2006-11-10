package org.csstudio.platform.ui.internal.localization;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.csstudio.platform.logging.CentralLogger;

/**
 * Access to the localization message ressources within this
 * plugin.
 * 
 * @author awill
 */
public final class Messages {
	/**
	 * The bundle name of the localization messages ressources.
	 */
	private static final String BUNDLE_NAME = "org.csstudio.platform.ui.internal.localization.messages"; //$NON-NLS-1$

	/**
	 * The localzation messages ressource bundle.
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * This constructor is private since this class only provides static
	 * methods.
	 */
	private Messages() {
	}

	/**
	 * Return the localization message string for the given key.
	 * 
	 * @param key
	 *            Message key.
	 * @return The localization message string for the given key.
	 */
	public static String getString(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			CentralLogger.getInstance().error(Messages.class, e);
			return '!' + key + '!';
		}
	}

}
