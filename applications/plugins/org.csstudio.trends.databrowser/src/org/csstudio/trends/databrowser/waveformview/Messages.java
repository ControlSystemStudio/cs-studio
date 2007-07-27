package org.csstudio.trends.databrowser.waveformview;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.trends.databrowser.waveformview.messages"; //$NON-NLS-1$

    public static String        WaveformView_NoPlot;
    public static String        WaveformView_PV;
    public static String        WaveformView_PV_TT;
    public static String        WaveformView_Quality;
    public static String        WaveformView_SampleIndex_TT;
    public static String        WaveformView_SevrStat;
    public static String        WaveformView_Source;
    public static String        WaveformView_Timestamp;
    public static String        WaveformView_XAxis;
    
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
