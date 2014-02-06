/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.vtype;

import org.epics.vtype.Time;
import java.util.*;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.ReadFunction;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.TimeInterval;

/**
 *
 * @author carcassi
 */
class TimedCacheCollector<T extends Time> implements Collector<T, List<T>> {

    private final Deque<T> buffer = new ArrayDeque<T>();
    private final ReadFunction<T> function;
    private final TimeDuration cachedPeriod;
    
    public TimedCacheCollector(ReadFunction<T> function, TimeDuration cachedPeriod) {
        this.function = function;
        this.cachedPeriod = cachedPeriod;
    }

    @Override
    public void writeValue(T newValue) {
        // Buffer is locked and updated
        if (newValue != null) {
            synchronized(buffer) {
                buffer.add(newValue);
                prune();
            }
        }
    }

    /**
     * Returns all values since last check and removes values from the queue.
     * @return a new array with the value; never null
     */
    @Override
    public List<T> readValue() {
        synchronized(buffer) {
            if (buffer.isEmpty())
                return Collections.emptyList();

            return new ArrayList<T>(buffer);
        }
    }
    
    private void prune() {
        // Remove all values that are too old
        TimeInterval periodAllowed = cachedPeriod.before(buffer.getLast().getTimestamp());
        while (!buffer.isEmpty() && !periodAllowed.contains(buffer.getFirst().getTimestamp())) {
            // Discard value
            buffer.removeFirst();
        }
    }

}
