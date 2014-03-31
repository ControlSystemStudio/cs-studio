/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.array.*;

/**
 *
 * @author carcassi
 */
public class Point1DCircularBuffer implements Point1DDataset {

    private final CircularBufferDouble buffer;
    private Statistics statistics;
    
    /**
     *Sets this object's buffer to a new <code>CircularBufferDouble</code> with a maxCapacity of capacity.
     * @param capacity - maxCapacity of this object's <code>CircularBufferDouble</code>.
     */
    public Point1DCircularBuffer(int capacity) {
        buffer = new CircularBufferDouble(capacity);
    }

    @Override
    public ListNumber getValues() {
        return buffer;
    }

    @Override
    public int getCount() {
        return buffer.size();
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    /**
     *Updates this object's private data according to the given update object.
     * <p>Possible private data changes</p>
     * <ul>
     *  <li>Clear buffer</li>
     *  <li>Fill buffer</li>
     * </ul>
     * @param update <code>Point1DDatasetUpdate</code>
     */
    public void update(Point1DDatasetUpdate update) {
        if (update.isToClear()) {
            buffer.clear();
        }
        IteratorNumber iteratorDouble = update.getNewData();
        while (iteratorDouble.hasNext()) {
            buffer.addDouble(iteratorDouble.nextDouble());
        }

        statistics = StatisticsUtil.statisticsOf(getValues());
    }
}
