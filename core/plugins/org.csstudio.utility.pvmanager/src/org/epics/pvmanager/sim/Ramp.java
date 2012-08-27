/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sim;

import org.epics.pvmanager.util.TimeStamp;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.AlarmStatus;
import org.epics.pvmanager.data.VDouble;
import static org.epics.pvmanager.data.ValueFactory.*;
import org.epics.util.time.Timestamp;

/**
 * Function to simulate a signal that increases constantly within a range
 * (saw-tooth shape). The warning
 * limits are set at 80% of the range and the alarm at 90% the range.
 * All values are going to have no alarm status, with the timestamp set at the
 * moment the sample was generated.
 *
 * @author carcassi
 */
public class Ramp extends SimFunction<VDouble> {

    private double min;
    private double max;
    private double currentValue;
    private double step;
    private double range;
    private VDouble lastValue;

    /**
     * Creates a ramp shaped signal between min and max, updating a step amount
     * every interval seconds.
     *
     * @param min minimum value
     * @param max maximum value
     * @param step increment for each sample
     * @param interval interval between samples in seconds
     */
    public Ramp(Double min, Double max, Double step, Double interval) {
        super(interval, VDouble.class);
        if (interval <= 0.0) {
            throw new IllegalArgumentException("Interval must be greater than zero (was " + interval + ")");
        }
        this.min = min;
        this.max = max;
        this.currentValue = min - step;
        this.step = step;
        range = max - min;
        lastValue = newVDouble(currentValue, alarmNone(), newTime(Timestamp.now()),
                newDisplay(min, min + range * 0.1, min + range * 0.2, "x", Constants.DOUBLE_FORMAT,
                min + range * 0.8, min + range * 0.9, max, min, max));
    }

    /**
     * Creates a ramp shaped signal between min and max, incrementing 1
     * every interval seconds.
     *
     * @param min minimum value
     * @param max maximum value
     * @param interval interval between samples in seconds
     */
    public Ramp(Double min, Double max, Double interval) {
        this(min, max, 1.0, interval);
    }
    
    /**
     * Creates a ramp shaped signal between -5 and +5, incrementing 1 every second.
     */
    public Ramp() {
        this (-5.0, 5.0, 1.0);
    }

    @Override
    VDouble nextValue() {
        currentValue = currentValue + step;
        if (currentValue > max) {
            currentValue = min;
        }

        return newValue(currentValue, lastValue);
    }
}
