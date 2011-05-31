/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the {@link GlobalAlarmReader}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GlobalAlarmReaderDemo
{
    @Test
    public void testGlobalAlarmReadout() throws Exception
    {
        final GlobalAlarmReader reader = new GlobalAlarmReader();
        System.out.println("Currently active global alarms:");
        final List<AlarmTreeRoot> alarms = reader.readGlobalAlarms();
        for (AlarmTreeRoot root : alarms)
        {
            root.dump(System.out);
        }
        reader.close();
    }
}
