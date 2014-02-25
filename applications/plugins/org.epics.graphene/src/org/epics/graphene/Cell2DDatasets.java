/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.array.*;

/**
 * Factory methods for wrapper datasets.
 *
 * @author carcassi
 */
public class Cell2DDatasets {

    public static interface Function2D {

        public double getValue(double x, double y);

    }
    /**
     *returns a Cell2DDataset, which is a 1D list of values that is treated like a 2D matrix.
     * @param data 1D list of z-values. x and y coordinates are calculated by partitioning the matrix
     * into pieces of length xCount. # of rows = yCount # of cols = xCount.
     * @param xRange
     * @param xCount
     * @param yRange
     * @param yCount
     * @return Cell2DDataset
     */
public static Cell2DDataset linearRange(final ListNumber data, final Range xRange, final int xCount, final Range yRange, final int yCount){
    if(data.size() == 0){
        throw new IllegalArgumentException("Empty Dataset. zData size = " + data.size());
    }
    if(xCount <= 0 || yCount <= 0){
        throw new IllegalArgumentException("Number of X (or Y) values must be greater than 0. xCount = " + xCount + " yCount = " + yCount);
    }
    if(((xCount)*(yCount-1)+xCount-1) != (data.size()-1)){
        throw new IllegalArgumentException("Unexpected number of X (or Y) values. Array length = " + (data.size())+ ", Predicted size(given X and Y) = " 
                + ((xCount)*(yCount-1)+xCount) + ", xCount = " + xCount + ", yCount = " + yCount);
    }
    
    final ListNumber xBoundaries = ListNumbers.linearListFromRange(xRange.getMinimum().doubleValue(), xRange.getMaximum().doubleValue(), xCount + 1);
    final ListNumber yBoundaries = ListNumbers.linearListFromRange(yRange.getMinimum().doubleValue(), yRange.getMaximum().doubleValue(), yCount + 1);
    
    final Statistics stats = StatisticsUtil.statisticsOf(data);
    return new Cell2DDataset() {
        @Override
        public double getValue(int x, int y) {
            return data.getDouble(y*xCount+x);
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
    public static Cell2DDataset linearRange(final Function2D function, final Range xRange, final int xCount, final Range yRange, final int yCount) {
        
        final ListNumber xBoundaries = ListNumbers.linearListFromRange(xRange.getMinimum().doubleValue(), xRange.getMaximum().doubleValue(), xCount + 1);
        final ListNumber yBoundaries = ListNumbers.linearListFromRange(yRange.getMinimum().doubleValue(), yRange.getMaximum().doubleValue(), yCount + 1);
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
    
    public static Cell2DDataset datasetFrom(final ListNumber values, final ListNumber xBoundaries, final ListNumber yBoundaries) {
        final Statistics statistics = StatisticsUtil.statisticsOf(values);
        final Range xRange = RangeUtil.range(xBoundaries.getDouble(0), xBoundaries.getDouble(xBoundaries.size() - 1));
        final Range yRange = RangeUtil.range(yBoundaries.getDouble(0), yBoundaries.getDouble(yBoundaries.size() - 1));
        
        // Check boundary sizes correspond match the number of points.
        final int xCount = xBoundaries.size() - 1;
        final int yCount = yBoundaries.size() - 1;
        if (values.size() != xCount * yCount) {
            throw new IllegalArgumentException("Number of boundaries do not match number of cells (" + xCount + " * " + yCount + " !+ " + values.size() + ")");
        }
        return new Cell2DDataset() {

            @Override
            public double getValue(int x, int y) {
                return values.getDouble(y * xCount + x);
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
