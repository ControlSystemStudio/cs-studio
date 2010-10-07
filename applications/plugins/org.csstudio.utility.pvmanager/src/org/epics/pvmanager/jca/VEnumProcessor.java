/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_TIME_Enum;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.data.VEnum;

/**
 *
 * @author carcassi
 */
public class VEnumProcessor extends SingleValueProcessor<VEnum, DBR_TIME_Enum, DBR_LABELS_Enum> {

    public VEnumProcessor(final Channel channel, Collector collector,
            ValueCache<VEnum> cache, final ExceptionHandler handler)
            throws CAException {
        super(channel, collector, cache, handler);
    }

    @Override
    protected DBRType getMetaType() {
        return DBR_LABELS_Enum.TYPE;
    }

    @Override
    protected DBRType getEpicsType() {
        return DBR_TIME_Enum.TYPE;
    }

    @Override
    protected VEnum createValue(DBR_TIME_Enum value, DBR_LABELS_Enum metadata, boolean disconnected) {
        return new VEnumFromDbr(value, metadata, disconnected);
    }
}
