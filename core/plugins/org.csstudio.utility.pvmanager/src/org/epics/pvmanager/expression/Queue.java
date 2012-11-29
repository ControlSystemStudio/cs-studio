/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import java.util.List;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.WriteFunction;

/**
 * A queue expression that can get values from sources other than
 * pvmanager datasources.
 *
 * @author carcassi
 */
public class Queue<T> extends DesiredRateExpressionImpl<List<T>> {
    
    private static <T> QueueCollector<T> createQueue(int maxSize) {
        return new QueueCollector<>(maxSize);
    }

    public Queue(int maxSize) {
        super(new DesiredRateExpressionListImpl<Object>(), Queue.<T>createQueue(maxSize), "queue");
    }

    public Queue(SourceRateExpression<T> sourceExpression, int maxSize) {
        super(sourceExpression, Queue.<T>createQueue(maxSize), "queue");
    }
    
    public WriteFunction<T> getWriteFunction() {
        return getCollector();
    }
    
    @SuppressWarnings("unchecked")
    private QueueCollector<T> getCollector() {
        return (QueueCollector<T>) getFunction();
    }
    
    public Queue<T> maxSize(int maxSize) {
        getCollector().setMaxSize(maxSize);
        return this;
    }
    
    public void add(T newValue) {
        getWriteFunction().writeValue(newValue);
    }
    
}
 