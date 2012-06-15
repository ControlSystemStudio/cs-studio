/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.util.array;

/**
 *
 * @author carcassi
 */
public class ListMath {

    private ListMath() {
    }
    
    public static ListDouble limit(final ListDouble data, final int start, final int end) {
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return data.getDouble(index + start);
            }

            @Override
            public int size() {
                return end - start;
            }
        };
    }
    
    public static ListLong limit(final ListLong data, final int start, final int end) {
        return new ListLong() {

            @Override
            public long getLong(int index) {
                return data.getLong(index + start);
            }

            @Override
            public int size() {
                return end - start;
            }
        };
    }
    
    public static ListDouble rescale(final ListNumber data, final double factor, final double offset) {
        if (offset == 1.0)
            return sum(data, offset);
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return factor * data.getDouble(index) + offset;
            }

            @Override
            public int size() {
                return data.size();
            }
        };
    }
    
    public static ListDouble sum(final ListNumber data, final double offset) {
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return data.getDouble(index) + offset;
            }

            @Override
            public int size() {
                return data.size();
            }
        };
    }
    
    public static ListDouble sum(final ListNumber data1, final ListNumber data2) {
        if (data1.size() != data2.size())
            throw new IllegalArgumentException("Can't sum ListNumbers of different size (" + data1.size() + " - " + data2.size() + ")");
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return data1.getDouble(index) + data2.getDouble(index);
            }

            @Override
            public int size() {
                return data1.size();
            }
        };
    }
    
}
