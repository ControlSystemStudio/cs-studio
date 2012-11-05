/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Groups all the parameters required to add a writer to a ChannelHandler.
 *
 * @author carcassi
 */
public class ChannelHandlerWriteSubscription {

    public ChannelHandlerWriteSubscription(WriteCache<?> cache, ExceptionHandler handler, ValueCache<Boolean> connectionCache, Collector<Boolean> connectionCollector) {
        this.cache = cache;
        this.handler = handler;
        this.connectionCache = connectionCache;
        this.connectionCollector = connectionCollector;
    }
    
    private final WriteCache<?> cache;
    private final ExceptionHandler handler;
    private final ValueCache<Boolean> connectionCache;
    private final Collector<Boolean> connectionCollector;

    /**
     * The cache to get the value to write.
     * 
     * @return the write cache
     */
    public WriteCache<?> getCache() {
        return cache;
    }

    /**
     * The exception handler for connection/disconnection errors.
     * 
     * @return the exception handler
     */
    public ExceptionHandler getHandler() {
        return handler;
    }

    /**
     * The cache to hold the connection flag.
     * 
     * @return the cache
     */
    public ValueCache<Boolean> getConnectionCache() {
        return connectionCache;
    }

    /**
     * The collector to notify when the connection changes.
     * 
     * @return the collector
     */
    public Collector<Boolean> getConnectionCollector() {
        return connectionCollector;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.cache != null ? this.cache.hashCode() : 0);
        hash = 11 * hash + (this.handler != null ? this.handler.hashCode() : 0);
        hash = 11 * hash + (this.connectionCache != null ? this.connectionCache.hashCode() : 0);
        hash = 11 * hash + (this.connectionCollector != null ? this.connectionCollector.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChannelHandlerWriteSubscription other = (ChannelHandlerWriteSubscription) obj;
        if (this.cache != other.cache && (this.cache == null || !this.cache.equals(other.cache))) {
            return false;
        }
        if (this.handler != other.handler && (this.handler == null || !this.handler.equals(other.handler))) {
            return false;
        }
        if (this.connectionCache != other.connectionCache && (this.connectionCache == null || !this.connectionCache.equals(other.connectionCache))) {
            return false;
        }
        if (this.connectionCollector != other.connectionCollector && (this.connectionCollector == null || !this.connectionCollector.equals(other.connectionCollector))) {
            return false;
        }
        return true;
    }
    
}
