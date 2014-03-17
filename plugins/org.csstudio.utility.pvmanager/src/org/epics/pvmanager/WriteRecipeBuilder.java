/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A builder for {@link WriteRecipe }.
 *
 * @author carcassi
 */
public class WriteRecipeBuilder {

    private final Map<String, Collection<WriteCache<?>>> caches;

    /**
     * A new builder
     */
    public WriteRecipeBuilder() {
        caches = new HashMap<>();
    }
    
    /**
     * Adds a channel and its write cache to the recipe.
     *
     * @param channelName the name of the channel
     * @param writeCache the cache that will store the value to write
     * @return this builder
     */
    public WriteRecipeBuilder addChannel(String channelName, WriteCache<?> writeCache) {
        Collection<WriteCache<?>> cachesForChannel = caches.get(channelName);
        if (cachesForChannel == null) {
            cachesForChannel = new HashSet<>();
            caches.put(channelName, cachesForChannel);
        }
        cachesForChannel.add(writeCache);
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
     * @return a new WriteRecipe
     */
    public WriteRecipe build(WriteFunction<Exception> exceptionWriteFunction, ConnectionCollector connectionCollector) {
        Set<ChannelWriteRecipe> recipes = new HashSet<>();
        for (Map.Entry<String, Collection<WriteCache<?>>> entry : caches.entrySet()) {
            String channelName = entry.getKey();
            Collection<WriteCache<?>> valueCaches = entry.getValue();
            for (WriteCache<?> valueCache : valueCaches) {
                recipes.add(new ChannelWriteRecipe(channelName, 
                        new ChannelHandlerWriteSubscription(valueCache, exceptionWriteFunction, connectionCollector.addChannel(channelName))));
            }
        }
        return new WriteRecipe(recipes);
    }
    
}
