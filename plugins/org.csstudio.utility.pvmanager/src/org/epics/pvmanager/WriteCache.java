/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represent part of the write recipe that holds the value for one pv.
 *
 * @param <T> the payload type to be held in the cache
 * @author carcassi
 */
public class WriteCache<T> implements WriteFunction<T> {

    /**
     * The value cached.
     * Will be guarded by the overall write recipe is cache is part of.
     */
    private T value;
    
    /**
     * The channel we should be writing to
     */
    private final String channelName;
    
    private List<String> precedingChannels = Collections.emptyList();

    /**
     * Creates a new cache.
     */
    public WriteCache() {
        this.value = null;
        this.channelName = null;
    }

    /**
     * Creates a new cache for the given channel name.
     * 
     * @param channelName the channel name
     */
    public WriteCache(String channelName) {
        this.value = null;
        this.channelName = channelName;
    }

    /**
     * Returns the value in the cache.
     *
     * @return value in the cache
     */
    public T getValue() {
        return value;
    }

    /**
     * Changes the value in the cache.
     *
     * @param newValue the new value
     */
    @Override
    public void writeValue(T newValue) {
        this.value = newValue;
    }

    /**
     * Changes which channels need to be written before the channel
     * for this cache can be written.
     * 
     * @param precedingChannels a list of channel names
     */
    public void setPrecedingChannels(List<String> precedingChannels) {
        this.precedingChannels = Collections.unmodifiableList(new ArrayList<String>(precedingChannels));
    }

    /**
     * Returns all the channels in the same recipe that should be written
     * before writing the channel for this cache.
     * 
     * @return a list of channel names
     */
    public Collection<String> getPrecedingChannels() {
        return precedingChannels;
    }

    /**
     * The channel associated to this cache
     * 
     * @return the channel name
     */
    public String getChannelName() {
        // TODO: to remove. The name should not be taken here because
        // the datasource may redirect it
        return channelName;
    }
    

}
