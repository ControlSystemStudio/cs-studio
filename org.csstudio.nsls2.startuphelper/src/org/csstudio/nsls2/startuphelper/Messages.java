package org.csstudio.nsls2.startuphelper;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.nsls2.startuphelper.messages"; //$NON-NLS-1$
	public static String StartupAuthenticationHelper_Login;
	public static String StartupAuthenticationHelper_LoginTip;
	public static String StartupHelper_And;
	public static String StartupHelper_Login;
	public static String StartupHelper_LoginTip;
	public static String StartupHelper_SelectWorkspace;
	public static String StartupHelper_SelectWorkspaceTip;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
