/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import static org.junit.Assert.*;

import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.apputil.test.TestProperties;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.junit.Test;

/** JUnit test of the {@link SeverityReader}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SeverityMessageReaderUnitTest
{
    @Test
    public void testSeverityReader() throws Exception
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

        // Create reader
        final RDBUtil rdb = RDBUtil.connect(rdb_url, rdb_user, rdb_password, false);
        final SQL sql = new SQL(rdb, "ALARM");
        final SeverityReader severities = new SeverityReader(rdb, sql);
        rdb.close();

        for (SeverityLevel level : SeverityLevel.values())
        {
            int id = severities.getID(level);
            System.out.println("ID for " + level + ": " + id);
            assertEquals(level, severities.getSeverity(id));
        }
    }

    @Test
    public void testMessageReader() throws Exception
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

        // Create reader
        final RDBUtil rdb = RDBUtil.connect(rdb_url, rdb_user, rdb_password, false);
        final SQL sql = new SQL(rdb, "ALARM");
        final MessageReader messages = new MessageReader(rdb, sql);
        rdb.close();

        final String message = "HIHI_ALARM";
        int id = messages.getID(message);
            System.out.println("ID for " + message + ": " + id);
        assertEquals(message, messages.getMessage(id));
    }
}
