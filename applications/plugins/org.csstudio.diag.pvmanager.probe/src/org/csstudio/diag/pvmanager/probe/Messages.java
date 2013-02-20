package org.csstudio.diag.pvmanager.probe;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.diag.pvmanager.probe.messages"; //$NON-NLS-1$

	public static String MultipleInstancesFmt;

    public static String MultipleInstancesTitle;

    public static String Probe_alarmLabelText;

	public static String Probe_infoButtonToolTipText;

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

	public static String Probe_infoNoInfoAvailable;

	public static String Probe_infoNumericDisplay;

	public static String Probe_infoStateConnected;

	public static String Probe_infoStateDisconnected;

	public static String Probe_infoStateNotConnected;

	public static String Probe_infoTitle;

	public static String Probe_newValueFieldToolTipText;

	public static String Probe_newValueLabelText;

	public static String Probe_pvNameFieldToolTipText;

	public static String Probe_pvNameLabelText;

	public static String Probe_saveToIocButtonText;

	public static String Probe_saveToIocButtonToolTipText;

	public static String Probe_showMeterButtonText;

	public static String Probe_showMeterButtonToolTipText;

	public static String Probe_statusConnected;

	public static String Probe_statusLabelText;

	public static String Probe_statusSearching;

	public static String Probe_statusWaitingForPV;

	public static String Probe_timestampLabelText;

	public static String Probe_valueLabelText;

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
    // Not used...
    public static String S_EnvInfo;
    public static String S_ModValue;
    public static String S_NoChannel;
    public static String S_OK;
    public static String S_Period;
    public static String S_Seconds;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
