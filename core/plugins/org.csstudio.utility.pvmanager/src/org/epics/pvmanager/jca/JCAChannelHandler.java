/*
 * Copyright 2010-11 Brookhaven National Laboratory
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
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;
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
    private volatile Channel channel;
    private volatile ExceptionHandler connectionExceptionHandler;
    private volatile boolean needsMonitor;

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
    
    /**
     * Return the TypeFactory to use to get data from the given channel and 
     * convert it to the given type
     * 
     * @param cacheType final type for the value update
     * @param channel channel access channel from which to get the data
     * @return the TypeFactory to use
     */
    protected TypeFactory matchFactoryFor(Class<?> cacheType, Channel channel) {
        return VTypeFactory.matchFor(cacheType, channel.getFieldType(), channel.getElementCount());
    }

    @Override
    public void connect(ExceptionHandler handler) {
        connectionExceptionHandler = handler;
        try {
            // Give the listener right away so that no event gets lost
            channel = context.createChannel(getChannelName(), connectionListener);
            needsMonitor = true;
        } catch (CAException ex) {
            handler.handleException(ex);
        }
    }

    // protected (not private) to allow different type factory
    protected void setup(Channel channel) throws CAException {
        vTypeFactory = matchFactoryFor(cacheType, channel);

        // If metadata is needed, get it
        if (vTypeFactory.getEpicsMetaType() != null) {
            // Need to use callback for the listener instead of doing a synchronous get
            // (which seemed to perform better) because JCA (JNI implementation)
            // would return an empty list of labels for the Enum metadata
            channel.get(vTypeFactory.getEpicsMetaType(), 1, new GetListener() {

                @Override
                public void getCompleted(GetEvent ev) {
                    synchronized(JCAChannelHandler.this) {
                        metadata = ev.getDBR();
                        // In case the metadata arrives after the monitor
                        dispatchValue();
                    }
                }
            });
        }

        // Start the monitor only if the channel was (re)created, and
        // not because a disconnection/reconnection
        if (needsMonitor) {
            if (vTypeFactory.isArray()) {
                channel.addMonitor(vTypeFactory.getEpicsValueType(), channel.getElementCount(), monitorMask, monitorListener);
            } else {
                channel.addMonitor(vTypeFactory.getEpicsValueType(), 1, monitorMask, monitorListener);
            }
            needsMonitor = false;
        }

        // Flush the entire context (it's the best we can do)
        channel.getContext().flushIO();
    }
    
    // protected (not private) to allow different type factory
    protected volatile Class<?> cacheType;
    // protected (not private) to allow different type factory
    protected volatile TypeFactory vTypeFactory;
    
    private final ConnectionListener connectionListener = new ConnectionListener() {

            @Override
            public void connectionChanged(ConnectionEvent ev) {
                try {
                    // Take the channel from the event so that there is no
                    // synchronization problem
                    Channel channel = (Channel) ev.getSource();
                    
                    // Setup monitors on connection
                    if (ev.isConnected()) {
                        setup(channel);
                        dispatchValue();
                    } else {
                        dispatchValue();
                    }
                } catch (Exception ex) {
                    connectionExceptionHandler.handleException(ex);
                }
            }
        };;
    
    // The change in metadata and even has to be atomic
    // so they are both guarded by this
    // protected (not private) to allow different type factory
    protected DBR metadata;
    private MonitorEvent event;
    
    // protected (not private) to allow different type factory
    protected final MonitorListener monitorListener = new MonitorListener() {

        @Override
        public void monitorChanged(MonitorEvent event) {
            synchronized(JCAChannelHandler.this) {
                JCAChannelHandler.this.event = event;
                dispatchValue();
            }
        }
    };
    
    private synchronized void dispatchValue() {
        // Only process the value if we have both event and metadata
        if (event != null && (vTypeFactory.getEpicsMetaType() == null || metadata != null)) {
            processValue(event);
        }
    }

    @Override
    public void disconnect(ExceptionHandler handler) {
        try {
            // Close the channel
            channel.destroy();
        } catch (CAException ex) {
            handler.handleException(ex);
        } finally {
            channel = null;
            synchronized(this) {
                metadata = null;
                event = null;
            }
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
