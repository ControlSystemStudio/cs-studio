/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.HashMap;
import java.util.Map;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.ForwardCache;
import org.epics.pvmanager.PVReaderDirector;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.ReadRecipeBuilder;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.ValueCacheImpl;

/**
 * Implementation class for {@link SourceRateExpression}.
 *
 * @param <R> type of the read payload
 * @author carcassi
 */
public class SourceRateExpressionImpl<R> extends SourceRateExpressionListImpl<R> implements SourceRateExpression<R> {

    private Map<String, ValueCache<?>> caches;
    private ReadFunction<R> function;
    private String name;
    
    {
        // Make sure that the list includes this expression
        addThis();
    }

    @Override
    public final SourceRateExpressionImpl<R> as(String name) {
        this.name = name;
        return this;
    }

    /**
     * Constructor that represents a single pv of a particular type.
     *
     * @param pvName the name of the pv
     * @param pvType the type of the pv
     */
    public SourceRateExpressionImpl(String pvName, Class<R> pvType) {
        if (pvName == null)
            throw new NullPointerException("Channel name can't be null");
        
        if (pvName.trim().isEmpty())
            throw new IllegalArgumentException("Channel name can't be an empty String");
        
        ValueCache<R> cache = new ValueCacheImpl<R>(pvType);
        caches = new HashMap<String, ValueCache<?>>();
        caches.put(pvName, cache);
        this.function = cache;
        this.name = pvName;
    }

    /**
     * Creates a new source rate expression.
     * 
     * @param childExpressions the expressions used as arguments by this expression
     * @param function the function that will calculate the value for this expression
     * @param defaultName the name for this expression
     */
    public SourceRateExpressionImpl(SourceRateExpressionList<?> childExpressions, ReadFunction<R> function, String defaultName) {
        caches = new HashMap<String, ValueCache<?>>();
        for (SourceRateExpression<?> childExpression : childExpressions.getSourceRateExpressions()) {
            for (Map.Entry<String, ValueCache<?>> entry : childExpression.getSourceRateExpressionImpl().getCaches().entrySet()) {
                String pvName = entry.getKey();
                if (caches.keySet().contains(pvName)) {
                    throw new UnsupportedOperationException("Need to implement functions that take the same PV twice (right now we probably get double notifications)");
                }
                caches.put(pvName, entry.getValue());
            }
        }
        this.function = function;
        this.name = defaultName;
    }

    @Override
    public final String getName() {
        return name;
    }

    /**
     * Returns all the {@link ValueCache}s required by this expression.
     *
     * @return the value caches for this expression
     */
    private Map<String, ValueCache<?>> getCaches() {
        return caches;
    }

    @Override
    public final ReadFunction<R> getFunction() {
        return function;
    }

    /**
     * Creates a data recipe for the given expression.
     *
     * @param collector the collector to be notified by changes in this expression
     * @return a data recipe
     */
    void fillDataRecipe(PVReaderDirector director, Collector<R, ?> collector, ReadRecipeBuilder builder) {
        for (Map.Entry<String, ValueCache<?>> entry : caches.entrySet()) {
            String channelName = entry.getKey();
            @SuppressWarnings("unchecked")
            ValueCache<Object> valueCache = (ValueCache<Object>) entry.getValue();
            builder.addChannel(channelName, new ForwardCache<Object, R>(valueCache, getFunction(), collector));
        }
    }

    @Override
    public final SourceRateExpressionImpl<R> getSourceRateExpressionImpl() {
        return this;
    }

}
