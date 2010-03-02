package org.csstudio.email.ui;

import org.eclipse.osgi.util.NLS;

/** Externalized Strings
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.email.ui.messages"; //$NON-NLS-1$
    
    public static String AttachedImage;
    public static String DefaultDestination;
    public static String EmailDialogMessage;
    public static String From;
    public static String FromTT;
    public static String MessageBodyTT;
    public static String Preferences;
    public static String SendEmail;
    public static String SMTPHost;
    public static String Subject;
    public static String SubjectTT;
    public static String To;
    public static String ToTT;
    
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
