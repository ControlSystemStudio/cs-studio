package org.csstudio.util.time.swt;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.util.time.swt.messages"; //$NON-NLS-1$

    public static String Date_Sep;
    public static String EndTime;
    public static String StartEndTitle;
    public static String StartTime;
    public static String Time_Date;
    public static String Time_Before;
    public static String Time_Before_TT;
    public static String Time_Now;
    public static String Time_Now_TT;
    public static String Time_SelectYear;
    public static String Time_SelectDate;
    public static String Time_SelectDay;
    public static String Time_SelectHour;
    public static String Time_SelectMinute;
    public static String Time_SelectMonth;
    public static String Time_SelectSeconds;
    public static String Time_Sep;
    public static String Time_Time;
    public static String Time_Midnight;
    public static String Time_Midnight_TT;
    public static String Time_Noon;
    public static String Time_Noon_TT;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
