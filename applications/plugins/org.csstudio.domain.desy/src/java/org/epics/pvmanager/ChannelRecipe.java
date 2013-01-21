/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 *
 * @author carcassi
 */
public class ChannelRecipe {
    private final String channelName;
    private final ChannelHandlerReadSubscription readSubscription;

    public ChannelRecipe(String channelName, ChannelHandlerReadSubscription readSubscription) {
        this.channelName = channelName;
        this.readSubscription = readSubscription;
    }
    
    public String getChannelName() {
        return channelName;
    }

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
        final ChannelRecipe other = (ChannelRecipe) obj;
        if (this.readSubscription != other.readSubscription && (this.readSubscription == null || !this.readSubscription.equals(other.readSubscription))) {
            return false;
        }
        return true;
    }
    
}
