/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sim;

import java.util.List;
import java.util.logging.Logger;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;

/**
 * Base class for all simulated signals. It provides the common mechanism for
 * registering the update on a timer and a few other utilities.
 *
 * @author carcassi
 */
abstract class Simulation<T> {

    private static final Logger log = Logger.getLogger(Simulation.class.getName());

    private final long intervalBetweenExecution;
    private final Class<T> classToken;
    volatile Timestamp lastTime;

    /**
     * Creates a new simulation.
     *
     * @param secondsBeetwenSamples seconds between each samples
     */
    Simulation(TimeDuration scanRate, Class<T> classToken) {
        if (scanRate.getNanoSec() < 1000000) {
            throw new IllegalArgumentException("Scans must be at least every ms (was " + scanRate + ")");
        }
        this.intervalBetweenExecution = Math.max(scanRate.getNanoSec() / 1000000, 1);
        this.classToken = classToken;
    }

    /**
     * Computes all the new values in the given time slice.
     *
     * @param interval the interval where the data should be generated
     * @return the new values
     */
    abstract List<T> createValues(TimeInterval interval);

    /**
     * Changes the time at which the data will be generated.
     *
     * @param lastTime new timestamp
     */
    void setLastTime(Timestamp lastTime) {
        this.lastTime = lastTime;
    }

}
