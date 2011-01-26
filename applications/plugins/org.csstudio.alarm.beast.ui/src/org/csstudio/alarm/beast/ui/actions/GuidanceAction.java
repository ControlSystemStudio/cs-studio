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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/** Action that displays guidance.
 *  @author Kay Kasemir, Xihui Chen
 */
public class GuidanceAction extends Action
{
    final private Shell shell;
    final private GDCDataStructure guidance;

    /** Initialize
     *  @param shell Shell to use for displayed dialog
     *  @param tree_position Origin of this guidance in alarm tree
     *  @param guidance Guidance message
     */
    public GuidanceAction(final Shell shell,
            final AlarmTreePosition tree_position,
            final GDCDataStructure guidance)
    {
        this.shell = shell;
        this.guidance = guidance;
        setText(guidance.getTeaser());
        setImageDescriptor(AlarmTreeActionIcon.createIcon("icons/info.gif", //$NON-NLS-1$
                                                            tree_position));
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        MessageDialog.openInformation(shell, getText(), guidance.getDetails());
    }
}
