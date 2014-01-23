/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.HashMap;
import java.util.Map;
import org.epics.pvmanager.PVWriterDirector;
import org.epics.pvmanager.WriteCache;
import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.WriteRecipeBuilder;

/**
 * Implementation class for {@link WriteExpression}.
 *
 * @param <W> type of the write payload
 * @author carcassi
 */
public class WriteExpressionImpl<W> extends WriteExpressionListImpl<W> implements WriteExpression<W> {

    private Map<String, WriteCache<?>> writeCaches;
    private WriteFunction<W> writeFunction;
    private String defaultName;
    
    {
        // Make sure that the list includes this expression
        addThis();
    }

    /**
     * Constructor that represents a single channel of a particular type.
     *
     * @param channelName the name of the channel
     */
    public WriteExpressionImpl(String channelName) {
        WriteCache<W> cache = new WriteCache<W>(channelName);
        writeCaches = new HashMap<String, WriteCache<?>>();
        writeCaches.put(channelName, cache);
        this.writeFunction = cache;
        this.defaultName = channelName;
    }

    /**
     * Changes the name for this expression
     * 
     * @param name new name
     * @return this
     */
    public final WriteExpression<W> as(String name) {
        defaultName = name;
        return this;
    }

    /**
     * Creates a new write expression.
     * 
     * @param childExpressions the expressions used as arguments by this expression
     * @param function the function that will decompose the payload for this expression
     * @param defaultName the name for this expression
     */
    public WriteExpressionImpl(WriteExpressionList<?> childExpressions, WriteFunction<W> function, String defaultName) {
        writeCaches = new HashMap<String, WriteCache<?>>();
        for (WriteExpression<?> childExpression : childExpressions.getWriteExpressions()) {
            for (Map.Entry<String, WriteCache<?>> entry : childExpression.getWriteExpressionImpl().getWriteCaches().entrySet()) {
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
    @Override
    public final String getName() {
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
    @Override
    public final WriteFunction<W> getWriteFunction() {
        return writeFunction;
    }

    @Override
    public void fillWriteRecipe(PVWriterDirector director, WriteRecipeBuilder builder) {
        for (Map.Entry<String, WriteCache<?>> entry : writeCaches.entrySet()) {
            String channelName = entry.getKey();
            WriteCache<? extends Object> writeCache = entry.getValue();
            builder.addChannel(channelName, writeCache);
        }
    }

    @Override
    public final WriteExpressionImpl<W> getWriteExpressionImpl() {
        return this;
    }

}
