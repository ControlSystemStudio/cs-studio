/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Float;
import java.util.Collections;
import java.util.List;
import org.epics.pvmanager.data.VFloatArray;
import org.epics.util.array.ArrayFloat;
import org.epics.util.array.ListFloat;

/**
 *
 * @author carcassi
 */
class VFloatArrayFromDbr extends VNumberMetadata<DBR_TIME_Float, DBR_CTRL_Double> implements VFloatArray {

    public VFloatArrayFromDbr(DBR_TIME_Float dbrValue, DBR_CTRL_Double metadata, boolean disconnected) {
        super(dbrValue, metadata, disconnected);
    }
    
    @Override
    public float[] getArray() {
        return dbrValue.getFloatValue();
    }

    @Override
    public List<Integer> getSizes() {
        return Collections.singletonList(dbrValue.getFloatValue().length);
    }

    @Override
    public ListFloat getData() {
        return new ArrayFloat(dbrValue.getFloatValue());
    }

}
