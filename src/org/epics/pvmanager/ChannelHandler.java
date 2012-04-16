/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the connection for each channel of a data source.
 *
 * @param <EType> type of the connection payload
 * @author carcassi
 */
public abstract class ChannelHandler<EType> {

    private static final Logger log = Logger.getLogger(ChannelHandler.class.getName());
    private final String channelName;
    private int readUsageCounter = 0;
    private int writeUsageCounter = 0;
    private volatile EType lastValue;
    private Map<Collector<?>, MonitorHandler> monitors = new ConcurrentHashMap<Collector<?>, MonitorHandler>();

    private class MonitorHandler {

        private final Collector<?> collector;
        private final ValueCache<?> cache;
        private final ExceptionHandler exceptionHandler;

        public MonitorHandler(Collector<?> collector, ValueCache<?> cache, ExceptionHandler exceptionHandler) {
            this.collector = collector;
            this.cache = cache;
            this.exceptionHandler = exceptionHandler;
        }

        public final void processValue(EType payload) {
            // Lock the collector and prepare the new value.
            synchronized (collector) {
                try {
                    if (updateCache(payload, cache)) {
                        collector.collect();
                    }
                } catch (RuntimeException e) {
                    exceptionHandler.handleException(e);
                }
            }
        }
    }
    
    /**
     * Creates a new channel handler.
     * 
     * @param channelName the name of the channel this handler will be responsible of
     */
    public ChannelHandler(String channelName) {
        if (channelName == null)
            throw new NullPointerException("Channel name cannot be null");
        this.channelName = channelName;
    }

    /**
     * Returns the name of the channel.
     * 
     * @return the channel name; can't be null
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Returns how many read or write PVs are open on
     * this channel.
     * 
     * @return the number of open read/writes
     */
    public synchronized int getUsageCounter() {
        return readUsageCounter + writeUsageCounter;
    }
    
    /**
     * Returns how many read PVs are open on this channel.
     * 
     * @return the number of open reads
     */
    public synchronized int getReadUsageCounter() {
        return readUsageCounter;
    }
    
    /**
     * Returns how many write PVs are open on this channel.
     * 
     * @return the number of open writes
     */
    public synchronized int getWriteUsageCounter() {
        return writeUsageCounter;
    }

    /**
     * Used by the data source to add a read request on the channel managed
     * by this handler.
     * 
     * @param collector collector to be notified at each update
     * @param cache cache to contain the new value
     * @param handler to be notified in case of errors
     */
    protected synchronized void addMonitor(Collector<?> collector, ValueCache<?> cache, final ExceptionHandler handler) {
        readUsageCounter++;
        MonitorHandler monitor = new MonitorHandler(collector, cache, handler);
        monitors.put(collector, monitor);
        guardedConnect(handler);
        if (readUsageCounter > 1 && lastValue != null) {
            monitor.processValue(lastValue);
        } 
    }

    /**
     * Used by the data source to remove a read request.
     * 
     * @param collector the collector that does not need to be notified anymore
     */
    protected synchronized void removeMonitor(Collector<?> collector) {
        monitors.remove(collector);
        readUsageCounter--;
        guardedDisconnect(new ExceptionHandler() {

            @Override
            public void handleException(Exception ex) {
                log.log(Level.WARNING, "Couldn't disconnect channel " + channelName, ex);
            }
        });
    }

    /**
     * Used by the data source to prepare the channel managed by this handler
     * for write.
     * 
     * @param handler to be notified in case of errors
     */
    protected synchronized void addWriter(ExceptionHandler handler) {
        writeUsageCounter++;
        guardedConnect(handler);
    }

    /**
     * Used by the data source to conclude writes to the channel managed by this handler.
     * 
     * @param exceptionHandler to be notified in case of errors
     */
    protected synchronized void removeWrite(ExceptionHandler exceptionHandler) {
        writeUsageCounter--;
        guardedDisconnect(exceptionHandler);
    }

    /**
     * Process the payload for this channel. This should be called whenever
     * a new value needs to be processed. The handler will take care of
     * calling {@link #updateCache(java.lang.Object, org.epics.pvmanager.ValueCache) }
     * for each read monitor that was setup.
     * 
     * @param payload the payload of for this type of channel
     */
    protected final void processValue(EType payload) {
        lastValue = payload;
        for (MonitorHandler monitor : monitors.values()) {
            monitor.processValue(payload);
        }
    }

    private void guardedConnect(final ExceptionHandler handler) {
        if (getUsageCounter() == 1) {
            connect(handler);
        }
    }

    private void guardedDisconnect(final ExceptionHandler handler) {
        if (getUsageCounter() == 0) {
            disconnect(handler);
        }
    }

    /**
     * Used by the handler to open the connection. This is called whenever
     * the first read or write request is made.
     * 
     * @param handler to be notified in case of errors
     */
    protected abstract void connect(final ExceptionHandler handler);

    /**
     * Used by the handler to close the connection. This is called whenever
     * the last reader or writer is de-registered.
     * 
     * @param handler to be notified in case of errors
     */
    protected abstract void disconnect(final ExceptionHandler handler);

    /**
     * Implements a write operation. Write the newValues to the channel
     * and call the callback when done.
     * 
     * @param newValue new value to be written
     * @param callback called when done or on error
     */
    protected abstract void write(Object newValue, ChannelWriteCallback callback);

    /**
     * Used by the handler to forward values. Extracts the value form the
     * payload and stores it in the cache.
     * 
     * @param event the payload
     * @param cache the cache where to store the new value
     * @return true if a new value was stored
     */
    protected abstract boolean updateCache(EType event, ValueCache<?> cache);

    /**
     * Returns true if it is connected.
     * 
     * @return true if underlying channel is connected
     */
    public abstract boolean isConnected();
}
