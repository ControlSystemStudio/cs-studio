package org.csstudio.apputil.ui.workbench;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.apputil.ui.workbench.messages"; //$NON-NLS-1$
    public static String OpenPerspectiveReset;
    public static String OpenPerspectiveResetQuestion;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
