/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.task;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.calc.AverageWithExponentialDecayCache;
import org.csstudio.domain.desy.time.ICurrentTimeProvider;
import org.csstudio.domain.desy.time.StopWatch;
import org.csstudio.domain.desy.time.StopWatch.RunningStopWatch;

/**
 * A runnable which measures its runtime.
 * The last run time is stored as well as the averaged value decaying with 0.1.
 *
 * @author bknerr
 * @since 10.05.2011
 */
public abstract class AbstractTimeMeasuredRunnable implements Runnable {

    /**
     * Average duration of all runs measured in nanos.
     * Current values are weighted with 0.2, all others with 0.8.
     */
    private final AverageWithExponentialDecayCache _avgRunDurationInNanos =
        new AverageWithExponentialDecayCache(0.8);

    private final RunningStopWatch _stopWatch;
    private long _lastElapsedRunTimeInNanos;

    /**
     * Constructor.
     */
    public AbstractTimeMeasuredRunnable(@Nonnull final ICurrentTimeProvider provider) {
        _stopWatch = StopWatch.startWith(provider);
    }

    /**
     * Constructor.
     */
    public AbstractTimeMeasuredRunnable() {
        _stopWatch = StopWatch.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void run() {
        _stopWatch.start();

        measuredRun();


        _lastElapsedRunTimeInNanos = _stopWatch.getElapsedTimeInNS();
        _avgRunDurationInNanos.accumulate(Double.valueOf(_lastElapsedRunTimeInNanos));
    }

    protected abstract void measuredRun();


    public long getLastElapsedTimeInNanos() {
        return _lastElapsedRunTimeInNanos;
    }

    public long getAverageRunTimeInMillis() {
        return getAccumulatedValue() / 1000000L;
    }

    public long getAverageRunTimeInNanos() {
        return getAccumulatedValue();
    }

    public long getNumberOfRuns() {
        return _avgRunDurationInNanos.getNumberOfAveragedValues();
    }

    public void clear() {
        _avgRunDurationInNanos.clear();
    }

    private long getAccumulatedValue() {
        final Double value = _avgRunDurationInNanos.getValue();
        if (value == null) {
            return 0L;
        }
        return value.longValue();
    }

}
