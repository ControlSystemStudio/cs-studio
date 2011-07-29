package org.csstudio.alarm.beast.ui.areapanel;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.csstudio.alarm.beast.ui.areapanel.messages"; //$NON-NLS-1$
	public static String ShowInAlarmTree;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
