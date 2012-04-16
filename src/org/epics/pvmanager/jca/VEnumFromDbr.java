/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_TIME_Enum;
import java.util.Arrays;
import java.util.List;
import org.epics.pvmanager.data.VEnum;

/**
 *
 * @author carcassi
 */
class VEnumFromDbr extends VMetadata<DBR_TIME_Enum> implements VEnum {

    private final DBR_LABELS_Enum metadata;

    public VEnumFromDbr(DBR_TIME_Enum dbrValue, DBR_LABELS_Enum metadata, boolean disconnected) {
        super(dbrValue, disconnected);
        this.metadata = metadata;
    }

    @Override
    public String getValue() {
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

}
