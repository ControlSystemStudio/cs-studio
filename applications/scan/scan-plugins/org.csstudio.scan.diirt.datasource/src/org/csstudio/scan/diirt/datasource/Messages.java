package org.csstudio.scan.diirt.datasource;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.scan.diirt.datasource.messages"; //$NON-NLS-1$

    public static String Pause;
    public static String Resume;
    public static String Abort;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
