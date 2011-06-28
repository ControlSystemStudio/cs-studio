package org.csstudio.ui.menu.pvscript;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.ui.menu.pvscript.messages"; //$NON-NLS-1$
	public static String Error;
	public static String PreferenceErrorFmt;
	public static String ScriptExecutionErrorFmt;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
