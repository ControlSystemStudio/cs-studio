/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import static org.junit.Assert.*;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.apputil.test.TestProperties;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.platform.data.TimestampFactory;
import org.junit.Test;

/** JUnit test of the {@link GlobalAlarm}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GlobalAlarmTest implements ReadInfoJobListener
{
    @Test
    public void testGlobalAlarm() throws Exception
    {
        final GlobalAlarm alarm = GlobalAlarm.fromPath("/Root/Area/System/TheAlarm",
                SeverityLevel.MAJOR, "Demo", TimestampFactory.now());
        alarm.getRoot().dump(System.out);
    }

    private boolean received_rdb_info = false;

    // ReadInfoJobListener
    @Override
    public void receivedAlarmInfo(GlobalAlarm alarm)
    {
        synchronized (this)
        {
            received_rdb_info = true;
            notifyAll();
        }
    }

    @Test
    public void testGlobalAlarmCompletionFromRDB() throws Exception
    {
        // Get test settings, abort if incomplete
        final TestProperties settings = new TestProperties();

        // Create global alarm for some path
        final String full_path = settings.getString("alarm_test_path");
        if (full_path == null)
        {
            System.out.println("Need test path, skipping test");
            return;
        }
        final GlobalAlarm alarm = GlobalAlarm.fromPath(full_path,
                SeverityLevel.MAJOR, "Demo", TimestampFactory.now());
        // It lacks ID, guidance etc.
        assertEquals(-1, alarm.getID());
        assertEquals(0, alarm.getGuidance().length);

        // Complete the guidance etc. from RDB
        final String rdb_url = settings.getString("alarm_rdb_url");
        final String rdb_user = settings.getString("alarm_rdb_user");
        final String rdb_password = settings.getString("alarm_rdb_password");
        if (rdb_url == null)
        {
            System.out.println("Need test RDB URL, skipping test");
            return;
        }

        System.out.println("Before reading GUI info:");
        alarm.getClientRoot().dump(System.out);

        // Read RDB info in background job
        BenchmarkTimer timer = new BenchmarkTimer();
        new ReadInfoJob(rdb_url, rdb_user, rdb_password, alarm, this).schedule();
        synchronized (this)
        {
            for (int i=0; !received_rdb_info  &&  i<10; ++i)
                wait(1000);
            assertTrue(received_rdb_info);
        }
        timer.stop();
        System.out.println("After reading GUI info (" + timer.toString() + "):");
        alarm.getClientRoot().dump(System.out);

        assertTrue(alarm.getID() >= 0);
        assertTrue(alarm.getGuidance().length > 0);
    }
}
