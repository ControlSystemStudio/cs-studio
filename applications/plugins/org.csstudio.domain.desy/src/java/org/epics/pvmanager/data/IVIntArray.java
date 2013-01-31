/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.text.NumberFormat;
import java.util.List;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
class IVIntArray extends IVNumeric implements VIntArray {

    private final int[] array;
    private final ListInt data;
    private final List<Integer> sizes;

    public IVIntArray(int[] array, List<Integer> sizes,
            AlarmSeverity alarmSeverity, AlarmStatus alarmStatus,
            Timestamp timestamp, Integer timeUserTag, boolean timeValid, Double lowerDisplayLimit,
            Double lowerCtrlLimit, Double lowerAlarmLimit, Double lowerWarningLimit,
            String units, NumberFormat format, Double upperWarningLimit, Double upperAlarmLimit,
            Double upperCtrlLimit, Double upperDisplayLimit) {
        super(alarmSeverity, alarmStatus, timestamp, timeUserTag, timeValid, lowerDisplayLimit,
                lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit, units, format, upperWarningLimit,
                upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
        this.array = array;
        this.sizes = sizes;
        this.data = new ArrayInt(array);
    }

    public IVIntArray(ListInt data, List<Integer> sizes,
            Alarm alarm, Time time, Display display) {
        super(alarm.getAlarmSeverity(), alarm.getAlarmStatus(), time.getTimestamp(), time.getTimeUserTag(), time.isTimeValid(),
                display.getLowerDisplayLimit(), display.getLowerCtrlLimit(), display.getLowerAlarmLimit(), display.getLowerWarningLimit(),
                display.getUnits(), display.getFormat(),
                display.getUpperWarningLimit(), display.getUpperAlarmLimit(), display.getUpperCtrlLimit(), display.getUpperDisplayLimit());
        this.array = null;
        this.sizes = sizes;
        this.data = data;
    }

    @Override
    public int[] getArray() {
        if (array == null) {
            int[] temp = new int[data.size()];
            for (int i = 0; i < data.size(); i++) {
                temp[i] = data.getInt(i);
            }
            return temp;
        }
        
        return array;
    }

    @Override
    public List<Integer> getSizes() {
        return sizes;
    }

    @Override
    public ListInt getData() {
        return data;
    }

}
