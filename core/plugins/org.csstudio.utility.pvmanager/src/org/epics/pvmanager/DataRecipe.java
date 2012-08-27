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

/**
 * Represents all the information necessary to connect to a {@link DataSource}.
 * It represents the contact between PVManager and the {@code DataSource}.
 *
 * @author carcassi
 */
public class DataRecipe {

    private final Map<Collector<?>, Map<String, ValueCache>> channelsPerCollector;
    private final ExceptionHandler exceptionHandler;
    
    private final Collector<Boolean> connectionCollector;
    private final Map<String, ValueCache<Boolean>> connectionCaches;
    
    private final Collection<ChannelRecipe> channelRecipes;

    /**
     * Creates an empty data recipe.
     */
    public DataRecipe() {
        this(Collections.<Collector<?>, Map<String, ValueCache>>emptyMap(), new ExceptionHandler());
    }

    /**
     * Creates a new recipe. The collections passed to the constructor must
     * already be immutable copies.
     *
     * @param channelsPerCollector the list of all channels needed by each collector
     */
    DataRecipe(Map<Collector<?>, Map<String, ValueCache>> channelsPerCollector) {
        this(channelsPerCollector, new ExceptionHandler());
    }

    private DataRecipe(Map<Collector<?>, Map<String, ValueCache>> channelsPerCollector, ExceptionHandler exceptionHandler) {
        this.channelsPerCollector = Collections.unmodifiableMap(new HashMap<Collector<?>, Map<String, ValueCache>>(channelsPerCollector));
        this.exceptionHandler = exceptionHandler;
        connectionCaches = generateConnectionCaches();
        connectionCollector = new ConnectionCollector(connectionCaches);
        channelRecipes = generateChannelRecipes(channelsPerCollector, connectionCaches, connectionCollector, exceptionHandler);
    }

    public DataRecipe(ExceptionHandler exceptionHandler) {
        this(Collections.<Collector<?>, Map<String, ValueCache>>emptyMap(), exceptionHandler);
    }

    DataRecipe(Collection<ChannelRecipe> channelRecipes) {
        if (channelRecipes.isEmpty()) {
            throw new IllegalArgumentException("Channel recipes can't be empty");
        }
        this.channelRecipes = channelRecipes;
        this.channelsPerCollector = null;
        this.exceptionHandler = channelRecipes.iterator().next().getReadSubscription().getHandler();
        connectionCaches = null;
        connectionCollector = channelRecipes.iterator().next().getReadSubscription().getConnCollector();
    }

    /**
     * Creates a new recipe by adding the new collector and the new caches.
     * <p>
     * Note: this method should be only used for testing as it does not scale.
     *
     * @param collector the new collector
     * @param caches the caches that the collector depends on
     * @return the new recipe
     */
    public DataRecipe includeCollector(Collector<?> collector, Map<String, ValueCache> caches) {
        Map<Collector<?>, Map<String, ValueCache>> newChannelsPerCollector =
                new HashMap<Collector<?>, Map<String, ValueCache>>(channelsPerCollector);
        Map<String, ValueCache> newCaches =
                Collections.unmodifiableMap(new HashMap<String, ValueCache>(caches));
        newChannelsPerCollector.put(collector, newCaches);
        return new DataRecipe(newChannelsPerCollector, exceptionHandler);
    }

    /**
     * Creates a new recipe by adding the given recipe to this one.
     * <p>
     * Note: this method should be only used for testing as it does not scale.
     *
     * @param dataRecipe the recipe to include
     * @return the new recipe
     */
    public DataRecipe includeRecipe(DataRecipe dataRecipe) {
        Map<Collector<?>, Map<String, ValueCache>> newChannelsPerCollector =
                new HashMap<Collector<?>, Map<String, ValueCache>>(channelsPerCollector);
        newChannelsPerCollector.putAll(dataRecipe.channelsPerCollector);
        return new DataRecipe(Collections.unmodifiableMap(newChannelsPerCollector), exceptionHandler);
    }

    /**
     * Returns all channels that need to be connected, organized by the collector
     * that needs them.
     * <p>
     * Every time there is a new value, the data source will need to lock
     * the collector, put the values in the
     * value cache relative for each channel, and notify the collector.
     *
     * @return a map with all the channels organized by collector.
     */
    public Map<Collector<?>, Map<String, ValueCache>> getChannelsPerCollectors() {
        return channelsPerCollector;
    }

    /**
     * Returns the exception handler to be used for this PV.
     *
     * @return the exception handler
     */
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Creates a new data recipe with the given handler.
     * 
     * @param handler a new exception handler
     * @return a new data recipe
     */
    public DataRecipe withExceptionHandler(ExceptionHandler handler) {
        return new DataRecipe(channelsPerCollector, handler);
    }

    public Collector<Boolean> getConnectionCollector() {
        return connectionCollector;
    }

    public Map<String, ValueCache<Boolean>> getConnectionCaches() {
        return connectionCaches;
    }
    
    private Map<String, ValueCache<Boolean>> generateConnectionCaches() {
        Map<String, ValueCache<Boolean>> newCaches = new HashMap<String, ValueCache<Boolean>>();
        for (Map.Entry<Collector<?>, Map<String, ValueCache>> collEntry : channelsPerCollector.entrySet()) {
            for (Map.Entry<String, ValueCache> entry : collEntry.getValue().entrySet()) {
                String name = entry.getKey();
                ValueCache<Boolean> cache = new ValueCache<Boolean>(Boolean.class);
                cache.setValue(false);
                newCaches.put(name, cache);
            }
        }
        return newCaches;
    }

    public Collection<ChannelRecipe> getChannelRecipes() {
        return channelRecipes;
    }

    private static Collection<ChannelRecipe> generateChannelRecipes(Map<Collector<?>, Map<String, ValueCache>> channelsPerCollector, Map<String, ValueCache<Boolean>> connectionCaches, Collector<Boolean> connectionCollector, ExceptionHandler exceptionHandler) {
        Set<ChannelRecipe> channelRecipes = new HashSet<ChannelRecipe>();
        for (Map.Entry<Collector<?>, Map<String, ValueCache>> entry : channelsPerCollector.entrySet()) {
            Collector<? extends Object> valueCollector = entry.getKey();
            for (Map.Entry<String, ValueCache> collEntry : entry.getValue().entrySet()) {
                String channelName = collEntry.getKey();
                ValueCache valueCache = collEntry.getValue();
                ValueCache<Boolean> connCache = connectionCaches.get(channelName);
                channelRecipes.add(new ChannelRecipe(channelName, new ChannelHandlerReadSubscription(valueCollector, valueCache, exceptionHandler, connectionCollector, connCache)));
            }
        }
        return channelRecipes;
    }
}
