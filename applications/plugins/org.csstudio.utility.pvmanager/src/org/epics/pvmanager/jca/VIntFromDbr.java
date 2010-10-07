/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Int;
import gov.aps.jca.dbr.DBR_TIME_Int;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import org.epics.pvmanager.TimeStamp;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.VInt;

/**
 *
 * @author carcassi
 */
public class VIntFromDbr implements VInt {

    private final DBR_TIME_Int dbrValue;
    private final DBR_CTRL_Int metadata;
    private final boolean disconnected;

    public VIntFromDbr(DBR_TIME_Int dbrValue, DBR_CTRL_Int metadata) {
        this(dbrValue, metadata, false);
    }

    public VIntFromDbr(DBR_TIME_Int dbrValue, DBR_CTRL_Int metadata, boolean disconnected) {
        this.dbrValue = dbrValue;
        this.metadata = metadata;
        this.disconnected = disconnected;
    }

    @Override
    public Integer getValue() {
        return dbrValue.getIntValue()[0];
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
    public Integer getLowerDisplayLimit() {
        return (Integer) metadata.getLowerDispLimit();
    }

    @Override
    public Integer getLowerCtrlLimit() {
        return (Integer) metadata.getLowerCtrlLimit();
    }

    @Override
    public Integer getLowerAlarmLimit() {
        return (Integer) metadata.getLowerAlarmLimit();
    }

    @Override
    public Integer getLowerWarningLimit() {
        return (Integer) metadata.getLowerWarningLimit();
    }

    @Override
    public String getUnits() {
        return metadata.getUnits();
    }

    @Override
    public NumberFormat getFormat() {
        // TODO: this needs to be revised
        return NumberFormat.getNumberInstance();
    }

    @Override
    public Integer getUpperWarningLimit() {
        return (Integer) metadata.getUpperWarningLimit();
    }

    @Override
    public Integer getUpperAlarmLimit() {
        return (Integer) metadata.getUpperAlarmLimit();
    }

    @Override
    public Integer getUpperCtrlLimit() {
        return (Integer) metadata.getUpperCtrlLimit();
    }

    @Override
    public Integer getUpperDisplayLimit() {
        return (Integer) metadata.getUpperDispLimit();
    }

}
