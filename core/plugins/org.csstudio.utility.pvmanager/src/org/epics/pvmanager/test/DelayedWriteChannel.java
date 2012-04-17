/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.test;

import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.ValueCache;

/**
 * Implementation for channels of a {@link TestDataSource}.
 *
 * @author carcassi
 */
class DelayedWriteChannel extends ChannelHandler<Object> {

    DelayedWriteChannel(String channelName) {
        super(channelName);
    }

    @Override
    public void connect(ExceptionHandler handler) {
        // Nothing to be done
    }

    @Override
    public void disconnect(ExceptionHandler handler) {
        // Nothing to be done
    }

    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        try {
            Thread.sleep(1000);
            processValue(newValue);
            callback.channelWritten(null);
        } catch (Exception ex) {
            callback.channelWritten(ex);
        }
    }

    @Override
    public boolean updateCache(Object event, ValueCache<?> cache) {
        Object oldValue = cache.getValue();
        cache.setValue(event);
        if ((event == oldValue) || (event != null && event.equals(oldValue)))
            return false;
        return true;
    }

    @Override
    public boolean isConnected() {
        return getUsageCounter() != 0;
    }
    
}
