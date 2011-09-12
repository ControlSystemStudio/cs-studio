/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_TIME_String;
import org.epics.pvmanager.data.VString;

/**
 *
 * @author carcassi
 */
class VStringFromDbr extends VMetadata<DBR_TIME_String> implements VString {

    public VStringFromDbr(DBR_TIME_String dbrValue, boolean disconnected) {
        super(dbrValue, disconnected);
    }

    @Override
    public String getValue() {
        return dbrValue.getStringValue()[0];
    }

}
