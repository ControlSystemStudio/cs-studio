package org.csstudio.utility.sysmon;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.utility.sysmon.messages"; //$NON-NLS-1$

    public static String        SysMon_Free_TT;
    public static String        SysMon_FreeLabel;
    public static String        SysMon_GC_TT;
    public static String        SysMon_GCLabel;
    public static String        SysMon_Max_TT;
    public static String        SysMon_MaxLabel;
    public static String        SysMon_MemFormat;
    public static String        SysMon_Total_TT;
    public static String        SysMon_TotalLabel;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // Prevent instantiation
    }
}
