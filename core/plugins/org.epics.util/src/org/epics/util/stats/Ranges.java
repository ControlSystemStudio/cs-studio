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
     * Returns the range of the absolute values within the range.
     * <p>
     * If the range is all positive, it returns the same range.
     * 
     * @param range a range
     * @return the range of the absolute values
     */
    public static Range absRange(Range range) {
        if (range.getMinimum().doubleValue() >= 0 && range.getMaximum().doubleValue() >= 0) {
            return range;
        } else if (range.getMinimum().doubleValue() < 0 && range.getMaximum().doubleValue() < 0) {
            return range(- range.getMaximum().doubleValue(), - range.getMinimum().doubleValue());
        } else {
            return range(0, Math.max(range.getMinimum().doubleValue(), range.getMaximum().doubleValue()));
        }
    }
    
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
    
    /**
     * Increases the given aggregated range with the new data range.
     * <p>
     * TODO: maybe this should be re-thought: it's the same as sum with 
     * different null handling. Maybe a RangeAggregator utility class
     * that also handles numbers?
     * 
     * @param dataRange the new data range; can't be null
     * @param aggregatedRange the old aggregated range; can be null
     * @return a range big enough to contain both ranges
     */
    public static Range aggregateRange(Range dataRange, Range aggregatedRange) {
        if (aggregatedRange == null) {
            return dataRange;
        } else {
            return Ranges.sum(dataRange, aggregatedRange);
        }
    }
    
    /**
     * Percentage, from 0 to 1, of the first range that is contained by
     * the second range.
     * 
     * @param range the range to be contained by the second
     * @param otherRange the range that has to contain the first
     * @return from 0 (if there is no intersection) to 1 (if the ranges are the same)
     */
    public static double overlap(Range range, Range otherRange) {
        double minOverlap = Math.max(range.getMinimum().doubleValue(), otherRange.getMinimum().doubleValue());
        double maxOverlap = Math.min(range.getMaximum().doubleValue(), otherRange.getMaximum().doubleValue());
        double overlapWidth = maxOverlap - minOverlap;
        double rangeWidth = range.getMaximum().doubleValue() - range.getMinimum().doubleValue();
        double fraction = Math.max(0.0, overlapWidth / rangeWidth);
        return fraction;
    }
    
    /**
     * Checks whether the range is of non-zero size and the boundaries are
     * neither NaN or Infinity.
     * 
     * @param range the range
     * @return true if range is of finite, non-zero size
     */
    public static boolean isValid(Range range) {
        if (range == null) {
            return false;
        }
        
        double min = range.getMinimum().doubleValue();
        double max = range.getMaximum().doubleValue();
        
        return min != max && !Double.isNaN(min) && !Double.isInfinite(min) &&
                !Double.isNaN(max) && !Double.isInfinite(max);
    }
    
    /**
     * True if the tow ranges have the same min and max.
     * 
     * @param r1 a range
     * @param r2 another range
     * @return true if equal
     */
    public static boolean equals(Range r1, Range r2) {
        // Check null cases
        if (r1 == null && r2 == null) {
            return true;
        }
        if (r1 == null || r2 == null) {
            return false;
        }
        
        return r1.getMinimum().equals(r2.getMinimum()) && r1.getMaximum().equals(r2.getMaximum());
    }
}
