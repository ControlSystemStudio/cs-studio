/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.array.IteratorDouble;

/**
 *
 * @author carcassi
 */
public class MathUtil {
    
    /**
     *Returns the base 10 logarithm of the given value, to the nearest integer value.
     * <p>Uses Math.log10(double a)</p>
     * <p>Special cases: (from Math.log10(double a))</p>
     * <ul>
     *  <li>If the argument is NaN or less than zero, then the result is NaN.</li>
     *  <li>If the argument is positive infinity, then the result is positive infinity.</li>
     *  <li>If the argument is positive zero or negative zero, then the result is negative infinity.</li>
     *  <li>If the argument is equal to 10n for integer n, then the result is n</li>
     * </ul>
     * @param value double
     * @return truncated base 10 logarithm of the given value.
     */
    public static int orderOf(double value) {
        return (int) Math.floor(Math.log10(value));
    }

    /**
     * Returns the ratio of the given value to the total range as a <code>double</code> between 0 and 1.
     *
     * @param value - number between min and max, inclusive.
     * @param min - lowest number in range.
     * @param max - highest number in range.
     * @return <code>double</code> from 0 to 1, inclusive.
     */
    public static double normalize(double value, double min, double max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException("value not in range");
        }
        return (value - min) / (max - min);
    }

    /**
     * Returns the ratio of the given value to the total range as a <code>double</code> between 0 and 1, multiplied by newWidth.
     *
     * @param value - number between min and max, inclusive.
     * @param min - lowest number in range.
     * @param max - highest number in range.
     * @param newWidth - scalar to multiply the ratio by.
     * @return <code>double</code> from 0 to newWidth, inclusive.
     */
    public static double scale(double value, double min, double max, double newWidth) {
        return (value - min) * newWidth / (max - min);
    }
}
