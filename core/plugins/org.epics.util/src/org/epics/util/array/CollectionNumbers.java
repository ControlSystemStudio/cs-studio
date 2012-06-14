/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.util.array;

/**
 *
 * @author carcassi
 */
public class CollectionNumbers {
    public static double[] toDoubleArray(CollectionNumber coll) {
        double[] data = new double[coll.size()];
        IteratorNumber iter = coll.iterator();
        int index = 0;
        while (iter.hasNext()) {
            data[index] = iter.nextDouble();
            index++;
        }
        return data;
    }
    
    public static class MinMax {
        public final Number min;
        public final Number max;

        private MinMax(double min, double max) {
            this.min = min;
            this.max = max;
        }
        
    }
    
    public static MinMax minMaxDouble(CollectionNumber coll) {
        IteratorNumber iterator = coll.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        double min = iterator.nextDouble();
        double max = min;
        
        while (iterator.hasNext()) {
            double value = iterator.nextDouble();
            if (value > max)
                max = value;
            if (value < min)
                min = value;
        }
        
        return new MinMax(min, max);
    }
}
