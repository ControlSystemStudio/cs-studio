/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.util.array;

/**
 * Math operations defined on lists of numbers.
 *
 * @author carcassi
 */
public class ListMath {

    private ListMath() {
    }
    
    /**
     * Returns a sublist of the given data.
     * 
     * @param data a list
     * @param start start point for the sublist
     * @param end end point (exclusive) for the sublist
     * @return the sublist
     */
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
    
    /**
     * Returns a sublist of the given data.
     * 
     * @param data a list
     * @param start start point for the sublist
     * @param end end point (exclusive) for the sublist
     * @return the sublist
     */
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
    
    /**
     * Performs a linear transformation on the data.
     * 
     * @param data a list of numbers
     * @param factor the multiplicative constant
     * @param offset the additive constant
     * @return the computed data
     */
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
    
    /**
     * Returns a new list where all elements are added to a constant.
     * 
     * @param data a list of number
     * @param offset the additive constant
     * @return the computed data
     */
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
    
    /**
     * Returns a list where each element is the sum of the elements of the two
     * lists at the same index. The lists have to match in size.
     * 
     * @param data1 a list of numbers
     * @param data2 another list of numbers
     * @return the computed data
     */
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
