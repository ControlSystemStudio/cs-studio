/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author carcassi
 */
public class DataRecipeBuilder {

    private final Map<Collector<?>, Map<String, ValueCache>> channelsPerCollector;

    public DataRecipeBuilder() {
        channelsPerCollector = new HashMap<Collector<?>, Map<String, ValueCache>>();
    }

    public void addCollector(Collector<?> collector, Map<String, ValueCache> caches) {
        channelsPerCollector.put(collector, caches);
    }

    public void addAll(DataRecipeBuilder recipe) {
        channelsPerCollector.putAll(recipe.channelsPerCollector);
    }

    public DataRecipe build() {
        return new DataRecipe(new HashMap<Collector<?>, Map<String, ValueCache>>(channelsPerCollector));
    }
}
