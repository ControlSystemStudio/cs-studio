package org.csstudio.archivereader.rdb;

import org.eclipse.osgi.util.NLS;

/** Externalized strings
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.archivereader.rdb.messages"; //$NON-NLS-1$
    public static String Password;
    public static String PreferenceTitle;
    public static String Schema;
    public static String StoredProcedure;
    public static String User;
    
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
