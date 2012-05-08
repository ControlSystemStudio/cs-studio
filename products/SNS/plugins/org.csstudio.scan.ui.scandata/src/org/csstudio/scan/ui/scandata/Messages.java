package org.csstudio.scan.ui.scandata;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.csstudio.scan.ui.scandata.messages"; //$NON-NLS-1$
	public static String ScanEditorTTFmt;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
