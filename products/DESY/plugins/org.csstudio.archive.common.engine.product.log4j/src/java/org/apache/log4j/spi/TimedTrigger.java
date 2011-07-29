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
package org.apache.log4j.spi;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

/**
 * Timed trigger. Sends email when a certain time interval is over or the number of events
 * exceed a certain limit.
 *
 * @author bknerr
 * @since 29.07.2011
 */
public class TimedTrigger implements TriggeringEventEvaluator {

    private final int INTERVAL_IN_MS = 1000*60*15;
    private TimeInstant _timeOfLastEmail;
    private int _noOfEventsSinceLastEmail;

    public TimedTrigger() {
      _timeOfLastEmail = TimeInstantBuilder.fromNow().minusMillis(INTERVAL_IN_MS);
      _noOfEventsSinceLastEmail = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTriggeringEvent(@Nonnull final LoggingEvent event) {
        final TimeInstant now = updateTrigger();

        if (triggerCondition(now)) {
            resetTrigger(now);
            return true;
        }
        return false;
    }

    @Nonnull
    private TimeInstant updateTrigger() {
        _noOfEventsSinceLastEmail++;
        final TimeInstant now = TimeInstantBuilder.fromNow();
        return now;
    }

    private void resetTrigger(@Nonnull final TimeInstant now) {
        _noOfEventsSinceLastEmail = 0;
        _timeOfLastEmail = now;
    }

    private boolean triggerCondition(@Nonnull final TimeInstant now) {
        return now.isAfter(_timeOfLastEmail.plusMillis(INTERVAL_IN_MS)) ||
               _noOfEventsSinceLastEmail >512;
    }

}
