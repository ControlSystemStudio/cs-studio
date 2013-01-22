/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_TIME_String;
import java.util.Collections;
import java.util.List;
import org.epics.pvmanager.data.VStringArray;

/**
 *
 * @author carcassi
 */
class VStringArrayFromDbr extends VMetadata<DBR_TIME_String> implements VStringArray {

    public VStringArrayFromDbr(DBR_TIME_String dbrValue, boolean disconnected) {
        super(dbrValue, disconnected);
    }
    
    @Override
    public String[] getArray() {
        return dbrValue.getStringValue();
    }

    @Override
    public List<Integer> getSizes() {
        return Collections.singletonList(dbrValue.getStringValue().length);
    }

}
