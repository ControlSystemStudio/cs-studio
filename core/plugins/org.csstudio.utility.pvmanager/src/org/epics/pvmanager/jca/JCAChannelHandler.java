/*
 * Copyright 2008-2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;
import gov.aps.jca.event.PutEvent;
import gov.aps.jca.event.PutListener;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.ValueCache;

/**
 *
 * @author carcassi
 */
public class JCAChannelHandler extends ChannelHandler<MonitorEvent> {

    private final Context context;
    private final int monitorMask;
    private Channel channel;

    public JCAChannelHandler(String channelName, Context context, int monitorMask) {
        super(channelName);
        this.context = context;
        this.monitorMask = monitorMask;
    }

    @Override
    public synchronized void addMonitor(Collector<?> collector, ValueCache<?> cache, ExceptionHandler handler) {
        if (cacheType == null) {
            cacheType = cache.getType();
        }
        super.addMonitor(collector, cache, handler);
    }

    @Override
    public void connect(ExceptionHandler handler) {
        try {
            channel = context.createChannel(getChannelName());

            connectionListener = createConnectionListener(channel, handler);

            // Need to wait for the connection to be established
            // before reading the metadata
            channel.addConnectionListener(connectionListener);

            // If the channel was already connected, then the monitor may
            // be never called. Set it up.
            if (channel.getConnectionState() == Channel.CONNECTED) {
                setup(channel);
            }
        } catch (CAException ex) {
            handler.handleException(ex);
        }
    }

    synchronized void setup(Channel channel) throws CAException {
        // This method may be called twice, if the connection happens
        // after the ConnectionListener is setup but before
        // the connection state is polled.

        // The synchronization makes sure that, if that happens, the
        // two calls are serial. Checking the monitor for null to
        // make sure the second call does not create another monitor.
        if (monitor == null) {
            vTypeFactory = VTypeFactory.matchFor(cacheType, channel.getFieldType(), channel.getElementCount());
            if (vTypeFactory.getEpicsMetaType() != null) {
                metadata = channel.get(vTypeFactory.getEpicsMetaType(), 1);
            }
            if (vTypeFactory.isArray()) {
                monitor = channel.addMonitor(vTypeFactory.getEpicsValueType(), channel.getElementCount(), monitorMask, monitorListener);
            } else {
                monitor = channel.addMonitor(vTypeFactory.getEpicsValueType(), 1, monitorMask, monitorListener);
            }
            channel.getContext().flushIO();
        }
    }
    
    private Class<?> cacheType;
    private volatile VTypeFactory vTypeFactory;
    private ConnectionListener connectionListener;
    private volatile Monitor monitor;
    private volatile DBR metadata;
    private volatile MonitorEvent event;
    private final MonitorListener monitorListener = new MonitorListener() {

        @Override
        public void monitorChanged(MonitorEvent event) {
            JCAChannelHandler.this.event = event;
            processValue(event);
        }
    };

    private ConnectionListener createConnectionListener(final Channel channel,
            final ExceptionHandler handler) {
        return new ConnectionListener() {

            @Override
            public void connectionChanged(ConnectionEvent ev) {
                try {
                    // Setup monitors on connection and tear them
                    // down on disconnection
                    if (ev.isConnected()) {
                        setup(channel);
                    } else {
                        close(handler);
                        processValue(event);
                    }
                } catch (Exception ex) {
                    handler.handleException(ex);
                }
            }
        };
    }

    public void close(ExceptionHandler handler) {
        if (monitor != null) {
            Monitor toClear = monitor;
            monitor = null;
            try {
                channel.removeConnectionListener(connectionListener);
                toClear.removeMonitorListener(monitorListener);
                toClear.clear();
            } catch (Exception ex) {
                handler.handleException(ex);
            }
        }
    }

    @Override
    public void disconnect(ExceptionHandler handler) {
        close(handler);
        try {
            channel.destroy();
            channel = null;
        } catch (CAException ex) {
            handler.handleException(ex);
        }
    }

    @Override
    public void write(Object newValue, final ChannelWriteCallback callback) {
        try {
            PutListener listener = new PutListener() {

                @Override
                public void putCompleted(PutEvent ev) {
                    callback.channelWritten(null);
                }
            };
            if (newValue instanceof String) {
                channel.put(Double.parseDouble(newValue.toString()), listener);
            } else if (newValue instanceof Byte || newValue instanceof Short
                    || newValue instanceof Integer || newValue instanceof Long) {
                channel.put(((Number) newValue).longValue(), listener);
            } else if (newValue instanceof Float || newValue instanceof Double) {
                channel.put(((Number) newValue).doubleValue(), listener);
            }
            context.flushIO();
        } catch (CAException ex) {
            callback.channelWritten(ex);
        }
    }

    @Override
    public boolean updateCache(MonitorEvent event, ValueCache<?> cache) {
        DBR rawvalue = event.getDBR();
        @SuppressWarnings("unchecked")
        Object newValue = cacheType.cast(vTypeFactory.createValue(rawvalue, metadata, monitor == null));
        cache.setValue(newValue);
        return true;
    }

    @Override
    public boolean isConnected() {
        return monitor != null;
    }
}
