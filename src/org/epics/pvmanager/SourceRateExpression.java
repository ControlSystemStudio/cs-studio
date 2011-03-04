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
 * An expression that represent a pv read at the CA rate.
 * Objects of this class are not created directly but through the operators defined
 * in {@link ExpressionLanguage}.
 *
 * @param <T> type returned by the expression
 * @author carcassi
 */
public class SourceRateExpression<T> {

    private Map<String, ValueCache> caches;
    private Function<T> function;
    private final String defaultName;

    /**
     * Constructor that represents a single pv of a particular type.
     *
     * @param pvName the name of the pv
     * @param pvType the type of the pv
     */
    public SourceRateExpression(String pvName, Class<T> pvType) {
        ValueCache<T> cache = new ValueCache<T>(pvType);
        caches = new HashMap<String, ValueCache>();
        caches.put(pvName, cache);
        this.function = cache;
        this.defaultName = pvName;
    }

    public SourceRateExpression(SourceRateExpression<?> childExpression, Function<T> function, String defaultName) {
        this(Collections.<SourceRateExpression<?>>singletonList(childExpression), function, defaultName);
    }

    public SourceRateExpression(List<SourceRateExpression<?>> childExpressions, Function<T> function, String defaultName) {
        caches = new HashMap<String, ValueCache>();
        for (SourceRateExpression<?> childExpression : childExpressions) {
            for (Map.Entry<String, ValueCache> entry : childExpression.getCaches().entrySet()) {
                String pvName = entry.getKey();
                if (caches.keySet().contains(pvName)) {
                    throw new UnsupportedOperationException("Need to implement functions that take the same PV twice (right now we probably get double notifications)");
                }
                caches.put(pvName, entry.getValue());
            }
        }
        this.function = function;
        this.defaultName = defaultName;
    }

    /**
     * Name representation of the expression.
     * <p>
     * TODO: is this really needed?
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
    private Map<String, ValueCache> getCaches() {
        return caches;
    }

    /**
     * Returns the function represented by this expression.
     *
     * @return the function
     */
    public Function<T> getFunction() {
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

}
