/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class RangeUtil {
    
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
                return RangeUtil.toString(this);
            }
            
        };
    }
    
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
    
    public static String toString(Range range) {
        return "[" + range.getMinimum() + " - " + range.getMaximum() + "]";
    }
    
    public static double normalize(Range range, double value) {
        return MathUtil.normalize(value, range.getMinimum().doubleValue(), range.getMaximum().doubleValue());
    }
    
    public static double[] createBins(double min, double max, int nBins) {
        double increment = (max - min) / nBins;
        double[] boundary = new double[nBins+1];
        boundary[0] = min;
        for (int i = 1; i < boundary.length; i++) {
            boundary[i] = min + ( (max - min) * i / nBins );
        }
        return boundary;
    }

    public static boolean contains(Range xRange, double value) {
        return value >= xRange.getMinimum().doubleValue() && value <= xRange.getMaximum().doubleValue();
    }
    
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
    
    public static Range aggregateRange(Range dataRange, Range aggregatedRange) {
        if (aggregatedRange == null) {
            return dataRange;
        } else {
            return RangeUtil.sum(dataRange, aggregatedRange);
        }
    }
}
