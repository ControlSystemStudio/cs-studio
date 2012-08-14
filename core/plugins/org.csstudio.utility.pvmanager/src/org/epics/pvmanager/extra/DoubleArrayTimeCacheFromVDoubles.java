/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.extra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VDouble;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
public class DoubleArrayTimeCacheFromVDoubles implements DoubleArrayTimeCache {
    
    private NavigableMap<Timestamp, double[]> cache = new TreeMap<Timestamp, double[]>();
    private List<Function<List<VDouble>>> functions;
    private Display display;
    private TimeDuration tolerance = TimeDuration.ofMillis(1);

    public DoubleArrayTimeCacheFromVDoubles(List<Function<List<VDouble>>> functions) {
        this.functions = functions;
    }
    
    public class Data implements DoubleArrayTimeCache.Data {
        
        private List<Timestamp> times = new ArrayList<Timestamp>();
        private List<double[]> arrays = new ArrayList<double[]>();
        private Timestamp begin;
        private Timestamp end;

        private Data(SortedMap<Timestamp, double[]> subMap, Timestamp begin, Timestamp end) {
            this.begin = begin;
            this.end = end;
            for (Map.Entry<Timestamp, double[]> en : subMap.entrySet()) {
                times.add(en.getKey());
                arrays.add(en.getValue());
            }
        }

        @Override
        public Timestamp getBegin() {
            return begin;
        }

        @Override
        public Timestamp getEnd() {
            return end;
        }

        @Override
        public int getNArrays() {
            return times.size();
        }

        @Override
        public double[] getArray(int index) {
            return arrays.get(index);
        }

        @Override
        public Timestamp getTimestamp(int index) {
            return times.get(index);
        }
        
    }
    
    /**
     * Finds the array in the cache that is within the tolerance from the 
     * given timestamp. If not found, it creates a new array and adds
     * it to the cache.
     * 
     * @param timeStamp a time
     * @return the array for that time
     */
    private double[] arrayFor(Timestamp timeStamp) {
        // Try to find the array at the exact time
        double[] array = cache.get(timeStamp);
        if (array != null)
            return array;
        
        // See if the array after the timeStamp is in range
        Timestamp newTime = cache.higherKey(timeStamp);
        if (newTime != null && newTime.minus(tolerance).compareTo(timeStamp) <= 0) {
            return cache.get(newTime);
        }
        
        // See if the array before the timeStamp is in range
        newTime = cache.lowerKey(timeStamp);
        if (newTime != null && newTime.plus(tolerance).compareTo(timeStamp) >= 0) {
            return cache.get(newTime);
        }
        
        // Nothing found. Create a new array and initialize it with
        // the previous data (if any)
        if (newTime != null) {
            array = Arrays.copyOf(cache.get(newTime), functions.size());
        } else {
            array = new double[functions.size()];
            Arrays.fill(array, Double.NaN);
        }
        cache.put(timeStamp, array);
        return array;
    }

    @Override
    public DoubleArrayTimeCache.Data getData(Timestamp begin, Timestamp end) {
        // Let's do it in a crappy way first...
        for (int n = 0; n < functions.size(); n++) {
            List<VDouble> vDoubles = functions.get(n).getValue();
            for (VDouble vDouble : vDoubles) {
                if (display == null)
                    display = vDouble;
                double[] array = arrayFor(vDouble.getTimestamp());
                double oldValue = array[n];
                array[n] = vDouble.getValue();
                
                // Fix the following values
                for (Map.Entry<Timestamp, double[]> en : cache.tailMap(vDouble.getTimestamp().plus(tolerance)).entrySet()) {
                    // If no value or same value as before, replace it
                    if (Double.isNaN(en.getValue()[n]) || en.getValue()[n] == oldValue)
                        en.getValue()[n] = vDouble.getValue();
                }
            }
        }

        if (cache.isEmpty())
            return null;
        
        Timestamp newBegin = cache.lowerKey(begin);
        if (newBegin == null)
            newBegin = cache.firstKey();
        
        deleteBefore(begin);
        return data(newBegin, end);
    }
    
    private List<TimeInterval> update() {
        // Let's do it in a crappy way first...
        // Only keep track of first and last change
        Timestamp firstChange = null;
        Timestamp lastChange = null;
        for (int n = 0; n < functions.size(); n++) {
            List<VDouble> vDoubles = functions.get(n).getValue();
            for (VDouble vDouble : vDoubles) {
                if (display == null)
                    display = vDouble;
                double[] array = arrayFor(vDouble.getTimestamp());
                double oldValue = array[n];
                array[n] = vDouble.getValue();
                if (firstChange == null) {
                    firstChange = vDouble.getTimestamp();
                }
                if (lastChange == null) {
                    lastChange = vDouble.getTimestamp();
                }
                firstChange = min(firstChange, vDouble.getTimestamp());
                lastChange = max(lastChange, vDouble.getTimestamp());
                
                // Fix the following values
                for (Map.Entry<Timestamp, double[]> en : cache.tailMap(vDouble.getTimestamp().plus(tolerance)).entrySet()) {
                    // If no value or same value as before, replace it
                    if (Double.isNaN(en.getValue()[n]) || en.getValue()[n] == oldValue)
                        en.getValue()[n] = vDouble.getValue();
                }
            }
        }
        
        if (firstChange == null) {
            return Collections.emptyList();
        }
        
        return Collections.singletonList(TimeInterval.between(firstChange.minus(tolerance), lastChange));
    }
    
    private void deleteBefore(Timestamp timeStamp) {
        if (cache.isEmpty())
            return;
        
        // This we want to keep as we need to draw the area
        // from the timestamp to the first new value
        Timestamp firstEntryBeforeTimestamp = cache.lowerKey(timeStamp);
        if (firstEntryBeforeTimestamp == null)
            return;
        
        // This is the last entry we want to delete
        Timestamp lastToDelete = cache.lowerKey(firstEntryBeforeTimestamp);
        if (lastToDelete == null)
            return;
        
        Timestamp firstKey = cache.firstKey();
        while (firstKey.compareTo(lastToDelete) <= 0) {
            cache.remove(firstKey);
            firstKey = cache.firstKey();
        }
    }
    
    private DoubleArrayTimeCache.Data data(Timestamp begin, Timestamp end) {
        return new Data(cache.subMap(begin, end), begin, end);
    }
    
    private <T extends Comparable<T>> T max(T a, T b) {
        if (a.compareTo(b) > 0) {
            return a;
        } else {
            return b;
        }
    }
    
    private <T extends Comparable<T>> T min(T a, T b) {
        if (a.compareTo(b) < 0) {
            return a;
        } else {
            return b;
        }
    }

    @Override
    public List<DoubleArrayTimeCache.Data> newData(Timestamp beginUpdate, Timestamp endUpdate, Timestamp beginNew, Timestamp endNew) {
        List<TimeInterval> updates = update();
        if (updates.isEmpty())
            return Collections.singletonList(data(cache.lowerKey(beginNew), endNew));
        
        TimeInterval updateInterval = updates.get(0);
        Timestamp newBegin = max(beginUpdate, updateInterval.getStart());
        newBegin = min(newBegin, beginNew);
        deleteBefore(beginUpdate);
        return Collections.singletonList(data(newBegin, endNew));
    }

    @Override
    public Display getDisplay() {
        return display;
    }
    
}
