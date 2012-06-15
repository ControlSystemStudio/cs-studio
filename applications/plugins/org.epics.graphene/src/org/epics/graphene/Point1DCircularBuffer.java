/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.array.*;

/**
 *
 * @author carcassi
 */
public class Point1DCircularBuffer implements Point1DDataset {

    private final CircularBufferDouble buffer;
    private double minValue = Double.NaN;
    private double maxValue = Double.NaN;
    
    public Point1DCircularBuffer(int capacity) {
        buffer = new CircularBufferDouble(capacity);
    }

    @Override
    public CollectionNumber getValues() {
        return buffer;
    }

    @Override
    public Number getMinValue() {
        return minValue;
    }

    @Override
    public Number getMaxValue() {
        return maxValue;
    }

    @Override
    public void update(Point1DDatasetUpdate update) {
        if (update.isToClear()) {
            buffer.clear();
        }
        IteratorNumber iteratorDouble = update.getNewData();
        while (iteratorDouble.hasNext()) {
            buffer.addDouble(iteratorDouble.nextDouble());
        }

        CollectionNumbers.MinMax minMax = CollectionNumbers.minMaxDouble(getValues());
        if (minMax == null) {
            minValue = Double.NaN;
            maxValue = Double.NaN;
        } else {
            minValue = minMax.min.doubleValue();
            maxValue = minMax.max.doubleValue();
        }
    }
}
