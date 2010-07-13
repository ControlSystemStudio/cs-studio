/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtree;

import org.eclipse.jface.action.Action;

/** Toolbar action that collapses the alarm tree
 *  @author Kay Kasemir
 */
public class CollapseAlarmTreeAction extends Action
{
    final private GUI gui;

    public CollapseAlarmTreeAction(final GUI gui)
    {
        super(Messages.Collapse, Activator.getImageDescriptor("icons/collapse.gif")); //$NON-NLS-1$
        setToolTipText(Messages.CollapseTT);
        this.gui = gui;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        gui.collapse();
    }
}
