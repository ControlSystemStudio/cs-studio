package org.csstudio.trayicon;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.csstudio.trayicon.messages"; //$NON-NLS-1$

    public static String TrayDialog_rememberDecision;
    public static String TrayDialog_title;
    public static String TrayDialog_question;
    public static String TrayDialog_minimize;
    public static String TrayDialog_exit;
    public static String TrayDialog_cancel;

    public static String TrayPreferences_saveFailed;
    public static String TrayPreferences_minimize;
    public static String TrayPreferences_startMinimized;
    public static String TrayPreferences_always;
    public static String TrayPreferences_never;
    public static String TrayPreferences_prompt;

    public static String TrayIcon_open;
    public static String TrayIcon_exit;

    public static String TrayIcon_tooltip;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() { /* prevent instantiation */ }

}
