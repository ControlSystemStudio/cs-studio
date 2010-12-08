/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.junit.Assert.*;

import java.util.Date;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.platform.data.TimestampFactory;
import org.junit.Test;

/** JUnit test of the {@link DelayedAlarmUpdate}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DelayedAlarmUpdateUnitTest implements DelayedAlarmListener
{
    private AlarmState delayed_state = null;

    // DelayedAlarmListener
    public void delayedStateUpdate(AlarmState delayed_state)
    {
        System.out.println(new Date() + ": Received update!");
        synchronized (this)
        {
            this.delayed_state = delayed_state;
            notifyAll();
        }
    }

    @Test
    public void testDelayedAlarmUpdate() throws Exception
    {
        final DelayedAlarmUpdate delay = new DelayedAlarmUpdate(this);
        final AlarmState state = new AlarmState(SeverityLevel.MAJOR, "Test", null, TimestampFactory.now());
        System.out.println(new Date() + ": Scheduling delayed alarm update ...");
        delay.schedule_update(state, 2);

        assertEquals(state, delay.getState());
        // Expect nothing right away
        synchronized (this)
        {
            assertNull(delayed_state);
        }

        // .. but after ~2 seconds, the update should arrive
        synchronized (this)
        {
            for (int secs=0; delayed_state == null  &&  secs<4; ++secs)
                wait(1000);
            assertEquals(state, delayed_state);
        }

        // Delay should be 'idle', no pending state
        assertNull(delay.getState());
    }
}
