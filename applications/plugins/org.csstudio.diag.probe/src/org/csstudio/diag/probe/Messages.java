package org.csstudio.diag.probe;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.diag.probe.messages"; //$NON-NLS-1$

    public static String S_Adjust;

    public static String S_AdjustFailed;

    public static String S_AdjustValue;

    public static String S_CHANNEL;

    public static String S_ChannelInfo;

    public static String S_CreateError;

    public static String S_Disconnected;

    public static String S_EnterPVName;

    public static String S_EnvInfo;

    public static String S_Info;

    public static String S_ModValue;

    public static String S_NoChannel;

    public static String S_NoInfo;

    public static String S_NotConnected;

    public static String S_ObtainInfo;

    public static String S_OK;

    public static String S_Period;

    public static String S_Seconds;
    
    public static String S_PVName;

    public static String S_Searching;

    public static String S_STATEConn;

    public static String S_STATEDisconn;

    public static String S_Status;

    public static String S_Timestamp;

    public static String S_Value;

    public static String S_Waiting;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
