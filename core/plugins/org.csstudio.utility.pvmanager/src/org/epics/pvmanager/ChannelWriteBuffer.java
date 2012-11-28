/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 *
 * @author carcassi
 */
public class ChannelWriteBuffer {
    private final String channelName;
    private final ChannelHandlerWriteSubscription writeSubscription;

    public ChannelWriteBuffer(String channelName, ChannelHandlerWriteSubscription writeSubscription) {
        this.channelName = channelName;
        this.writeSubscription = writeSubscription;
    }

    public String getChannelName() {
        return channelName;
    }

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
        final ChannelWriteBuffer other = (ChannelWriteBuffer) obj;
        if ((this.channelName == null) ? (other.channelName != null) : !this.channelName.equals(other.channelName)) {
            return false;
        }
        if (this.writeSubscription != other.writeSubscription && (this.writeSubscription == null || !this.writeSubscription.equals(other.writeSubscription))) {
            return false;
        }
        return true;
    }
    
}
