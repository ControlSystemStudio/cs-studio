/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.util.array;

import java.math.BigInteger;

/**
 * Math operations defined on lists of numbers.
 *
 * @author carcassi
 * @author Mark Davis (NSCL/FRIB)
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
     * <p>
     * TODO: this should go as a member method
     *
     * @param data a list
     * @param start start point for the sublist
     * @param end end point (exclusive) for the sublist
     * @return the sublist
     */
    public static ListNumber limit(final ListNumber data, final int start, final int end) {
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
     * @param data A list of numbers
     * @param factor The multiplicative constant
     * @param offset The additive constant
     * @return result[x] = data[x] * factor + offset
     */
    public static ListDouble rescale(final ListNumber data, final double factor, final double offset) {
        if (factor == 1.0)
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
     * Performs a linear transformation on inverse value of each number in a list.
     *
     * @param data  The list of numbers to divide the numerator by
     * @param numerator The numerator for each division
     * @param offset The additive constant
     * @return result[x] = numerator / data[x] + offset
     */
    public static ListDouble invrescale(final ListNumber data, final double numerator, final double offset) {
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return numerator / data.getDouble(index) + offset;
            }

            @Override
            public int size() {
                return data.size();
            }
        };
    }

    /**
     * Raises each value in a list to the same power.
     *
     * @param data The list of numbers to raise to a power
     * @param expon The power to raise each number in the list to
     * @return result[x] = data[x] ** expon
     */
    public static ListDouble listToPow(final ListNumber data, final double expon) {
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return Math.pow(data.getDouble(index), expon);
            }

            @Override
            public int size() {
                return data.size();
            }
        };
    }

    /**
     * Raises a value to the power of each value in a list.
     *
     * @param base The value to raise to each power
     * @param expons The list of exponents to raise the base value to
     * @return result[x] = base ** expons[x]
     */
    public static ListDouble powList(final double base, final ListNumber expons) {
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return Math.pow(base, expons.getDouble(index));
            }

            @Override
            public int size() {
                return expons.size();
            }
        };
    }

    /**
     * Returns a new list where all elements are added to a constant.
     *
     * @param data a list of number
     * @param offset the additive constant
     * @return result[x] = data[x] + offset
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
     * @return result[x] = data1[x] + data2[x]
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

    /**
     * Returns a list where each element is the difference of the elements of the two
     * lists at the same index. The lists have to match in size.
     *
     * @param data1 a list of numbers
     * @param data2 another list of numbers
     * @return result[x] = data1[x] - data2[x]
     */
    public static ListDouble subtract(final ListNumber data1, final ListNumber data2) {
        if (data1.size() != data2.size())
            throw new IllegalArgumentException("Can't subtract ListNumbers of different size (" + data1.size() + " - " + data2.size() + ")");
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return data1.getDouble(index) - data2.getDouble(index);
            }

            @Override
            public int size() {
                return data1.size();
            }
        };
    }

    /**
     * Returns a list where each element is the product of the elements of the two
     * lists at the same index. The lists have to match in size.
     *
     * @param data1 a list of numbers
     * @param data2 another list of numbers
     * @return result[x] = data1[x] * data2[x]
     */
    public static ListDouble mult(final ListNumber data1, final ListNumber data2) {
        if (data1.size() != data2.size())
            throw new IllegalArgumentException(
                        "Can't do element-wise mult on ListNumbers of different size ("
                     + data1.size() + " - " + data2.size() + ")");
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return data1.getDouble(index) * data2.getDouble(index);
            }

            @Override
            public int size() {
                return data1.size();
            }
        };
    }

    /**
     * Returns a list where each element is the division of the elements of the two
     * lists at the same index. The lists have to match in size.
     *
     * @param data1 a list of numbers
     * @param data2 another list of numbers
     * @return result[x] = data1[x] / data2[x]
     */
    public static ListDouble div(final ListNumber data1, final ListNumber data2) {
        if (data1.size() != data2.size())
            throw new IllegalArgumentException(
                        "Can't do element-wise mult on ListNumbers of different size ("
                     + data1.size() + " - " + data2.size() + ")");
        return new ListDouble() {

            @Override
            public double getDouble(int index) {
                return data1.getDouble(index) / data2.getDouble(index);
            }

            @Override
            public int size() {
                return data1.size();
            }
        };
    }

}
