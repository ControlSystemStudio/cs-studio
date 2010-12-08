/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.apputil.test.TestProperties;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.junit.Test;

@SuppressWarnings("nls")
public class GlobalAlarmTest
{
    @Test
    public void testGlobalAlarm() throws Exception
    {
        // Get test settings, abort if incomplete
        final TestProperties settings = new TestProperties();
        final String rdb_url = settings.getString("alarm_rdb_url");
        final String rdb_user = settings.getString("alarm_rdb_user");
        final String rdb_password = settings.getString("alarm_rdb_password");
        if (rdb_url == null)
        {
            System.out.println("Need test RDB URL, skipping test");
            return;
        }
        final String path = settings.getString("alarm_test_path");
        if (path == null)
        {
            System.out.println("Need test path, skipping test");
            return;
        }

        final RDBUtil rdb = RDBUtil.connect(rdb_url, rdb_user, rdb_password, false);

        final GlobalAlarm alarm = new GlobalAlarm(path, SeverityLevel.MAJOR, "Demo", TimestampFactory.now());

        alarm.readInformation(rdb);
        System.out.println(alarm);

        rdb.close();
    }
}
