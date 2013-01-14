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
public class Point2DDatasets {

    public static Point2DDataset lineData(final double[] data) {
        return lineData(new ArrayDouble(data));
    }

    public static Point2DDataset lineData(final ListNumber data) {
        return new Point2DDataset() {
            
            private final Statistics statistics = StatisticsUtil.statisticsOf(data);

            @Override
            public ListNumber getXValues() {
                return new ListInt() {

                    @Override
                    public int getInt(int index) {
                        return index;
                    }

                    @Override
                    public int size() {
                        return getCount();
                    }
                };
            }

            @Override
            public ListNumber getYValues() {
                return data;
            }

            @Override
            public double getXMinValue() {
                return 0;
            }

            @Override
            public double getXMaxValue() {
                return data.size() - 1;
            }

            @Override
            public double getYMinValue() {
                return statistics.getMinimum().doubleValue();
            }

            @Override
            public double getYMaxValue() {
                return statistics.getMaximum().doubleValue();
            }

            @Override
            public int getCount() {
                return data.size();
            }
        };
    }

    public static Point2DDataset lineData(final double[] data, final double xInitialOffset, final double xIncrementSize) {
        return lineData(new ArrayDouble(data), xInitialOffset, xIncrementSize);
    }

    public static Point2DDataset lineData(final ListNumber data, final double xInitialOffset, final double xIncrementSize) {
        return new Point2DDataset() {
            
            private final Statistics statistics = StatisticsUtil.statisticsOf(data);

            @Override
            public ListNumber getXValues() {
                return new ListDouble() {

                    @Override
                    public double getDouble(int index) {
                        return xInitialOffset + xIncrementSize *index;
                    }

                    @Override
                    public int size() {
                        return getCount();
                    }
                };
            }

            @Override
            public ListNumber getYValues() {
                return data;
            }

            @Override
            public double getXMinValue() {
                return getXValues().getDouble(0);
            }

            @Override
            public double getXMaxValue() {
                return getXValues().getDouble(data.size() - 1);
            }

            @Override
            public double getYMinValue() {
                return statistics.getMinimum().doubleValue();
            }

            @Override
            public double getYMaxValue() {
                return statistics.getMaximum().doubleValue();
            }

            @Override
            public int getCount() {
                return data.size();
            }
        };
    }

    public static Point2DDataset lineData(final double[] x, final double[] y) {
        return lineData(new ArrayDouble(x), new ArrayDouble(y));
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
