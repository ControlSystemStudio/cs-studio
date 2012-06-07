/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.array.CollectionNumbers;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public class Arrays {

    public static OrderedDataset2D lineData(final double[] data) {
        return new OrderedDataset2D() {
            
            private final double[] minMax = NumberUtil.minMax(data);

            @Override
            public double getXValue(int index) {
                return index;
            }

            @Override
            public double getYValue(int index) {
                return data[index];
            }

            @Override
            public double getXMinValue() {
                return 0;
            }

            @Override
            public double getXMaxValue() {
                return data.length - 1;
            }

            @Override
            public double getYMinValue() {
                return minMax[0];
            }

            @Override
            public double getYMaxValue() {
                return minMax[1];
            }

            @Override
            public int getCount() {
                return data.length;
            }
        };
    }

    public static OrderedDataset2D lineData(final ListNumber data) {
        return new OrderedDataset2D() {
            
            private final CollectionNumbers.MinMax minMax = CollectionNumbers.minMaxDouble(data);

            @Override
            public double getXValue(int index) {
                return index;
            }

            @Override
            public double getYValue(int index) {
                return data.getDouble(index);
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
                return minMax.min.doubleValue();
            }

            @Override
            public double getYMaxValue() {
                return minMax.max.doubleValue();
            }

            @Override
            public int getCount() {
                return data.size();
            }
        };
    }

    public static OrderedDataset2D lineData(final double[] data, final double xInitialOffset, final double xIncrementSize) {
        return new OrderedDataset2D() {
            
            private final double[] minMax = NumberUtil.minMax(data);

            @Override
            public double getXValue(int index) {
                return xInitialOffset + xIncrementSize *index;
            }

            @Override
            public double getYValue(int index) {
                return data[index];
            }

            @Override
            public double getXMinValue() {
                return getXValue(0);
            }

            @Override
            public double getXMaxValue() {
                return getXValue(data.length);
            }

            @Override
            public double getYMinValue() {
                return minMax[0];
            }

            @Override
            public double getYMaxValue() {
                return minMax[1];
            }

            @Override
            public int getCount() {
                return data.length;
            }
        };
    }

    public static OrderedDataset2D lineData(final ListNumber data, final double xInitialOffset, final double xIncrementSize) {
        return new OrderedDataset2D() {
            
            private final CollectionNumbers.MinMax minMax = CollectionNumbers.minMaxDouble(data);

            @Override
            public double getXValue(int index) {
                return index;
            }

            @Override
            public double getYValue(int index) {
                return data.getDouble(index);
            }

            @Override
            public double getXMinValue() {
                return getXValue(0);
            }

            @Override
            public double getXMaxValue() {
                return getXValue(data.size());
            }

            @Override
            public double getYMinValue() {
                return minMax.min.doubleValue();
            }

            @Override
            public double getYMaxValue() {
                return minMax.max.doubleValue();
            }

            @Override
            public int getCount() {
                return data.size();
            }
        };
    }

    public static OrderedDataset2D lineData(final double[] x, final double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Arrays length don't match: " + x.length + " - " + y.length);
        }
        
        return new OrderedDataset2D() {
            
            private final double[] xMinMax = NumberUtil.minMax(x);
            private final double[] yMinMax = NumberUtil.minMax(y);

            @Override
            public double getXValue(int index) {
                return x[index];
            }

            @Override
            public double getYValue(int index) {
                return y[index];
            }

            @Override
            public double getXMinValue() {
                return xMinMax[0];
            }

            @Override
            public double getXMaxValue() {
                return xMinMax[1];
            }

            @Override
            public double getYMinValue() {
                return yMinMax[0];
            }

            @Override
            public double getYMaxValue() {
                return yMinMax[1];
            }

            @Override
            public int getCount() {
                return x.length;
            }
        };
    }

    public static OrderedDataset2D lineData(final ListNumber x, final ListNumber y) {
        if (x.size() != y.size()) {
            throw new IllegalArgumentException("Arrays length don't match: " + x.size() + " - " + y.size());
        }
        
        return new OrderedDataset2D() {
            
            private final CollectionNumbers.MinMax xMinMax = CollectionNumbers.minMaxDouble(x);
            private final CollectionNumbers.MinMax yMinMax = CollectionNumbers.minMaxDouble(y);

            @Override
            public double getXValue(int index) {
                return x.getDouble(index);
            }

            @Override
            public double getYValue(int index) {
                return y.getDouble(index);
            }

            @Override
            public double getXMinValue() {
                return xMinMax.min.doubleValue();
            }

            @Override
            public double getXMaxValue() {
                return xMinMax.max.doubleValue();
            }

            @Override
            public double getYMinValue() {
                return yMinMax.min.doubleValue();
            }

            @Override
            public double getYMaxValue() {
                return yMinMax.max.doubleValue();
            }

            @Override
            public int getCount() {
                return x.size();
            }
        };
    }
    
}
