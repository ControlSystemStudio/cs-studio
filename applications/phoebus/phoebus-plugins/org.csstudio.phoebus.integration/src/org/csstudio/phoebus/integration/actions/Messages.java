package org.csstudio.phoebus.integration.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    /**
     * The bundle name of the localization messages ressources.
     */
    private static final String BUNDLE_NAME = "org.csstudio.phoebus.integration.actions.messages"; //$NON-NLS-1$

    public static String MultipleInstancesFmt;
    public static String MultipleInstancesTitle;

    // Probe messages
    public static String Probe;
    public static String ProbeErrorOpenProbe;
    public static String ProbeLabel;

    // Pvtree messages
    public static String PVTree;
    public static String PVTreeErrorOpenPvtree;
    public static String PVTreeLabel;

    // Email messages
    public static String Email;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        /* prevent instantiation */ }
}
