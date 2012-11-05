/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.epics.pvmanager.WriteCache;

/**
 * Represents all the values, channel names and ordering information needed
 * for writing
 *
 * @author carcassi
 */
public class WriteBuffer {
    private final Collector<Boolean> connectionCollector;
    private final Collection<ChannelWriteBuffer> channelWriteBuffers;

    WriteBuffer(Map<String, WriteCache<?>> caches, ExceptionHandler exceptionHandler) {
        Map<String, ValueCache<Boolean>> connectionCaches = generateConnectionCaches(caches);
        this.connectionCollector = new ConnectionCollector(connectionCaches);
        this.channelWriteBuffers = generateChannelWriteBuffers(caches, connectionCollector, connectionCaches, exceptionHandler);
    }

    WriteBuffer(Collection<ChannelWriteBuffer> channelWriteBuffers) {
        this.channelWriteBuffers = channelWriteBuffers;
        if (channelWriteBuffers.isEmpty()) {
            this.connectionCollector = null;
        } else {
            this.connectionCollector = channelWriteBuffers.iterator().next().getWriteSubscription().getConnectionCollector();
        }
    }
    
    private static Collection<ChannelWriteBuffer> generateChannelWriteBuffers(Map<String, WriteCache<?>> caches, Collector<Boolean> connectionCollector, Map<String, ValueCache<Boolean>> connectionCaches, ExceptionHandler exceptionHandler) {
        Set<ChannelWriteBuffer> channelRecipes = new HashSet<ChannelWriteBuffer>();
        for (Map.Entry<String, WriteCache<?>> entry : caches.entrySet()) {
            String channelName = entry.getKey();
            WriteCache<? extends Object> writeCache = entry.getValue();
            ValueCache<Boolean> connectionCache = connectionCaches.get(channelName);
            channelRecipes.add(new ChannelWriteBuffer(channelName, new ChannelHandlerWriteSubscription(writeCache, exceptionHandler, connectionCache, connectionCollector)));
        }
        return channelRecipes;
    }
    
    private static Map<String, ValueCache<Boolean>> generateConnectionCaches(Map<String, WriteCache<?>> caches) {
        // TODO: should refactor with similar method in DataRecipe
        Map<String, ValueCache<Boolean>> newCaches = new HashMap<String, ValueCache<Boolean>>();
        for (String name : caches.keySet()) {
            ValueCache<Boolean> cache = new ValueCache<Boolean>(Boolean.class);
            cache.setValue(false);
            newCaches.put(name, cache);
        }
        return newCaches;
    }

    /**
     * Returns the collector with the connection value to be passed
     * to the PVWriter.
     * 
     * @return the connection collector
     */
    public Collector<Boolean> getConnectionCollector() {
        return connectionCollector;
    }

    public Collection<ChannelWriteBuffer> getChannelWriteBuffers() {
        return channelWriteBuffers;
    }
    
}
