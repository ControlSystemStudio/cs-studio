/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.text.NumberFormat;
import java.util.List;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListFloat;
import org.epics.util.array.ListNumber;
import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
class IVFloatArray extends IVNumeric implements VFloatArray {

    private final float[] array;
    private final ListFloat data;
    private final List<Integer> sizes;

    public IVFloatArray(ListFloat data, List<Integer> sizes,
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
    public float[] getArray() {
        if (array == null) {
            float[] temp = new float[data.size()];
            for (int i = 0; i < data.size(); i++) {
                temp[i] = data.getFloat(i);
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
    public ListFloat getData() {
        return data;
    }

}
