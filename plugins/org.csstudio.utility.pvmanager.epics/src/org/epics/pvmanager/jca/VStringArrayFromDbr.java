/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_TIME_String;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class VStringArrayFromDbr extends VMetadata<DBR_TIME_String> implements VStringArray {
    
    private List<String> data;

    public VStringArrayFromDbr(DBR_TIME_String dbrValue, JCAConnectionPayload connPayload) {
        super(dbrValue, connPayload);
        data = Collections.unmodifiableList(Arrays.asList(dbrValue.getStringValue()));
    }
    
    @Override
    public List<String> getData() {
        return data;
    }

    @Override
    public ListInt getSizes() {
        return new ArrayInt(dbrValue.getStringValue().length);
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
