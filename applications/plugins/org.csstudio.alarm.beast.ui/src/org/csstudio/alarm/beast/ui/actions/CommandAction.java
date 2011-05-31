/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.csstudio.alarm.beast.client.GDCDataStructure;
import org.csstudio.alarm.beast.ui.AlarmTreeActionIcon;
import org.eclipse.swt.widgets.Shell;

/** Action that executes some command (reset, open valve, ...).
 *  @author Kay Kasemir, Xihui Chen
 */
public class CommandAction extends AbstractExecuteAction
{
    /** Initialize
     *  @param shell Shell to use for displayed dialog
     *  @param tree_position Origin of this command in alarm tree
     *  @param command Command description
     */
    public CommandAction(final Shell shell, 
            final AlarmTreePosition tree_position,
            final GDCDataStructure command)
    {
        super(shell, 
              AlarmTreeActionIcon.createIcon("icons/command.gif",  //$NON-NLS-1$
                                               tree_position),
              command.getTeaser(),
              command.getDetails());
    }
}
