/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.loc;

import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.AlarmStatus;
import org.epics.pvmanager.data.ValueFactory;
import org.epics.pvmanager.util.TimeStamp;

/**
 * Implementation for channels of a {@link LocalDataSource}.
 *
 * @author carcassi
 */
class LocalChannelHandler extends ChannelHandler<Object> {

    LocalChannelHandler(String channelName) {
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
    protected synchronized void addMonitor(Collector<?> collector, ValueCache<?> cache, ExceptionHandler handler) {
        // Override for test visibility purposes
        super.addMonitor(collector, cache, handler);
    }

    @Override
    protected synchronized void addWriter(ExceptionHandler handler) {
        // Override for test visibility purposes
        super.addWriter(handler);
    }

    @Override
    protected synchronized void removeMonitor(Collector<?> collector) {
        // Override for test visibility purposes
        super.removeMonitor(collector);
    }

    @Override
    protected synchronized void removeWrite(ExceptionHandler exceptionHandler) {
        // Override for test visibility purposes
        super.removeWrite(exceptionHandler);
    }

    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        try {
            if (newValue instanceof Number) {
                // Special support for numbers
                newValue = ValueFactory.newVDouble(((Number) newValue).doubleValue(),
                        AlarmSeverity.NONE, AlarmStatus.NONE, TimeStamp.now(), null, null, null,
                        null, null, null, null, null, null, null, null);
            } else if (newValue instanceof String) {
                // Special support for strings
                newValue = ValueFactory.newVString(((String) newValue),
                        AlarmSeverity.NONE, AlarmStatus.NONE, TimeStamp.now(), null);
            }
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
