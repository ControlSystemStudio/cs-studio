/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.epics.pvmanager.util.TimeDuration;
import org.epics.pvmanager.util.TimeInterval;
import org.epics.pvmanager.util.TimeStamp;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.AlarmStatus;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.ValueFactory;

/**
 * Base class for all simulated functions. It provide constant rate data generation
 * facilities.
 *
 * @author carcassi
 */
abstract class SimFunction<T> extends Simulation<T> {

    private static final Logger log = Logger.getLogger(SimFunction.class.getName());

    private TimeDuration timeBetweenSamples;

    /**
     * Creates a new simulation function.
     *
     * @param secondsBeetwenSamples seconds between each samples
     * @param classToken simulated class
     */
    SimFunction(double secondsBeetwenSamples, Class<T> classToken) {
        // The timer only accepts interval up to the millisecond.
        // For intervals shorter than that, we calculate the extra samples
        // we need to generate within each time execution.
        super(TimeDuration.ms(Math.max((int) (secondsBeetwenSamples * 1000) / 2, 1)), classToken);

        if (secondsBeetwenSamples <= 0.0) {
            throw new IllegalArgumentException("Interval must be greater than zero (was " + secondsBeetwenSamples + ")");
        }

        timeBetweenSamples = TimeDuration.nanos((long) (secondsBeetwenSamples * 1000000000));
    }

    /**
     * Calculates and returns the next value.
     *
     * @return the next value
     */
    abstract T nextValue();

    /**
     * Computes all the new values in the given time slice by calling nextValue()
     * appropriately.
     *
     * @param interval the interval where the data should be generated
     * @return the new values
     */
    @Override
    List<T> createValues(TimeInterval interval) {
        List<T> values = new ArrayList<T>();
        TimeStamp newTime = lastTime.plus(timeBetweenSamples);

        while (interval.contains(newTime)) {
            lastTime = newTime;
            values.add(nextValue());
            newTime = lastTime.plus(timeBetweenSamples);
        }

        return values;
    }

    /**
     * Creating new value based on the metadata from the old value.
     *
     * @param value new numeric value
     * @param oldValue old VDouble
     * @return new VDouble
     */
    VDouble newValue(double value, VDouble oldValue) {
        if (lastTime == null)
            lastTime = TimeStamp.now();
        
        return ValueFactory.newVDouble(value, lastTime, oldValue);
    }

}
