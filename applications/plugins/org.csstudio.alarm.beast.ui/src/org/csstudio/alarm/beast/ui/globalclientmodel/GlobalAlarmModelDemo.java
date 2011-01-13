/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the {@link GlobalAlarmModel}
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GlobalAlarmModelDemo implements GlobalAlarmModelListener
{
    final Timer delayed_update = new Timer();

    // GlobalAlarmModelListener
    @Override
    public void globalAlarmsChanged(final GlobalAlarmModel model)
    {
        System.out.println("\nGlobal alarms:");
        final AlarmTreeRoot trees[] = model.getAlarmRoots();
        final AlarmTreeLeaf alarms[] = model.getAlarms();
        for (AlarmTreeLeaf alarm : alarms)
            System.out.println(alarm.getPathName() + ", Description: " + alarm.getDescription());

        // Delayed printout that would (usually) have GUI detail
        delayed_update.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                for (AlarmTreeRoot root : trees)
                    root.dump(System.out);
            }
        }, 3000);
    }

    @Test
    public void testGlobalClientModel() throws Exception
    {
        final GlobalAlarmModel model = GlobalAlarmModel.reference();
        model.addListener(this);

        System.out.println("Model is running ....");
        while (true)
        {
            Thread.sleep(5000);
        }
    }
}
