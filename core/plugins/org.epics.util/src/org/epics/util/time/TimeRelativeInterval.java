/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.util.time;

/**
 * A period of time where each end can either be an absolute moment in time
 * (e.g. 5/16/2012 11:30 AM) or a relative moment from a reference (e.g. 30 seconds before)
 * which typically is going to be "now".
 * <p>
 * This class stores a reference for start and a reference for end. Each reference
 * can either be absolute, in which case it's a TimeStamp, or relative, in
 * which case it's a TimeDuration. The {@link #toAbsoluteInterval(org.epics.util.time.Timestamp) }
 * can be used to transform the relative interval into an absolute one
 * calculated from the reference. This allows to keep the relative interval,
 * and then to convert multiple time to an absolute interval every time
 * that one needs to calculate. For example, one can keep the range of a plot
 * from 1 minute ago to now, and then get a specific moment the absolute range
 * of that plot.
 *
 * @author carcassi
 */
public class TimeRelativeInterval {
    
    private final Object start;
    private final Object end;
    
    private TimeRelativeInterval(Object start, Object end) {
        this.start = start;
        this.end = end;
    }

    public static TimeRelativeInterval of(Timestamp start, Timestamp end) {
        return new TimeRelativeInterval(start, end);
    }
    
    public boolean isIntervalAbsolute() {
        return isStartAbsolute() && isEndAbsolute();
    }
    
    public boolean isStartAbsolute() {
        return start instanceof Timestamp || start == null;
    }
    
    public boolean isEndAbsolute() {
        return end instanceof Timestamp || end == null;
    }

    public Object getStart() {
        return start;
    }

    public Object getEnd() {
        return end;
    }

    public Timestamp getAbsoluteStart() {
        return (Timestamp) start;
    }
    
    public Timestamp getAbsoluteEnd() {
        return (Timestamp) end;
    }
    
    public TimeDuration getRelativeStart() {
        return (TimeDuration) start;
    }
    
    public TimeDuration getRelativeEnd() {
        return (TimeDuration) end;
    }
    
    public TimeInterval toAbsoluteInterval(Timestamp reference) {
        Timestamp absoluteStart;
        if (isStartAbsolute()) {
            absoluteStart = getAbsoluteStart();
        } else {
            absoluteStart = reference.plus(getRelativeStart());
        }
        Timestamp absoluteEnd;
        if (isEndAbsolute()) {
            absoluteEnd = getAbsoluteEnd();
        } else {
            absoluteEnd = reference.plus(getRelativeEnd());
        }
        return TimeInterval.between(absoluteStart, absoluteEnd);
    }
    
    
}
