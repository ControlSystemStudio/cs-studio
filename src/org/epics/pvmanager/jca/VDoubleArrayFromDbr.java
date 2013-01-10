/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Double;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class VDoubleArrayFromDbr extends VNumberMetadata<DBR_TIME_Double, DBR_CTRL_Double> implements VDoubleArray {

    public VDoubleArrayFromDbr(DBR_TIME_Double dbrValue, DBR_CTRL_Double metadata, JCAConnectionPayload connPayload) {
        super(dbrValue, metadata, connPayload);
    }

    @Override
    public ListDouble getData() {
        return new ArrayDouble(dbrValue.getDoubleValue());
    }

    @Override
    public ListInt getSizes() {
        return new ArrayInt(dbrValue.getDoubleValue().length);
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
