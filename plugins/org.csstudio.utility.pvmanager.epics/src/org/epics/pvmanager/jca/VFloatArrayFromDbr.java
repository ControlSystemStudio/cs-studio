/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Float;
import java.util.List;
import org.epics.vtype.VFloatArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayFloat;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListFloat;
import org.epics.util.array.ListInt;
import org.epics.vtype.ArrayDimensionDisplay;
import org.epics.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
class VFloatArrayFromDbr extends VNumberMetadata<DBR_TIME_Float, DBR_CTRL_Double> implements VFloatArray {

    public VFloatArrayFromDbr(DBR_TIME_Float dbrValue, DBR_CTRL_Double metadata, JCAConnectionPayload connPayload) {
        super(dbrValue, metadata, connPayload);
    }

    @Override
    public ListInt getSizes() {
        return new ArrayInt(dbrValue.getFloatValue().length);
    }

    @Override
    public ListFloat getData() {
        return new ArrayFloat(dbrValue.getFloatValue());
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

    @Override
    public List<ArrayDimensionDisplay> getDimensionDisplay() {
        return ValueUtil.defaultArrayDisplay(this);
    }

}
