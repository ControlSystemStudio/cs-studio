/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.time;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.apputil.ui.time.messages"; //$NON-NLS-1$

    public static String Date_Sep;

    public static String half_day;

    public static String half_day_TT;

    public static String one_day;

    public static String one_day_TT;

    public static String seven_days;

    public static String seven_days_TT;
    
    public static String StartEnd_AbsEnd;
    public static String StartEnd_AbsEnd_TT;
    public static String StartEnd_EndError;
    public static String StartEnd_EndTime;
    public static String StartEnd_EndTime_TT;
    public static String StartEnd_Error;
    public static String StartEnd_RelEnd;
    public static String StartEnd_RelEnd_TT;
    public static String StartEnd_AbsStart;
    public static String StartEnd_AbsStart_TT;
    public static String StartEnd_RelStart;
    public static String StartEnd_RelStart_TT;
    public static String StartEnd_StartError;
    public static String StartEnd_StartExceedsEnd;
    public static String StartEnd_StartTime;
    public static String StartEnd_StartTime_TT;
    public static String StartEnd_Title;

    public static String three_days;

    public static String three_days_TT;

    public static String Time_Years;
    public static String Time_Months;
    public static String Time_Days;
    public static String Time_Hours;
    public static String Time_Minutes;
    public static String Time_Seconds;
    
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
    { // prevent instantiation
    }
}
