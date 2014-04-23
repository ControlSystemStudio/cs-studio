/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * Groups all the parameters required to add a reader to a ChannelHandler.
 * <p>
 * All parameters where grouped in this class so that if something needs to be
 * added or removed the impact is lessened. The class is immutable so that
 * the ChannelHandler can cache it for reference.
 *
 * @author carcassi
 */
public class ChannelHandlerReadSubscription {

    /**
     * Creates the parameters for a new subscription.
     * 
     * @param valueCache the cache where to write the value
     * @param exceptionWriteFunction the write function to dispatch exceptions
     * @param connectionWriteFunction the connection write function to dispatch exceptions
     */
    public ChannelHandlerReadSubscription(ValueCache<?> valueCache, WriteFunction<Exception> exceptionWriteFunction, WriteFunction<Boolean> connectionWriteFunction) {
        this.valueCache = valueCache;
        this.exceptionWriteFunction = exceptionWriteFunction;
        this.connectionWriteFunction = connectionWriteFunction;
    }
    
    private final ValueCache<?> valueCache;
    private final WriteFunction<Exception> exceptionWriteFunction;
    private final WriteFunction<Boolean> connectionWriteFunction;

    /**
     * The cache where to write the value.
     * 
     * @return never null
     */
    public ValueCache<?> getValueCache() {
        return valueCache;
    }

    /**
     * The write function for exceptions.
     * 
     * @return never null
     */
    public WriteFunction<Exception> getExceptionWriteFunction() {
        return exceptionWriteFunction;
    }

    /**
     * The write function for the connection flag.
     * 
     * @return never null
     */
    public WriteFunction<Boolean> getConnectionWriteFunction() {
        return connectionWriteFunction;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.valueCache != null ? this.valueCache.hashCode() : 0);
        hash = 67 * hash + (this.exceptionWriteFunction != null ? this.exceptionWriteFunction.hashCode() : 0);
        hash = 67 * hash + (this.connectionWriteFunction != null ? this.connectionWriteFunction.hashCode() : 0);
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
        if (this.valueCache != other.valueCache && (this.valueCache == null || !this.valueCache.equals(other.valueCache))) {
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
