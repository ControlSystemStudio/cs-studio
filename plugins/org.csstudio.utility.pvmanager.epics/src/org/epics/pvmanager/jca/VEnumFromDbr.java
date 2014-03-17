/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_TIME_Enum;
import java.util.Arrays;
import java.util.List;
import org.epics.vtype.VEnum;
import org.epics.vtype.VTypeToString;

/**
 *
 * @author carcassi
 */
class VEnumFromDbr extends VMetadata<DBR_TIME_Enum> implements VEnum {

    private final DBR_LABELS_Enum metadata;

    public VEnumFromDbr(DBR_TIME_Enum dbrValue, DBR_LABELS_Enum metadata, JCAConnectionPayload connPayload) {
        super(dbrValue, connPayload);
        this.metadata = metadata;
    }

    @Override
    public String getValue() {
        // There are pathological cases in which CA returns no labels.
        // In those cases, we return the integer value converted to String.
        if (metadata.getLabels() == null) {
            return Integer.toString(getIndex());
        }
        
        // There are also pathologica cases in which the labels
        // are less than the actual value
        if (getIndex() >= metadata.getLabels().length || getIndex() < 0) {
            return Integer.toString(getIndex());
        }
        
        return getLabels().get(getIndex());
    }

    @Override
    public int getIndex() {
        return dbrValue.getEnumValue()[0];
    }

    @Override
    public List<String> getLabels() {
        if (metadata.getLabels() == null)
            throw new RuntimeException("Metadata returned no labels");
        return Arrays.asList(metadata.getLabels());
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
