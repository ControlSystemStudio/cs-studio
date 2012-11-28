/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Builder class for {@link DataRecipe}.
 *
 * @author carcassi
 */
public class ReadRecipeBuilder {

    private final Map<String, ValueCache<?>> channelCaches
            = new HashMap<>();

    /**
     * Add a collector and the channel/caches this collector will get values from.
     * 
     * @param collector a collector
     * @param caches the channel/caches
     */
    public void addChannel(String channelName, ValueCache<?> caches) {
        channelCaches.put(channelName, caches);
    }

    /**
     * Builds the recipe.
     * 
     * @return a new recipe
     */
    public ReadRecipe build(WriteFunction<Exception> exceptionWriteFunction, ConnectionCollector connectionCollector) {
        Set<ChannelReadRecipe> recipes = new HashSet<>();
        for (Map.Entry<String, ValueCache<?>> entry : channelCaches.entrySet()) {
            String channelName = entry.getKey();
            ValueCache<?> valueCache = entry.getValue();
            recipes.add(new ChannelReadRecipe(channelName, 
                    new ChannelHandlerReadSubscription(valueCache, exceptionWriteFunction, connectionCollector.addChannel(channelName))));
        }
        return new ReadRecipe(recipes);
    }
}
