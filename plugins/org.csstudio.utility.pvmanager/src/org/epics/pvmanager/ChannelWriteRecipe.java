/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * The recipe for the write connection to a single channel.
 * <p>
 * The recipe is made up of two parts to make it easy to forward
 * the request to a channel with a different name.
 *
 * @author carcassi
 */
public class ChannelWriteRecipe {
    private final String channelName;
    private final ChannelHandlerWriteSubscription writeSubscription;

    /**
     * Creates a new write recipe for the given channel.
     * 
     * @param channelName the name of the channel to connect to
     * @param writeSubscription the subscription parameters for the write
     */
    public ChannelWriteRecipe(String channelName, ChannelHandlerWriteSubscription writeSubscription) {
        this.channelName = channelName;
        this.writeSubscription = writeSubscription;
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
     * The write subscription parameters.
     *
     * @return the write subscription parameters
     */
    public ChannelHandlerWriteSubscription getWriteSubscription() {
        return writeSubscription;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.channelName != null ? this.channelName.hashCode() : 0);
        hash = 71 * hash + (this.writeSubscription != null ? this.writeSubscription.hashCode() : 0);
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
        final ChannelWriteRecipe other = (ChannelWriteRecipe) obj;
        if ((this.channelName == null) ? (other.channelName != null) : !this.channelName.equals(other.channelName)) {
            return false;
        }
        if (this.writeSubscription != other.writeSubscription && (this.writeSubscription == null || !this.writeSubscription.equals(other.writeSubscription))) {
            return false;
        }
        return true;
    }
    
}
