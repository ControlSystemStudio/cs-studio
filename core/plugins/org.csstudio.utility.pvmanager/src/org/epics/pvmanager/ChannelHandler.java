/*
 * Copyright 2008-2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
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

    public ChannelHandler(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public int getUsageCounter() {
        return readUsageCounter + writeUsageCounter;
    }

    public synchronized void addMonitor(Collector<?> collector, ValueCache<?> cache, final ExceptionHandler handler) {
        readUsageCounter++;
        MonitorHandler monitor = new MonitorHandler(collector, cache, handler);
        monitors.put(collector, monitor);
        guardedConnect(handler);
        if (readUsageCounter > 1 && lastValue != null) {
            monitor.processValue(lastValue);
        } 
    }

    public synchronized void removeMonitor(Collector<?> collector) {
        monitors.remove(collector);
        readUsageCounter--;
        guardedDisconnect(new ExceptionHandler() {

            @Override
            public void handleException(Exception ex) {
                log.log(Level.WARNING, "Couldn't disconnect channel " + channelName, ex);
            }
        });
    }

    public synchronized void addWriter(ExceptionHandler handler) {
        guardedConnect(handler);
        writeUsageCounter++;
    }

    public synchronized void removeWrite(ExceptionHandler exceptionHandler) {
        writeUsageCounter--;
        guardedDisconnect(exceptionHandler);
    }

    public final void processValue(EType payload) {
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

    public abstract void connect(final ExceptionHandler handler);

    public abstract void disconnect(final ExceptionHandler handler);

    public abstract void write(Object newValue, ChannelWriteCallback callback);

    public abstract boolean updateCache(EType event, ValueCache<?> cache);

    public abstract boolean isConnected();
}
