package org.csstudio.diag.epics.pvtree;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.diag.epics.pvtree.messages"; //$NON-NLS-1$

    public static String PV;
    public static String PV_Label;
    public static String PV_TT;
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
