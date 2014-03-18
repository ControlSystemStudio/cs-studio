/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import java.util.Random;
import org.epics.vtype.VDouble;
import static org.epics.vtype.ValueFactory.*;
import org.epics.util.time.Timestamp;

/**
 * Function to simulate a signal that has a uniform distribution. The warning
 * limits are set at 80% of the range and the alarm at 90% the range.
 * All values are going to have no alarm status, with the timestamp set at the
 * moment the sample was generated.
 *
 * @author carcassi
 */
public class Noise extends SimFunction<VDouble> {

    private Random rand = new Random();
    private double min;
    private double max;
    private double range;
    private VDouble lastValue;

    /**
     * Creates a signal uniformly distributed between -5.0 and 5.0, updating
     * every 100ms (10Hz).
     */
    public Noise() {
        this(-5.0, 5.0, 1.0);
    }
    
    /**
     * Do not use: only provided to provide some sort of error message
     * for people migrating from utility.pv.
     * 
     * @param min minimum value
     * @param max maximum value
     * @param step ignored
     * @param interval interval between samples in seconds
     */
    public Noise(Double min, Double max, Double step, Double interval) {
        super(interval, VDouble.class);
        throw new IllegalArgumentException("Please rename to noise(" + min + ", " + max + ", " + interval + ")");
    }

    /**
     * Creates a signal uniformly distributed between min and max, updating
     * every interval seconds.
     *
     * @param min minimum value
     * @param max maximum value
     * @param interval interval between samples in seconds
     */
    public Noise(Double min, Double max, Double interval) {
        super(interval, VDouble.class);
        if (interval <= 0.0) {
            throw new IllegalArgumentException("Interval must be greater than zero (was " + interval + ")");
        }
        this.min = min;
        this.max = max;
        range = this.max - this.min;
        lastValue = newVDouble(min, alarmNone(), newTime(Timestamp.now()),
                newDisplay(min, min + range * 0.1, min + range * 0.2, "x", Constants.DOUBLE_FORMAT,
                min + range * 0.8, min + range * 0.9, max, min, max));
    }

    @Override
    VDouble nextValue() {
        return newValue(min + rand.nextDouble() * range, lastValue);
    }
}
