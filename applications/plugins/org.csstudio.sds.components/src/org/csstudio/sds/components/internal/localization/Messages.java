package org.csstudio.sds.components.internal.localization;

import org.eclipse.osgi.util.NLS;

/**
 * Access to the localization message ressources within this plugin.
 * 
 * @author Alexander Will
 */
public final class Messages extends NLS {
	/**
	 * The bundle name of the localization messages ressources.
	 */
	private static final String BUNDLE_NAME = "org.csstudio.sds.components.internal.localization.messages"; //$NON-NLS-1$

	public static String FillLevelProperty;

	public static String PolyElement_POINTS;

	public static String LabelElement_LABEL;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Messages() {
	}
}
