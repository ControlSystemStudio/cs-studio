/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Short;
import java.util.Collections;
import java.util.List;
import org.epics.pvmanager.data.VShortArray;

/**
 *
 * @author carcassi
 */
class VShortArrayFromDbr extends VNumberMetadata<DBR_TIME_Short, DBR_CTRL_Double> implements VShortArray {

    public VShortArrayFromDbr(DBR_TIME_Short dbrValue, DBR_CTRL_Double metadata, boolean disconnected) {
        super(dbrValue, metadata, disconnected);
    }
    
    @Override
    public short[] getArray() {
        return dbrValue.getShortValue();
    }

    @Override
    public List<Integer> getSizes() {
        return Collections.singletonList(dbrValue.getShortValue().length);
    }

}
