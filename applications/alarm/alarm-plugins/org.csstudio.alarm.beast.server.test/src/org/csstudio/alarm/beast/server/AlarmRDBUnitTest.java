/*******************************************************************************
 * Copyright (c) 2010-2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import org.csstudio.apputil.test.TestProperties;
import org.junit.Test;

/** JUnit test of the alarm configuration reader
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmRDBUnitTest
{
    @Test
    public void readAlarmConfiguration() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String alarm_root = settings.getString("alarm_root");
        if (alarm_root == null)
        {
            System.out.println("Skipped, no configuration.");
            return;
        }

        final AlarmRDB rdb = new AlarmRDB(null,
                settings.getString("alarm_rdb_url"),
                settings.getString("alarm_rdb_user"),
                settings.getString("alarm_rdb_password"),
                "ALARM",
                alarm_root);
        final ServerTreeItem root = rdb.readConfiguration();
        root.dump(System.out);
    }
}
