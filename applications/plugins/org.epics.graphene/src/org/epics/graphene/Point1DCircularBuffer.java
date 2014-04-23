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
