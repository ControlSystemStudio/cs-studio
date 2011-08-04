/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.extra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.util.TimeStamp;

/**
 *
 * @author carcassi
 */
public class DoubleArrayTimeCacheFromVDoubleArray implements DoubleArrayTimeCache {
    
    private NavigableMap<TimeStamp, VDoubleArray> cache = new TreeMap<TimeStamp, VDoubleArray>();
    private Function<List<VDoubleArray>> function;
    private Display display;

    public DoubleArrayTimeCacheFromVDoubleArray(Function<List<VDoubleArray>> function) {
        this.function = function;
    }
    
    public class Data implements DoubleArrayTimeCache.Data {
        
        private List<TimeStamp> times = new ArrayList<TimeStamp>();
        private List<double[]> arrays = new ArrayList<double[]>();
        private TimeStamp begin;
        private TimeStamp end;

        private Data(SortedMap<TimeStamp, VDoubleArray> subMap, TimeStamp begin, TimeStamp end) {
            this.begin = begin;
            this.end = end;
            for (Map.Entry<TimeStamp, VDoubleArray> en : subMap.entrySet()) {
                times.add(en.getKey());
                arrays.add(en.getValue().getArray());
            }
        }

        @Override
        public TimeStamp getBegin() {
            return begin;
        }

        @Override
        public TimeStamp getEnd() {
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
        public TimeStamp getTimeStamp(int index) {
            return times.get(index);
        }
        
    }

    @Override
    public DoubleArrayTimeCache.Data getData(TimeStamp begin, TimeStamp end) {
        List<VDoubleArray> newValues = function.getValue();
        for (VDoubleArray value : newValues) {
            cache.put(value.getTimeStamp(), value);
        }
        if (cache.isEmpty())
            return null;
        
        TimeStamp newBegin = cache.lowerKey(begin);
        if (newBegin == null)
            newBegin = cache.firstKey();
        
        return data(newBegin, end);
    }
    
    private DoubleArrayTimeCache.Data data(TimeStamp begin, TimeStamp end) {
        return new Data(cache.subMap(begin, end), begin, end);
    }

    @Override
    public List<DoubleArrayTimeCache.Data> newData(TimeStamp beginUpdate, TimeStamp endUpdate, TimeStamp beginNew, TimeStamp endNew) {
        List<VDoubleArray> newValues = function.getValue();
        
        // No new values, just return the last value
        if (newValues.isEmpty()) {
            return Collections.singletonList(data(cache.lowerKey(endNew), endNew));
        }
        
        List<TimeStamp> newTimeStamps = new ArrayList<TimeStamp>();
        for (VDoubleArray value : newValues) {
            cache.put(value.getTimeStamp(), value);
            newTimeStamps.add(value.getTimeStamp());
        }
        if (cache.isEmpty())
            return Collections.emptyList();
        
        Collections.sort(newTimeStamps);
        TimeStamp firstNewValue = newTimeStamps.get(0);
        
        // We have just one section that start from the oldest update.
        // If the oldest update is too far, we use the start of the update region.
        // If the oldest update is too recent, we start from the being period
        TimeStamp newBegin = firstNewValue;
        if (firstNewValue.compareTo(beginUpdate) < 0) {
            newBegin = beginUpdate;
        }
        if (firstNewValue.compareTo(beginNew) > 0) {
            newBegin = beginNew;
        }
        
        
        newBegin = cache.lowerKey(newBegin);
        if (newBegin == null)
            newBegin = cache.firstKey();
        
        return Collections.singletonList(data(newBegin, endNew));
    }

    @Override
    public Display getDisplay() {
        if (display == null) {
            display = cache.firstEntry().getValue();
        }
            
        return display;
    }
    
}
