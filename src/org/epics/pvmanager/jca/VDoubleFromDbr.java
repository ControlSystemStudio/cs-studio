/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Double;
import org.epics.pvmanager.data.VDouble;

/**
 *
 * @author carcassi
 */
class VDoubleFromDbr extends VNumberMetadata<DBR_TIME_Double, DBR_CTRL_Double> implements VDouble {

    public VDoubleFromDbr(DBR_TIME_Double dbrValue, DBR_CTRL_Double metadata, boolean disconnected) {
        super(dbrValue, metadata, disconnected);
    }

    private static DBR_TIME_Double convert(DBR dbrValue) {
        try {
            return (DBR_TIME_Double) dbrValue.convert(DBRType.TIME_DOUBLE);
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't convert " + dbrValue.getType() + " to DBR_TIME_DOUBLE", ex);
        }
    }

    public VDoubleFromDbr(DBR dbrValue, DBR_CTRL_Double metadata, boolean disconnected) {
        this(convert(dbrValue), metadata, disconnected);
    }

    @Override
    public Double getValue() {
        return dbrValue.getDoubleValue()[0];
    }

}
