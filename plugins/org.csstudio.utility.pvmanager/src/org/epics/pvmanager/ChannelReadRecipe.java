/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * The recipe for the read connection to a single channel.
 * <p>
 * The recipe is made up of two parts to make it easy to forward
 * the request to a channel with a different name.
 *
 * @author carcassi
 */
public class ChannelReadRecipe {
    private final String channelName;
    private final ChannelHandlerReadSubscription readSubscription;

    /**
     * Creates a new read recipe for the given channel.
     *
     * @param channelName the name of the channel to connect to
     * @param readSubscription the subscription parameters for the read
     */
    public ChannelReadRecipe(String channelName, ChannelHandlerReadSubscription readSubscription) {
        this.channelName = channelName;
        this.readSubscription = readSubscription;
    }
    
    /**
     * The name of the channel to read.
     *
     * @return the channel name
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * The read subscription parameters.
     *
     * @return the read subscription parameters
     */
    public ChannelHandlerReadSubscription getReadSubscription() {
        return readSubscription;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.channelName != null ? this.channelName.hashCode() : 0);
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
        final ChannelReadRecipe other = (ChannelReadRecipe) obj;
        if (this.readSubscription != other.readSubscription && (this.readSubscription == null || !this.readSubscription.equals(other.readSubscription))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[ChannelReadRecipe for " + channelName + ": " + readSubscription + "]";
    }
    
}
