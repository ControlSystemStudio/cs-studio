/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.junit.Assert.*;

import java.time.Instant;

import static org.hamcrest.CoreMatchers.*;

import org.csstudio.alarm.beast.SeverityLevel;
import org.diirt.util.time.TimeDuration;
import org.junit.Test;

/** JUnit test of the {@link AlarmStateHistory}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmStateHistoryUnitTest
{
    @Test
    public void testAlarmStateHistory()
    {
        // Check for 2 faults within 3 seconds
        final AlarmStateHistory history = new AlarmStateHistory(2);
        assertThat(history.receivedAlarmsWithinTimerange(3.0), equalTo(false));

        // 1 fault at the 'start' time
        final Instant start = Instant.now();
        history.add(new AlarmState(SeverityLevel.MINOR, "Low", "1", start));
        assertThat(history.receivedAlarmsWithinTimerange(3.0), equalTo(false));

        // 2 faults within _4_ seconds
        history.add(new AlarmState(SeverityLevel.MINOR, "Low", "1", start.plus(TimeDuration.ofSeconds(4.0))));
        assertThat(history.receivedAlarmsWithinTimerange(3.0), equalTo(false));

        // Original fault drops off the list. Keeping 2 faults within _2_ seconds
        history.add(new AlarmState(SeverityLevel.MINOR, "Low", "1", start.plus(TimeDuration.ofSeconds(6.0))));
        assertThat(history.receivedAlarmsWithinTimerange(3.0), equalTo(true));
    }
}
