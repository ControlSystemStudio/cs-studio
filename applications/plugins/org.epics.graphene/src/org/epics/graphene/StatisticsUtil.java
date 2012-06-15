/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.graphene;

import org.epics.util.array.CollectionNumbers;
import org.epics.util.array.IteratorNumber;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public class StatisticsUtil {
    
    private static class StatisticsImpl implements Statistics {
        
        private final int count;
        private final double minimum;
        private final double maximum;
        private final double average;
        private final double stdDev;

        public StatisticsImpl(int count, double minimum, double maximum, double average, double stdDev) {
            this.count = count;
            this.minimum = minimum;
            this.maximum = maximum;
            this.average = average;
            this.stdDev = stdDev;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Number getMinimum() {
            return minimum;
        }

        @Override
        public Number getMaximum() {
            return maximum;
        }

        @Override
        public double getAverage() {
            return average;
        }

        @Override
        public double getStdDev() {
            return stdDev;
        }
        
    }
    
    public static Statistics statisticsOf(ListNumber data) {
        IteratorNumber iterator = data.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        int count = data.size();
        double min = iterator.nextDouble();
        double max = min;
        double total = 0;
        double totalSquare = 0;
        
        while (iterator.hasNext()) {
            double value = iterator.nextDouble();
            if (value > max)
                max = value;
            if (value < min)
                min = value;
            total += value;
            totalSquare += value*value;
        }
        
        double average = total/count;
        double stdDev = Math.sqrt(totalSquare / count - average * average);
        
        return new StatisticsImpl(count, min, max, average, stdDev);
    }
}
