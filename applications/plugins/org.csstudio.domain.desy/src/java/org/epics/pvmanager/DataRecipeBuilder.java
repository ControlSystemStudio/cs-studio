/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder class for {@link DataRecipe}.
 *
 * @author carcassi
 */
public class DataRecipeBuilder {

    private final Map<Collector<?>, Map<String, ValueCache>> channelsPerCollector;

    /**
     * New builder.
     */
    public DataRecipeBuilder() {
        channelsPerCollector = new HashMap<Collector<?>, Map<String, ValueCache>>();
    }

    /**
     * Add a collector and the channel/caches this collector will get values from.
     * 
     * @param collector a collector
     * @param caches the channel/caches
     */
    public void addCollector(Collector<?> collector, Map<String, ValueCache> caches) {
        channelsPerCollector.put(collector, caches);
    }

    /**
     * Add all elements from another builder.
     * 
     * @param recipe another recipse
     */
    public void addAll(DataRecipeBuilder recipe) {
        channelsPerCollector.putAll(recipe.channelsPerCollector);
    }

    /**
     * Builds the recipe.
     * 
     * @return a new recipe
     */
    public DataRecipe build() {
        return new DataRecipe(channelsPerCollector);
    }
}
