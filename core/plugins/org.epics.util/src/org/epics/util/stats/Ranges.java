/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.util.stats;

/**
 * Utility classes to compute ranges.
 *
 * @author carcassi
 */
public class Ranges {
    
    /**
     * Range from given min and max.
     * 
     * @param minValue minimum value
     * @param maxValue maximum value
     * @return the range
     */
    public static Range range(final double minValue, final double maxValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("minValue should be less then or equal to maxValue (" + minValue+ ", " + maxValue + ")");
        }
        return new Range() {

            @Override
            public Number getMinimum() {
                return minValue;
            }

            @Override
            public Number getMaximum() {
                return maxValue;
            }

            @Override
            public String toString() {
                return Ranges.toString(this);
            }
            
        };
    }
    
    /**
     * Determines whether the subrange is contained in the range or not.
     * 
     * @param range a range
     * @param subrange a possible subrange
     * @return true if subrange is contained in range
     */
    public static boolean contains(Range range, Range subrange) {
        return range.getMinimum().doubleValue() <= subrange.getMinimum().doubleValue()
                && range.getMaximum().doubleValue() >= subrange.getMaximum().doubleValue();
        
    }

    /**
     * Determines the range that can contain both ranges. If one of the
     * ranges in contained in the other, the bigger range is returned.
     * 
     * @param range1 a range
     * @param range2 another range
     * @return the bigger range
     */
    public static Range sum(Range range1, Range range2) {
        if (range1.getMinimum().doubleValue() <= range2.getMinimum().doubleValue()) {
            if (range1.getMaximum().doubleValue() >= range2.getMaximum().doubleValue()) {
                return range1;
            } else {
                return range(range1.getMinimum().doubleValue(), range2.getMaximum().doubleValue());
            }
        } else {
            if (range1.getMaximum().doubleValue() >= range2.getMaximum().doubleValue()) {
                return range(range2.getMinimum().doubleValue(), range1.getMaximum().doubleValue());
            } else {
                return range2;
            }
        }
        
    }

    /**
     * Returns a String representation of the give range
     * 
     * @param range a range
     * @return the string representation
     */
    public static String toString(Range range) {
        return "[" + range.getMinimum() + " - " + range.getMaximum() + "]";
    }
    
    /**
     * Returns the value normalized within the range. It performs a linear
     * transformation where the minimum value of the range becomes 0 while
     * the maximum becomes 1.
     * 
     * @param range a range
     * @param value a value
     * @return the value transformed based on the range
     */
    public static double normalize(Range range, double value) {
        return normalize(value, range.getMinimum().doubleValue(), range.getMaximum().doubleValue());
    }
    
    private static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    /**
     * Determines whether the value is contained by the range or not.
     * 
     * @param range a range
     * @param value a value
     * @return true if the value is within the range
     */
    public static boolean contains(Range range, double value) {
        return value >= range.getMinimum().doubleValue() && value <= range.getMaximum().doubleValue();
    }
}
