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
 * A builder for {@link WriteBuffer }.
 *
 * @author carcassi
 */
public class WriteRecipeBuilder {

    private final Map<String, WriteCache<?>> caches;

    /**
     * A new builder
     */
    public WriteRecipeBuilder() {
        caches = new HashMap<>();
    }
    
    public WriteRecipeBuilder addChannel(String channelName, WriteCache<?> writeCache) {
        caches.put(channelName, writeCache);
        return this;
    }

    /**
     * Creates a new WriteRecipe.
     * 
     * @return a new WriteRecipe
     */
    public WriteRecipe build(WriteFunction<Exception> exceptionWriteFunction, ConnectionCollector connectionCollector) {
        Set<ChannelWriteRecipe> recipes = new HashSet<>();
        for (Map.Entry<String, WriteCache<?>> entry : caches.entrySet()) {
            String channelName = entry.getKey();
            WriteCache<?> valueCache = entry.getValue();
            recipes.add(new ChannelWriteRecipe(channelName, 
                    new ChannelHandlerWriteSubscription(valueCache, exceptionWriteFunction, connectionCollector.addChannel(channelName))));
        }
        return new WriteRecipe(recipes);
    }
    
}
