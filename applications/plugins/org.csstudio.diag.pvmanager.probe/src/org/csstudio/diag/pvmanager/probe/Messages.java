package org.csstudio.diag.pvmanager.probe;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.diag.pvmanager.probe.messages"; //$NON-NLS-1$

	public static String Probe_alarmLabelTest;

	public static String Probe_infoChannelInformationFor;

	public static String Probe_infoDataType;

	public static String Probe_infoEnumMetadata;

	public static String Probe_infoHighAlarmLimit;

	public static String Probe_infoHighDisplayLimit;

	public static String Probe_infoHighWarnLimit;

	public static String Probe_infoLabels;

	public static String Probe_infoLowAlarmLimit;

	public static String Probe_infoLowDisplayLimit;

	public static String Probe_infoLowWarnLimit;

	public static String Probe_infoNumericDisplay;

	public static String Probe_pvNameLabelText;

	public static String Probe_statusConnected;

	public static String S_SaveToIoc;
	public static String S_SaveToIocTooltip;
	public static String S_ErrorDialogTitle;
	public static String S_SaveToIocExecutionError;
	public static String S_SaveToIocNotDefinedError;
	public static String S_SaveToIocNotEnabled;
    public static String S_Adjust;
    public static String S_AdjustFailed;
    public static String S_AdjustValue;
    public static String S_ChannelInfo;
    public static String S_CreateError;
    public static String S_Disconnected;
    public static String S_EnterPVName;

    // Not used...
    public static String S_EnvInfo;
    public static String S_Info;
    public static String S_Meter;
    public static String S_Meter_TT;
    public static String S_ModValue;
    public static String S_NewValueLabel;

    public static String S_NewValueTT;

    public static String S_NoChannel;
    public static String S_NoInfo;
    public static String S_NotConnected;
    public static String S_ObtainInfo;
    public static String S_OK;
    public static String S_Period;
    public static String S_Seconds;
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
    { /* prevent instantiation */ }
}
