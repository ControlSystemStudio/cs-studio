/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.Channel;

/**
 * Represents the connection payload, which consists of the actual JCA
 * Channel and the JCADataSource (which can be used to extract
 * configuration parameters).
 *
 * @author carcassi
 */
public class JCAConnectionPayload {
    private final JCADataSource jcaDataSource;
    private final Channel channel;
    private final boolean connected;
    private final boolean longString;

    public JCAConnectionPayload(JCAChannelHandler channleHandler, Channel channel) {
        this.jcaDataSource = channleHandler.getJcaDataSource();
        this.channel = channel;
        this.connected = channel != null && channel.getConnectionState() == Channel.ConnectionState.CONNECTED;
        this.longString = channleHandler.isLongString();
    }

    /**
     * The JCADataSource that is using the channel.
     * 
     * @return the JCA data source
     */
    public JCADataSource getJcaDataSource() {
        return jcaDataSource;
    }

    /**
     * The JCA channel.
     * 
     * @return JCA channel
     */
    public Channel getChannel() {
        return channel;
    }
    
    /**
     * True if the channel is not null and the connection state is connected.
     * 
     * @return ture if channel exists and is connected
     */
    public boolean isChannelConnected() {
        return connected;
    }
    
    /**
     * True if the channel is not null, connected, and can be written to.
     * 
     * @return true if the channel is ready for write
     */
    public boolean isWriteConnected() {
        return isChannelConnected() && channel.getWriteAccess();
    }

    /**
     * Whether the message payload should be handled as a long string.
     * 
     * @return true if long string support should be used
     */
    public boolean isLongString() {
        return longString;
    }
    
}
