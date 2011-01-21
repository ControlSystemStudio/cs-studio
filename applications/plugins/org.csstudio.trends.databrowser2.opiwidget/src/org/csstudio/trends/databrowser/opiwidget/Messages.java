package org.csstudio.trends.databrowser.opiwidget;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.opiwidget.messages"; //$NON-NLS-1$
    public static String Error;
    public static String ErrorDetailFmt;
    public static String OpenDataBrowserErrorFmt;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
