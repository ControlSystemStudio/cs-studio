package org.csstudio.apputil.ui.elog;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.apputil.ui.elog.messages"; //$NON-NLS-1$

    public static String ELog_ActionName;
    public static String ELog_ActionName_TT;
    public static String ELog_Dialog_Body;
    public static String ELog_Dialog_Body_TT;
    public static String ELog_Dialog_DialogTitle;

    public static String Elog_Dialog_ImageComment;
    public static String ELog_Dialog_Logbook;
    public static String ELog_Dialog_Logbook_TT;
    public static String ELog_Dialog_Password;
    public static String ELog_Dialog_Password_TT;
    public static String ELog_Dialog_Title;
    public static String ELog_Dialog_Title_TT;
    public static String ELog_Dialog_User;
    public static String ELog_Dialog_User_TT;
    public static String ELog_Dialog_WindowTitle;
    
    public static String ImagePreview_ImageError;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
