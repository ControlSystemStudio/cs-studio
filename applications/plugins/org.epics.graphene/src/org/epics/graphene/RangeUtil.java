/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class RangeUtil {
    
    public static double[] ticksForRange(double min, double max, int nTicks) {
        return ticksForRange(min, max, nTicks, Double.MIN_VALUE);
    }
    
    public static double[] ticksForRange(double min, double max, int nTicks, double minIncrement) {
        double magnitude = Math.pow(10.0, Math.floor(Math.log10(Math.max(Math.abs(max), Math.abs(min)))));
        if (magnitude < minIncrement) {
            return new double[] {min, max};
        }
        int ticks = countTicks(min, max, magnitude);
        if (ticks > nTicks) {
            if (ticks / 2 < nTicks) {
                int newTicks = countTicks(min, max, magnitude * 5);
                if (newTicks > 2 && newTicks <= nTicks) {
                    return createTicks(min, max, magnitude * 5);
                }
            }
            
            if (ticks / 5 < nTicks) {
                int newTicks = countTicks(min, max, magnitude * 2);
                if (newTicks > 2 && newTicks <= nTicks) {
                    return createTicks(min, max, magnitude * 2);
                }
            }
            
            return new double[] {min, max};
        } else {
            double increment = magnitude;
            // Refine if there is still space to refine
            while (countTicks(min, max, increment / 2) <= nTicks) {
                if (increment / 10 >= minIncrement && countTicks(min, max, increment / 10) <= nTicks) {
                    increment /= 10;
                } else if (increment / 5 >= minIncrement && countTicks(min, max, increment / 5) <= nTicks) {
                    return createTicks(min, max, increment / 5);
                } else if(increment / 2 >= minIncrement) {
                    return createTicks(min, max, increment / 2);
                } else {
                    return createTicks(min, max, increment);
                }
            }
            return createTicks(min, max, increment);
        }
    }
    
    static int countTicks(double min, double max, double increment) {
        int start = (int) Math.ceil(min / increment);
        int end = (int) Math.floor(max / increment);
        return end - start + 1;
    }
    
    static double[] createTicks(double min, double max, double increment) {
        int start = (int) Math.ceil(min / increment);
        int end = (int) Math.floor(max / increment);
        double[] ticks = new double[end-start+1];
        for (int i = 0; i < ticks.length; i++) {
            ticks[i] = (i + start) * increment;
        }
        return ticks;
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
}
