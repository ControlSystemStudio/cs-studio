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
    private JCADataSource jcaDataSource;
    private Channel channel;

    public JCAConnectionPayload(JCADataSource jcaDataSource, Channel channel) {
        this.jcaDataSource = jcaDataSource;
        this.channel = channel;
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
        return channel != null && channel.getConnectionState() == Channel.ConnectionState.CONNECTED;
    }
    
}
