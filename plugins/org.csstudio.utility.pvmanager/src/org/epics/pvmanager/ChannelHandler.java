/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages the connection for each channel of a data source.
 *
 * @author carcassi
 */
public abstract class ChannelHandler {

    private static final Logger log = Logger.getLogger(ChannelHandler.class.getName());
    private final String channelName;
    
    /**
     * Creates a new channel handler.
     * 
     * @param channelName the name of the channel this handler will be responsible of
     */
    public ChannelHandler(String channelName) {
        if (channelName == null) {
            throw new NullPointerException("Channel name cannot be null");
        }
        this.channelName = channelName;
    }
    
    /**
     * Returns extra information about the channel, typically
     * useful for debugging.
     * 
     * @return a property map
     */
    public Map<String, Object> getProperties() {
        return Collections.emptyMap();
    }

    /**
     * Returns the name of the channel.
     * 
     * @return the channel name; can't be null
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Returns how many readers or writers are open on
     * this channel.
     * 
     * @return the number of open readers and writers
     */
    public abstract int getUsageCounter();
    
    /**
     * Returns how many readers are open on this channel.
     * 
     * @return the number of open readers
     */
    public abstract int getReadUsageCounter();
    
    /**
     * Returns how many writers are open on this channel.
     * 
     * @return the number of open writers
     */
    public abstract int getWriteUsageCounter();

    /**
     * Used by the data source to add a read request on the channel managed
     * by this handler.
     * 
     * @param subscription the data required for a subscription
     */
    protected abstract void addReader(ChannelHandlerReadSubscription subscription);

    /**
     * Used by the data source to remove a read request.
     * 
     * @param subscription the subscription to remove
     */
    protected abstract void removeReader(ChannelHandlerReadSubscription subscription);

    /**
     * Used by the data source to prepare the channel managed by this handler
     * for write.
     * 
     * @param subscription the data required for the subscription
     */
    protected abstract void addWriter(ChannelHandlerWriteSubscription subscription);

    /**
     * Used by the data source to conclude writes to the channel managed by this handler.
     * 
     * @param subscription the subscription to remove
     */
    protected abstract void removeWrite(ChannelHandlerWriteSubscription subscription);

    /**
     * Implements a write operation. Writes the newValues to the channel
     * and call the callback when done.
     * 
     * @param newValue new value to be written
     * @param callback called when done or on error
     */
    protected abstract void write(Object newValue, ChannelWriteCallback callback);

    /**
     * Returns true if it is connected.
     * 
     * @return true if underlying channel is connected
     */
    public abstract boolean isConnected();
}
