/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Double;
import java.util.Collections;
import java.util.List;
import org.epics.pvmanager.data.VDoubleArray;

/**
 *
 * @author carcassi
 */
class VDoubleArrayFromDbr extends VNumberMetadata<DBR_TIME_Double, DBR_CTRL_Double> implements VDoubleArray {

    public VDoubleArrayFromDbr(DBR_TIME_Double dbrValue, DBR_CTRL_Double metadata, boolean disconnected) {
        super(dbrValue, metadata, disconnected);
    }
    
    @Override
    public double[] getArray() {
        return dbrValue.getDoubleValue();
    }

    @Override
    public List<Integer> getSizes() {
        return Collections.singletonList(dbrValue.getDoubleValue().length);
    }

}
