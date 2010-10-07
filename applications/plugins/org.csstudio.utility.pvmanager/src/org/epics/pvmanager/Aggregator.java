/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.Collections;
import java.util.List;

/**
 * Aggregates the data out of a Collector into a new data type.
 * <p>
 * The Aggregator uses {@link #calculate(java.util.List) } to aggregate
 * the values of the current time slice. If no values were collected in the
 * current time slice, the last value (and only the last value, is passed
 * so that the output remains consistent with what would be currently
 * posted. Non calculation is done if no values were collected in the current
 * and in the previous time slice.
 *
 * @author carcassi
 */
public abstract class Aggregator<T, E> extends Function<T> {

    private final Function<List<E>> collector;
    private T lastCalculatedValue;
    private E lastValue;

    protected Aggregator(Class<T> type, Function<List<E>> collector) {
        super(type);
        this.collector = collector;
    }

    @Override
    public T getValue() {
        List<E> data = collector.getValue();
        if (data.size() > 0) {
            lastCalculatedValue = calculate(data);
            lastValue = data.get(data.size() - 1);
        } else if (lastValue != null) {
            lastCalculatedValue = calculate(Collections.singletonList(lastValue));
            lastValue = null;
        }
        return lastCalculatedValue;
    }

    /**
     * Calculates the new value from a set of collected value. This function
     * will never be called with an empty list
     *
     * @param data the new data to aggregate
     * @return the aggregated value
     */
    protected abstract T calculate(List<E> data);
}
