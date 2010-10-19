/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.sim;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.TimeDuration;
import org.epics.pvmanager.TimeStamp;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.ValueFactory;

/**
 * Base class for all simulated signals. It provides the common mechanism for
 * registering the update on a timer and a few other utilities.
 *
 * @author carcassi
 */
public abstract class SimFunction<T> {

    private static final Logger log = Logger.getLogger(SimFunction.class.getName());

    private double secondsBeetwenSamples;
    private long intervalBetweenExecution;
    private TimeDuration timeBetweenSamples;
    private Class<T> classToken;
    private volatile TimeStamp lastTime;

    /**
     * Creates a new simulation function.
     *
     * @param secondsBeetwenSamples seconds between each samples
     */
    public SimFunction(double secondsBeetwenSamples, Class<T> classToken) {
        if (secondsBeetwenSamples <= 0.0) {
            throw new IllegalArgumentException("Interval must be greater than zero (was " + secondsBeetwenSamples + ")");
        }
        this.secondsBeetwenSamples = secondsBeetwenSamples;
        timeBetweenSamples = TimeDuration.nanos((long) (secondsBeetwenSamples * 1000000000));
        this.classToken = classToken;
    }

    /**
     * Calculates and returns the next value.
     *
     * @return the next value
     */
    protected abstract T nextValue();

    private TimerTask task;

    /**
     * Initialize timer task. Must be called before start.
     *
     * @param collector collector notified of updates
     * @param cache cache to put the new value in
     */
    public void initialize(final Collector collector, final ValueCache<T> cache) {
        if (!cache.getType().equals(classToken)) {
            throw new IllegalArgumentException("Function is of type " + classToken.getSimpleName() + " (requested " + cache.getType().getSimpleName() + ")");
        }

        // The timer only accepts interval up to the millisecond.
        // For intervals shorter than that, we calculate the extra samples
        // we need to generate within each time execution.
        intervalBetweenExecution = (long) (secondsBeetwenSamples * 1000) / 2;
        if (intervalBetweenExecution == 0)
            intervalBetweenExecution = 1;

        if (task != null)
            task.cancel();
        task = new TimerTask() {
            int innerCounter;
            SimulationDataSource.ValueProcessor<Object, T> processor = new SimulationDataSource.ValueProcessor<Object, T>(collector, cache) {

                @Override
                public void close() {
                    log.fine("Closing " + this);
                    cancel();
                }

                @Override
                public boolean updateCache(Object payload, ValueCache<T> cache) {
                    cache.setValue(nextValue());
                    return true;
                }
            };


            @Override
            public void run() {
                // Protect the timer thread for possible problems.
                try {
                    if (lastTime == null)
                        lastTime = TimeStamp.now();
                    TimeStamp currentTime = TimeStamp.now();
                    TimeStamp newTime = lastTime.plus(timeBetweenSamples);

                    while (currentTime.compareTo(newTime) >= 0) {
                        lastTime = newTime;
                        processor.processValue(null);
                        newTime = lastTime.plus(timeBetweenSamples);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARNING, "Data simulation problem", ex);
                }
            }
        };
    }

    /**
     * Starts notification by dispatching the prepared task on the timer.
     *
     * @param timer timer on which to execute the updates
     */
    public void start(Timer timer) {
        if (task == null)
            throw new IllegalStateException("Must call initialize first");

        timer.schedule(task, 0, intervalBetweenExecution);
        log.log(Level.FINE, "Synch starting {0} every " + intervalBetweenExecution + " ms", task);
    }

    /**
     * Stops the variable from further notifications.
     */
    public void stop() {
        if (task != null) {
            task.cancel();
            log.log(Level.FINE, "Synch closing {0}", task);
        }
        task = null;
    }

    /**
     * Creating new value based on the metadata from the old value.
     *
     * @param value new numeric value
     * @param oldValue old VDouble
     * @return new VDouble
     */
    protected VDouble newValue(double value, VDouble oldValue) {
        if (lastTime == null)
            lastTime = TimeStamp.now();
        
        // Calculate new AlarmSeverity, using oldValue ranges
        AlarmSeverity severity = AlarmSeverity.NONE;
        if (value <= oldValue.getLowerAlarmLimit() || value >= oldValue.getUpperAlarmLimit())
            severity = AlarmSeverity.MAJOR;
        else if(value <= oldValue.getLowerWarningLimit() || value >= oldValue.getUpperWarningLimit())
            severity = AlarmSeverity.MINOR;

        return ValueFactory.newVDouble(value, severity, Constants.NO_ALARMS,
                null, lastTime, oldValue);
    }

    void setLastTime(TimeStamp lastTime) {
        this.lastTime = lastTime;
    }



}
