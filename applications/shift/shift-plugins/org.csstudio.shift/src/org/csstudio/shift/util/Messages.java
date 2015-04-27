package org.csstudio.shift.util;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.csstudio.shift.util.messages"; //$NON-NLS-1$

    public static String search;
    public static String shift;
    public static String to;
    public static String from;
    public static String owner;
    public static String leadOperator;
    public static String onShiftPersonal;
    public static String closeUser;
    public static String type;
    public static String status;

    static {
	// initialize resource bundle
	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
