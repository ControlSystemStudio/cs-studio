package org.csstudio.scan.ui.plot;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.scan.ui.plot.messages"; //$NON-NLS-1$
    public static String Device_X;
    public static String Device_X_TT;
    public static String Device_Y;
    public static String Device_Y_Add;
    public static String Device_Y_Add_TT;
    public static String Device_Y_Fmt;
    public static String Device_Y_Remove;
    public static String Device_Y_Remove_TT;
    public static String Device_Y_TT;
    public static String Error;
    public static String OpenPlotError;
    public static String Plot_DefaultXAxisLabel;
    public static String Plot_DefaultYAxisLabel;
    public static String Scan;
    public static String Scan_TT;
    public static String Toolbar_Hide;
    public static String Toolbar_Show;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
