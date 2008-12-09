package org.csstudio.apputil.ui.swt;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.apputil.ui.swt.messages"; //$NON-NLS-1$
    public static String AutoSizeColumns;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // prevent instantiation
    }
}
