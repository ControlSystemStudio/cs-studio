package org.csstudio.logbook.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.logbook.ui.messages"; //$NON-NLS-1$
    public static String ELogEntryView_AddImage;
    public static String ELogEntryView_AddImageTT;
    public static String LogEntry_ErrorCannotConnectFMT;
    public static String LogEntry_ErrorFMT;
    public static String LogEntry_ErrorNoLog;
    public static String LogEntry_InitialMessage;
    public static String LogEntry_Logbook;
    public static String LogEntry_Logbook_TT;
    public static String LogEntry_OKMessage;
    public static String LogEntry_Password;
    public static String LogEntry_Password_TT;
    public static String LogEntry_Submit;
    public static String LogEntry_Submit_TT;
    public static String LogEntry_Text;
    public static String LogEntry_Text_TT;
    public static String LogEntry_Title;
    public static String LogEntry_Title_TT;
    public static String LogEntry_User;
    public static String LogEntry_User_TT;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
