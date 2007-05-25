package org.csstudio.trends.databrowser.plotpart;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.plotpart.messages"; //$NON-NLS-1$

    public static String Fetch_Archive;
    public static String FetchDataForPV;
    public static String FetchingSample;
    
    public static String RemoveMarkers;
    public static String RemoveMarkers_TT;
    
    public static String StartScroll_TT;
    public static String StopScroll_TT;
    public static String TimeConfig;
    public static String TimeConfig_TT;

    public static String UnitMarkerEnd;
    public static String UnitMarkerStart;

    static
    {   // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {}
}
