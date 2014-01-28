/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.vtype;

import org.epics.vtype.Time;
import org.epics.vtype.VMultiDouble;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.VDouble;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.pvmanager.ReadFunction;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;

/**
 * Provides an aggregator that returns a synchronized set of data by looking
 * into a timed cache.
 *
 * @author carcassi
 */
class SynchronizedVDoubleAggregator implements ReadFunction<VMultiDouble> {

    private static final Logger log = Logger.getLogger(SynchronizedVDoubleAggregator.class.getName());
    private final TimeDuration tolerance;
    private final List<ReadFunction<List<VDouble>>> collectors;

    /**
     * Creates a new aggregators, that takes a list of collectors
     * and reconstructs a synchronized array.
     *
     * @param names names of the individual pvs
     * @param collectors collectors that contain the past few samples
     * @param tolerance the tolerance around the reference time for samples to be included
     */
    @SuppressWarnings("unchecked")
    public SynchronizedVDoubleAggregator(List<String> names, List<ReadFunction<List<VDouble>>> collectors, TimeDuration tolerance) {
        if (!tolerance.isPositive())
            throw new IllegalArgumentException("Tolerance between samples must be non-zero and positive");
        this.tolerance = tolerance;
        this.collectors = collectors;
    }

    @Override
    public VMultiDouble readValue() {
        Timestamp reference = electReferenceTimeStamp(collectors);
        if (reference == null)
            return null;

        TimeInterval allowedInterval = tolerance.around(reference);
        List<VDouble> values = new ArrayList<VDouble>(collectors.size());
        StringBuilder buffer = new StringBuilder();
        for (ReadFunction<List<VDouble>> collector : collectors) {
            List<VDouble> data = collector.readValue();
            if (log.isLoggable(Level.FINE)) {
                buffer.append(data.size()).append(", ");
            }
            VDouble value = closestElement(data, allowedInterval, reference);
            values.add(value);
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine(buffer.toString());
        }
        return ValueFactory.newVMultiDouble(values, ValueFactory.alarmNone(),
                ValueFactory.newTime(reference), ValueFactory.displayNone());
    }

    static <T extends Time> Timestamp electReferenceTimeStamp(List<ReadFunction<List<T>>> collectors) {
        for (ReadFunction<List<T>> collector : collectors) {
            List<T> data = collector.readValue();
            if (data.size() > 1) {
                Timestamp time = data.get(data.size() - 2).getTimestamp();
                if (time != null)
                    return time;
            }
        }
        return null;
    }

    static <T extends Time> T closestElement(List<T> data, TimeInterval interval, Timestamp reference) {
        StringBuilder buffer = new StringBuilder();
        T latest = null;
        long latestDistance = Long.MAX_VALUE;
        for (T value : data) {
            Timestamp newTime = value.getTimestamp();
            if (log.isLoggable(Level.FINEST)) {
                buffer.append(newTime.getNanoSec()).append(", ");
            }

            if (interval.contains(newTime)) {
                if (latest == null) {
                    latest = value;
                    latestDistance = newTime.durationBetween(reference).getNanoSec();
                } else {
                    long newDistance = newTime.durationBetween(reference).getNanoSec();
                    if (newDistance < latestDistance) {
                        latest = value;
                        latestDistance = newDistance;
                    }
                }
            }
        }
        if (log.isLoggable(Level.FINEST)) {
            buffer.append("[").append(latest.getTimestamp().getNanoSec()).append("|").append(reference.getNanoSec()).append("]");
            log.finest(buffer.toString());
        }
        return latest;
    }

}
