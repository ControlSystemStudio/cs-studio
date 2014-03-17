/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * Groups all the parameters required to add a writer to a ChannelHandler.
 * <p>
 * All parameters where grouped in this class so that if something needs to be
 * added or removed the impact is lessened. The class is immutable so that
 * the ChannelHandler can cache it for reference.
 *
 * @author carcassi
 */
public class ChannelHandlerWriteSubscription {

    /**
     * Creates a new subscription.
     * 
     * @param writeCache the cache where to read the value from
     * @param exceptionWriteFunction the write function to notify to process errors
     * @param connectionWriteFunction the write function to notify for connection updates
     */
    public ChannelHandlerWriteSubscription(WriteCache<?> writeCache, WriteFunction<Exception> exceptionWriteFunction, WriteFunction<Boolean> connectionWriteFunction) {
        this.writeCache = writeCache;
        this.exceptionWriteFunction = exceptionWriteFunction;
        this.connectionWriteFunction = connectionWriteFunction;
    }
    
    private final WriteCache<?> writeCache;
    private final WriteFunction<Exception> exceptionWriteFunction;
    private final WriteFunction<Boolean> connectionWriteFunction;

    /**
     * The cache to hold the value to write.
     * 
     * @return the write cache
     */
    public WriteCache<?> getWriteCache() {
        return writeCache;
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
        hash = 11 * hash + (this.writeCache != null ? this.writeCache.hashCode() : 0);
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
        if (this.writeCache != other.writeCache && (this.writeCache == null || !this.writeCache.equals(other.writeCache))) {
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
