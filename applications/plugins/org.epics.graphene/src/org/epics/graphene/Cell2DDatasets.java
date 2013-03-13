/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.array.CollectionDouble;
import org.epics.util.array.CollectionNumber;
import org.epics.util.array.IteratorDouble;
import org.epics.util.array.ListNumber;

/**
 * Factory methods for wrapper datasets.
 *
 * @author carcassi
 */
public class Cell2DDatasets {

    public static interface Function2D {

        public double getValue(double x, double y);

    }

    public static Cell2DDataset linearRange(final Function2D function, final Range xRange, final int xCount, final Range yRange, final int yCount) {
        final ListNumber xBoundaries = ListNumbers.linearRange(xRange.getMinimum().doubleValue(), xRange.getMaximum().doubleValue(), xCount + 1);
        final ListNumber yBoundaries = ListNumbers.linearRange(yRange.getMinimum().doubleValue(), yRange.getMaximum().doubleValue(), yCount + 1);
        final double xHalfStep = (xBoundaries.getDouble(1) - xBoundaries.getDouble(0)) / 2.0;
        final double yHalfStep = (yBoundaries.getDouble(1) - yBoundaries.getDouble(0)) / 2.0;
        CollectionNumber data = new CollectionDouble() {

            @Override
            public IteratorDouble iterator() {
                return new IteratorDouble() {
                    int x;
                    int y;

                    @Override
                    public boolean hasNext() {
                        return y < yCount;
                    }

                    @Override
                    public double nextDouble() {
                        double value = function.getValue(xBoundaries.getDouble(x) + xHalfStep, yBoundaries.getDouble(y) + yHalfStep);
                        x++;
                        if (x == xCount) {
                            x=0;
                            y++;
                        }
                        return value;
                    }
                };
            }

            @Override
            public int size() {
                return xCount * yCount;
            }
        };
        final Statistics stats = StatisticsUtil.statisticsOf(data);
        return new Cell2DDataset() {
            @Override
            public double getValue(int x, int y) {
                return function.getValue(xBoundaries.getDouble(x) + xHalfStep, yBoundaries.getDouble(y) + yHalfStep);
            }

            @Override
            public Statistics getStatistics() {
                return stats;
            }

            @Override
            public ListNumber getXBoundaries() {
                return xBoundaries;
            }

            @Override
            public Range getXRange() {
                return xRange;
            }

            @Override
            public int getXCount() {
                return xCount;
            }

            @Override
            public ListNumber getYBoundaries() {
                return yBoundaries;
            }

            @Override
            public Range getYRange() {
                return yRange;
            }

            @Override
            public int getYCount() {
                return yCount;
            }
        };
    }
}
