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
class DelayedWriteChannel extends MultiplexedChannelHandler<Object, Object> {

    DelayedWriteChannel(String channelName) {
        super(channelName);
    }

    @Override
    public void connect() {
        processConnection(new Object());
    }

    @Override
    public void disconnect() {
        processConnection(null);
    }

    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        try {
            Thread.sleep(1000);
            processMessage(newValue);
            callback.channelWritten(null);
        } catch (Exception ex) {
            callback.channelWritten(ex);
        }
    }
    
}
