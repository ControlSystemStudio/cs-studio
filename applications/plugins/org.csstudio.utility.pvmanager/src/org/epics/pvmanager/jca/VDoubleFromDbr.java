/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import org.epics.pvmanager.TimeStamp;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.VDouble;

/**
 *
 * @author carcassi
 */
public class VDoubleFromDbr implements VDouble {

    private final DBR_TIME_Double dbrValue;
    private final DBR_CTRL_Double metadata;
    private final boolean disconnected;

    public VDoubleFromDbr(DBR_TIME_Double dbrValue, DBR_CTRL_Double metadata) {
        this(dbrValue, metadata, false);
    }

    public VDoubleFromDbr(DBR_TIME_Double dbrValue, DBR_CTRL_Double metadata, boolean disconnected) {
        this.dbrValue = dbrValue;
        this.metadata = metadata;
        this.disconnected = disconnected;
    }

    @Override
    public Double getValue() {
        return dbrValue.getDoubleValue()[0];
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
    public Double getLowerDisplayLimit() {
        return (Double) metadata.getLowerDispLimit();
    }

    @Override
    public Double getLowerCtrlLimit() {
        return (Double) metadata.getLowerCtrlLimit();
    }

    @Override
    public Double getLowerAlarmLimit() {
        return (Double) metadata.getLowerAlarmLimit();
    }

    @Override
    public Double getLowerWarningLimit() {
        return (Double) metadata.getLowerWarningLimit();
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
    public Double getUpperWarningLimit() {
        return (Double) metadata.getUpperWarningLimit();
    }

    @Override
    public Double getUpperAlarmLimit() {
        return (Double) metadata.getUpperAlarmLimit();
    }

    @Override
    public Double getUpperCtrlLimit() {
        return (Double) metadata.getUpperCtrlLimit();
    }

    @Override
    public Double getUpperDisplayLimit() {
        return (Double) metadata.getUpperDispLimit();
    }

}
