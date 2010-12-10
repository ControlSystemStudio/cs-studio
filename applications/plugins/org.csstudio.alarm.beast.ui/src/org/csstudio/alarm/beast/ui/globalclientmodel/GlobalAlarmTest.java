/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import static org.junit.Assert.*;

import org.csstudio.alarm.beast.AlarmTreeItem;
import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.AlarmTreeRoot;
import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.apputil.test.TestProperties;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.junit.Test;

@SuppressWarnings("nls")
public class GlobalAlarmTest
{
    // TODO combine with similar code that'll be needed in the model
    // to create alarm based on partially available root, area, ...
    private GlobalAlarm createAlarmFromPath(final String full_path,
            final SeverityLevel severity, final String message,
            final ITimestamp timestamp)
    {
        final String path[] = AlarmTreePath.splitPath(full_path);

        AlarmTreeItem parent = null;
        for (int i=0; i<path.length-1; ++i)
        {
            if (i == 0)
                parent = new AlarmTreeRoot(path[i], -1);
            else
                parent = new AlarmTreeItem(parent, path[i], -1);
        }
        return new GlobalAlarm(parent, path[path.length-1], -1,
                severity, message, timestamp);
    }

    @Test
    public void testGlobalAlarm() throws Exception
    {
        GlobalAlarm alarm = createAlarmFromPath("/Root/Area/System/TheAlarm",
                SeverityLevel.MAJOR, "Demo", TimestampFactory.now());
        alarm.getRoot().dump(System.out);
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
        final GlobalAlarm alarm = createAlarmFromPath(full_path,
                SeverityLevel.MAJOR, "Demo", TimestampFactory.now());

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
        final RDBUtil rdb = RDBUtil.connect(rdb_url, rdb_user, rdb_password, false);
        final SQL sql = new SQL(rdb);
        try
        {
            alarm.completeGuiInfo(rdb, sql);
        }
        finally
        {
            rdb.close();
        }
        alarm.getClientRoot().dump(System.out);

        assertTrue(alarm.getID() >= 0);
        assertTrue(alarm.getGuidance().length > 0);
    }
}
