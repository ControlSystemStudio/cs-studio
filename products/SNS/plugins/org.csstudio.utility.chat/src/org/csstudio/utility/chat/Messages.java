package org.csstudio.utility.chat;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.csstudio.utility.chat.messages"; //$NON-NLS-1$
	public static String Error;
	public static String Participants;
	public static String Send;
	public static String SendErrorFmt;
	public static String UserName;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
