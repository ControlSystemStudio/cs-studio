/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.util.time;

/**
 * A period of time that spans two instances (included) at the nanosecond
 * precision.
 *
 * @author carcassi
 */
public class TimeInterval {

    private final Timestamp start;
    private final Timestamp end;

    private TimeInterval(Timestamp start, Timestamp end) {
        this.start = start;
        this.end = end;
    }

    /**
     * True if the given time stamp is inside the interval.
     *
     * @param instant a time stamp
     * @return true if inside the interval
     */
    public boolean contains(Timestamp instant) {
        return (start == null || start.compareTo(instant) <= 0) && (end == null || end.compareTo(instant) >= 0);
    }

    /**
     * Returns the interval between the given timestamps.
     *
     * @param start the beginning of the interval
     * @param end the end of the interval
     * @return a new interval
     */
    public static TimeInterval between(Timestamp start, Timestamp end) {
        return new TimeInterval(start, end);
    }

    /**
     * Returns a new interval shifted backward in time by the given duration.
     *
     * @param duration a time duration
     * @return the new shifted interval
     */
    public TimeInterval minus(TimeDuration duration) {
        return between(start.minus(duration), end.minus(duration));
    }

    /**
     * Initial value of the interval.
     *
     * @return the initial instant
     */
    public Timestamp getStart() {
        return start;
    }

    /**
     * Final value of the interval.
     *
     * @return the final instant
     */
    public Timestamp getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.valueOf(start) + " - " + String.valueOf(end);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TimeInterval) {
            TimeInterval other = (TimeInterval) obj;
            boolean startEqual = (getStart() == other.getStart()) || (getStart() != null && getStart().equals(other.getStart()));
            boolean endEqual = (getEnd() == other.getEnd()) || (getEnd() != null && getEnd().equals(other.getEnd()));
            return startEqual && endEqual;
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.start != null ? this.start.hashCode() : 0);
        hash = 29 * hash + (this.end != null ? this.end.hashCode() : 0);
        return hash;
    }

}
