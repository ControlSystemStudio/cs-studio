/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.test;

import org.epics.pvmanager.*;

/**
 * Implementation for channels of a {@link TestDataSource}.
 *
 * @author carcassi
 */
class DelayedConnectionChannel extends MultiplexedChannelHandler<Object, Object> {

    DelayedConnectionChannel(String channelName) {
        super(channelName);
    }

    @Override
    public void connect() {
        try {
            Thread.sleep(1000);
        } catch(Exception ex) {
        }
        
        processConnection(new Object());
        processMessage("Initial value");
    }

    @Override
    public void disconnect() {
        processConnection(null);
    }

    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        try {
            processMessage(newValue);
            callback.channelWritten(null);
        } catch (Exception ex) {
            callback.channelWritten(ex);
        }
    }
    
}
