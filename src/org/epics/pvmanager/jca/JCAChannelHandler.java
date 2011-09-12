/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
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
 * A ChannelHandler for the JCADataSource.
 * <p>
 * NOTE: this class is extensible as per Bastian request so that DESY can hook
 * a different type factory. This is a temporary measure until the problem
 * is solved in better, more general way, so that data sources
 * can work only with data source specific types, while allowing
 * conversions to normalized type through operators. The contract of this
 * class is, therefore, expected to change.
 * <p>
 * Related changes are marked so that they are not accidentally removed in the
 * meantime, and can be intentionally removed when a better solution is implemented.
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

    // protected (not private) to allow different type factory
    protected synchronized void setup(Channel channel) throws CAException {
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
    
    // protected (not private) to allow different type factory
    protected Class<?> cacheType;
    // protected (not private) to allow different type factory
    protected volatile TypeFactory vTypeFactory;
    private ConnectionListener connectionListener;
    // protected (not private) to allow different type factory
    protected volatile Monitor monitor;
    // protected (not private) to allow different type factory
    protected volatile DBR metadata;
    private volatile MonitorEvent event;
    // protected (not private) to allow different type factory
    protected final MonitorListener monitorListener = new MonitorListener() {

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
                    // Setup monitors on connection
                    if (ev.isConnected()) {
                        setup(channel);
                        if (event != null)
                            processValue(event);
                    } else {
                        if (event != null)
                            processValue(event);
                    }
                } catch (Exception ex) {
                    handler.handleException(ex);
                }
            }
        };
    }

    @Override
    public void disconnect(ExceptionHandler handler) {
        // Close the monitor
        try {
            monitor.removeMonitorListener(monitorListener);
            monitor.clear();
        } catch (Exception ex) {
            handler.handleException(ex);
        }
        
        // Remove connection listener
        try {
            channel.removeConnectionListener(connectionListener);
        } catch (CAException ex) {
            handler.handleException(ex);
        }
        
        // Close the channel
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
                    if (ev.getStatus().isSuccessful()) {
                        callback.channelWritten(null);
                    } else {
                        callback.channelWritten(new Exception(ev.toString()));
                    }
                }
            };
            if (newValue instanceof String) {
                channel.put(newValue.toString(), listener);
            } else if (newValue instanceof byte[]) {
                channel.put((byte[]) newValue, listener);
            } else if (newValue instanceof short[]) {
                channel.put((short[]) newValue, listener);
            } else if (newValue instanceof int[]) {
                channel.put((int[]) newValue, listener);
            } else if (newValue instanceof float[]) {
                channel.put((float[]) newValue, listener);
            } else if (newValue instanceof double[]) {
                channel.put((double[]) newValue, listener);
            } else if (newValue instanceof Byte || newValue instanceof Short
                    || newValue instanceof Integer || newValue instanceof Long) {
                channel.put(((Number) newValue).longValue(), listener);
            } else if (newValue instanceof Float || newValue instanceof Double) {
                channel.put(((Number) newValue).doubleValue(), listener);
            } else {
                throw new RuntimeException("Unsupported type for CA: " + newValue.getClass());
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
        Object newValue = cacheType.cast(vTypeFactory.createValue(rawvalue, metadata, !isConnected()));
        cache.setValue(newValue);
        return true;
    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.getConnectionState() == Channel.ConnectionState.CONNECTED;
    }
}
