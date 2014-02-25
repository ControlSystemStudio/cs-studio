/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Collects value at read rate and keeps the last n.
 * <p>
 * The values are returned oldest value first. When maxSize is reached,
 * the oldest values are discarded.
 *
 * @param <T> the type contained in the cache
 * @author carcassi
 */
public class CacheCollector<T> implements Collector<T, List<T>> {
    
    private final Object lock = new Object();
    private final List<T> readBuffer = new ArrayList<>();
    private final List<T> writeBuffer = new LinkedList<>();
    private int maxSize;

    /**
     * A new cache collector with max size for the cache.
     * 
     * @param maxSize maximum number of elements in the cache
     */
    public CacheCollector(int maxSize) {
        synchronized(lock) {
            this.maxSize = maxSize;
        }
    }

    @Override
    public void writeValue(T newValue) {
        synchronized(lock) {
            writeBuffer.add(newValue);
            if (writeBuffer.size() > maxSize) {
                writeBuffer.remove(0);
            }
        }
    }

    @Override
    public List<T> readValue() {
        readBuffer.clear();
        synchronized(lock) {
            readBuffer.addAll(writeBuffer);
        }
        return readBuffer;
    }

    /**
     * Changes the maximum size of the cache.
     * <p>
     * If new maxSize is less than the current number of element in the cache,
     * the old values are discarded.
     * 
     * @param maxSize the maximum number of elements in the cache.
     */
    public void setMaxSize(int maxSize) {
        synchronized(lock){
            this.maxSize = maxSize;
            while (writeBuffer.size() > maxSize) {
                writeBuffer.remove(0);
            }
        }
    }

    /**
     * The maximum number of elements in the cache.
     * 
     * @return the maximum number of elements in the cache
     */
    public int getMaxSize() {
        synchronized(lock) {
            return maxSize;
        }
    }
    
}
