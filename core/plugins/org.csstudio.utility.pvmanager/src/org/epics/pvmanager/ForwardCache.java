/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
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
 * @author carcassi
 */
public class ForwardCache<T, R> implements ValueCache<T> {
    
    private final ValueCache<T> valueCache;
    private final Function<R> forwardFunction;
    private final WriteFunction<R> forwardWriter;

    public ForwardCache(ValueCache<T> valueCache, Function<R> forwardFunction, WriteFunction<R> forwardWriter) {
        this.valueCache = valueCache;
        this.forwardFunction = forwardFunction;
        this.forwardWriter = forwardWriter;
    }

    @Override
    public T getValue() {
        return valueCache.getValue();
    }

    @Override
    public void setValue(T newValue) {
        synchronized(forwardFunction) {
            valueCache.setValue(newValue);
            R forwardValue = forwardFunction.getValue();
            forwardWriter.setValue(forwardValue);
        }
    }

    @Override
    public Class<T> getType() {
        return valueCache.getType();
    }
    
}
