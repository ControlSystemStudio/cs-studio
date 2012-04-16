/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.DataRecipeBuilder;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.ValueCache;

/**
 * Implementation class for {@link SourceRateExpression}.
 *
 * @param <R> type of the read payload
 * @author carcassi
 */
public class SourceRateExpressionImpl<R> extends SourceRateExpressionListImpl<R> implements SourceRateExpression<R> {

    private Map<String, ValueCache> caches;
    private Function<R> function;
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
        
        ValueCache<R> cache = new ValueCache<R>(pvType);
        caches = new HashMap<String, ValueCache>();
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
    public SourceRateExpressionImpl(SourceRateExpressionList<?> childExpressions, Function<R> function, String defaultName) {
        caches = new HashMap<String, ValueCache>();
        for (SourceRateExpression<?> childExpression : childExpressions.getSourceRateExpressions()) {
            for (Map.Entry<String, ValueCache> entry : childExpression.getSourceRateExpressionImpl().getCaches().entrySet()) {
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
    private final Map<String, ValueCache> getCaches() {
        return caches;
    }

    @Override
    public final Function<R> getFunction() {
        return function;
    }

    /**
     * Creates a data recipe for the given expression.
     *
     * @param collector the collector to be notified by changes in this expression
     * @return a data recipe
     */
    DataRecipeBuilder createDataRecipe(Collector collector) {
        DataRecipeBuilder recipe = new DataRecipeBuilder();
        recipe.addCollector(collector, caches);
        return recipe;
    }

    @Override
    public final SourceRateExpressionImpl<R> getSourceRateExpressionImpl() {
        return this;
    }

}
