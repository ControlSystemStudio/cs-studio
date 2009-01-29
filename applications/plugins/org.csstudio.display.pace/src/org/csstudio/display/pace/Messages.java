package org.csstudio.display.pace;

import org.eclipse.osgi.util.NLS;

/** Access to messages externalized to
 *  language-specific messages*.properties files.
 *  
 *  @author Kay Kasemir
 *  @author Eclipse "Externalize Strings" wizard
 *    reviewed by Delphy 01/28/09
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.display.pace.messages"; //$NON-NLS-1$

    public static String InstanceLabelProvider_OrigAppendix;
    public static String InstanceLabelProvider_PVValueFormat;
    public static String InstanceLabelProvider_ReadOnlyAppendix;
    public static String RestoreCell;
    public static String RestoreCell_TT;
    public static String SaveError;
    public static String SaveInto;
    public static String SaveMessage;
    public static String SavePVInfoFmt;
    public static String SaveTitle;
    public static String SetColumnValue_Msg;
    public static String SetValue;
    public static String SetValue_Msg;
    public static String SetValue_Title;
    public static String SetValue_TT;
    public static String SystemColumn;
    public static String UnknownValue;
    
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
