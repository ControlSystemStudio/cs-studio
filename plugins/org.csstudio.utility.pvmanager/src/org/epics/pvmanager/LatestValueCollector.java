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
    
    private T value;

    @Override
    public void writeValue(T newValue) {
        value = newValue;
    }

    @Override
    public T readValue() {
        return value;
    }
    
}
