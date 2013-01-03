/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Short;
import org.epics.vtype.VShortArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ArrayShort;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListShort;

/**
 *
 * @author carcassi
 */
class VShortArrayFromDbr extends VNumberMetadata<DBR_TIME_Short, DBR_CTRL_Double> implements VShortArray {

    public VShortArrayFromDbr(DBR_TIME_Short dbrValue, DBR_CTRL_Double metadata, JCAConnectionPayload connPayload) {
        super(dbrValue, metadata, connPayload);
    }

    @Override
    public ListInt getSizes() {
        return new ArrayInt(dbrValue.getShortValue().length);
    }

    @Override
    public ListShort getData() {
        return new ArrayShort(dbrValue.getShortValue());
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
