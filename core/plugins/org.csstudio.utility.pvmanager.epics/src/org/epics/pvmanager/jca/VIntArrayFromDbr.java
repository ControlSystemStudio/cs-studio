/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Int;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class VIntArrayFromDbr extends VNumberMetadata<DBR_TIME_Int, DBR_CTRL_Double> implements VIntArray {

    public VIntArrayFromDbr(DBR_TIME_Int dbrValue, DBR_CTRL_Double metadata, JCAConnectionPayload connPayload) {
        super(dbrValue, metadata, connPayload);
    }

    @Override
    public ListInt getSizes() {
        return new ArrayInt(dbrValue.getIntValue().length);
    }

    @Override
    public ListInt getData() {
        return new ArrayInt(dbrValue.getIntValue());
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
