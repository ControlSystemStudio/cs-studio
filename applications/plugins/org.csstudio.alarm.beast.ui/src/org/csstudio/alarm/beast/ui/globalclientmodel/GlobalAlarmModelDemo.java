/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the {@link GlobalAlarmModel}
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GlobalAlarmModelDemo implements GlobalAlarmModelListener
{
    // GlobalAlarmModelListener
    @Override
    public void globalAlarmsChanged(final GlobalAlarmModel model)
    {
        System.out.println("\nGlobal alarms:");
        final GlobalAlarm alarms[] = model.getAlarms();
        for (GlobalAlarm alarm : alarms)
            System.out.println(alarm);
    }

    @Test
    public void testGlobalClientModel() throws Exception
    {
        final GlobalAlarmModel model = new GlobalAlarmModel(this);
        model.readConfiguration(new NullProgressMonitor());

        System.out.println("Model is running ....");
        while (true)
        {
            Thread.sleep(5000);
        }
    }
}
