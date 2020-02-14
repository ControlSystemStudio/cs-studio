package org.csstudio.phoebus.integration.pvtree;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    /**
     * The bundle name of the localization messages resources.
     */
    private static final String BUNDLE_NAME = "org.csstudio.phoebus.integration.pvtree.messages"; //$NON-NLS-1$

    public static String MultipleInstancesFmt;
    public static String MultipleInstancesTitle;

    // Pvtree messages
    public static String PVTree;
    public static String PVTreeErrorOpenPvtree;
    public static String PVTreeLabel;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        /* prevent instantiation */ }
}
