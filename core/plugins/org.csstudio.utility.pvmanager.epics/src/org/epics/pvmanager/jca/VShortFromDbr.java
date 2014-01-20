/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Short;
import org.epics.vtype.VShort;
import org.epics.vtype.VTypeToString;

/**
 *
 * @author carcassi
 */
class VShortFromDbr extends VNumberMetadata<DBR_TIME_Short, DBR_CTRL_Double> implements VShort {

    public VShortFromDbr(DBR_TIME_Short dbrValue, DBR_CTRL_Double metadata, JCAConnectionPayload connPayload) {
        super(dbrValue, metadata, connPayload);
    }

    @Override
    public Short getValue() {
        return dbrValue.getShortValue()[0];
    }

    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
