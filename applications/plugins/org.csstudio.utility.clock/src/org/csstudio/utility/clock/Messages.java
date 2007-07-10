package org.csstudio.utility.clock;

import org.eclipse.osgi.util.NLS;

/** @author Kay Kasemir */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.utility.clock.preferences.messages"; //$NON-NLS-1$

    public static String        PreferencePage_ErrorMsg;

    public static String        PreferencePage_Hours;

    public static String        PreferencePage_Title;
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
