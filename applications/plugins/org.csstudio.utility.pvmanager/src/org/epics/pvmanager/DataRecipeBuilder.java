/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author carcassi
 */
class DataRecipeBuilder {

    private final Map<Collector, Map<String, ValueCache>> channelsPerCollector;

    DataRecipeBuilder() {
        channelsPerCollector = new HashMap<Collector, Map<String, ValueCache>>();
    }

    void addCollector(Collector collector, Map<String, ValueCache> caches) {
        channelsPerCollector.put(collector, caches);
    }

    void addAll(DataRecipeBuilder recipe) {
        channelsPerCollector.putAll(recipe.channelsPerCollector);
    }

    DataRecipe build() {
        return new DataRecipe(new HashMap<Collector, Map<String, ValueCache>>(channelsPerCollector));
    }
}
