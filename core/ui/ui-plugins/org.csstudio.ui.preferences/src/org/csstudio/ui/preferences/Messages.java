package org.csstudio.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.ui.preferences.messages"; //$NON-NLS-1$

    public static String CorePreferencesMessage;
    public static String ApplicationsPreferenceMessage;

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
