/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.util.Collections;
import org.epics.pvmanager.Aggregator;
import org.epics.pvmanager.Collector;
import java.util.List;
import static java.lang.Math.*;
import static org.epics.pvmanager.data.AlarmSeverity.*;

/**
 * Aggregates statistics out of multiple VDoubles.
 * <p>
 * All data with severity NONE, MINOR and MAJOR is used for calculating
 * statistics. If all samples are UNDEFINED, the severity will be undefined.
 * If all samples are UNDEFINED/INVALID, the severity will
 * be INVALID. In all other cases, the severity will be the highest between
 * NONE, MINOR and MAJOR. Only valid samples are used for computation.
 * <p>
 * TODO: what is the best alarm calculation?
 * TODO: what should be the weight? Each sample one, or weighted by time?
 * TODO: timestamp? Average? Median? Time of calculation? If weight is
 *       one, should be median. If weight by time, should be average.
 *
 * @author carcassi
 */
class StatisticsDoubleAggregator extends Aggregator<VStatistics, VDouble> {

    StatisticsDoubleAggregator(Collector<VDouble> collector) {
        super(collector);
    }

    @Override
    protected VStatistics calculate(List<VDouble> data) {
        double totalSum = 0;
        double totalSquareSum = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        AlarmSeverity statSeverity = null;
        for (VDouble vDouble : data) {
            switch(vDouble.getAlarmSeverity()) {
                case NONE:
                    // if severity was never MINOR or MAJOR,
                    // severity should be NONE
                    if (statSeverity != MINOR || statSeverity != MAJOR)
                        statSeverity = NONE;
                case MINOR:
                    // If severity was never MAJOR,
                    // set it to MINOR
                    if (statSeverity != MAJOR)
                        statSeverity = MINOR;
                case MAJOR:
                    statSeverity = MAJOR;
                double value = vDouble.getValue();
                totalSum += value;
                totalSquareSum += value * value;
                min = min(min, value);
                max = max(min, value);
                break;
                
                case UNDEFINED:
                    if (statSeverity == null)
                        statSeverity = UNDEFINED;
                
                case INVALID:
                    if (statSeverity == null || statSeverity == UNDEFINED)
                        statSeverity = INVALID;
            }
        }
        return ValueFactory.newVStatistics(totalSum / data.size(),
                sqrt(totalSquareSum / data.size() - (totalSum * totalSum) / (data.size() * data.size())),
                min, max, data.size(),
                statSeverity, Collections.<String>emptySet(), null, data.get(data.size() / 2).getTimeStamp(), data.get(0));
    }

}
