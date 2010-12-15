/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.lang.ref.WeakReference;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A source for data that is going to be processed by the PVManager.
 * PVManager can work with more than one source at a time. Support
 * for each different source can be added by external libraries.
 *
 * @author carcassi
 */
public abstract class DataSource {

    private static Logger logger = Logger.getLogger(DataSource.class.getName());

    /**
     * Helper class that contains the logic for processing a new value.
     * It takes care of locking the collector and calling the disconnect
     * when appropriate.
     *
     * @param <P> event payload type
     * @param <V> requested value type
     */
    public static abstract class ValueProcessor<P, V> {

        private final Collector collector;
        private final ValueCache<V> cache;
        private final ExceptionHandler exceptionHandler;

        /**
         * Creates a value processor using the collector and the value cache
         * given.
         *
         * @param collector collector to notify of updates
         * @param cache cache where to put the new data
         * @param exceptionHandler where to forward exceptions
         */
        public ValueProcessor(Collector collector, ValueCache<V> cache, ExceptionHandler exceptionHandler) {
            this.collector = collector;
            this.cache = cache;
            this.exceptionHandler = exceptionHandler;
        }

        /**
         * Processes the given payload, by locking the collector, updating the
         * cache and notifying the collector.
         * 
         * @param payload payload for the data source specific event
         */
        public final void processValue(P payload) {
            // Lock the collector and prepare the new value.
            synchronized (collector) {
                try {
                    if (updateCache(payload, cache))
                        collector.collect();
                } catch (RuntimeException e) {
                    exceptionHandler.handleException(e);
                }
            }
        }

        /**
         * Called by the framework if this callback is no longer needed.
         */
        public abstract void close();

        /**
         * Implements the update of the cache given the protocol specific payload.
         *
         * @param payload the payload of the notification
         * @param cache the cache to update
         * @return true if an update is needed; false if not
         */
        public abstract boolean updateCache(P payload, ValueCache<V> cache);

    }

    /**
     * Connects to a set of channels based on the given recipe.
     * <p>
     * The data source must update the value caches relative to each channel.
     * Before updating any cache, it must lock the collector relative to that
     * cache and after any update it must notify the collector.
     *
     * @param recipe the instructions for the data connection
     */
    public abstract void connect(DataRecipe recipe);

    /**
     * Disconnects the set of channels given by the recipe.
     * <p>
     * The disconnect call is guaranteed to be given the same object,
     * so that the recipe itself can be used as a key in a map to retrieve
     * the list of resources needed to be closed.
     *
     * @param recipe the instructions for the data connection
     */
    public abstract void disconnect(DataRecipe recipe);
}
