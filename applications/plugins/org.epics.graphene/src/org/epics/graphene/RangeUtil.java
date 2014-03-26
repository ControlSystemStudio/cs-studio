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
        return NumberUtil.normalize(value, range.getMinimum().doubleValue(), range.getMaximum().doubleValue());
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
}
