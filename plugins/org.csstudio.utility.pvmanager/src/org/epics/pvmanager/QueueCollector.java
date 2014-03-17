/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects value at read rate and puts them in a queue.
 * <p>
 * Always returns new value as every read clears the cache.
 * The values are returned oldest value first. When maxSize is reached,
 * the oldest values are discarded.
 *
 * @param <T> the type contained in the queue
 * @author carcassi
 */
public class QueueCollector<T> implements Collector<T, List<T>> {
    
    private final Object lock = new Object();
    private List<T> readBuffer;
    private List<T> writeBuffer;
    private int maxSize;

    /**
     * New queue collector with the given max size for the queue.
     * 
     * @param maxSize maximum number of elements in the queue
     */
    public QueueCollector(int maxSize) {
        synchronized(lock) {
            this.maxSize = maxSize;
            readBuffer = new ArrayList<>();
            writeBuffer = new ArrayList<>();
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
        synchronized(lock) {
            List<T> data = writeBuffer;
            writeBuffer = readBuffer;
            writeBuffer.clear();
            readBuffer = data;
        }
        return readBuffer;
    }

    /**
     * Changes the number of maximum values in the queue.
     * <p>
     * If new maxSize is less than the current number of element in the queue,
     * the old values are discarded.
     * 
     * @param maxSize the maximum number of elements in the queue
     */
    public void setMaxSize(int maxSize) {
        synchronized(lock) {
            this.maxSize = maxSize;
            while (writeBuffer.size() > maxSize) {
                writeBuffer.remove(0);
            }
        }
    }

    /**
     * The maximum number of elements in the queue.
     * 
     * @return the maximum number of elements in the queue
     */
    public int getMaxSize() {
        synchronized(lock) {
            return maxSize;
        }
    }
    
}
