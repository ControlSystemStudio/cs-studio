/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.vtype;

import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VDouble;
import java.util.List;
import org.epics.pvmanager.Aggregator;
import org.epics.pvmanager.ReadFunction;
import static org.epics.vtype.AlarmSeverity.*;
import static org.epics.vtype.ValueFactory.*;

/**
 * Aggregates the values by taking the average.
 * 
 * @author carcassi
 */
class AverageAggregator extends Aggregator<VDouble, VDouble> {

    AverageAggregator(ReadFunction<List<VDouble>> collector) {
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
        return newVDouble(totalSum / data.size(), newAlarm(statSeverity, "NONE"),
                newTime(data.get(data.size() / 2).getTimestamp()), data.get(0));
    }

}
