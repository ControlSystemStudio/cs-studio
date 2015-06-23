package org.csstudio.logbook.midas;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.csstudio.logbook.midas.messages"; //$NON-NLS-1$
    public static String PreferenceTitle;
    public static String Host;
    public static String Port;

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
