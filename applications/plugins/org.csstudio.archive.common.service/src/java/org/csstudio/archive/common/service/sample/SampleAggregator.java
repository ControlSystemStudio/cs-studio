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
package org.csstudio.archive.common.service.sample;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.calc.CumulativeAverageCache;
import org.csstudio.domain.desy.time.TimeInstant;

import com.google.common.collect.Ordering;

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
    private TimeInstant _lastSampleTimeStamp;
    private TimeInstant _resetTimeStamp;


    /**
     * Constructor.
     * @param firstVal
     * @param firstAlarm
     * @param timestamp
     */
    public SampleAggregator(@Nonnull final Double firstVal,
                            @Nonnull final TimeInstant timestamp) {

        _avg.accumulate(firstVal);

        _minVal = firstVal;
        _maxVal = firstVal;
        _lastWrittenValue = null;

        _lastSampleTimeStamp = timestamp;
        _resetTimeStamp = _lastSampleTimeStamp;
    }

    public void aggregateNewVal(@Nonnull final Double newVal,
                         @Nonnull final TimeInstant timestamp) {
        aggregateNewVal(newVal, newVal, newVal, timestamp);
    }

    public void aggregateNewVal(@Nonnull final Double newVal,
                         @Nonnull final Double min,
                         @Nonnull final Double max,
                         @Nonnull final TimeInstant timestamp) {
        _avg.accumulate(newVal);
        _minVal = Ordering.natural().nullsLast().min(newVal, min, max, _minVal);
        _maxVal = Ordering.natural().nullsFirst().max(newVal, min, max, _maxVal);
        _lastSampleTimeStamp = timestamp;
    }

    /**
     * Resets the aggregator count to the last accumulated avg value.
     * Minimum and maximum values equal this last average.
     * Alarm state is initialised to the minimum known alarm accumulated until now
     * (not necessarily the minimum alarm possible).
     */
    public void reset() {
        _resetTimeStamp = _lastSampleTimeStamp;
        _lastSampleTimeStamp = null;
        _lastWrittenValue = _avg.getValue();
        _minVal = null;
        _maxVal = null;

        _avg.clear();
    }
    @CheckForNull
    public Double getAvg() {
        return _avg.getValue();
    }
    @CheckForNull
    public Double getMin() {
        return _minVal;
    }
    @CheckForNull
    public Double getMax() {
        return _maxVal;
    }
    @CheckForNull
    public Double getAverageBeforeReset() {
        return _lastWrittenValue;
    }
    @CheckForNull
    public TimeInstant getSampleTimestamp() {
        return _lastSampleTimeStamp;
    }
    @CheckForNull
    public TimeInstant getResetTimestamp() {
        return _resetTimeStamp;
    }
}
