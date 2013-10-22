/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.array.ListNumber;
import org.epics.util.array.ListNumbers;

/**
 * Factory methods for wrapper datasets.
 *
 * @author carcassi
 */
public class Cell1DDatasets {
    
    /**
     * Wraps a {@link ListNumber} into a {@link Point1DDataset}.
     * <p>
     * It assumes the argument is either immutable or mutable but
     * will not be changed in the future.
     * 
     * @param values the values for the dataset
     * @return the dataset from the values; never null
     */
    public static Cell1DDataset linearRange(final ListNumber values, final double minValue, final double maxValue) {
        final Statistics statistics = StatisticsUtil.statisticsOf(values);
        final Range range = RangeUtil.range(minValue, maxValue);
        final ListNumber xBoundaries = ListNumbers.linearListFromRange(minValue, maxValue, values.size() + 1);
        return new Cell1DDataset() {

            @Override
            public double getValue(int x) {
                return values.getDouble(x);
            }

            @Override
            public Statistics getStatistics() {
                return statistics;
            }

            @Override
            public ListNumber getXBoundaries() {
                return xBoundaries;
            }

            @Override
            public Range getXRange() {
                return range;
            }

            @Override
            public int getXCount() {
                return values.size();
            }
        };
    }
}
