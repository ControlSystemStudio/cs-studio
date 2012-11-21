/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

/** Time stamp gymnastics
 *  @author Kay Kasemir
 */
public class TimestampUtil
{
	/** @param timestamp EPICS Timestamp
	 *  @return SQL Timestamp
	 */
    public static java.sql.Timestamp toSQLTimestamp(final org.epics.util.time.Timestamp timestamp)
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
    public static org.epics.util.time.Timestamp fromSQLTimestamp(final java.sql.Timestamp sql_time)
    {
        final long millisecs = sql_time.getTime();
        final long seconds = millisecs/1000;
        final int nanoseconds = sql_time.getNanos();
        return org.epics.util.time.Timestamp.of(seconds,  nanoseconds);
    }

    
//    /** Round time to next multiple of given seconds */
//    public static ITimestamp roundUp(final ITimestamp time, long seconds)
//    {
//        if (seconds <= 0)
//            return time;
//        long secs = time.seconds();
//        if (time.nanoseconds() > 0)
//            ++secs;
//        final long periods = secs / seconds;
//        secs = (periods + 1) * seconds;
//        return TimestampFactory.createTimestamp(secs, 0);
//    }
//
//    /** Add seconds to given time stamp.
//     *  @param time Original time stamp
//     *  @param seconds Seconds to add; may be negative
//     *  @return new time stamp
//     */
//    public static ITimestamp add(final ITimestamp time, long seconds)
//    {
//        return TimestampFactory.createTimestamp(time.seconds() + seconds, 0);
//    }

}
