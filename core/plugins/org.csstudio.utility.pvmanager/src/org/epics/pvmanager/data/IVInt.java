/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.text.NumberFormat;
import org.epics.pvmanager.util.TimeStamp;

/**
 * Immutable VInt implementation.
 *
 * @author carcassi
 */
class IVInt extends IVNumeric implements VInt {
    
    private final Integer value;

    IVInt(Integer value, AlarmSeverity alarmSeverity,
            AlarmStatus alarmStatus,
            TimeStamp timeStamp, Integer timeUserTag, boolean timeValid, Double lowerDisplayLimit,
            Double lowerCtrlLimit, Double lowerAlarmLimit, Double lowerWarningLimit,
            String units, NumberFormat format, Double upperWarningLimit, Double upperAlarmLimit,
            Double upperCtrlLimit, Double upperDisplayLimit) {
        super(alarmSeverity, alarmStatus, timeStamp, timeUserTag, timeValid, lowerDisplayLimit, lowerCtrlLimit,
                lowerAlarmLimit, lowerWarningLimit, units, format, upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

}
