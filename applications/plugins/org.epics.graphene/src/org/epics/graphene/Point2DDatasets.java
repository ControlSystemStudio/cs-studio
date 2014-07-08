/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.StatisticsUtil;
import org.epics.util.stats.Statistics;
import org.epics.util.stats.Range;
import org.epics.util.array.*;

/**
 *
 * @author carcassi
 */
public class Point2DDatasets {

    public static Point2DDataset lineData(final double[] data) {
        return lineData(new ArrayDouble(data));
    }

    public static Point2DDataset lineData(final ListNumber data) {
        return lineData(ListNumbers.linearList(0, 1, data.size()), data);
    }

    public static Point2DDataset lineData(final double[] data, final double xInitialOffset, final double xIncrementSize) {
        return lineData(new ArrayDouble(data), xInitialOffset, xIncrementSize);
    }

    public static Point2DDataset lineData(final ListNumber data, final double xInitialOffset, final double xIncrementSize) {
        return lineData(ListNumbers.linearList(xInitialOffset, xIncrementSize, data.size()), data);
    }

    public static Point2DDataset lineData(final double[] x, final double[] y) {
        return lineData(new ArrayDouble(x), new ArrayDouble(y));
    }
    
    public static Point2DDataset lineData(final Range xRange, final ListNumber y) {
        return lineData(ListNumbers.linearListFromRange(xRange.getMinimum().doubleValue(), xRange.getMaximum().doubleValue(), y.size()), y);
    }

    public static Point2DDataset lineData(final ListNumber x, final ListNumber y) {
        if (x.size() != y.size()) {
            throw new IllegalArgumentException("Arrays length don't match: " + x.size() + " - " + y.size());
        }
        
        return new Point2DDataset() {
            
            private final Statistics xStatistics = StatisticsUtil.statisticsOf(x);
            private final Statistics yStatistics = StatisticsUtil.statisticsOf(y);

            @Override
            public ListNumber getXValues() {
                return x;
            }

            @Override
            public ListNumber getYValues() {
                return y;
            }

            @Override
            public Statistics getXStatistics() {
                return xStatistics;
            }

            @Override
            public Statistics getYStatistics() {
                return yStatistics;
            }

            @Override
            public Range getXDisplayRange() {
                return null;
            }

            @Override
            public Range getYDisplayRange() {
                return null;
            }

            @Override
            public int getCount() {
                return x.size();
            }
        };
    }
    
}
