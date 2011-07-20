/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import static org.junit.Assert.*;

import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.SQL;
import org.csstudio.apputil.test.TestProperties;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.junit.Test;

/** JUnit test of the {@link AlarmConfigurationReader}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmConfigurationReaderUnitTest
{
    @Test
    public void testAlarmConfigurationReader() throws Exception
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
        final String full_path = settings.getString("alarm_test_path");
        if (full_path == null)
        {
            System.out.println("Need test path, skipping test");
            return;
        }
        final String path[] = AlarmTreePath.splitPath(full_path);

        // Create reader
        final RDBUtil rdb = RDBUtil.connect(rdb_url, rdb_user, rdb_password, false);
        final SQL sql = new SQL(rdb, "ALARM");
        final AlarmConfigurationReader reader = new AlarmConfigurationReader(rdb, sql);
        final SeverityReader severities = new SeverityReader(rdb, sql);
        final MessageReader messages = new MessageReader(rdb, sql);

        // Get root
        final AlarmTreeRoot root = reader.readRoot(path[0]);
        System.out.println("Root: " + root);
        int id = reader.readRootID(path[0]);
        assertEquals(root.getID(), id);

        // Complete guidance, displays, commands
        reader.readGuidanceDisplaysCommands(root);

        // Read remaining path elements
        AlarmTreeItem parent = root;
        for (int i=1; i<path.length; ++i)
        {
            parent = reader.readItem(path[i], parent, severities, messages);
            reader.readGuidanceDisplaysCommands(parent);
        }

        reader.closeStatements();

        System.out.println("Sub-tree to item:");
        root.dump(System.out);
    }
}
