
package org.csstudio.alarm.jms2ora.util;

public class ApplicState
{
    public static final int INIT = 0;
    public static final int OK = 2;
    public static final int WORKING = 4;
    public static final int SLEEPING = 8;
    public static final int LEAVING = 16;
    public static final int ERROR = 1;
    public static final int FATAL = 3;
    
    public static final int ERROR_MASK = 0x00000001;
}
