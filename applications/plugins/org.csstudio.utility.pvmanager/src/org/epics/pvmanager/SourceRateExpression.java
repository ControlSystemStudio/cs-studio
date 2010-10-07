/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An expression that represent a pv read at the CA rate.
 * Objects of this class are not created directly but through the operators defined
 * in {@link PVExpressionLanguage}.
 *
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

    private SourceRateExpression(List<SourceRateExpression<?>> childExpressions, Function<T> function, String defaultName) {
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

    public String getDefaultName() {
        return defaultName;
    }

    public Map<String, ValueCache> getCaches() {
        return caches;
    }

    public Function<T> getFunction() {
        return function;
    }

    DataRecipeBuilder createMontiorRecipes(Collector collector) {
        DataRecipeBuilder recipe = new DataRecipeBuilder();
        recipe.addCollector(collector, caches);
        return recipe;
    }

}
