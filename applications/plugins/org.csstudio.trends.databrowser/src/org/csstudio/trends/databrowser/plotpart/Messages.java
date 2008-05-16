package org.csstudio.trends.databrowser.plotpart;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.plotpart.messages"; //$NON-NLS-1$

    public static String AddPV;
    public static String AddPV_TT;
    public static String EnterPVName;
    public static String EnterNewPVName;
    public static String AddFormula;
    public static String AddFormula_TT;

    public static String Fetch_Archive;
    public static String FetchDataForPV;
    public static String FetchingSample;
    
    public static String StartScroll_TT;
    public static String StopScroll_TT;
    public static String TimeConfig;
    public static String TimeConfig_TT;

    public static String Undo_Format;
    public static String Undo_TT;
    public static String UnitMarkerEnd;
    public static String UnitMarkerStart;

    static
    {   // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
