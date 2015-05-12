package org.csstudio.utility.pv.ui;

import org.eclipse.osgi.util.NLS;

/** Localization
 *  @author IDE
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.utility.pv.ui.messages"; //$NON-NLS-1$
    public static String PreferencePage_DefaultPV;
    public static String PreferencePage_Message;
    public static String PreferencePage_RestartInfo;
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
