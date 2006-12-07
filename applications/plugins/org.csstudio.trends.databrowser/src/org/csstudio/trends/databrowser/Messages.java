package org.csstudio.trends.databrowser;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.messages"; //$NON-NLS-1$

    public static String DataBrowser;

    public static String Fetch_Archive;

    public static String FetchDataForPV;

    public static String FetchingSample;

    public static String AddPV;

    public static String AddPV_TT;

    public static String EnterPVName;

    public static String EnterNewPVName;

    public static String UnitMarkerEnd;

    public static String UnitMarkerStart;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
