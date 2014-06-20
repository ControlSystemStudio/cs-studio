/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Float;
import org.epics.vtype.VFloat;
import org.epics.vtype.VTypeToString;

/**
 *
 * @author carcassi
 */
class VFloatFromDbr extends VNumberMetadata<DBR_TIME_Float, DBR_CTRL_Double> implements VFloat {

    public VFloatFromDbr(DBR_TIME_Float dbrValue, DBR_CTRL_Double metadata, JCAConnectionPayload connPayload) {
        super(dbrValue, metadata, connPayload);
    }

    @Override
    public Float getValue() {
        return dbrValue.getFloatValue()[0];
    }

    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
