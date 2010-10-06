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
import gov.aps.jca.dbr.DBR_TIME_String;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VString;

/**
 *
 * @author carcassi
 */
public class VStringProcessor extends SingleValueProcessor<VString, DBR_TIME_String, DBR_TIME_String> {

    public VStringProcessor(final Channel channel, Collector collector,
            ValueCache<VString> cache, final ExceptionHandler handler)
            throws CAException {
        super(channel, collector, cache, handler);
    }

    @Override
    protected DBRType getMetaType() {
        return null;
    }

    @Override
    protected DBRType getEpicsType() {
        return DBR_TIME_String.TYPE;
    }

    @Override
    protected VString createValue(DBR_TIME_String value, DBR_TIME_String metadata, boolean disconnected) {
        return new VStringFromDbr(value, disconnected);
    }
}
