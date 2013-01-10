/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.areapanel;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.alarmtree.AlarmTreeView;
import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.eclipse.ui.IViewPart;

/** Action that displays the alarm tree for an item
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ShowInAlarmTreeAction extends OpenViewAction
{
	final private AlarmTreeItem item;

	public ShowInAlarmTreeAction(final AlarmTreeItem item)
    {
		super(AlarmTreeView.ID,
		      Messages.ShowInAlarmTree,
			  org.csstudio.alarm.beast.ui.alarmtree.Activator.getImageDescriptor("icons/alarmtree.gif"));
		this.item = item;
    }

    @Override
	public void run()
	{
        final IViewPart view = doShowView();
        if (view instanceof AlarmTreeView)
        {
            final AlarmTreeView alarm_view = (AlarmTreeView) view;
            alarm_view.setFocus(item);
        }
 	}
}
