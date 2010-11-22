/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBRType;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.ValueCache;

/**
 * Generic class that manages the monitor and the connection of an
 * epics channel.
 *
 * @author carcassi
 */
abstract class ArrayProcessor<VType, EpicsType, MetaType> extends SingleValueProcessor<VType, EpicsType, MetaType> {

    protected ArrayProcessor(final Channel channel, Collector collector,
            ValueCache<VType> cache, final ExceptionHandler handler,
            DBRType epicsType, DBRType metaType)
            throws CAException {
        super(channel, collector, cache, handler, epicsType, metaType);
    }

    @Override
    synchronized void setup(Channel channel) throws CAException {
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
            monitor = channel.addMonitor(getEpicsType(), channel.getElementCount(), Monitor.VALUE, monitorListener);
            channel.getContext().flushIO();
        }
    }
}
