package org.csstudio.logbook.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.csstudio.logbook.util.messages"; //$NON-NLS-1$

    public static String search;
    public static String logbook;
    public static String tag;
    public static String properties;
    public static String to;
    public static String from;

    public static String history;
    public static String page;
    public static String count;

    static {
	// initialize resource bundle
	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
