/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.util.Iterator;
import java.util.List;
import org.epics.util.array.CollectionNumber;
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

    /**
     * Calculates data statistics, excluding NaN values.
     * 
     * @param data the data
     * @return the calculated statistics
     */
    public static Statistics statisticsOf(CollectionNumber data) {
        IteratorNumber iterator = data.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        int count = 0;
        double min = iterator.nextDouble();
        while (Double.isNaN(min)) {
            if (!iterator.hasNext()) {
                return null;
            } else {
                min = iterator.nextDouble();
            }
        }
        double max = min;
        double total = min;
        double totalSquare = min*min;
        count++;
        
        while (iterator.hasNext()) {
            double value = iterator.nextDouble();
            if (!Double.isNaN(value)) {
                if (value > max)
                    max = value;
                if (value < min)
                    min = value;
                total += value;
                totalSquare += value*value;
                count++;
            }
        }
        
        double average = total/count;
        double stdDev = Math.sqrt(totalSquare / count - average * average);
        
        return new StatisticsImpl(count, min, max, average, stdDev);
    }
    
    public static Statistics statisticsOf(List<Statistics> data) {
        if (data.isEmpty())
            return null;
        
        Iterator<Statistics> iterator = data.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        Statistics first = null;
        while (first == null && iterator.hasNext()) {
            first = iterator.next();
        }
        if (first == null)
            return null;
        
        int count = first.getCount();
        double min = first.getMinimum().doubleValue();
        double max = first.getMaximum().doubleValue();
        double total = first.getAverage() * first.getCount();
        double totalSquare = (first.getStdDev() * first.getStdDev() + first.getAverage() * first.getAverage()) * first.getCount();
        
        while (iterator.hasNext()) {
            Statistics stats = iterator.next();
            if (stats.getMaximum().doubleValue() > max)
                max = stats.getMaximum().doubleValue();
            if (stats.getMinimum().doubleValue() < min)
                min = stats.getMinimum().doubleValue();
            total += stats.getAverage() * stats.getCount();
            totalSquare += ( stats.getStdDev() * stats.getStdDev() + stats.getAverage() * stats.getAverage() ) * stats.getCount();
            count += stats.getCount();
        }
        
        double average = total/count;
        double stdDev = Math.sqrt(totalSquare / count - average * average);
        
        return new StatisticsImpl(count, min, max, average, stdDev);
    }
}
