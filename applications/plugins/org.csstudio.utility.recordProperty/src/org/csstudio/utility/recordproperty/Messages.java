package org.csstudio.utility.recordproperty;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.csstudio.utility.recordproperty.messages";
	public static String RecordPropertyView_PV_COLUMN;
	public static String RecordPropertyView_RDB_COLUMN;
	public static String RecordPropertyView_VAL_COLUMN;
	public static String RecordPropertyView_RMI_COLUMN;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
