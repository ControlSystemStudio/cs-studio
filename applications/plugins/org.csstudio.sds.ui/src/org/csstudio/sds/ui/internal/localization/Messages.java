package org.csstudio.sds.ui.internal.localization;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Message bundle class for the SDS UI Plugin.
 * 
 * @author Sven Wende
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.sds.ui.internal.localization.messages";//$NON-NLS-1$
	
	/**
	 * The resource bundle.
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public static String SetPropertyValueCommand_label;

	public static String SetDynamicsDescriptorCommand_label;
	
	public static String SetAliasDescriptorsCommand_label;

	// ==============================================================================
	// Properties View
	// ==============================================================================
	public static String Alias_text;
	
	public static String Alias_toolTip;

	public static String Categories_text;

	public static String Categories_toolTip;

	public static String CopyProperty_text;

	public static String Defaults_text;

	public static String Defaults_toolTip;

	public static String Filter_text;

	public static String Filter_toolTip;

	public static String PropertyViewer_property;

	public static String PropertyViewer_value;

	public static String PropertyViewer_misc;

	public static String CopyToClipboardProblemDialog_title;

	public static String CopyToClipboardProblemDialog_message;

	/**
	 * Static constructor.
	 */
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	/**
	 * Gets the String for the specified key in its localized version according
	 * to the current locale.
	 * 
	 * @param key
	 *            the key
	 * @return the localized String
	 */
	public static String getString(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}