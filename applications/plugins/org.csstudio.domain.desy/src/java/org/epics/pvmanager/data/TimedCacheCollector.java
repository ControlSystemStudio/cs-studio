/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.util.*;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.Function;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.TimeInterval;

/**
 *
 * @author carcassi
 */
class TimedCacheCollector<T extends Time> extends Collector<T> {

    private final Deque<T> buffer = new ArrayDeque<T>();
    private final Function<T> function;
    private final TimeDuration cachedPeriod;
    
    public TimedCacheCollector(Function<T> function, TimeDuration cachedPeriod) {
        this.function = function;
        this.cachedPeriod = cachedPeriod;
    }
    /**
     * Calculates the next value and puts it in the queue.
     */
    @Override
    public synchronized void collect() {
        // Calculation may take time, and is locked by this
        T newValue = function.getValue();

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
    public List<T> getValue() {
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
