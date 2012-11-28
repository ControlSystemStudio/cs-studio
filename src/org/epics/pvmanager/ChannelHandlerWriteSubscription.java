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

    public ChannelHandlerWriteSubscription(WriteCache<?> cache, WriteFunction<Exception> exceptionWriteFunction, WriteFunction<Boolean> connectionWriteFunction) {
        this.cache = cache;
        this.exceptionWriteFunction = exceptionWriteFunction;
        this.connectionWriteFunction = connectionWriteFunction;
    }
    
    private final WriteCache<?> cache;
    private final WriteFunction<Exception> exceptionWriteFunction;
    private final WriteFunction<Boolean> connectionWriteFunction;

    /**
     * The cache to hold the value to write.
     * 
     * @return the write cache
     */
    public WriteCache<?> getWriteCache() {
        return cache;
    }

    /**
     * The write function for connection/disconnection errors.
     * 
     * @return the write function; never null
     */
    public WriteFunction<Exception> getExceptionWriteFunction() {
        return exceptionWriteFunction;
    }

    /**
     * The write function for the connection flag.
     * 
     * @return the write function; never null
     */
    public WriteFunction<Boolean> getConnectionWriteFunction() {
        return connectionWriteFunction;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.cache != null ? this.cache.hashCode() : 0);
        hash = 11 * hash + (this.exceptionWriteFunction != null ? this.exceptionWriteFunction.hashCode() : 0);
        hash = 11 * hash + (this.connectionWriteFunction != null ? this.connectionWriteFunction.hashCode() : 0);
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
        if (this.exceptionWriteFunction != other.exceptionWriteFunction && (this.exceptionWriteFunction == null || !this.exceptionWriteFunction.equals(other.exceptionWriteFunction))) {
            return false;
        }
        if (this.connectionWriteFunction != other.connectionWriteFunction && (this.connectionWriteFunction == null || !this.connectionWriteFunction.equals(other.connectionWriteFunction))) {
            return false;
        }
        return true;
    }
    
}
