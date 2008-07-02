package org.csstudio.utility.logsender;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.utility.logsender.messages"; //$NON-NLS-1$
    public static String DateFmt;
    public static String InitialText;
    public static String Level;
    public static String LevelTT;
    public static String Send;
    public static String SendTT;
    public static String StatusFmt;
    public static String Text;
    public static String TextTT;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
