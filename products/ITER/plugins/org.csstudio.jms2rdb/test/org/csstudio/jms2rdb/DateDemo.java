/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/** Date/time test that was used to analyze a problem where
 *  JMS2RDB created entries in RDB that were 1 hour off during
 *  eastern daylight saving time.
 *  
 *  jdk1.5.0_05: 1 hour off
 *  jdk1.5.0_09: OK (also if compiled with _05)
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DateDemo
{
    public static void main(String[] args)
    {
        final SimpleDateFormat date_format =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        final Calendar now = Calendar.getInstance();
        System.out.println(now.getTimeZone().getDisplayName());
        System.out.println("In DST: " + 
                now.getTimeZone().inDaylightTime(now.getTime()));
        System.out.println(date_format.format(now.getTime()));
    }
}
