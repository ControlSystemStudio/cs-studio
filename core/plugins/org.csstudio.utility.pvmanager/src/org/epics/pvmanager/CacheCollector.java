/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A collector the keeps the last n elements.
 *
 * @author carcassi
 */
public class CacheCollector<T> implements Collector<T, List<T>> {
    
    private final Object lock = new Object();
    private final List<T> readBuffer = new ArrayList<>();
    private final List<T> writeBuffer = new LinkedList<>();
    private int maxSize;

    public CacheCollector(int maxSize) {
        synchronized(lock) {
            this.maxSize = maxSize;
        }
    }

    @Override
    public void setValue(T newValue) {
        synchronized(lock) {
            writeBuffer.add(newValue);
            if (writeBuffer.size() > maxSize) {
                writeBuffer.remove(0);
            }
        }
    }

    @Override
    public List<T> getValue() {
        readBuffer.clear();
        synchronized(lock) {
            readBuffer.addAll(writeBuffer);
        }
        return readBuffer;
    }

    public void setMaxSize(int maxSize) {
        synchronized(lock){
            this.maxSize = maxSize;
            while (writeBuffer.size() > maxSize) {
                writeBuffer.remove(0);
            }
        }
    }

    public int getMaxSize() {
        synchronized(lock) {
            return maxSize;
        }
    }
    
}
