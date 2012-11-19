package org.csstudio.alarm.beast.notifier;

import org.eclipse.osgi.util.NLS;

/**
 * Access to externalized strings.
 * @author Eclipse Externalization Wizard
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.alarm.beast.notifier.messages"; //$NON-NLS-1$

	public static String Priority_IMPORTANT;
	public static String Priority_MAJOR;
	public static String Priority_MINOR;
	public static String Priority_OK;
	
	public static String Status_OK;
	public static String Status_STOPPED;
	public static String Status_CANCELED;
	public static String Status_FORCED;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Prevent instantiation
	}
}