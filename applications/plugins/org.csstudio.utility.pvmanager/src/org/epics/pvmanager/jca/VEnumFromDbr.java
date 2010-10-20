/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_TIME_Enum;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.epics.pvmanager.TimeStamp;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.VEnum;

/**
 *
 * @author carcassi
 */
class VEnumFromDbr implements VEnum {

    private final DBR_TIME_Enum dbrValue;
    private final DBR_LABELS_Enum metadata;
    private final boolean disconnected;

    public VEnumFromDbr(DBR_TIME_Enum dbrValue, DBR_LABELS_Enum metadata) {
        this(dbrValue, metadata, false);
    }

    public VEnumFromDbr(DBR_TIME_Enum dbrValue, DBR_LABELS_Enum metadata, boolean disconnected) {
        this.dbrValue = dbrValue;
        this.metadata = metadata;
        this.disconnected = disconnected;
    }

    @Override
    public String getValue() {
        return getLabels().get(getIndex());
    }

    @Override
    public AlarmSeverity getAlarmSeverity() {
        if (disconnected)
            return AlarmSeverity.UNDEFINED;
        return DataUtils.fromEpics(dbrValue.getSeverity());
    }

    @Override
    public Set<String> getAlarmStatus() {
        return DataUtils.fromEpics(dbrValue.getStatus());
    }

    @Override
    public List<String> getPossibleAlarms() {
        return DataUtils.epicsPossibleStatus();
    }

    @Override
    public TimeStamp getTimeStamp() {
        if (dbrValue.getTimeStamp() == null)
            return null;
        
        return TimeStamp.epicsTime(dbrValue.getTimeStamp().secPastEpoch(),
                dbrValue.getTimeStamp().secPastEpoch());
    }

    @Override
    public Integer getTimeUserTag() {
        return null;
    }

    @Override
    public int getIndex() {
        return dbrValue.getEnumValue()[0];
    }

    @Override
    public List<String> getLabels() {
        return Arrays.asList(metadata.getLabels());
    }

}
