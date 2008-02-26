/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.util.time.swt;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.util.time.swt.messages"; //$NON-NLS-1$

    public static String Date_Sep;
    
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
