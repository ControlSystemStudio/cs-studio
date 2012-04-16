/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.Collections;
import java.util.List;

/**
 * Aggregates the data out of a Collector into a new data type.
 * <p>
 * The {@link Aggregator} uses {@link #calculate(java.util.List) } to aggregate
 * the values of the current time slice. If no values were collected in the
 * current time slice, the last value (and only the last value, is passed
 * so that the output remains consistent with what would be currently
 * posted. Non calculation is done if no values were collected in the current
 * and in the previous time slice.
 *
 * @param <R> result type of aggregation
 * @param <A> argument type being aggregated
 * @author carcassi
 */
public abstract class Aggregator<R, A> extends Function<R> {

    private final Function<List<A>> collector;
    private R lastCalculatedValue;
    private A lastValue;

    /**
     * Creates a new aggregator. The given collector will be the source
     * of data for the new aggregator.
     *
     * @param collector a suitable collector
     */
    protected Aggregator(Function<List<A>> collector) {
        this.collector = collector;
    }

    @Override
    public final R getValue() {
        List<A> data = collector.getValue();
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
    protected abstract R calculate(List<A> data);
}
