/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.util.Collections;
import org.epics.pvmanager.Aggregator;
import org.epics.pvmanager.Collector;
import java.util.List;
import static org.epics.pvmanager.data.AlarmSeverity.*;

/**
 * Aggregates the values by taking the average.
 * 
 * @author carcassi
 */
class AverageAggregator extends Aggregator<VDouble, VDouble> {

    AverageAggregator(Collector<VDouble> collector) {
        super(collector);
    }

    @Override
    protected VDouble calculate(List<VDouble> data) {
        // TODO: this code should be consolidated with the StatisticsDoubleAggregator
        double totalSum = 0;
        AlarmSeverity statSeverity = null;
        for (VDouble vDouble : data) {
            switch(vDouble.getAlarmSeverity()) {
                case NONE:
                    // if severity was never MINOR or MAJOR,
                    // severity should be NONE
                    if (statSeverity != MINOR || statSeverity != MAJOR)
                        statSeverity = NONE;
                    totalSum += vDouble.getValue();
                    break;

                case MINOR:
                    // If severity was never MAJOR,
                    // set it to MINOR
                    if (statSeverity != MAJOR)
                        statSeverity = MINOR;
                    totalSum += vDouble.getValue();
                    break;

                case MAJOR:
                    statSeverity = MAJOR;
                    totalSum += vDouble.getValue();
                    break;

                case UNDEFINED:
                    if (statSeverity == null)
                        statSeverity = UNDEFINED;
                    break;

                case INVALID:
                    if (statSeverity == null || statSeverity == UNDEFINED)
                        statSeverity = INVALID;
                    break;

                default:
            }
        }
        return ValueFactory.newVDouble(totalSum / data.size(),
                statSeverity, AlarmStatus.NONE, null, data.get(data.size() / 2).getTimeStamp(), data.get(0));
    }

}
