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

import org.csstudio.domain.desy.time.ICurrentTimeProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link AbstractTimeMeasuredRunnable}.
 *
 * @author bknerr
 * @since 10.05.2011
 */
public class TimeMeasuredRunnableTest {

    /**
     * Test time provider.
     *
     * @author bknerr
     * @since 11.05.2011
     */
    private static final class TestTimeProviderImplementation implements ICurrentTimeProvider {
        private int _index;
        private final int[] _time = new int[] {0,  // init runnable's internal stop watch
                                               0,  // start first run
                                               20, // end first run
                                               20, // start second run
                                               30, // end second run
                                              };

        /**
         * Constructor.
         */
        public TestTimeProviderImplementation() {
            // Empty
        }

        @Override
        public long getCurrentTimeInNanos() {
            return (long) (getCurrentTimeInMillis()*1e6);
        }

        @Override
        public long getCurrentTimeInMillis() {
            return _time[_index++];
        }
    }

    @Test
    public void testRunnableStatistics() {
        final ICurrentTimeProvider provider =
            new TestTimeProviderImplementation();

        final AbstractTimeMeasuredRunnable worker =
            new AbstractTimeMeasuredRunnable(provider) {
                @Override
                protected void measuredRun() {
                    // Ignore
                }
            };
        Assert.assertTrue(worker.getLastElapsedTimeInNanos() == 0L);
        Assert.assertTrue(worker.getAverageRunTimeInMillis() == 0L);
        Assert.assertTrue(worker.getAverageRunTimeInNanos() == 0L);

        worker.run();
        final long firstRunTimeInNanos = worker.getLastElapsedTimeInNanos();
        Assert.assertTrue(firstRunTimeInNanos == 20*1e6);
        //CHECKSTYLE OFF: NestedBlock
        {
            final long averageRunTimeInNanos = worker.getAverageRunTimeInNanos();
            final long avg = 20;
            Assert.assertTrue(averageRunTimeInNanos == avg*1e6);
            final long averageRunTimeInMillis = worker.getAverageRunTimeInMillis();
            Assert.assertTrue(averageRunTimeInMillis == avg);
        }

        worker.run();
        final long secondRunTimeInNanos = worker.getLastElapsedTimeInNanos();
        Assert.assertTrue(secondRunTimeInNanos == 10*1e6);
        {
            final long averageRunTimeInNanos = worker.getAverageRunTimeInNanos();
            final long avg = (long) (20*0.8 + 10*0.2);
            Assert.assertTrue(averageRunTimeInNanos == avg*1e6);
            final long averageRunTimeInMillis = worker.getAverageRunTimeInMillis();
            Assert.assertTrue(averageRunTimeInMillis == avg);
        }
        //CHECKSTYLE ON: NestedBlock
    }

}
