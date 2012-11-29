/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import java.util.List;
import org.epics.pvmanager.CacheCollector;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.WriteFunction;

/**
 * A queue expression that can get values from sources other than
 * pvmanager datasources.
 *
 * @author carcassi
 */
public class Cache<T> extends DesiredRateExpressionImpl<List<T>> {
    
    private static <T> CacheCollector<T> createCache(int maxElements) {
        return new CacheCollector<>(maxElements);
    }

    public Cache(int maxElements) {
        super(new DesiredRateExpressionListImpl<Object>(), Cache.<T>createCache(maxElements), "queue");
    }

    public Cache(SourceRateExpression<T> sourceExpression, int maxElements) {
        super(sourceExpression, Cache.<T>createCache(maxElements), "queue");
    }
    
    public WriteFunction<T> getWriteFunction() {
        return getCollector();
    }
    
    @SuppressWarnings("unchecked")
    private CacheCollector<T> getCollector() {
        return (CacheCollector<T>) getFunction();
    }
    
    public Cache<T> maxSize(int maxSize) {
        getCollector().setMaxSize(maxSize);
        return this;
    }
    
    public void add(T newValue) {
        getWriteFunction().setValue(newValue);
    }
    
}
 