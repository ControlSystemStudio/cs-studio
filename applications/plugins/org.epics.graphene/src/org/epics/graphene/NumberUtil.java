/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import org.epics.util.array.IteratorDouble;

/**
 *
 * @author carcassi
 */
public class NumberUtil {
    
    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
    
    public static double scale(double value, double min, double max, double newWidth) {
        return (value - min) * newWidth / (max - min);
    }
    
    /**
     * Returns the minimum and the maximum value in the array.
     * 
     * @param array an array
     * @return the minimum and the maximum
     */
    public static int[] minMax(int[] array) {
        if (array.length == 0) {
            return null;
        }
        
        int[] minMax = new int[] {array[0], array[0]};
        for (int i = 1; i < array.length; i++) {
            int value = array[i];
            if (value > minMax[1])
                minMax[1] = value;
            if (value < minMax[0])
                minMax[0] = value;
        }
        
        return minMax;
    }
    
    /**
     * Returns the minimum and the maximum value in the array.
     * 
     * @param array an array
     * @return the minimum and the maximum
     */
    public static double[] minMax(double[] array) {
        return minMax(Iterators.arrayIterator(array));
    }
    
    public static double[] minMax(IteratorDouble iterator) {
        if (!iterator.hasNext()) {
            return null;
        }
        double firstValue = iterator.nextDouble();
        
        double[] minMax = new double[] {firstValue, firstValue};
        while (iterator.hasNext()) {
            double value = iterator.nextDouble();
            if (value > minMax[1])
                minMax[1] = value;
            if (value < minMax[0])
                minMax[0] = value;
        }
        
        return minMax;
    }
}
