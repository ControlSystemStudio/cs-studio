/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * A cache that, after a value is put in the given value cache, calculates
 * the value of the forward function and stores it in the forward writer, while
 * locking on the forward function.
 * <p>
 * This can be used to connect a function of multiple values to a single
 * collector.
 *
 * @param <T> type to cache
 * @param <R> type to forward when the cache changes
 * @author carcassi
 */
public class ForwardCache<T, R> implements ValueCache<T> {
    
    private final ValueCache<T> valueCache;
    private final ReadFunction<R> forwardFunction;
    private final WriteFunction<R> forwardWriter;

    /**
     * Creates a new ForwardCache.
     *
     * @param valueCache the cache that will hold the value
     * @param forwardFunction the function that calculates the value to forward
     * @param forwardWriter the destination of the calculated value
     */
    public ForwardCache(ValueCache<T> valueCache, ReadFunction<R> forwardFunction, WriteFunction<R> forwardWriter) {
        this.valueCache = valueCache;
        this.forwardFunction = forwardFunction;
        this.forwardWriter = forwardWriter;
    }

    @Override
    public T readValue() {
        return valueCache.readValue();
    }

    @Override
    public void writeValue(T newValue) {
        synchronized(forwardFunction) {
            valueCache.writeValue(newValue);
            R forwardValue = forwardFunction.readValue();
            forwardWriter.writeValue(forwardValue);
        }
    }

    @Override
    public Class<T> getType() {
        return valueCache.getType();
    }
    
}
