/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An expression that represent a pv write.
 * Objects of this class are not created directly but through the operators defined
 * in {@link ExpressionLanguage}.
 *
 * @param <T> type taken by the expression
 * @author carcassi
 */
public class WriteExpression<T> {

    private Map<String, WriteCache<?>> writeCaches;
    private WriteFunction<T> writeFunction;
    private final String defaultName;

    /**
     * Constructor that represents a single channel of a particular type.
     *
     * @param channelName the name of the channel
     */
    public WriteExpression(String channelName) {
        WriteCache<T> cache = new WriteCache<T>();
        writeCaches = new HashMap<String, WriteCache<?>>();
        writeCaches.put(channelName, cache);
        this.writeFunction = cache;
        this.defaultName = channelName;
    }

    public WriteExpression(WriteExpression<?> childExpression, WriteFunction<T> function, String defaultName) {
        this(Collections.<WriteExpression<?>>singletonList(childExpression), function, defaultName);
    }

    public WriteExpression(List<WriteExpression<?>> childExpressions, WriteFunction<T> function, String defaultName) {
        writeCaches = new HashMap<String, WriteCache<?>>();
        for (WriteExpression<?> childExpression : childExpressions) {
            for (Map.Entry<String, WriteCache<?>> entry : childExpression.getWriteCaches().entrySet()) {
                String pvName = entry.getKey();
                if (writeCaches.keySet().contains(pvName)) {
                    throw new RuntimeException("Can't define a write operation that writes to the same channel more than once.");
                }
                writeCaches.put(pvName, entry.getValue());
            }
        }
        this.writeFunction = function;
        this.defaultName = defaultName;
    }

    /**
     * Name representation of the expression.
     *
     * @return a name
     */
    public String getDefaultName() {
        return defaultName;
    }

    /**
     * Returns all the {@link ValueCache}s required by this expression.
     *
     * @return the value caches for this expression
     */
    private Map<String, WriteCache<?>> getWriteCaches() {
        return writeCaches;
    }

    /**
     * Returns the function represented by this expression.
     *
     * @return the function
     */
    public WriteFunction<T> getWriteFunction() {
        return writeFunction;
    }

    /**
     * Creates a data recipe for the given expression.
     *
     * @param collector the collector to be notified by changes in this expression
     * @return a data recipe
     */
    WriteBufferBuilder createWriteBuffer() {
        WriteBufferBuilder buffer = new WriteBufferBuilder();
        buffer.addCaches(writeCaches);
        return buffer;
    }

}
