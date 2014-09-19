/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * A collector that keeps only the latest value.
 *
 * @param <T> the type stored in the collector
 * @author carcassi
 */
public class LatestValueCollector<T> implements Collector<T, T> {
    
    private final Object lock = new Object();
    private T value;
    private Runnable notification;

    @Override
    public void writeValue(T newValue) {
        Runnable task;
        synchronized (lock) {
            value = newValue;
            task = notification;
        }
        // Run the task without holding the lock
        if (task != null) {
            task.run();
        }
    }

    @Override
    public T readValue() {
        synchronized (lock) {
            return value;
        }
    }

    @Override
    public void setChangeNotification(Runnable notification) {
        synchronized (lock) {
            this.notification = notification;
        }
    }
    
}
