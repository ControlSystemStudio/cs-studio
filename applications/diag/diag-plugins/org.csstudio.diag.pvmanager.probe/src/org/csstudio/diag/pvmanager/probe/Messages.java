package org.csstudio.diag.pvmanager.probe;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.diag.pvmanager.probe.messages"; //$NON-NLS-1$

    public static String MultipleInstancesFmt;

    public static String MultipleInstancesTitle;

    public static String Probe_copyValueToClipboardButtonText;

    public static String Probe_copyValueToClipboardButtonToolTipText;

    public static String Probe_errorCopyValueToClipboard;

    public static String Probe_errorOpenProbe;

    public static String Probe_infoAlarmLimits;

    public static String Probe_infoChannelHandlerName;

    public static String Probe_infoChannel;

    public static String Probe_infoChannelProperties;

    public static String Probe_infoConnected;

    public static String Probe_infoControlLimits;

    public static String Probe_infoDisplay;

    public static String Probe_infoDisplayLimits;

    public static String Probe_infoExpressionName;

    public static String Probe_infoExpressionType;

    public static String Probe_infoFormula;

    public static String Probe_infoLabels;

    public static String Probe_infoTimestamp;

    public static String Probe_infoType;

    public static String Probe_infoUnit;

    public static String Probe_infoUsageCount;

    public static String Probe_infoValue;

    public static String Probe_infoWarningLimits;

    public static String Probe_newValueFieldToolTipText;

    public static String Probe_newValueLabelText;

    public static String Probe_retryAfterTimeout;

    public static String Probe_sectionChangeValue;

    public static String Probe_sectionDetails;

    public static String Probe_sectionMetadata;

    public static String Probe_sectionValue;

    public static String Probe_sectionViewer;

    public static String Probe_showHideButtonText;

    public static String Probe_showHideButtonToolTipText;

    public static String Probe_statusConnected;

    public static String Probe_statusLabelText;

    public static String Probe_statusSearching;

    public static String Probe_statusWaitingForPV;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
