package org.csstudio.phoebus.integration.channel.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    /**
     * The bundle name of the localization messages ressources.
     */
    private static final String BUNDLE_NAME = "org.csstudio.phoebus.integration.channel.actions.messages"; //$NON-NLS-1$

    public static String MultipleInstancesFmt;
    public static String MultipleInstancesTitle;

    // ChannelTable messages
    public static String ChannelTable;
    public static String ChannelTableErrorOpen;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        /* prevent instantiation */
    }
}
