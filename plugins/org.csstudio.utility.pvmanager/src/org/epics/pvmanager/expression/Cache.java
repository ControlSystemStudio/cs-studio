/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.List;
import org.epics.pvmanager.CacheCollector;
import org.epics.pvmanager.WriteFunction;

/**
 * A cache expression.
 * <p>
 * The expression can be used to collect values from pvmanager datasources or from
 * external sources.
 *
 * @param <T> the type of values in the cache
 * @author carcassi
 */
public class Cache<T> extends DesiredRateExpressionImpl<List<T>> {
    
    private static <T> CacheCollector<T> createCache(int maxElements) {
        return new CacheCollector<>(maxElements);
    }

    /**
     * Creates a new cache expression.
     *
     * @param maxSize the maximum number of elements in the cache
     */
    public Cache(int maxSize) {
        super(new DesiredRateExpressionListImpl<Object>(), Cache.<T>createCache(maxSize), "queue");
    }

    /**
     * Creates a new cache expression.
     *
     * @param sourceExpression the source rate expression that will fill the cache
     * @param maxSize the maximum number of elements in the cache
     */
    public Cache(SourceRateExpression<T> sourceExpression, int maxSize) {
        super(sourceExpression, Cache.<T>createCache(maxSize), "queue");
    }
    
    /**
     * The write function to be used to fill the cache.
     *
     * @return a write function
     */
    public WriteFunction<T> getWriteFunction() {
        return getCollector();
    }
    
    @SuppressWarnings("unchecked")
    private CacheCollector<T> getCollector() {
        return (CacheCollector<T>) getFunction();
    }
    
    /**
     * Changes the maximum size of the cache.
     *
     * @param maxSize the number of values kept in the cache
     * @return this expression
     */
    public Cache<T> maxSize(int maxSize) {
        getCollector().setMaxSize(maxSize);
        return this;
    }
    
    /**
     * Adds a new value to the cache.
     *
     * @param newValue the value to be added
     */
    public void add(T newValue) {
        getWriteFunction().writeValue(newValue);
    }
    
}
 