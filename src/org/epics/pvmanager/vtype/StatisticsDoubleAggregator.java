/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.vtype;

import org.epics.vtype.VStatistics;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VDouble;
import static java.lang.Math.*;
import java.util.List;
import org.epics.pvmanager.Aggregator;
import org.epics.pvmanager.ReadFunction;
import static org.epics.vtype.AlarmSeverity.*;
import static org.epics.vtype.ValueFactory.*;

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

    StatisticsDoubleAggregator(ReadFunction<List<VDouble>> collector) {
        super(collector);
    }

    private static class Stats {
        double totalSum = 0;
        double totalSquareSum = 0;
        double min = Double.MAX_VALUE;
        double max = - Double.MAX_VALUE;
        int nElements = 0;

        void includeValue(double value) {
            totalSum += value;
            totalSquareSum += value * value;
            min = min(min, value);
            max = max(min, value);
            nElements++;
        }
    }

    @Override
    protected VStatistics calculate(List<VDouble> data) {
        Stats stats = new Stats();
        AlarmSeverity statSeverity = null;
        for (VDouble vDouble : data) {
            switch(vDouble.getAlarmSeverity()) {
                case NONE:
                    // if severity was never MINOR or MAJOR,
                    // severity should be NONE
                    if (statSeverity != MINOR || statSeverity != MAJOR)
                        statSeverity = NONE;
                    stats.includeValue(vDouble.getValue());
                    break;

                case MINOR:
                    // If severity was never MAJOR,
                    // set it to MINOR
                    if (statSeverity != MAJOR)
                        statSeverity = MINOR;
                    stats.includeValue(vDouble.getValue());
                    break;

                case MAJOR:
                    statSeverity = MAJOR;
                    stats.includeValue(vDouble.getValue());
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
        return newVStatistics(stats.totalSum / stats.nElements,
                sqrt(stats.totalSquareSum / stats.nElements - (stats.totalSum * stats.totalSum) / (stats.nElements * stats.nElements)),
                stats.min, stats.max, stats.nElements,
                newAlarm(statSeverity, "NONE"), newTime(data.get(data.size() / 2).getTimestamp()), data.get(0));
    }

}
