package org.csstudio.scan.ui.scanmonitor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.scan.ui.scanmonitor.messages"; //$NON-NLS-1$
    public static String Abort;
    public static String CreateTime;
    public static String CreateTimeFmt;
    public static String CurrentCommand;
    public static String CurrentCommandEmpty;
    public static String CurrentCommandFmt;
    public static String Error;
    public static String ErrorMsgFmt;
    public static String FinishTime;
    public static String FinishTimeFmt;
    public static String ID;
    public static String ID_Fmt;
    public static String Info;
    public static String InfoTitle;
	public static String MemInfo;
	public static String MemInfoTT;
    public static String Name;
    public static String NameFmt;
    public static String NoError;
    public static String Pause;
    public static String Percent;
    public static String PercentFmt;
    public static String Remove;
    public static String RemoveCompleted;
    public static String RemoveCompletedScans;
    public static String RemoveScan;
    public static String RemoveSelectedScan;
    public static String Resume;
    public static String Runtime;
    public static String Runtime_TT;
    public static String ShowDevices;
    public static String State;
    public static String StateFmt;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
