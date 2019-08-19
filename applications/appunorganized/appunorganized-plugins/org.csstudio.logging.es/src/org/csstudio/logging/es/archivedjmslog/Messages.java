package org.csstudio.logging.es.archivedjmslog;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.logging.es.archivedjmslog.messages"; //$NON-NLS-1$

    public static String Disconnected;
    public static String ErrorNoURL;

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
