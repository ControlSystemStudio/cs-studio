package org.csstudio.phoebus.integration.alarm.actions;

import org.csstudio.phoebus.integration.alarm.Activator;
import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    /**
     * The bundle name of the localization messages ressources.
     */

    public static String MultipleInstancesFmt;
    public static String MultipleInstancesTitle;

    // AlarmTable app name
    public static String AlarmTable;
    public static String AlarmTableErrorOpen;

    // AlarmTree app name
    public static String AlarmTree;
    public static String AlarmTreeErrorOpen;

    // AlarmPanel app name
    public static String AlarmPanel;
    public static String AlarmPanelErrorOpen;

    static {
        // initialize resource bundle
        NLS.initializeMessages(Activator.PLUGIN_ID, Messages.class);
    }

    private Messages() {
        /* prevent instantiation */
    }
}
