/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.eclipse.swt.widgets.Shell;

/** Action that enables a PV or other Component and all of its children from the configuration.
 *  @author Kay Kasemir
 *  @author Xinyu Wu
 */
public class EnableComponentAction extends DisableComponentAction
{
    /** Initialize action
     *  @param shell Shell
     *  @param model Alarm model
     *  @param items Items to enable
     */
    public EnableComponentAction(final Shell shell, final AlarmClientModel model,
            final List<AlarmTreeItem> items)
    {
        super(shell, model, items);
    }

    @Override
    protected boolean doEnable()
    {
        return true;
    }
}
