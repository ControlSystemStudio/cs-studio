/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.data.values;

import java.util.Calendar;
import java.util.Date;

import org.csstudio.data.values.internal.Timestamp;

/** A factory for {@link ITimestamp} time stamps.
 *  @author Sven Wende
 *  @author Kay Kasemir
 */
public final class TimestampFactory
{
    /** Private constructor to prevent instantiation. */
    private TimestampFactory()
    { /* NOP */ }

    /** Creates a time stamp based on the specified seconds and nanoseconds.
     *  <p>
     *  Refer to {@link ITimestamp} for details on epoch etc.
     *  <p>
     *  The nanoseconds will get normalized, i.e. it's OK to
     *  provide nanoseconds that amount to seconds.
     *  <p>
     *  @param seconds Seconds since 1970 epoch
     *  @param nanoseconds Nanoseconds within the seconds
     *  @return New time stamp
     */
    public static ITimestamp createTimestamp(final long seconds,
                    final long nanoseconds)
    {
        return new Timestamp(seconds, nanoseconds);
    }

    /** Creates a time stamp for the current system time.
     *  @return Time stamp set to the current system time
     */
    public static ITimestamp now()
    {
        return fromMillisecs(new Date().getTime());
    }

    /** Creates a time stamp based on the specified seconds.
     *  <p>
     *  Refer to {@link ITimestamp} for details on epoch etc.
     *  <p>
     *  @param seconds Seconds since 1970 epoch
     *  @return New time stamp
     */
    public static ITimestamp fromDouble(final double seconds)
    {
        return new Timestamp(seconds);
    }

    /** Create a time stamp for the given Calendar.
     *  @param calendar Calendar value to convert into time stamp
     *  @return Time stamp set to given Calendar value.
     */
    public static ITimestamp fromCalendar(final Calendar calendar)
    {
        return fromMillisecs(calendar.getTimeInMillis());
    }

    /** Create a time stamp for the given milliseconds since the epoch.
     *  @param millisecs Milliseconds since 1970 epoch
     *  @return Time stamp set to given milliseconds.
     */
    public static ITimestamp fromMillisecs(long millisecs)
    {
        final long secs = millisecs / Timestamp.millis_per_sec;
        millisecs -= secs *  Timestamp.millis_per_sec;
        final long nano = millisecs * Timestamp.nanos_per_milli;
        return new Timestamp(secs, nano);
    }

    /** Convert SQL Timestamp into CSS Timestamp.
     *  @param time SQL Timestamp
     *  @return CSS ITimestamp
     */
    public static ITimestamp fromSQLTimestamp(final java.sql.Timestamp sql_time)
    {
        final long millisecs = sql_time.getTime();
        final long seconds = millisecs/1000;
        final long nanoseconds = sql_time.getNanos();
        return createTimestamp(seconds, nanoseconds);
    }
}
