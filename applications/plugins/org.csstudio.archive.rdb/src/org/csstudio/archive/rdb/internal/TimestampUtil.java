package org.csstudio.archive.rdb.internal;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;

/** Time stamp gymnastics
 *  @author Kay Kasemir
 */
public class TimestampUtil
{
    /** Round time to next multiple of given seconds */
    public static ITimestamp roundUp(final ITimestamp time, long seconds)
    {
        if (seconds <= 0)
            return time;
        long secs = time.seconds();
        if (time.nanoseconds() > 0)
            ++secs;
        final long periods = secs / seconds;
        secs = (periods + 1) * seconds;
        return TimestampFactory.createTimestamp(secs, 0);
    }

    /** Add seconds to given time stamp.
     *  @param time Original time stamp
     *  @param seconds Seconds to add; may be negative
     *  @return new time stamp
     */
    public static ITimestamp add(final ITimestamp time, long seconds)
    {
        return TimestampFactory.createTimestamp(time.seconds() + seconds, 0);
    }

}
