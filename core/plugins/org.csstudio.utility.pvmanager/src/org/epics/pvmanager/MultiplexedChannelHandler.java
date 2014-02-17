/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a {@link ChannelHandler} on top of a single subscription and
 * multiplexes all reads on top of it.
 * <p>
 * This abstract handler takes care of forwarding the connection and message
 * events of a single connection to multiple readers and writers. One needs
 * to:
 * <ul>
 * <li>implement the {@link #connect() } and {@link #disconnect() } function
 * to add the protocol specific connection and disconnection logic; the resources
 * shared across multiple channels should be left in the datasource</li>
 * <li>every time the connection state changes, call {@link #processConnection(java.lang.Object) },
 * which will trigger the proper connection notification mechanism;
 * the type chosen as connection payload should be one that stores all the
 * information about the channel of communications</li>
 * <li>every time an event is sent, call {@link #processMessage(java.lang.Object) }, which
 * will trigger the proper value notification mechanism</li>
 * <li>implement {@link #isConnected(java.lang.Object) } and {@link #isWriteConnected(java.lang.Object) }
 * with the logic to extract the connection information from the connection payload</li>
 * <li>use {@link #reportExceptionToAllReadersAndWriters(java.lang.Exception) }
 * to report errors</li>
 * <li>implement a set of {@link DataSourceTypeAdapter} that can convert
 * the payload to types for pvmanager consumption; the connection payload and
 * message payload never leave this handler, only value types created by the
 * type adapters</li>
 * </ul>
 *
 * @param <ConnectionPayload> type of the payload for the connection
 * @param <MessagePayload> type of the payload for each message
 * @author carcassi
 */
public abstract class MultiplexedChannelHandler<ConnectionPayload, MessagePayload> extends ChannelHandler {
    
    private static final Logger log = Logger.getLogger(MultiplexedChannelHandler.class.getName());
    private int readUsageCounter = 0;
    private int writeUsageCounter = 0;
    private boolean connected = false;
    private boolean writeConnected = false;
    private MessagePayload lastMessage;
    private ConnectionPayload connectionPayload;
    private Map<ChannelHandlerReadSubscription, MonitorHandler> monitors = new ConcurrentHashMap<>();
    private Map<WriteCache<?>, ChannelHandlerWriteSubscription> writeSubscriptions = new ConcurrentHashMap<>();
    private boolean processMessageOnDisconnect = true;
    private boolean processMessageOnReconnect = true;
    
    private class MonitorHandler {

        private final ChannelHandlerReadSubscription subscription;
        private DataSourceTypeAdapter<ConnectionPayload, MessagePayload> typeAdapter;

        public MonitorHandler(ChannelHandlerReadSubscription subscription) {
            this.subscription = subscription;
        }
        
        public final void processConnection(boolean connection) {
            subscription.getConnectionWriteFunction().writeValue(connection);
        }

        public final void processValue(MessagePayload payload) {
            if (typeAdapter == null)
                return;
            
            // Lock the collector and prepare the new value.
            try {
                typeAdapter.updateCache(subscription.getValueCache(), getConnectionPayload(), payload);
            } catch (RuntimeException e) {
                subscription.getExceptionWriteFunction().writeValue(e);
            }
        }
        
        public final void findTypeAdapter() {
            if (getConnectionPayload() == null) {
                typeAdapter = null;
            } else {
                try {
                    typeAdapter = MultiplexedChannelHandler.this.findTypeAdapter(subscription.getValueCache(), getConnectionPayload());
                } catch(RuntimeException ex) {
                    subscription.getExceptionWriteFunction().writeValue(ex);
                }
            }
        }
        
    }
    
    /**
     * Notifies all readers and writers of an error condition.
     * 
     * @param ex the exception to notify
     */
    protected synchronized final void reportExceptionToAllReadersAndWriters(Exception ex) {
        for (MonitorHandler monitor : monitors.values()) {
            monitor.subscription.getExceptionWriteFunction().writeValue(ex);
        }
        for (ChannelHandlerWriteSubscription subscription : writeSubscriptions.values()) {
            subscription.getExceptionWriteFunction().writeValue(ex);
        }
    }
    
    /**
     * Notifies all writers of an error condition.
     * 
     * @param ex the exception to notify
     */
    protected synchronized final void reportExceptionToAllWriters(Exception ex) {
        for (ChannelHandlerWriteSubscription subscription : writeSubscriptions.values()) {
            subscription.getExceptionWriteFunction().writeValue(ex);
        }
    }
    
    private void reportConnectionStatus(boolean connected) {
        for (MonitorHandler monitor : monitors.values()) {
            monitor.processConnection(connected);
        }
    }
    
    private void reportWriteConnectionStatus(boolean writeConnected) {
        for (ChannelHandlerWriteSubscription subscription : writeSubscriptions.values()) {
            subscription.getConnectionWriteFunction().writeValue(writeConnected);
        }
    }

    /**
     * The last processes connection payload.
     * 
     * @return the connection payload or null
     */
    protected synchronized final ConnectionPayload getConnectionPayload() {
        return connectionPayload;
    }

    /**
     * The last processed message payload.
     * 
     * @return the message payload or null
     */
    protected synchronized final MessagePayload getLastMessagePayload() {
        return lastMessage;
    }

    /**
     * Process the next connection payload. This should be called whenever
     * the connection state has changed.
     * 
     * @param connectionPayload 
     */
    protected synchronized final void processConnection(ConnectionPayload connectionPayload) {
        if (log.isLoggable(Level.FINEST)) {
            log.log(Level.FINEST, "processConnection for channel {0} connecionPayload {1}", new Object[] {getChannelName(), connectionPayload});
        }
        
        this.connectionPayload = connectionPayload;
        setConnected(isConnected(connectionPayload));
        setWriteConnected(isWriteConnected(connectionPayload));
        
        for (MonitorHandler monitor : monitors.values()) {
            monitor.findTypeAdapter();
        }
        
        if (isConnected() && lastMessage != null && processMessageOnReconnect) {
            processMessage(lastMessage);
        }
        if (!isConnected() && lastMessage != null && processMessageOnDisconnect) {
            processMessage(lastMessage);
        }
    }
    
    private static DataSourceTypeAdapter<?, ?> defaultTypeAdapter = new DataSourceTypeAdapter<Object, Object>() {

            @Override
            public int match(ValueCache<?> cache, Object connection) {
                return 1;
            }

            @Override
            public Object getSubscriptionParameter(ValueCache<?> cache, Object connection) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean updateCache(ValueCache cache, Object connection, Object message) {
                Object oldValue = cache.readValue();
                cache.writeValue(message);
                if ((message == oldValue) || (message != null && message.equals(oldValue)))
                    return false;
                return true;
            }
        };
    
    /**
     * Finds the right adapter to use for the particular cache given the information
     * of the channels in the connection payload. By overriding this method
     * a datasource can implement their own matching logic. One
     * can use the logic provided in {@link DataSourceTypeSupport} as
     * a good first implementation.
     * 
     * @param cache the cache that will store the data
     * @param connection the connection payload
     * @return the matched type adapter
     */
    @SuppressWarnings("unchecked")
    protected DataSourceTypeAdapter<ConnectionPayload, MessagePayload> findTypeAdapter(ValueCache<?> cache, ConnectionPayload connection) {
        return (DataSourceTypeAdapter<ConnectionPayload, MessagePayload>) (DataSourceTypeAdapter) defaultTypeAdapter;
    }
    
    /**
     * Creates a new channel handler.
     * 
     * @param channelName the name of the channel this handler will be responsible of
     */
    public MultiplexedChannelHandler(String channelName) {
        super(channelName);
    }

    @Override
    public synchronized int getUsageCounter() {
        return readUsageCounter + writeUsageCounter;
    }
    
    @Override
    public synchronized int getReadUsageCounter() {
        return readUsageCounter;
    }
    
    @Override
    public synchronized int getWriteUsageCounter() {
        return writeUsageCounter;
    }

    @Override
    protected synchronized void addReader(ChannelHandlerReadSubscription subscription) {
        readUsageCounter++;
        MonitorHandler monitor = new MonitorHandler(subscription);
        monitors.put(subscription, monitor);
        monitor.findTypeAdapter();
        guardedConnect();
        if (getUsageCounter() > 1) {
            if (connectionPayload != null) {
                monitor.processConnection(isConnected());
            }
            if (lastMessage != null) {
                monitor.processValue(lastMessage);
            }
        } 
    }

    @Override
    protected synchronized void removeReader(ChannelHandlerReadSubscription subscription) {
        monitors.remove(subscription);
        readUsageCounter--;
        guardedDisconnect();
    }
    
    @Override
    protected synchronized void addWriter(ChannelHandlerWriteSubscription subscription) {
        writeUsageCounter++;
        writeSubscriptions.put(subscription.getWriteCache(), subscription);
        guardedConnect();
        if (connectionPayload != null) {
            subscription.getConnectionWriteFunction().writeValue(isWriteConnected());
        }
    }

    @Override
    protected synchronized void removeWrite(ChannelHandlerWriteSubscription subscription) {
        writeUsageCounter--;
        writeSubscriptions.remove(subscription.getWriteCache());
        guardedDisconnect();
    }
    
    /**
     * Resets the last message to null. This can be used to invalidate
     * the last message without triggering a notification. It is useful
     * when a reconnect should behave as the first connection.
     */
    protected synchronized final void resetMessage() {
        lastMessage = null;
    }

    /**
     * Process the payload for this channel. This should be called whenever
     * a new value needs to be processed. The handler will take care of
     * using the correct {@link DataSourceTypeAdapter}
     * for each read monitor that was setup.
     * 
     * @param payload the payload of for this type of channel
     */
    protected synchronized final void processMessage(MessagePayload payload) {
        if (log.isLoggable(Level.FINEST)) {
            log.log(Level.FINEST, "processMessage for channel {0} messagePayload {1}", new Object[]{getChannelName(), payload});
        }
        
        lastMessage = payload;
        for (MonitorHandler monitor : monitors.values()) {
            monitor.processValue(payload);
        }
    }

    private void guardedConnect() {
        if (getUsageCounter() == 1) {
            try {
                connect();
            } catch(RuntimeException ex) {
                reportExceptionToAllReadersAndWriters(ex);
            }
        }
    }

    private void guardedDisconnect() {
        if (getUsageCounter() == 0) {
            try {
                disconnect();
                if (!saveMessageAfterDisconnect()) {
                    lastMessage = null;
                }
                connectionPayload = null;
            } catch (RuntimeException ex) {
                reportExceptionToAllReadersAndWriters(ex);
                log.log(Level.WARNING, "Couldn't disconnect channel " + getChannelName(), ex);
           }
        }
    }
    
    /**
     * Signals whether the last message received after the disconnect should
     * be kept so that it is available at reconnect.
     * <p>
     * By default, the message is discarded so that no memory is kept allocated.
     * 
     * @return true if the message should be kept
     */
    protected boolean saveMessageAfterDisconnect() {
        return false;
    }

    /**
     * Used by the handler to open the connection. This is called whenever
     * the first read or write request is made.
     */
    protected abstract void connect();
    
    /**
     * Used by the handler to close the connection. This is called whenever
     * the last reader or writer is de-registered.
     */
    protected abstract void disconnect();

    @Override
    protected abstract void write(Object newValue, ChannelWriteCallback callback);

    private void setConnected(boolean connected) {
        this.connected = connected;
        reportConnectionStatus(connected);
    }
    
    private void setWriteConnected(boolean writeConnected) {
        this.writeConnected = writeConnected;
        reportWriteConnectionStatus(writeConnected);
    }
    
    /**
     * Determines from the payload whether the channel is connected or not.
     * <p>
     * By default, this uses the usage counter to determine whether it's
     * connected or not. One should override this to use the actual
     * connection payload to check whether the actual protocol connection
     * has been established.
     * 
     * @param payload the connection payload
     * @return true if connected
     */
    protected boolean isConnected(ConnectionPayload  payload) {
        return getUsageCounter() > 0;
    }
    
    /**
     * Determines from the payload whether the channel can be written to.
     * <p>
     * By default, this always return false. One should override this
     * if it's implementing a write-able data source.
     * 
     * @param payload
     * @return true if ready for writes
     */
    protected boolean isWriteConnected(ConnectionPayload payload) {
        return false;
    }
    
    @Override
    public synchronized final boolean isConnected() {
        return connected;
    }
    
    /**
     * Returns true if it is channel can be written to.
     * 
     * @return true if underlying channel is write ready
     */
    public synchronized final boolean isWriteConnected() {
        // TODO: push this in ChannleHandler?
        return writeConnected;
    }

    /**
     * Determines whether {@link #processConnection(java.lang.Object) should
     * trigger {@link #processMessage(java.lang.Object) with the same (non-null)
     * payload in case the channel has been disconnected. Default is true.
     * 
     * @param processMessageOnDisconnect whether to process the message on disconnect
     */
    protected synchronized final void setProcessMessageOnDisconnect(boolean processMessageOnDisconnect) {
        this.processMessageOnDisconnect = processMessageOnDisconnect;
    }

    /**
     * Determines whether {@link #processConnection(java.lang.Object) should
     * trigger {@link #processMessage(java.lang.Object) with the same (non-null)
     * payload in case the channel has reconnected. Default is true.
     * 
     * @param processMessageOnReconnect whether to process the message on disconnect
     */
    protected synchronized final void setProcessMessageOnReconnect(boolean processMessageOnReconnect) {
        this.processMessageOnReconnect = processMessageOnReconnect;
    }
    
    
}
