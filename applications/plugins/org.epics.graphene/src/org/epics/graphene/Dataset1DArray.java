/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

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
    public IteratorDouble getValues() {
        return Iterators.arrayIterator(data, startOffset, endOffset);
    }

    @Override
    public double getMinValue() {
        return minValue;
    }

    @Override
    public double getMaxValue() {
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
        IteratorDouble iteratorDouble = update.getNewData();
        while (iteratorDouble.hasNext()) {
            addValue(iteratorDouble.next());
        }

        double[] minMax = NumberUtil.minMax(getValues());
        if (minMax == null) {
            minValue = Double.NaN;
            maxValue = Double.NaN;
        } else {
            minValue = minMax[0];
            maxValue = minMax[1];
        }
    }
}
