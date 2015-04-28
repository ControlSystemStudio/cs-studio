package org.csstudio.alarm.beast.ui.alarmtable;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.csstudio.alarm.beast.ui.alarmtable.messages"; //$NON-NLS-1$
	public static String AlarmTableRowLimitInfoFmt;
	public static String AlarmTableRowLimitMessage;
	public static String AlarmTableGroup;
    public static String AlarmTableUngroup;
    public static String ColumnConfigDescription;
    public static String ColumnConfigTitle;
    
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
