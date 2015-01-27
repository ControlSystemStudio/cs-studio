/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtree;

import org.eclipse.jface.action.Action;

/** Toolbar action that selects if we only show current alarms or all entries
 *  @author Kay Kasemir
 */
public class OnlyAlarmsAction extends Action
{
    final private GUI gui;

    public OnlyAlarmsAction(final GUI gui)
    {
        super(Messages.OnlyAlarms, AS_CHECK_BOX);
        setImageDescriptor(Activator.getImageDescriptor("icons/only_alarms.gif")); //$NON-NLS-1$
        setToolTipText(Messages.OnlyAlarmsTT);
        this.gui = gui;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        gui.setAlarmDisplayMode(isChecked());
    }
}
