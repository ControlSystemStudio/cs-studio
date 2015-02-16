package org.csstudio.utility.channel.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.utility.channel.actions.messages"; //$NON-NLS-1$
	public static String treeDialogTitle;
	public static String treeDialogMessage;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
