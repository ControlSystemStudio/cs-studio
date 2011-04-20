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
 * Sample aggregator for average, min, max values for {@link java.lang.Double} type values.
 * Caches the sum of the accumulated values and stores information about the timestamps of the
 * last accumulated sample.
 *
 * @author bknerr
 * @since 25.11.2010
 */
public class SampleMinMaxAggregator {

    private final CumulativeAverageCache _avg = new CumulativeAverageCache();

    private Double _minVal;
    private Double _maxVal;
    private Double _lastAvgBeforeReset;
    private TimeInstant _lastSampleTimeStamp;
    private TimeInstant _resetTimeStamp;


    /**
     * Constructor.
     */
    public SampleMinMaxAggregator(@Nonnull final Double firstVal,
                                  @Nonnull final Double firstMin,
                                  @Nonnull final Double firstMax,
                                  @Nonnull final TimeInstant timestamp) {
        _avg.accumulate(firstVal);

        _minVal = firstMin;
        _maxVal = firstMax;
        _lastAvgBeforeReset = null;

        _lastSampleTimeStamp = timestamp;
        _resetTimeStamp = _lastSampleTimeStamp;

    }
    /**
     * Constructor.
     */
    public SampleMinMaxAggregator(@Nonnull final Double firstVal,
                                  @Nonnull final TimeInstant timestamp) {
        this(firstVal, firstVal, firstVal, timestamp);
    }
    /**
     * Constructor.
     */
    public SampleMinMaxAggregator() {
        // EMPTY
    }

    public void aggregate(@Nonnull final Double newVal,
                          @Nonnull final TimeInstant timestamp) {
        aggregate(newVal, newVal, newVal, timestamp);
    }

    public synchronized void aggregate(@Nonnull final Double newVal,
                                       @Nonnull final Double min,
                                       @Nonnull final Double max,
                                       @Nonnull final TimeInstant timestamp) {
        _avg.accumulate(newVal);
        _minVal = Ordering.natural().nullsLast().min(newVal, min, max, _minVal);
        _maxVal = Ordering.natural().nullsFirst().max(newVal, min, max, _maxVal);
        _lastSampleTimeStamp = timestamp;
    }

    /**
     * Resets the aggregator. <br/>
     * Caches the timestamp of the last aggregated sample.
     * Caches the last average value.
     * Sets the minimum and maximum values to <code>null</code>.
     * Clears the current average value cache.
     */
    public synchronized void reset() {
        _resetTimeStamp = _lastSampleTimeStamp;
        _lastSampleTimeStamp = null;
        _lastAvgBeforeReset = _avg.getValue();
        _minVal = null;
        _maxVal = null;

        _avg.clear();
    }
    @CheckForNull
    public synchronized Double getAvg() {
        return _avg.getValue();
    }
    @CheckForNull
    public synchronized Double getMin() {
        return _minVal;
    }
    @CheckForNull
    public synchronized Double getMax() {
        return _maxVal;
    }
    @CheckForNull
    public synchronized Double getAverageBeforeReset() {
        return _lastAvgBeforeReset;
    }
    @CheckForNull
    public synchronized TimeInstant getSampleTimestamp() {
        return _lastSampleTimeStamp;
    }
    @CheckForNull
    public synchronized TimeInstant getResetTimestamp() {
        return _resetTimeStamp;
    }
}
