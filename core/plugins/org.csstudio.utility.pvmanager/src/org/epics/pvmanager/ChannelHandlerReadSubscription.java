/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager;

/**
 * Groups all the parameters required to add a reader to a ChannelHandler.
 *
 * @author carcassi
 */
public class ChannelHandlerReadSubscription {

    public ChannelHandlerReadSubscription(Collector<?> collector, ValueCache<?> cache, ExceptionHandler handler, Collector<Boolean> connCollector, ValueCache<Boolean> connCache) {
        this.collector = collector;
        this.cache = cache;
        this.handler = handler;
        this.connCollector = connCollector;
        this.connCache = connCache;
    }
    
    private final Collector<?> collector;
    private final ValueCache<?> cache;
    private final ExceptionHandler handler;
    private final Collector<Boolean> connCollector;
    private final ValueCache<Boolean> connCache;

    /**
     * The collector to lock and notify when a new value is available.
     * 
     * @return the value collector
     */
    public Collector<?> getCollector() {
        return collector;
    }

    /**
     * The cache to update when a new value is available.
     * 
     * @return the value cache
     */
    public ValueCache<?> getCache() {
        return cache;
    }

    /**
     * The handler to notify whenever an exception is thrown.
     * 
     * @return the exception handler
     */
    public ExceptionHandler getHandler() {
        return handler;
    }

    /**
     * The collector to lock and notify whenever the connection state changes.
     * 
     * @return the connection state collector
     */
    public Collector<Boolean> getConnCollector() {
        return connCollector;
    }

    /**
     * The cache to update whenever the connection state changes.
     * 
     * @return the connection state cache
     */
    public ValueCache<Boolean> getConnCache() {
        return connCache;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.collector != null ? this.collector.hashCode() : 0);
        hash = 67 * hash + (this.cache != null ? this.cache.hashCode() : 0);
        hash = 67 * hash + (this.handler != null ? this.handler.hashCode() : 0);
        hash = 67 * hash + (this.connCollector != null ? this.connCollector.hashCode() : 0);
        hash = 67 * hash + (this.connCache != null ? this.connCache.hashCode() : 0);
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
        final ChannelHandlerReadSubscription other = (ChannelHandlerReadSubscription) obj;
        if (this.collector != other.collector && (this.collector == null || !this.collector.equals(other.collector))) {
            return false;
        }
        if (this.cache != other.cache && (this.cache == null || !this.cache.equals(other.cache))) {
            return false;
        }
        if (this.handler != other.handler && (this.handler == null || !this.handler.equals(other.handler))) {
            return false;
        }
        if (this.connCollector != other.connCollector && (this.connCollector == null || !this.connCollector.equals(other.connCollector))) {
            return false;
        }
        if (this.connCache != other.connCache && (this.connCache == null || !this.connCache.equals(other.connCache))) {
            return false;
        }
        return true;
    }
    
    
}
