package org.csstudio.startuphelper;

import org.eclipse.osgi.util.NLS;

/** Localized messages
 *  @author IDE
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.startuphelper.messages"; //$NON-NLS-1$
    public static String CloseProjectErrorFmt;
    public static String CreateProjectErrorFmt;
    public static String DefaultProjectName;
    public static String Error;
    public static String OpenProjectErrorFmt;

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
