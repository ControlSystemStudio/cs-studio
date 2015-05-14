package org.csstudio.diag.postanalyser;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.diag.postanalyser.messages"; //$NON-NLS-1$
    public static String Algorithm_Correlation;
    public static String Algorithm_CorrelationMessage;
    public static String Algorithm_ExpFit;
    public static String Algorithm_FFT;
    public static String Algorithm_FitError;
    public static String Algorithm_GaussFit;
    public static String Algorithm_LineFit;
    public static String Algorithm_NoDataPoints;
    public static String Algorithm_NoSecondChannelError;
    public static String Algorithm_Original;
    public static String Algorithm_TimeAxisLabel;
    public static String Algorithm_XYArraysDiffer;
    public static String ExponentialFit_DecayMessage;
    public static String ExponentialFit_RiseMessage;
    public static String FFT_Message;
    public static String FFT_XAxisLabel;
    public static String GaussFit_Message;
    public static String GUI_Algorithm;
    public static String GUI_AlgorithmError;
    public static String GUI_AutoZoomX;
    public static String GUI_AutoZoomX_TT;
    public static String GUI_Baseline;
    public static String GUI_Baseline_TT;
    public static String GUI_Channel_;
    public static String GUI_Crop;
    public static String GUI_Crop_TT;
    public static String GUI_Export;
    public static String GUI_NoChannelError;
    public static String GUI_NoSecondChannelError;
    public static String GUI_SecondChannel_;
    public static String GUI_Window_;
    public static String LineFit_Error;
    public static String LineFit_Message;
    public static String MinMaxFinder_Message;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // NOP
    }
}
