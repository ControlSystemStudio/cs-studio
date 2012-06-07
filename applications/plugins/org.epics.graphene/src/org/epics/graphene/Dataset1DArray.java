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
public class Dataset1DArray implements Dataset1D {

    private double[] data;
    private int startOffset;
    private int endOffset;
    private double minValue = Double.NaN;
    private double maxValue = Double.NaN;
    
    public Dataset1DArray(int capacity) {
        data = new double[capacity+1];
    }

    @Override
    public CollectionNumber getValues() {
        return new CollectionDouble() {

            @Override
            public IteratorDouble iterator() {
                return Iterators.arrayIterator(data, startOffset, endOffset);
            }

            @Override
            public int size() {
                int size = endOffset - startOffset;
                if (size < 0) {
                    size += data.length;
                }
                return size;
            }
        };
    }

    @Override
    public Number getMinValue() {
        return minValue;
    }

    @Override
    public Number getMaxValue() {
        return maxValue;
    }
    
    private void addValue(double value) {
        data[endOffset] = value;
        endOffset++;
        if (endOffset == data.length) {
            endOffset = 0;
        }
        if (endOffset == startOffset)
            startOffset++;
        if (startOffset == data.length)
            startOffset = 0;
    }

    @Override
    public void update(Dataset1DUpdate update) {
        if (update.isToClear()) {
            startOffset = 0;
            endOffset = 0;
        }
        IteratorNumber iteratorDouble = update.getNewData();
        while (iteratorDouble.hasNext()) {
            addValue(iteratorDouble.nextDouble());
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
