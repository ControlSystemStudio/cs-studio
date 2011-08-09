package org.csstudio.navigator.applaunch;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.csstudio.navigator.applaunch.messages"; //$NON-NLS-1$
	public static String ConfigFileErrorFmt;
	public static String Error;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
