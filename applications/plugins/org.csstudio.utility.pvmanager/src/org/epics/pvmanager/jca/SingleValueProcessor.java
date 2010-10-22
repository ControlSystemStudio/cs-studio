/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.ValueCache;

/**
 * Generic class that manages the monitor and the connection of an
 * epics channel.
 *
 * @author carcassi
 */
abstract class SingleValueProcessor<VType, EpicsType, MetaType> extends DataSource.ValueProcessor<MonitorEvent, VType> {

    protected SingleValueProcessor(final Channel channel, Collector collector,
            ValueCache<VType> cache, final ExceptionHandler handler)
            throws CAException {
        super(collector, cache);

        // Need to wait for the connection to be established
        // before reading the metadata
        channel.addConnectionListener(new ConnectionListener() {

            @Override
            public void connectionChanged(ConnectionEvent ev) {
                try {
                    // Setup monitors on connection and tear them
                    // down on disconnection
                    if (ev.isConnected()) {
                        setup(channel);
                    } else {
                        close();
                        processValue(event);
                    }
                } catch (CAException ex) {
                    handler.handleException(ex);
                }
            }
        });

        // If the channel was already connected, then the monitor may
        // be never called. Set it up.
        if (channel.getConnectionState() == Channel.CONNECTED) {
            setup(channel);
        }
    }

    private synchronized void setup(Channel channel) throws CAException {
        // This method may be called twice, if the connection happens
        // after the ConnectionListener is setup but before
        // the connection state is polled.

        // The synchronization makes sure that, if that happens, the
        // two calls are serial. Checking the monitor for null to
        // make sure the second call does not create another monitor.
        if (monitor == null) {
            if (getMetaType() != null) {
                @SuppressWarnings("unchecked")
                MetaType temp = (MetaType) channel.get(getMetaType(), 1);
                metadata = temp;
            }
            monitor = channel.addMonitor(getEpicsType(), 1, Monitor.VALUE, monitorListener);
            channel.getContext().flushIO();
        }
    }

    protected abstract DBRType getMetaType();

    protected abstract DBRType getEpicsType();

    protected abstract VType createValue(EpicsType value, MetaType metadata, boolean disconnected);
    private volatile Monitor monitor;
    private volatile MetaType metadata;
    private volatile MonitorEvent event;
    private final MonitorListener monitorListener = new MonitorListener() {

        @Override
        public void monitorChanged(MonitorEvent event) {
            SingleValueProcessor.this.event = event;
            processValue(event);
        }
    };

    @Override
    public void close() {
        if (monitor != null) {
            monitor.removeMonitorListener(monitorListener);
            monitor = null;
        }
    }

    @Override
    public boolean updateCache(MonitorEvent event, ValueCache<VType> cache) {
        @SuppressWarnings("unchecked")
        EpicsType rawvalue = (EpicsType) event.getDBR();
        cache.setValue(createValue(rawvalue, metadata, monitor == null));
        return true;
    }
}
