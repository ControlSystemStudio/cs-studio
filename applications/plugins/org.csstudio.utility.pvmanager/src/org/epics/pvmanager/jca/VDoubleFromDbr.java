/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

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

    @Override
    public Double getValue() {
        return dbrValue.getDoubleValue()[0];
    }

}
