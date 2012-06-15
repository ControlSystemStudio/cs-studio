/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.*;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;
import gov.aps.jca.event.PutEvent;
import gov.aps.jca.event.PutListener;
import org.epics.pvmanager.*;

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
public class JCAChannelHandler extends MultiplexedChannelHandler<Channel, JCAMessagePayload> {

    private static final int LARGE_ARRAY = 100000;
    private final JCADataSource jcaDataSource;
    private volatile Channel channel;
    private volatile boolean needsMonitor;
    private volatile boolean largeArray = false;

    public JCAChannelHandler(String channelName, JCADataSource jcaDataSource) {
        super(channelName);
        this.jcaDataSource = jcaDataSource;
    }
 
    @Override
    protected JCATypeAdapter findTypeAdapter(ValueCache<?> cache, Channel channel) {
        return jcaDataSource.getTypeSupport().find(cache, channel);
    }

    @Override
    public void connect() {
        try {
            // Give the listener right away so that no event gets lost
	    // If it's a large array, connect using lower priority
	    if (largeArray) {
                channel = jcaDataSource.getContext().createChannel(getChannelName(), connectionListener, Channel.PRIORITY_MIN);
	    } else {
                channel = jcaDataSource.getContext().createChannel(getChannelName(), connectionListener, (short) (Channel.PRIORITY_MIN + 1));
	    }
            needsMonitor = true;
        } catch (CAException ex) {
            throw new RuntimeException("JCA Connection failed", ex);
        }
    }

    private void setup(Channel channel) throws CAException {
        processConnection(channel);
        
        DBRType metaType = metadataFor(channel);

        // If metadata is needed, get it
        if (metaType != null) {
            // Need to use callback for the listener instead of doing a synchronous get
            // (which seemed to perform better) because JCA (JNI implementation)
            // would return an empty list of labels for the Enum metadata
            channel.get(metaType, 1, new GetListener() {

                @Override
                public void getCompleted(GetEvent ev) {
                    synchronized(JCAChannelHandler.this) {
                        // In case the metadata arrives after the monitor
                        MonitorEvent event = null;
                        if (getLastMessagePayload() != null) {
                            event = getLastMessagePayload().getEvent();
                        }
                        processMessage(new JCAMessagePayload(ev.getDBR(), event));
                    }
                }
            });
        }

        // Start the monitor only if the channel was (re)created, and
        // not because a disconnection/reconnection
        if (needsMonitor) {
            channel.addMonitor(valueTypeFor(channel), channel.getElementCount(), jcaDataSource.getMonitorMask(), monitorListener);
            needsMonitor = false;
        }

        // Flush the entire context (it's the best we can do)
        channel.getContext().flushIO();
    }
    
    private final ConnectionListener connectionListener = new ConnectionListener() {

            @Override
            public void connectionChanged(ConnectionEvent ev) {
                synchronized(JCAChannelHandler.this) {
                    try {
                        // Take the channel from the event so that there is no
                        // synchronization problem
                        Channel channel = (Channel) ev.getSource();

                        // Check whether the channel is large and was opened
                        // as large. Reconnect if does not match
                        if (ev.isConnected() && channel.getElementCount() >= LARGE_ARRAY && !largeArray) {
                            disconnect();
                            largeArray = true;
                            connect();
                            return;
                        }

                        // Setup monitors on connection
                        if (ev.isConnected()) {
                            setup(channel);
                            processMessage(getLastMessagePayload());
                        } else {
                            processMessage(getLastMessagePayload());
                        }
                    } catch (Exception ex) {
                        reportExceptionToAllReadersAndWriters(ex);
                    }
                }
            }
        };;
    
    private final MonitorListener monitorListener = new MonitorListener() {

        @Override
        public void monitorChanged(MonitorEvent event) {
            synchronized(JCAChannelHandler.this) {
                DBR metadata = null;
                if (getLastMessagePayload() != null) {
                    metadata = getLastMessagePayload().getMetadata();
                }
                processMessage(new JCAMessagePayload(metadata, event));
            }
        }
    };

    @Override
    public void disconnect() {
        try {
            // Close the channel
            channel.destroy();
        } catch (CAException ex) {
            throw new RuntimeException("JCA Disconnect fail", ex);
        } finally {
            channel = null;
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
            jcaDataSource.getContext().flushIO();
        } catch (CAException ex) {
            callback.channelWritten(ex);
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected(channel);
    }
    
    static boolean isConnected(Channel channel) {
        return channel != null && channel.getConnectionState() == Channel.ConnectionState.CONNECTED;
    }

    protected DBRType metadataFor(Channel channel) {
        DBRType type = channel.getFieldType();
        
        if (type.isBYTE() || type.isSHORT() || type.isINT() || type.isFLOAT() || type.isDOUBLE())
            return DBR_CTRL_Double.TYPE;
        
        if (type.isENUM())
            return DBR_LABELS_Enum.TYPE;
        
        return null;
    }

    protected DBRType valueTypeFor(Channel channel) {
        DBRType type = channel.getFieldType();
        
        // For scalar numbers, only use Double or Int
        if (channel.getElementCount() == 1) {
            if (type.isBYTE() || type.isSHORT() || type.isINT())
                return DBR_TIME_Int.TYPE;
            if (type.isFLOAT() || type.isDOUBLE())
                return DBR_TIME_Double.TYPE;
        }
        
        if (type.isBYTE()) {
            return DBR_TIME_Byte.TYPE;
        } else if (type.isSHORT()) {
            return DBR_TIME_Short.TYPE;
        } else if (type.isINT()) {
            return DBR_TIME_Int.TYPE;
        } else if (type.isFLOAT()) {
            return DBR_TIME_Float.TYPE;
        } else if (type.isDOUBLE()) {
            return DBR_TIME_Double.TYPE;
        } else if (type.isENUM()) {
            return DBR_TIME_Enum.TYPE;
        } else if (type.isSTRING()) {
            return DBR_TIME_String.TYPE;
        }
        
        throw new IllegalArgumentException("Unsupported type " + type);
    }
}
