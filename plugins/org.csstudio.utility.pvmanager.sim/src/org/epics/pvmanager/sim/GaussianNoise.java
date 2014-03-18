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
 * Function to simulate a signal that has a gaussian distribution. The warning
 * limits are set above the standard deviation and the alarm above two times
 * the standard deviation. The total range is 4 times the standard deviation.
 * All values are going to have no alarm status, with the timestamp set at the
 * moment the sample was generated.
 *
 * @author carcassi
 */
public class GaussianNoise extends SimFunction<VDouble> {

    private Random rand = new Random();
    private double average;
    private double stdDev;
    private VDouble lastValue;

    /**
     * Creates a signal with a normal distribution (average zero and
     * standard deviation one), updating every 100ms (10Hz).
     */
    public GaussianNoise() {
        this(0.0, 1.0, 0.1);
    }

    /**
     * Creates a signal with a gaussian distribution, updating at the rate
     * specified.
     *
     * @param average average of the gaussian distribution
     * @param stdDev standard deviation of the gaussian distribution
     * @param interval time between samples in seconds
     */
    public GaussianNoise(Double average, Double stdDev, Double interval) {
        super(interval, VDouble.class);
        if (interval <= 0.0) {
            throw new IllegalArgumentException("Interval must be greater than zero (was " + interval + ")");
        }
        this.average = average;
        this.stdDev = stdDev;
        lastValue = newVDouble(average, alarmNone(), newTime(Timestamp.now()),
                newDisplay(average - 4 * stdDev, average - 2 * stdDev, average - stdDev, "x", Constants.DOUBLE_FORMAT,
                average + stdDev, average + 2 * stdDev, average + 4 * stdDev, average - 4 * stdDev, average + 4 * stdDev));
    }

    @Override
    VDouble nextValue() {
        return newValue(average + rand.nextGaussian() * stdDev, lastValue);
    }
}
