/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Int;
import org.epics.vtype.VInt;
import org.epics.vtype.VTypeToString;

/**
 *
 * @author carcassi
 */
class VIntFromDbr extends VNumberMetadata<DBR_TIME_Int, DBR_CTRL_Double> implements VInt {

    public VIntFromDbr(DBR_TIME_Int dbrValue, DBR_CTRL_Double metadata, JCAConnectionPayload connPayload) {
        super(dbrValue, metadata, connPayload);
    }

    private static DBR_TIME_Int convert(DBR dbrValue) {
        try {
            return (DBR_TIME_Int) dbrValue.convert(DBRType.TIME_INT);
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't convert " + dbrValue.getType() + " to DBR_TIME_DOUBLE", ex);
        }
    }

    public VIntFromDbr(DBR dbrValue, DBR_CTRL_Double metadata, JCAConnectionPayload connPayload) {
        this(convert(dbrValue), metadata, connPayload);
    }

    @Override
    public Integer getValue() {
        return dbrValue.getIntValue()[0];
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
