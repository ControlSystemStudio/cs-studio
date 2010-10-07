/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.Date;

/**
 * Represent a time stamp at nanosecond accuracy. The time is internally stored
 * as two values: the UNIX timestamp (number of seconds since
 * 1/1/1970) and the nanoseconds past that timestamp. The UNIX timestamp is
 * stored as a signed long, which has the range of 292 billion years before
 * and another 292 past the epoch.
 * <p>
 * Note that while TimeStamp are usually created according to system clocks which
 * takes into account leap seconds, all the math operations on TimeStamps do
 * not take leap seconds into account.
 *
 * @author carcassi
 */
public class TimeStamp implements Comparable {

    /*
     * When the class is initialized, get the current timestamp and nanotime,
     * so that future instances can be calculated from this reference.
     */
    private static final TimeStamp base = TimeStamp.timestampOf(new Date());
    private static final long baseNano = System.nanoTime();
    
    /**
     * Constant to convert epics seconds to UNIX seconds. It counts the number
     * of seconds for 20 years, 5 of which leap years. It does _not_ count the
     * number of leap seconds (which should have been 15).
     */
    private static long TS_EPOCH_SEC_PAST_1970=631152000; //7305*86400;

    /**
     * Unix timestamp
     */
    private final long unixSec;

    /**
     * Nanoseconds past the timestamp. Must be 0 < nanoSec < 999,999,999
     */
    private final long nanoSec;
    
    /**
     * Date object is created lazily. In multi-threaded environments,
     * the object may be created twice, but it's guaranteed to be of the same
     * value, so it should not cause problems.
     */
    private volatile Date date;

    private TimeStamp(long unixSec, long nanoSec) {
        if (nanoSec < 0 || nanoSec > 999999999)
            throw new IllegalArgumentException("Nanoseconds cannot be between 0 and 999,999,999");
        this.unixSec = unixSec;
        this.nanoSec = nanoSec;
    }

    /**
     * Epics time; seconds from midnight 1/1/1990.
     * @return epics time
     */
    public long getEpicsSec() {
        return unixSec - TS_EPOCH_SEC_PAST_1970;
    }

    /**
     * Unix time; seconds from midnight 1/1/1970.
     * @return unix time
     */
    public long getSec() {
        return unixSec;
    }

    /**
     * Nanoseconds within the given second.
     * @return nanoseconds (0 < nanoSec < 999,999,999)
     */
    public long getNanoSec() {
        return nanoSec;
    }

    /**
     * Returns a new timestamp from EPICS time.
     *
     * @param epicsSec number of second in EPICS time
     * @param nanoSec nanoseconds from the given second (must be 0 < nanoSec < 999,999,999)
     * @return a new timestamp
     */
    public static TimeStamp epicsTime(long epicsSec, long nanoSec) {
        return new TimeStamp(epicsSec + TS_EPOCH_SEC_PAST_1970, nanoSec);
    }

    /**
     * Returns a new timestamp from UNIX time.
     *
     * @param unixSec number of seconds in the UNIX epoch.
     * @param nanoSec nanoseconds past the given seconds (must be 0 < nanoSec < 999,999,999)
     * @return a new timestamp
     */
    public static TimeStamp time(long unixSec, long nanoSec) {
        return new TimeStamp(unixSec, nanoSec);
    }

    /**
     * Converts a {@link java.util.Date} to a timestamp. Date is accurate to
     * milliseconds, so the last 6 digits are always going to be zeros.
     *
     * @param date the date to convert
     * @return a new timestamp
     */
    public static TimeStamp timestampOf(Date date) {
        long time = date.getTime();
        long nanoSec = (time % 1000) * 1000000;
        long epicsSec = (time / 1000);
        return time(epicsSec, nanoSec);
    }

    /**
     * Returns a new timestamp for the current instant. The timestamp is calculated
     * using {@link java.lang.System#nanoTime()}, so it has the accuracy given
     * by that function.
     *
     * @return a new timestamp
     */
    public static TimeStamp now() {
        return base.plus(TimeDuration.nanos(System.nanoTime() - baseNano));
    }

    /**
     * Converts the time stamp to a standard Date. The conversion is done once,
     * and it trims all precision below milliSec.
     *
     * @return a date
     */
    public Date asDate() {
        if (date == null)
            prepareDate();
        return date;
    }

    /**
     * Prepares the date object
     */
    private void prepareDate() {
        date = new Date((unixSec+TS_EPOCH_SEC_PAST_1970)*1000+nanoSec/1000000);
    }

    @Override
    public int hashCode() {
        return new Long(nanoSec).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TimeStamp) {
            TimeStamp other = (TimeStamp) obj;
            return other.nanoSec == nanoSec && other.unixSec == unixSec;
        }

        return false;
    }

    /**
     * Defines the natural ordering for timestamp as forward in time.
     *
     * @param o another object
     * @return comparison result
     */
    @Override
    public int compareTo(Object o) {
        TimeStamp other = (TimeStamp) o;
	if (unixSec < other.unixSec) {
            return -1;
        } else if (unixSec == other.unixSec) {
            if (nanoSec < other.nanoSec) {
                return -1;
            } else if (nanoSec == other.nanoSec) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    /**
     * Adds the given duration to this timestamp and returns the result.
     * @param duration a time duration
     * @return a new timestamp
     */
    public TimeStamp plus(TimeDuration duration) {
        return createWithCarry(unixSec, nanoSec + duration.getNanoSec());
    }

    /**
     * Creates a new time stamp by carrying nanosecs into seconds if necessary.
     *
     * @param seconds new seconds
     * @param nanos new nanoseconds (can be the whole long range)
     * @return the new timestamp
     */
    private static TimeStamp createWithCarry(long seconds, long nanos) {
        if (nanos > 999999999) {
            seconds = seconds + nanos / 1000000000;
            nanos = nanos % 1000000000;
        }

        if (nanos < 0) {
            long pastSec = nanos / 1000000000;
            pastSec--;
            seconds += pastSec;
            nanos -= pastSec * 1000000000;
        }

        return new TimeStamp(seconds, nanos);
    }

    /**
     * Subtracts the given duration to this timestamp and returns the result.
     * @param duration a time duration
     * @return a new timestamp
     */
    public TimeStamp minus(TimeDuration duration) {
        return createWithCarry(unixSec, nanoSec - duration.getNanoSec());
    }

    @Override
    public String toString() {
        return unixSec + "." + nanoSec;
    }

    public TimeDuration durationFrom(TimeStamp reference) {
        long nanoSecDiff = reference.nanoSec - nanoSec;
        nanoSecDiff += (reference.unixSec - unixSec) * 1000000000;
        nanoSecDiff = Math.abs(nanoSecDiff);
        return TimeDuration.nanos(nanoSecDiff);
    }

}
