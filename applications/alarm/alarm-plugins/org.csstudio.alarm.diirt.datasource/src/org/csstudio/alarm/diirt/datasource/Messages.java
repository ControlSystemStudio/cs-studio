package org.csstudio.alarm.diirt.datasource;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.alarm.diirt.datasource.messages"; //$NON-NLS-1$

    public static String Name;
    public static String AlarmSeverity;
    public static String CurrentSeverity;
    public static String CurrentStatus;
    public static String Active;
    public static String AlarmState;
    public static String Value;
    public static String Enable;
    public static String Type;
    public static String Time;
    public static String AlarmCount;

    public static String Acknowledge;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
