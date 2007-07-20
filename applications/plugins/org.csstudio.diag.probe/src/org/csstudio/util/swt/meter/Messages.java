package org.csstudio.util.swt.meter;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.util.swt.meter.messages"; //$NON-NLS-1$

    public static String        MeterWidget_NoNumericInfo;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // Prevent instantiation
    }
}
