/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Helper for dealing with time stamps
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimestampHelper
{
    private static final ZoneId zone = ZoneId.systemDefault();
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

    /** Format EPICS time stamp as string
     *  @param time {@link Instant}
     *  @return {@link String}
     */
    public static String format(Instant time)
    {
        if (time != null) {
            return time.atZone(ZoneId.systemDefault()).format(format);
        }
        return "null";
    }

    /** Convert EPICS time stamp into SQL time stamp
     *  @param timestamp {@link Instant}
     *  @return {@link java.sql.Timestamp}
     */
    public static java.sql.Timestamp toSQLTime(Instant timestamp)
    {
        final java.sql.Timestamp sql = new java.sql.Timestamp(timestamp.toEpochMilli());
        sql.setNanos(timestamp.getNano());
        return sql;
    }

    /** Convert SQL time stamp into EPICS time stamp
     *  @param timestamp{@link java.sql.Timestamp}
     *  @return {@link Instant}
     */
    public static Instant toEPICSTime(java.sql.Timestamp timestamp)
    {
        return Instant.ofEpochSecond(timestamp.getTime()/1000L, timestamp.getNanos());
    }
}
