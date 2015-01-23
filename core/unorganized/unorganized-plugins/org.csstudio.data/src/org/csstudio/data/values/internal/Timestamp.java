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
package org.csstudio.data.values.internal;

import java.util.Calendar;

import org.csstudio.data.values.ITimestamp;

/** Implementation of the {@link ITimestamp} interface.
 *  @author Sven Wende
 *  @author Kay Kasemir
 */
public final class Timestamp implements ITimestamp
{
    private static final long serialVersionUID = 1L;

    /** Milliseconds per second. */
    public static final long millis_per_sec = 1000L;

    /** Nanoseconds per millisecond. */
    public static final long nanos_per_milli = 1000000L;

    /** Nanoseconds per second. */
    public static final long nanos_per_sec = 1000000000L;

    /** Seconds since epoch. */
    private final long seconds;

    /** Nanoseconds within the seconds.
     *  <p>
     *  Normalized, i.e. within 0..nanos_per_milli. */
    private final long nanoseconds;

    /** Constructor with seconds and nanoseconds since epoch.
     *  @param seconds Seconds since epoch
     *  @param nanoseconds Nanoseconds within seconds
     */
    public Timestamp(long seconds, long nanoseconds)
    {
        if (nanoseconds < 0  ||  nanoseconds >= nanos_per_sec)
        {
            long fullsecs = nanoseconds / nanos_per_sec;
            seconds += fullsecs;
            nanoseconds -= fullsecs * nanos_per_sec;
        }
        this.seconds = seconds;
        this.nanoseconds = nanoseconds;
    }

    /** Constructor with fractional seconds since epoch.
     *  @param seconds Seconds since epoch
     */
    public Timestamp(final double seconds)
    {
        this.seconds = (long) seconds;
        this.nanoseconds = (long) ((seconds - this.seconds) * 1e9);
    }

    /** {@inheritDoc} */
    @Override
    public long seconds()
    {
        return seconds;
    }

    /** {@inheritDoc} */
    @Override
    public long nanoseconds()
    {
        return nanoseconds;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid()
    {
        return seconds > 0 || nanoseconds > 0;
    }

    /** {@inheritDoc} */
    @Override
    public double toDouble()
    {
        return seconds + nanoseconds() / 1e9;
    }

    /** {@inheritDoc} */
    @Override
    public Calendar toCalendar()
    {
        final Calendar result = Calendar.getInstance();
        result.setTimeInMillis(seconds * millis_per_sec
                               + nanoseconds / nanos_per_milli);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public java.sql.Timestamp toSQLTimestamp()
    {
        // Only millisecond resolution
        java.sql.Timestamp stamp = new java.sql.Timestamp(seconds * 1000  +
                             nanoseconds / 1000000);
        // Set nanoseconds (again), but this call uses the full
        // nanosecond resolution
        stamp.setNanos((int) nanoseconds);
        return stamp;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("nls")
    public String format(final Format format)
    {
        final Calendar cal = toCalendar();

        // Formatting the time as a string is expensive.
        // JProfiler comparison of this String.format() code with
        // with custom StringBuilder.append(...) code seemed to save
        // very little CPU time, but String.format() is a lot easier to read.
        switch (format)
        {
        case Date:
            return String.format("%04d/%02d/%02d",
                             cal.get(Calendar.YEAR),
                             cal.get(Calendar.MONTH)+1,
                             cal.get(Calendar.DAY_OF_MONTH));
        case DateTime:
            return String.format("%04d/%02d/%02d %02d:%02d",
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH)+1,
                            cal.get(Calendar.DAY_OF_MONTH),
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE));
        case DateTimeSeconds:
            return String.format("%04d/%02d/%02d %02d:%02d:%02d",
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH)+1,
                            cal.get(Calendar.DAY_OF_MONTH),
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            cal.get(Calendar.SECOND));
        default:
        // case Full:
            return String.format("%04d/%02d/%02d %02d:%02d:%02d.%09d",
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH)+1,
                            cal.get(Calendar.DAY_OF_MONTH),
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            cal.get(Calendar.SECOND),
                            nanoseconds);
        }
    }

    /** @return The formatted time with full detail. */
    @Override
    public String toString()
    {
        return format(Format.Full);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGreaterThan(final ITimestamp other)
    {
        if (seconds > other.seconds())
        {
            return true;
        }
        if (seconds < other.seconds())
        {
            return false;
        }
        // Seconds tie, let nanoseconds decide.
        return nanoseconds > other.nanoseconds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGreaterOrEqual(final ITimestamp other)
    {
        if (seconds > other.seconds())
        {
            return true;
        }
        if (seconds < other.seconds())
        {
            return false;
        }
        // Seconds tie, let nanoseconds decide.
        return nanoseconds >= other.nanoseconds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLessThan(final ITimestamp other)
    {
        return !isGreaterOrEqual(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLessOrEqual(final ITimestamp other)
    {
        return !isGreaterThan(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof ITimestamp))
        {
            return false;
        }
        ITimestamp rhs = (ITimestamp) obj;
        return rhs.seconds() == seconds && rhs.nanoseconds() == nanoseconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = prime + (int) (nanoseconds ^ (nanoseconds >>> 32));
	    result = prime * result + (int) (seconds ^ (seconds >>> 32));
	    return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final ITimestamp rhs)
    {
        if (isGreaterThan(rhs))
        {
            return 1;
        }
        if (equals(rhs))
        {
            return 0;
        }
        return -1;
    }
}
