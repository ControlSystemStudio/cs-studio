/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/** Helper for dealing with time stamps
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimestampHelper
{
    final private static DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:SS");
   
    /** Format EPICS time stamp as string
     *  @param timestamp {@link org.epics.util.time.Timestamp}
     *  @return {@link String}
     */
    public static String format(org.epics.util.time.Timestamp timestamp)
    {
        synchronized (format)
        {
            return format.format(timestamp.toDate());
        }
    }
    
    /** Convert EPICS time stamp into SQL time stamp
     *  @param timestamp {@link org.epics.util.time.Timestamp}
     *  @return {@link java.sql.Timestamp}
     */
    public static java.sql.Timestamp toSQLTime(org.epics.util.time.Timestamp timestamp)
    {
        final java.sql.Timestamp sql = new java.sql.Timestamp(timestamp.toDate().getTime());
        sql.setNanos(timestamp.getNanoSec());
        return sql;
    }

    /** Convert SQL time stamp into EPICS time stamp
     *  @param timestamp{@link java.sql.Timestamp}
     *  @return {@link org.epics.util.time.Timestamp}
     */
    public static org.epics.util.time.Timestamp toEPICSTime(java.sql.Timestamp timestamp)
    {
        return org.epics.util.time.Timestamp.of(timestamp.getTime()/1000L, timestamp.getNanos());
    }
}
