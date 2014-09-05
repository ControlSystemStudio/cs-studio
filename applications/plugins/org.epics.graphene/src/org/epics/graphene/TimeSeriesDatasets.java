/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import org.epics.util.stats.StatisticsUtil;
import org.epics.util.stats.Statistics;
import java.util.List;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListNumber;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
public class TimeSeriesDatasets {
    /**
     *Returns a TimeSeriesDataset with the <code>Statistics</code> and time interval.
     * the time interval is the difference in time between the first and second time intervals
     * @param values - List of values associated with timestamps
     * @param timestamps - list of <code>Timestamp</code> 
     * @return TimeSeriesDataset
     */
    public static TimeSeriesDataset timeSeriesOf(final ListNumber values, final List<Timestamp> timestamps) {
        // TODO: make sure timestamps are monotinic
        final TimeInterval timeInterval = TimeInterval.between(timestamps.get(0), timestamps.get(timestamps.size() - 1));
        final Statistics stats = StatisticsUtil.statisticsOf(values);
        return new TimeSeriesDataset() {

            @Override
            public ListNumber getValues() {
                return values;
            }

            @Override
            public List<Timestamp> getTimestamps() {
                return timestamps;
            }

            @Override
            public ListNumber getNormalizedTime() {
                return new ListDouble() {

                    @Override
                    public double getDouble(int index) {
                        return TimeScales.normalize(timestamps.get(index), timeInterval);
                    }

                    @Override
                    public int size() {
                        return timestamps.size();
                    }
                };
            }

            @Override
            public Statistics getStatistics() {
                return stats;
            }

            @Override
            public TimeInterval getTimeInterval() {
                return timeInterval;
            }

            @Override
            public int getCount() {
                return values.size();
            }
        };
    }
}
