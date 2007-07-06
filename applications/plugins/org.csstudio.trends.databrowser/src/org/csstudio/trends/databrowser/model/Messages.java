package org.csstudio.trends.databrowser.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.model.messages"; //$NON-NLS-1$
    
    public static String INVALID;
    public static String LastArchivedSample;
    public static String LiveSample;
    public static String LivePVDisconnected;
    public static String ModelSample_QualityOriginal;
    public static String ModelSample_QualityInterpolated;
    public static String ModelSample_SourceQuality;
    public static String NoNumericValue;
    public static String Request_optimized;
    public static String Request_raw;
    public static String Sevr_INVALID;
    public static String Sevr_MAJOR;
    public static String Sevr_MINOR;
    public static String Sevr_OK;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
