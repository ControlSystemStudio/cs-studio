/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import java.text.Format;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.epics.util.time.TimestampFormat;

/** Time stamp gymnastics
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimestampHelper
{
    final public static String FORMAT_FULL = "yyyy/MM/dd HH:mm:ss.NNNNNNNNN";
	final public static String FORMAT_SECONDS = "yyyy/MM/dd HH:mm:ss";

	/** Time stamp format */
	final private static Format time_format = new TimestampFormat(TimestampHelper.FORMAT_FULL);
	
    /** @param timestamp {@link Timestamp}, may be <code>null</code>
	 *  @return Time stamp formatted as string
	 */
	public static String format(final Timestamp timestamp)
	{
		if (timestamp == null)
			return "null";
		synchronized (time_format)
		{
			return time_format.format(timestamp);
		}
	}
	
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
    
    /** @param millisecs Milliseconds since 1970 epoch
     *  @return EPICS Timestamp
     */
    public static Timestamp fromMillisecs(final long millisecs)
    {
        long seconds = millisecs/1000;
        int nanoseconds = (int) (millisecs % 1000) * 1000000;
        if (nanoseconds < 0)
        {
            long pastSec = nanoseconds / 1000000000;
            pastSec--;
            seconds += pastSec;
            nanoseconds -= pastSec * 1000000000;
        }
        return Timestamp.of(seconds,  nanoseconds);
    }
    
    /** @param calendar Calendar
     *  @return EPICS Timestamp
     */
    public static Timestamp fromCalendar(final Calendar calendar)
    {
        return fromMillisecs(calendar.getTimeInMillis());
    }

    /** @param timestamp EPICS Timestamp
     *  @return Milliseconds since 1970 epoch
     */
    public static long toMillisecs(final Timestamp timestamp)
    {
        return timestamp.getSec()*1000 + timestamp.getNanoSec()/1000000;
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

    final public static long SECS_PER_HOUR = TimeUnit.HOURS.toSeconds(1);
    final public static long SECS_PER_MINUTE = TimeUnit.MINUTES.toSeconds(1);
    final public static long SECS_PER_DAY = TimeUnit.DAYS.toSeconds(1);
    
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
        if (seconds <= SECS_PER_HOUR)
        {
	        long secs = time.getSec();
	        if (time.getNanoSec() > 0)
	        	++secs;
	    	final long periods = secs / seconds;
	        secs = (periods + 1) * seconds;
	        return Timestamp.of(secs, 0);
        }

        // When rounding "2012/01/19 12:23:14" by 2 hours,
        // the user likely expects "2012/01/19 14:00:00"
        // because 12.xx rounded up by 2 is 14.
        //
        // In other words, rounding by 2 should result in an even hour,
        // but this is in the user's time zone.
        // When rounding based on the epoch seconds, which could differ
        // by an odd number of hours from the local time zone, rounding by
        // 2 hours could result in odd-numbered hours in local time.
        //
        // The addition of leap seconds can further confuse matters,
        // so perform computations that go beyond an hour in local time,
        // relative to midnight of the given time stamp.
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(time.toDate());

        // Round the HH:MM within the day
        long secs = cal.get(Calendar.HOUR_OF_DAY) * SECS_PER_HOUR +
        		    cal.get(Calendar.MINUTE) * SECS_PER_MINUTE;
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
