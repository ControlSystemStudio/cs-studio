/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Int;
import org.epics.pvmanager.data.VInt;

/**
 *
 * @author carcassi
 */
class VIntFromDbr extends VNumberMetadata<DBR_TIME_Int, DBR_CTRL_Double> implements VInt {

    public VIntFromDbr(DBR_TIME_Int dbrValue, DBR_CTRL_Double metadata, boolean disconnected) {
        super(dbrValue, metadata, disconnected);
    }

    @Override
    public Integer getValue() {
        return dbrValue.getIntValue()[0];
    }
}
