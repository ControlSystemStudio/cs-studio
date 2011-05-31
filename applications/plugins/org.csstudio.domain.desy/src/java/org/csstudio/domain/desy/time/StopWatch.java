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
package org.csstudio.domain.desy.time;

import javax.annotation.Nonnull;

/**
 * The StopWatch factory to measure time in nanoseconds.
 *
 * @author bknerr
 * @since Mar 24, 2011
 */
public final class StopWatch {
    private StopWatch() {
        // EMPTY
    }

    @Nonnull
    public static RunningStopWatch start() {
        return new RunningStopWatch();
    }

    @Nonnull
    public static RunningStopWatch startWith(@Nonnull final ICurrentTimeProvider provider) {
        return new RunningStopWatch(provider);
    }

    /**
     * A stopwatch that has been started.
     *
     * @author bknerr
     * @since Mar 24, 2011
     */
    public static final class RunningStopWatch {
        private long _startInNS;

        private final ICurrentTimeProvider _provider;
        /**
         * Constructor.
         */
        RunningStopWatch() {
            this(new SystemTimeProvider());
        }
        /**
         * Constructor.
         */
        RunningStopWatch(@Nonnull final ICurrentTimeProvider provider) {
            _provider = provider;
            _startInNS = _provider.getCurrentTimeInNanos();
        }
        public long getStartTimeInNS() {
            return _startInNS;
        }
        public long getElapsedTimeInNS() {
            return _provider.getCurrentTimeInNanos() - _startInNS;
        }
        public long getElapsedTimeInMillis() {
            return (long) (getElapsedTimeInNS() / 1.0e6);
        }
        public void restart() {
            _startInNS = _provider.getCurrentTimeInNanos();
        }
        public void start() {
            restart();
        }
    }

}
