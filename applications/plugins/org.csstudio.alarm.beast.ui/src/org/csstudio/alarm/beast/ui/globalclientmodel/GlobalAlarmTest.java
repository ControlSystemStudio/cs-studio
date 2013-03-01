/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.apputil.test.TestProperties;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.epics.util.time.Timestamp;
import org.junit.Test;

/** JUnit test of the {@link GlobalAlarm}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GlobalAlarmTest implements ReadInfoJobListener
{
    final List<AlarmTreeRoot> configurations = new ArrayList<AlarmTreeRoot>();

    @Test
    public void testGlobalAlarm() throws Exception
    {
        // Create completely new alarm
        configurations.clear();
        final GlobalAlarm alarm = GlobalAlarm.fromPath(configurations,
                "/Root/Area/System/TheAlarm",
                SeverityLevel.MAJOR, "Demo", Timestamp.now());
        final AlarmTreeRoot root = alarm.getRoot();
        root.dump(System.out);
        // New root should be in list of configurations
        assertEquals(1, configurations.size());
        assertEquals(root, configurations.get(0));

        // Create another alarm with same path
        final GlobalAlarm alarm2 = GlobalAlarm.fromPath(configurations,
                "/Root/Area/System/OtherAlarm",
                SeverityLevel.MAJOR, "Demo", Timestamp.now());
        root.dump(System.out);
        assertSame(root, alarm2.getRoot());
        assertSame(alarm.getParent(), alarm2.getParent());

        // Update existing alarm
        final GlobalAlarm alarm_copy = GlobalAlarm.fromPath(configurations,
                "/Root/Area/System/TheAlarm",
                SeverityLevel.MAJOR, "Demo2", Timestamp.now());
        // Locates existing alarm, changes its alarm message
        assertSame(alarm, alarm_copy);
        assertEquals("Demo2", alarm.getMessage());
    }

    private int received_rdb_info = 0;

    // ReadInfoJobListener
    @Override
    public void receivedAlarmInfo(GlobalAlarm alarm)
    {
        synchronized (this)
        {
            ++received_rdb_info;
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
        final GlobalAlarm alarm = GlobalAlarm.fromPath(configurations, full_path,
                SeverityLevel.MAJOR, "Demo", Timestamp.now());
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
        alarm.getRoot().dump(System.out);

        // Read RDB info in background job
        BenchmarkTimer timer = new BenchmarkTimer();
        synchronized (this)
        {
            received_rdb_info = 0;
            new ReadInfoJob(rdb_url, rdb_user, rdb_password, "ALARM", alarm, this).schedule();
            for (int i=0; received_rdb_info == 0  &&  i<10; ++i)
                wait(1000);
            assertEquals(1, received_rdb_info);
        }
        timer.stop();
        System.out.println("After reading GUI info (" + timer.toString() + "):");
        alarm.getRoot().dump(System.out);

        assertTrue(alarm.getID() >= 0);
        assertTrue(alarm.getGuidance().length > 0);
    }

    @Test
    public void testConcurrentAlarmCompletionFromRDB() throws Exception
    {
        // Get test settings, abort if incomplete
        final TestProperties settings = new TestProperties();

        // Create global alarm for some path
        final String full_path = settings.getString("alarm_test_path");
        final String full_path2 = settings.getString("alarm_test_path2");
        final String rdb_url = settings.getString("alarm_rdb_url");
        final String rdb_user = settings.getString("alarm_rdb_user");
        final String rdb_password = settings.getString("alarm_rdb_password");
        if (rdb_url == null)
        {
            System.out.println("Need test RDB URL, skipping test");
            return;
        }
        if (full_path == null  ||  full_path2 == null)
        {
            System.out.println("Need two test paths, skipping test");
            return;
        }
        // Start with 2 'global' alarms
        final GlobalAlarm alarm1 = GlobalAlarm.fromPath(configurations, full_path,
                SeverityLevel.MAJOR, "Demo", Timestamp.now());
        final GlobalAlarm alarm2 = GlobalAlarm.fromPath(configurations, full_path2,
                SeverityLevel.MAJOR, "Demo", Timestamp.now());
        // Same root, no detail from RDB, yet
        final AlarmTreeRoot root = alarm1.getRoot();
        assertNotSame(alarm1, alarm2);
        assertSame(root, alarm2.getRoot());
        assertEquals(-1, root.getID());

        // Read RDB info for both in background jobs
        synchronized (this)
        {
            received_rdb_info = 0;
            final ReadInfoJob job1 = new ReadInfoJob(rdb_url, rdb_user, rdb_password, "ALARM", alarm1, this);
            final ReadInfoJob job2 = new ReadInfoJob(rdb_url, rdb_user, rdb_password, "ALARM", alarm2, this);
            job1.schedule();
            job2.schedule();
            for (int i=0; received_rdb_info != 2 &&  i<10; ++i)
                wait(1000);
            assertEquals(2, received_rdb_info);
        }
        root.dump(System.out);

        assertTrue(root.getID() >= 0);
        assertTrue(alarm1.getID() >= 0);
        assertTrue(alarm1.getGuidance().length > 0);
    }
}
