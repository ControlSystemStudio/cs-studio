/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb;

import java.sql.Timestamp;
import java.util.Calendar;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;

/** Time stamp format conversions.
 *  @author Kay Kasemir
 */
public class TimeWarp
{
	/** Convert CSS Timestamp into SQL Timestamp.
     *  @param time CSS ITimestamp
     *  @return SQL Timestamp
     */
    public static Timestamp getSQLTimestamp(final ITimestamp time)
    {
        // Only millisecond resolution
        Timestamp stamp = new Timestamp(time.seconds() * 1000  + 
                             time.nanoseconds() / 1000000);
        // Set nanoseconds (again), but this call uses the full
        // nanosecond resolution
        stamp.setNanos((int) time.nanoseconds());
        return stamp;
    }
    
	/** Convert CSS Timestamp into SQL Timestamp.
     *  @param time CSS ITimestamp
     *  @return SQL Timestamp
     */
    @SuppressWarnings("deprecation")
    public static Timestamp oldGetSQLTimestamp(final ITimestamp time)
    {
        final Calendar calendar = time.toCalendar();
        // Issues:
        // 1) This constructor is deprecated,
        // 2) This uses about 3 times the CPU
        //    of the getSQLTimestamp() implementation above
        Timestamp stamp = new Timestamp(
                        calendar.get(Calendar.YEAR) - 1900,
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        calendar.get(Calendar.SECOND),
                        (int)time.nanoseconds());
        return stamp;
    }

    /** Convert SQL Timestamp into CSS Timestamp.
     *  @param time SQL Timestamp
     *  @return CSS ITimestamp
     */
    public static ITimestamp getCSSTimestamp(final Timestamp sql_time)
	{
		final long millisecs = sql_time.getTime();
		final long seconds = millisecs/1000;
		final long nanoseconds = sql_time.getNanos();
		return TimestampFactory.createTimestamp(seconds, nanoseconds);
	}
}
