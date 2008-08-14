package org.csstudio.utility.recordproperty;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.csstudio.utility.recordproperty.messages";
	public static String RecordPropertyView_PV_COLUMN;
	public static String RecordPropertyView_RDB_COLUMN;
	public static String RecordPropertyView_VAL_COLUMN;
	public static String RecordPropertyView_RMI_COLUMN;
	public static String RecordPropertyView_NA;
	public static String RecordPropertyView_PLEASE_WAIT;
	public static String RecordPropertyView_DONE;
	public static String RecordPropertyView_TYPE_HERE;
	public static String RecordPropertyView_RECORD;
	public static String RecordPropertyView_GET_DATA;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
