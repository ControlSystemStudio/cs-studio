/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.util;

/**
 * A duration of time (such as 3 sec, 30ms, 1nsec) at the nanosecond precision.
 * The duration is stored a (signed) long, which makes the maximum valid duration
 * to around 292 years. No checks for overflows are done.
 * <p>
 * Note that while TimeStamp are usually created according to system clocks which
 * takes into account leap seconds, all the math operations on TimeStamps do
 * not take leap seconds into account.
 * 
 * @deprecated This class is being retired in favor of {@link org.epics.util.time.TimeDuration}
 * @author carcassi
 */
@Deprecated
public class TimeDuration {

    private long nanoSec;

    private TimeDuration(long nanoSec) {
        if (nanoSec < 0)
            throw new IllegalArgumentException("Time duration has to be positive");
        this.nanoSec = nanoSec;
    }

    /**
     * Duration in nanoseconds.
     * @return duration in nanoseconds.
     */
    public long getNanoSec() {
        return nanoSec;
    }
    
    /**
     * A new duration in hours.
     * 
     * @param hour hours
     * @return a new duration
     */
    public static TimeDuration hour(double hour) {
        return nanos((long) (hour * 60 * 60 * 1000000000));
    }
    
    /**
     * A new duration in minutes.
     * 
     * @param min minutes
     * @return a new duration
     */
    public static TimeDuration min(double min) {
        return nanos((long) (min * 60 * 1000000000));
    }
    
    /**
     * A new duration in seconds.
     * 
     * @param sec seconds
     * @return a new duration
     */
    public static TimeDuration sec(double sec) {
        return nanos((long) (sec * 1000000000));
    }
    
    /**
     * A new duration in hertz, will convert to the length of the period.
     * 
     * @param hz frequency to be converted to a duration
     * @return a new duration
     */
    public static TimeDuration hz(double hz) {
        if (hz <= 0.0) {
            throw new IllegalArgumentException("Frequency has to be greater than 0.0");
        }
        return nanos((long) (1000000000.0 / hz));
    }

    /**
     * A new duration in milliseconds.
     * @param ms milliseconds of the duration
     * @return a new duration
     * @throws IllegalArgumentException if the duration is negative
     */
    public static TimeDuration ms(int ms) {
        return new TimeDuration(((long) ms) * 1000000);
    }

    /**
     * A new duration in milliseconds.
     * @param ms milliseconds of the duration
     * @return a new duration
     * @throws IllegalArgumentException if the duration is negative
     */
    public static TimeDuration ms(double ms) {
        return new TimeDuration((long) (ms * 1000000));
    }

    /**
     * A new duration in nanoseconds.
     * @param nanoSec nanoseconds of the duration
     * @return a new duration
     * @throws IllegalArgumentException if the duration is negative
     */
    public static TimeDuration nanos(long nanoSec) {
        return new TimeDuration(nanoSec);
    }

    /**
     * Returns a new duration which is smaller by the given factor.
     * 
     * @param factor constant to divide
     * @return a new duration
     * @throws IllegalArgumentException if factor is negative
     */
    public TimeDuration divideBy(int factor) {
        return new TimeDuration(nanoSec / factor);
    }

    /**
     * Returns a new duration which is bigger by the given factor.
     *
     * @param factor constant to multiply
     * @return a new duration
     * @throws IllegalArgumentException if factor is negative
     */
    public TimeDuration multiplyBy(int factor) {
        return new TimeDuration(nanoSec * factor);
    }

    /**
     * Returns a time interval that lasts this duration and is centered
     * around the given timestamp.
     * 
     * @param reference a timestamp
     * @return a new time interval
     */
    public TimeInterval around(TimeStamp reference) {
        TimeDuration half = this.divideBy(2);
        return TimeInterval.between(reference.minus(half), reference.plus(half));
    }

    /**
     * Returns a time interval that lasts this duration and starts from the
     * given timestamp.
     *
     * @param reference a timestamp
     * @return a new time interval
     */
    public TimeInterval after(TimeStamp reference) {
        return TimeInterval.between(reference, reference.plus(this));
    }

    /**
     * Returns a time interval that lasts this duration and ends at the
     * given timestamp.
     *
     * @param reference a timestamp
     * @return a new time interval
     */
    public TimeInterval before(TimeStamp reference) {
        return TimeInterval.between(reference.minus(this), reference);
    }

    @Override
    public String toString() {
        return "" + nanoSec;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(nanoSec).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj instanceof TimeDuration) {
            if (nanoSec == ((TimeDuration) obj).nanoSec)
                return true;
        }

        return false;
    }

    public static TimeDuration durationOf(org.epics.util.time.TimeDuration duration) {
        if (duration == null)
            return null;
        return nanos(duration.getSec() * 1000000000 + duration.getNanoSec());
    }
    
    public static org.epics.util.time.TimeDuration asTimeDuration(TimeDuration duration) {
        if (duration == null)
            return null;
        
        return org.epics.util.time.TimeDuration.ofNanos(duration.getNanoSec());
    }
}
