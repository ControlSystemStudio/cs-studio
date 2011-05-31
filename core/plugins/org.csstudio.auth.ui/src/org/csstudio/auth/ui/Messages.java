package org.csstudio.auth.ui;

import org.eclipse.osgi.util.NLS;

/** Localized strings
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.auth.ui.messages"; //$NON-NLS-1$

    public static String LogOut;

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
