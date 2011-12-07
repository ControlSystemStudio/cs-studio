package org.csstudio.scan.ui.scantree;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.scan.ui.scantree.messages"; //$NON-NLS-1$
    public static String Cat_Delay;
    public static String Cat_Log;
    public static String Cat_Loop;
    public static String Cat_Set;
    public static String Cat_Wait;
    public static String Lbl_Delay;
    public static String Lbl_Device;
    public static String Lbl_LogDevices;
    public static String Lbl_LoopEnd;
    public static String Lbl_LoopStart;
    public static String Lbl_LoopStep;
    public static String Lbl_SetValue;
    public static String Lbl_WaitTolerance;
    public static String Lbl_WaitValue;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
