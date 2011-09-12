/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.text.NumberFormat;
import java.util.List;
import org.epics.pvmanager.util.TimeStamp;

/**
 * Immutable VMultiDouble implementation.
 *
 * @author carcassi
 */
class IVMultiDouble extends IVNumeric implements VMultiDouble {
    
    private final List<VDouble> values;

    IVMultiDouble(List<VDouble> values, AlarmSeverity alarmSeverity,
            AlarmStatus alarmStatus,
            TimeStamp timeStamp, Integer timeUserTag, boolean timeValid, Double lowerDisplayLimit,
            Double lowerCtrlLimit, Double lowerAlarmLimit, Double lowerWarningLimit,
            String units, NumberFormat format, Double upperWarningLimit, Double upperAlarmLimit,
            Double upperCtrlLimit, Double upperDisplayLimit) {
        super(alarmSeverity, alarmStatus, timeStamp, timeUserTag, timeValid, lowerDisplayLimit, lowerCtrlLimit,
                lowerAlarmLimit, lowerWarningLimit, units, format, upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
        this.values = values;
    }

    @Override
    public List<VDouble> getValues() {
        return values;
    }

}
