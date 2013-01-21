/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Int;
import java.util.Collections;
import java.util.List;
import org.epics.pvmanager.data.VIntArray;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class VIntArrayFromDbr extends VNumberMetadata<DBR_TIME_Int, DBR_CTRL_Double> implements VIntArray {

    public VIntArrayFromDbr(DBR_TIME_Int dbrValue, DBR_CTRL_Double metadata, boolean disconnected) {
        super(dbrValue, metadata, disconnected);
    }
    
    @Override
    public int[] getArray() {
        return dbrValue.getIntValue();
    }

    @Override
    public List<Integer> getSizes() {
        return Collections.singletonList(dbrValue.getIntValue().length);
    }

    @Override
    public ListInt getData() {
        return new ArrayInt(dbrValue.getIntValue());
    }

}
