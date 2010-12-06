/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.service.mysqlimpl.sample;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.data.CumulativeAverageCache;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.time.TimeInstant;

/**
 * Cumulative average for Double types.
 * Caches the sum of the accumulated values, and stores their number.
 * On the {@link CumulativeAverageCache#getValue()} the accumulated value is divided by this
 * number of contributing values.
 *
 * @author bknerr
 * @since 25.11.2010
 */
public class SampleAggregator {

    private final CumulativeAverageCache _avg = new CumulativeAverageCache();

    private Double _minVal;
    private Double _maxVal;
    private Double _lastWrittenValue;
    private TimeInstant _lastWritetime;
    private EpicsAlarm _highestAlarm;
    private EpicsAlarm _lowestAlarm;


    /**
     * Constructor.
     * @param firstVal
     * @param firstAlarm
     * @param timestamp
     */
    public SampleAggregator(@Nonnull final Double firstVal,
                            @Nonnull final EpicsAlarm firstAlarm,
                            @Nonnull final TimeInstant timestamp) {

        _avg.accumulate(firstVal);

        _minVal = firstVal;
        _maxVal = firstVal;
        _lastWrittenValue = null;

        _highestAlarm = firstAlarm;
        _lowestAlarm = firstAlarm;
        _lastWritetime = timestamp;
    }

    void aggregateNewVal(@Nonnull final Double newVal,
                         @Nonnull final EpicsAlarm alarm) {
        _avg.accumulate(newVal);
        _maxVal = Math.max(_maxVal, newVal);
        _minVal = Math.min(_minVal, newVal);
        _highestAlarm = _highestAlarm.compareTo(alarm) < 0 ? alarm : _highestAlarm;
        _lowestAlarm = _lowestAlarm.compareTo(alarm) > 0 ? alarm : _lowestAlarm;
    }

    /**
     * Resets the aggregator count to the last accumulated avg value.
     * Minimum and maximum values equal this last average.
     * Alarm state is initialised to the minimum known alarm accumulated until now
     * (not necessarily the minimum alarm possible).
     *
     * @param lastWriteTime the time instant of the last accumulated sample
     */
    void reset(@Nonnull final TimeInstant lastWriteTime) {
        final Double lastAvg = _avg.getValue();
        _lastWrittenValue = lastAvg;
        _minVal = lastAvg;
        _maxVal = lastAvg;
        _highestAlarm = _lowestAlarm;
        _lastWritetime = lastWriteTime;

        _avg.clear();
    }
    @CheckForNull
    public Double getAvg() {
        return _avg.getValue();
    }
    @Nonnull
    public Double getMin() {
        return _minVal;
    }
    @Nonnull
    public Double getMax() {
        return _maxVal;
    }
    @Nonnull
    public EpicsAlarm getHighestAlarm() {
        return _highestAlarm;
    }
    @Nonnull
    public Double getLastWrittenValue() {
        return _lastWrittenValue;
    }
    @Nonnull
    public TimeInstant getLastWriteTime() {
        return _lastWritetime;
    }
}
