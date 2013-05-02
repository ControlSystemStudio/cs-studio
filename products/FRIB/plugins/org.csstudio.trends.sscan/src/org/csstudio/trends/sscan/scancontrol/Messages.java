package org.csstudio.trends.sscan.scancontrol;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	 private static final String BUNDLE_NAME = "org.csstudio.trends.sscan.scancontrol.messages"; //$NON-NLS-1$

		public static String Scan_statusWaitingForPV;
		public static String Scan_statusLabelText;
		public static String Scan_statusSearching;
		public static String Scan_infoStateDisconnected;
		public static String Scan_infoStateConnected;
		public static String Scan_pvNameLabelText;
		public static String Scan_infoStateNotConnected;
		public static String Scan_pvNameFieldToolTipText;
		public static String Scan_statusConnected;

		public static String S_ErrorDialogTitle;
	    public static String S_Adjust;
	    public static String S_AdjustFailed;
	    public static String S_AdjustValue;
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
