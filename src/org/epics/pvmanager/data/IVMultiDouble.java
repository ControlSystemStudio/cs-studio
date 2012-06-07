/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.text.NumberFormat;
import java.util.List;
import org.epics.util.time.Timestamp;

/**
 * Immutable VMultiDouble implementation.
 *
 * @author carcassi
 */
class IVMultiDouble extends IVNumeric implements VMultiDouble {
    
    private final List<VDouble> values;

    IVMultiDouble(List<VDouble> values, AlarmSeverity alarmSeverity,
            AlarmStatus alarmStatus,
            Timestamp timestamp, Integer timeUserTag, boolean timeValid, Double lowerDisplayLimit,
            Double lowerCtrlLimit, Double lowerAlarmLimit, Double lowerWarningLimit,
            String units, NumberFormat format, Double upperWarningLimit, Double upperAlarmLimit,
            Double upperCtrlLimit, Double upperDisplayLimit) {
        super(alarmSeverity, alarmStatus, timestamp, timeUserTag, timeValid, lowerDisplayLimit, lowerCtrlLimit,
                lowerAlarmLimit, lowerWarningLimit, units, format, upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
        this.values = values;
    }

    @Override
    public List<VDouble> getValues() {
        return values;
    }

}
