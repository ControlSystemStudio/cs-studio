package org.csstudio.phoebus.integration.pvtable;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    /**
     * The bundle name of the localization messages resources.
     */
    private static final String BUNDLE_NAME = "org.csstudio.phoebus.integration.pvtable.messages"; //$NON-NLS-1$

    // PvTable app name
    public static String Pvtable;
    public static String PvtableErrorOpenPvtable;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        /* prevent instantiation */
    }
}
