package org.csstudio.trends.databrowser.ploteditor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.ploteditor.messages"; //$NON-NLS-1$

    public static String Error_Not_Saved_Message;

    public static String Error_Not_Saved_Title;

    public static String OpenArchiveView;

    public static String OpenAsView;
    public static String OpenConfigView;
    public static String OpenPerspective;
    public static String OpenSampleView;
    public static String OpenExportView;
    public static String SaveBrowserConfig;
    
    static
    {   // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
