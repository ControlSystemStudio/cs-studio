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
 * Builder class for {@link ReadRecipe}.
 *
 * @author carcassi
 */
public class ReadRecipeBuilder {

    private final Map<String, ValueCache<?>> channelCaches
            = new HashMap();

    /**
     * Adds a channel and its read cache to the recipe.
     * 
     * @param channelName the name of the channel
     * @param caches the cache that contains the value
     * @return this builder
     */
    public ReadRecipeBuilder addChannel(String channelName, ValueCache<?> caches) {
        channelCaches.put(channelName, caches);
        return this;
    }

    /**
     * Builds the recipe.
     * <p>
     * To finish building the recipe, one needs to specify where to send errors
     * and where to send the connection status changes.
     * 
     * @param exceptionWriteFunction where exception should be routed
     * @param connectionCollector where connection status should be routed
     * @return a new recipe
     */
    public ReadRecipe build(WriteFunction<Exception> exceptionWriteFunction, ConnectionCollector connectionCollector) {
        Set<ChannelReadRecipe> recipes = new HashSet();
        for (Map.Entry<String, ValueCache<?>> entry : channelCaches.entrySet()) {
            String channelName = entry.getKey();
            ValueCache<?> valueCache = entry.getValue();
            recipes.add(new ChannelReadRecipe(channelName, 
                    new ChannelHandlerReadSubscription(valueCache, exceptionWriteFunction, connectionCollector.addChannel(channelName))));
        }
        return new ReadRecipe(recipes);
    }
}
