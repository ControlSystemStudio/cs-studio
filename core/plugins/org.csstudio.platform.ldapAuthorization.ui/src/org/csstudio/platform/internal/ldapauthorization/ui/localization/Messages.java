package org.csstudio.platform.internal.ldapauthorization.ui.localization;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.platform.internal.ldapauthorization.ui.localization.messages"; //$NON-NLS-1$
	public static String RoleInformationToolbar_ButtonText;
	public static String RoleInformationToolbar_Head;
	public static String RoleInformationToolbar_Teaser;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
