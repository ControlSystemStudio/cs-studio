package org.csstudio.openfile;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.openfile.messages"; //$NON-NLS-1$
	public static String DisplayUtil_ErrorEmptyExt;
	public static String DisplayUtil_ErrorUnknownExtFmt;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
