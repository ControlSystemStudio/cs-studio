package org.csstudio.trends.databrowser.sampleview;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.sampleview.messages"; //$NON-NLS-1$

    public static String InfoCol;
    public static String NoPlot;
    public static String PV_TT;
    public static String PVLabel;
    public static String Refesh;
    public static String Refresh_TT;
    public static String SourceCol;
    public static String TimeCol;
    public static String ValueCol;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
