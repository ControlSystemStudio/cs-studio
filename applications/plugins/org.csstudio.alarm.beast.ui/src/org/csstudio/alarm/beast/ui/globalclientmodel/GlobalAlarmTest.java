/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import org.csstudio.alarm.beast.AlarmTreeItem;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.platform.data.TimestampFactory;
import org.junit.Test;

@SuppressWarnings("nls")
public class GlobalAlarmTest
{
    @Test
    public void testGlobalAlarm() throws Exception
    {
        final AlarmTreeItem root = new AlarmTreeItem(null, "Root", 1);
        final AlarmTreeItem area = new AlarmTreeItem(root, "Area", 2);
        final AlarmTreeItem system = new AlarmTreeItem(area, "System", 3);
        GlobalAlarm alarm = new GlobalAlarm(system, "TheAlarm", 4,
                SeverityLevel.MAJOR, "Demo", TimestampFactory.now());
        root.dump(System.out);
    }
}
