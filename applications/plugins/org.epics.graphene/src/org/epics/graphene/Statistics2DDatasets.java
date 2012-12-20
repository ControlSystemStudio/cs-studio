/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.util.List;

/**
 *
 * @author carcassi
 */
public class Statistics2DDatasets {

    public static Statistics2DDataset statisticsData(final List<Statistics> x, final List<Statistics> y) {
        if (x.size() != y.size()) {
            throw new IllegalArgumentException("Arrays length don't match: " + x.size() + " - " + y.size());
        }
        
        return new Statistics2DDataset() {

            private final Statistics xStatistics = StatisticsUtil.statisticsOf(x);
            private final Statistics yStatistics = StatisticsUtil.statisticsOf(y);

            @Override
            public List<Statistics> getXValues() {
                return x;
            }

            @Override
            public List<Statistics> getYValues() {
                return y;
            }

            @Override
            public double getXMinValue() {
                return xStatistics.getMinimum().doubleValue();
            }

            @Override
            public double getXMaxValue() {
                return xStatistics.getMaximum().doubleValue();
            }

            @Override
            public double getYMinValue() {
                return yStatistics.getMinimum().doubleValue();
            }

            @Override
            public double getYMaxValue() {
                return yStatistics.getMaximum().doubleValue();
            }

            @Override
            public int getCount() {
                return x.size();
            }
        };
    }
    
}
