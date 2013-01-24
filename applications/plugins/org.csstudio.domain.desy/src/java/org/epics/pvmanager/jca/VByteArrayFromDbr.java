/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Byte;
import java.util.Collections;
import java.util.List;
import org.epics.pvmanager.data.VByteArray;
import org.epics.util.array.ArrayByte;
import org.epics.util.array.ListByte;

/**
 *
 * @author carcassi
 */
class VByteArrayFromDbr extends VNumberMetadata<DBR_TIME_Byte, DBR_CTRL_Double> implements VByteArray {

    public VByteArrayFromDbr(DBR_TIME_Byte dbrValue, DBR_CTRL_Double metadata, boolean disconnected) {
        super(dbrValue, metadata, disconnected);
    }
    
    @Override
    public byte[] getArray() {
        return dbrValue.getByteValue();
    }

    @Override
    public List<Integer> getSizes() {
        return Collections.singletonList(dbrValue.getByteValue().length);
    }

    @Override
    public ListByte getData() {
        return new ArrayByte(dbrValue.getByteValue());
    }

}
