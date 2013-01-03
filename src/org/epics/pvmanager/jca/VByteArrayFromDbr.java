/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Byte;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayByte;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListByte;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class VByteArrayFromDbr extends VNumberMetadata<DBR_TIME_Byte, DBR_CTRL_Double> implements VByteArray {

    public VByteArrayFromDbr(DBR_TIME_Byte dbrValue, DBR_CTRL_Double metadata, JCAConnectionPayload connPayload) {
        super(dbrValue, metadata, connPayload);
    }

    @Override
    public ListInt getSizes() {
        return new ArrayInt(dbrValue.getByteValue().length);
    }

    @Override
    public ListByte getData() {
        return new ArrayByte(dbrValue.getByteValue());
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
