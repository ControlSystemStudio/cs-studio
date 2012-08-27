/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.text.NumberFormat;
import org.epics.util.time.Timestamp;

/**
 * Immutable VInt implementation.
 *
 * @author carcassi
 */
class IVDouble extends IVNumeric implements VDouble {
    
    private final Double value;

    IVDouble(Double value, AlarmSeverity alarmSeverity,
            AlarmStatus alarmStatus,
            Timestamp timestamp, Integer timeUserTag, boolean timeValid, Double lowerDisplayLimit,
            Double lowerCtrlLimit, Double lowerAlarmLimit, Double lowerWarningLimit,
            String units, NumberFormat format, Double upperWarningLimit, Double upperAlarmLimit,
            Double upperCtrlLimit, Double upperDisplayLimit) {
        super(alarmSeverity, alarmStatus, timestamp, timeUserTag, timeValid, lowerDisplayLimit, lowerCtrlLimit,
                lowerAlarmLimit, lowerWarningLimit, units, format, upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

}
