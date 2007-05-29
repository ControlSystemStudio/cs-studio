package org.csstudio.trends.databrowser.plotview;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.plotview.messages"; //$NON-NLS-1$

    public static String OpenInEditor;
    
    static
    {   // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
