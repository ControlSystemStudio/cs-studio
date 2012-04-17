/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

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
    
}
