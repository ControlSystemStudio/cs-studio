/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_TIME_Byte;
import gov.aps.jca.dbr.DBR_TIME_String;
import org.epics.pvmanager.vtype.VString;
import org.epics.pvmanager.vtype.VTypeToString;

/**
 *
 * @author carcassi
 */
class VStringFromDbr extends VMetadata<DBR_TIME_String> implements VString {

    public VStringFromDbr(DBR_TIME_String dbrValue, JCAConnectionPayload connPayload) {
        super(dbrValue, connPayload);
    }

    VStringFromDbr(DBR_TIME_Byte dbrValue, JCAConnectionPayload JCAConnectionPayload) {
        this(convert(dbrValue), JCAConnectionPayload);
    }
    
    private static DBR_TIME_String convert(DBR_TIME_Byte dbrValue) {
        int stringEnd = 0;
        while (dbrValue.getByteValue()[stringEnd] != 0) {
            stringEnd++;
        }
        DBR_TIME_String converted = new DBR_TIME_String(new String[] {new String(dbrValue.getByteValue(), 0, stringEnd)});
        converted.setTimeStamp(dbrValue.getTimeStamp());
        converted.setStatus(dbrValue.getStatus());
        converted.setSeverity(dbrValue.getSeverity());
        return converted;
    }

    @Override
    public String getValue() {
        return dbrValue.getStringValue()[0];
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
