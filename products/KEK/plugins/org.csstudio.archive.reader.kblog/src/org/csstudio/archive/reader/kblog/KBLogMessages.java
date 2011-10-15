package org.csstudio.archive.reader.kblog;

import org.eclipse.osgi.util.NLS;

/**
 * Externalized strings
 * 
 * @author Takashi Nakamoto
 */
public class KBLogMessages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.archive.reader.kblog.messages"; //$NON-NLS-1$
	public static String PreferenceTitle;
	public static String PathToKBLogRD;
	public static String SeverityConnected;
	public static String StatusConnected;
	public static String StatusNormal;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, KBLogMessages.class);
	}
	
	private KBLogMessages()
	{
		// Prevent instantiation
	}
}
