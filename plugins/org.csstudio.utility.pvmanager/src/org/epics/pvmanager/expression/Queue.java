/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.List;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.WriteFunction;

/**
 * A queue expression.
 * <p>
 * The expression can be used to collect values from pvmanager datasources or from
 * external sources.
 *
 * @param <T> the type of values in the queue
 * @author carcassi
 */
public class Queue<T> extends DesiredRateExpressionImpl<List<T>> {
    
    private static <T> QueueCollector<T> createQueue(int maxSize) {
        return new QueueCollector<>(maxSize);
    }

    /**
     * Creates a new queue expression.
     *
     * @param maxSize the maximum number of elements in the cache
     */
    public Queue(int maxSize) {
        super(new DesiredRateExpressionListImpl<Object>(), Queue.<T>createQueue(maxSize), "queue");
    }

    /**
     * Creates a new queue expression.
     *
     * @param sourceExpression the source rate expression that will fill the queue
     * @param maxSize the maximum number of elements in the cache
     */
    public Queue(SourceRateExpression<T> sourceExpression, int maxSize) {
        super(sourceExpression, Queue.<T>createQueue(maxSize), "queue");
    }
    
    /**
     * The write function to be used to fill the queue.
     *
     * @return a write function
     */
    public WriteFunction<T> getWriteFunction() {
        return getCollector();
    }
    
    @SuppressWarnings("unchecked")
    private QueueCollector<T> getCollector() {
        return (QueueCollector<T>) getFunction();
    }
    
    /**
     * Changes the maximum size of the queue.
     *
     * @param maxSize the number of values kept in the queue
     * @return this expression
     */
    public Queue<T> maxSize(int maxSize) {
        getCollector().setMaxSize(maxSize);
        return this;
    }
    
    /**
     *
     * @param newValue
     */
    public void add(T newValue) {
        getWriteFunction().writeValue(newValue);
    }
    
}
 