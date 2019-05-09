package org.csstudio.logging.es.widget;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.logging.es.widget.messages"; //$NON-NLS-1$

    public static String Column_Severity;
    public static String Column_Text;
    public static String Column_Time;
    public static String Error;
    public static String LogviewFigure_ErrorOpening;
    public static String LogviewFigure_OpenInView;
    public static String LogviewModel_FilterApplication;
    public static String LogviewModel_FilterClass;
    public static String LogviewModel_FilterHost;
    public static String LogviewModel_MinSeverity;
    public static String LogviewModel_StartTime;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
