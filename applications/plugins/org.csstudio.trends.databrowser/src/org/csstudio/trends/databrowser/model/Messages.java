package org.csstudio.trends.databrowser.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.model.messages"; //$NON-NLS-1$
    
    public static String LivePVDisconnected;

    public static String Sevr_INVALID;

    public static String Sevr_MAJOR;

    public static String Sevr_MINOR;

    public static String Sevr_OK;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
