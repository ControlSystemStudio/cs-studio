/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import java.util.Calendar;

import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;

/** Time stamp gymnastics
 *  @author Kay Kasemir
 */
public class TimestampUtil
{
	final public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.NNNNNNNNN";
	final public static String FORMAT_SECONDS = "yyyy-MM-dd HH:mm:ss";

	/** @param timestamp EPICS Timestamp
	 *  @return SQL Timestamp
	 */
    public static java.sql.Timestamp toSQLTimestamp(final Timestamp timestamp)
    {
    	final long nanoseconds = timestamp.getNanoSec();
    	// Only millisecond resolution
        java.sql.Timestamp stamp = new java.sql.Timestamp(timestamp.getSec() * 1000  +
                             nanoseconds / 1000000);
        // Set nanoseconds (again), but this call uses the full
        // nanosecond resolution
        stamp.setNanos((int) nanoseconds);
        return stamp;
    }

	/** @param sql_time SQL Timestamp
	 *  @return EPICS Timestamp
	 */
    public static Timestamp fromSQLTimestamp(final java.sql.Timestamp sql_time)
    {
        final long millisecs = sql_time.getTime();
        final long seconds = millisecs/1000;
        final int nanoseconds = sql_time.getNanos();
        return Timestamp.of(seconds,  nanoseconds);
    }

    /** Round time to next multiple of given duration
     *  @param time Original time stamp
     *  @param duration Duration to use for rounding
     *  @return Time stamp rounded up to next multiple of duration
     */
    public static Timestamp roundUp(final Timestamp time, final TimeDuration duration)
    {
    	return roundUp(time, duration.getSec());
    }

    /** Round time to next multiple of given seconds
     *  @param time Original time stamp
     *  @param seconds Seconds to use for rounding
     *  @return Time stamp rounded up to next multiple of seconds
     */
    public static Timestamp roundUp(final Timestamp time, final long seconds)
    {
        if (seconds <= 0)
            return time;
        
        // Directly round seconds within an hour
        if (seconds <= 60*60)
        {
	        long secs = time.getSec();
	        if (time.getNanoSec() > 0)
	        	++secs;
	    	final long periods = secs / seconds;
	        secs = (periods + 1) * seconds;
	        return Timestamp.of(secs, 0);
        }

        // When rounding "2012-01-19 12:23:14" by 2 hours,
        // the user likely expects "2012-01-19 14:00:00"
        // because 12.xx rounded up by 2 is 14.
        //
        // Depending on the time zone, rounding up by 2 hours
        // based on epoch seconds might however result in odd-numbered hours
        // in local time.
        
        // Perform computation in local time, relative to midnight.
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(time.toDate());

        // Round the HH:MM within the day
        long secs = cal.get(Calendar.HOUR_OF_DAY) * 60L * 60 +
        		    cal.get(Calendar.MINUTE) * 60L;
        final long periods = secs / seconds;
        secs = (periods + 1) * seconds;

        // Create time for rounded HH:MM
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        final long midnight = cal.getTimeInMillis() / 1000;
        return Timestamp.of(midnight + secs, 0);
    }
}
