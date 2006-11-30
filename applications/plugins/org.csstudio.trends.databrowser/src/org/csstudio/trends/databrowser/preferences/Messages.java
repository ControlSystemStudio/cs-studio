package org.csstudio.trends.databrowser.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.preferences.messages"; //$NON-NLS-1$

    public static String Default_URL1;

    public static String Default_URL2;

    public static String Default_URL3;

    public static String PageTitle;

    public static String URL_Label1;
    
    public static String URL_Label2;
    
    public static String URL_Label3;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
