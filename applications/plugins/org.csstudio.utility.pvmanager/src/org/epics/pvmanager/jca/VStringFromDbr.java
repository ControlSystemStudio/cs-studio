/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_TIME_String;
import java.util.List;
import java.util.Set;
import org.epics.pvmanager.TimeStamp;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.VString;

/**
 *
 * @author carcassi
 */
class VStringFromDbr implements VString {

    private final DBR_TIME_String dbrValue;
    private final boolean disconnected;

    public VStringFromDbr(DBR_TIME_String dbrValue) {
        this(dbrValue, false);
    }

    public VStringFromDbr(DBR_TIME_String dbrValue, boolean disconnected) {
        this.dbrValue = dbrValue;
        this.disconnected = disconnected;
    }

    @Override
    public String getValue() {
        return dbrValue.getStringValue()[0];
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

}
