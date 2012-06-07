/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.loc;

import org.epics.pvmanager.*;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.AlarmStatus;
import org.epics.pvmanager.util.TimeStamp;
import static org.epics.pvmanager.data.ValueFactory.*;
import org.epics.util.time.Timestamp;

/**
 * Implementation for channels of a {@link LocalDataSource}.
 *
 * @author carcassi
 */
class LocalChannelHandler extends MultiplexedChannelHandler<Object, Object> {
    
    private final Object initialValue;

    LocalChannelHandler(String channelName) {
        super(channelName);
        initialValue = null;
    }

    LocalChannelHandler(String channelName, Object initialValue) {
        super(channelName);
        this.initialValue = wrapValue(initialValue);
    }

    @Override
    public void connect() {
        processConnection(new Object());
        if (initialValue != null)
            processMessage(initialValue);
    }

    @Override
    public void disconnect() {
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
    
    private Object wrapValue(Object value) {
        if (value instanceof Number) {
            // Special support for numbers
            return newVDouble(((Number) value).doubleValue(), alarmNone(), newTime(Timestamp.now()),
                    displayNone());
        } else if (value instanceof String) {
            // Special support for strings
            return newVString(((String) value),
                    AlarmSeverity.NONE, AlarmStatus.NONE, TimeStamp.now(), null);
        }
        return value;
    }

    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        try {
            newValue = wrapValue(newValue);
            processMessage(newValue);
            callback.channelWritten(null);
        } catch (Exception ex) {
            callback.channelWritten(ex);
        }
    }

    @Override
    public boolean isConnected() {
        return getUsageCounter() != 0;
    }
    
}
