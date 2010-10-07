/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Double;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.data.VDouble;

/**
 *
 * @author carcassi
 */
public class VDoubleProcessor extends SingleValueProcessor<VDouble, DBR_TIME_Double, DBR_CTRL_Double> {

    public VDoubleProcessor(final Channel channel, Collector collector,
            ValueCache<VDouble> cache, final ExceptionHandler handler)
            throws CAException {
        super(channel, collector, cache, handler);
    }

    @Override
    protected DBRType getMetaType() {
        return DBR_CTRL_Double.TYPE;
    }

    @Override
    protected DBRType getEpicsType() {
        return DBR_TIME_Double.TYPE;
    }

    @Override
    protected VDouble createValue(DBR_TIME_Double value, DBR_CTRL_Double metadata, boolean disconnected) {
        return new VDoubleFromDbr(value, metadata, disconnected);
    }
}
