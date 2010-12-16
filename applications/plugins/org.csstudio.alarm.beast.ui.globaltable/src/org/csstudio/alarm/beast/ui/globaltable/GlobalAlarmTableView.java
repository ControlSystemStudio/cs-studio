/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globaltable;

import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarmModel;
import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarmModelListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/** Eclipse 'View' for global alarms
 *  @author Kay Kasemir
 */
public class GlobalAlarmTableView extends ViewPart
{
    /** View ID defined in plugin.xml */

    final public static String ID = "org.csstudio.alarm.beast.ui.globaltable.view"; //$NON-NLS-1$

    @Override
    public void createPartControl(final Composite parent)
    {
        Label l = new Label(parent, 0);
        l.setText("TODO: Display in table instead of console dump");

        final GlobalAlarmModel model = GlobalAlarmModel.reference();

        model.addListener(new GlobalAlarmModelListener()
        {
            @Override
            public void globalAlarmsChanged(final GlobalAlarmModel model)
            {
                // TODO Auto-generated method stub
                System.out.println("Global Alarms:");
                final AlarmTreeLeaf alarms[] = model.getAlarms();
                for (AlarmTreeLeaf alarm : alarms)
                    System.out.println(alarm);
            }
        });

        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                model.release();
            }
        });
    }

    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub
    }
}
