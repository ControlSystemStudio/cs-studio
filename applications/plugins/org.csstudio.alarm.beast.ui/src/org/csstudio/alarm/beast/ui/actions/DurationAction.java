/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that displays the duration of an alarm.
 *  @author Kay Kasemir
 */
public class DurationAction extends Action
{
    final private Shell shell;
    final private AlarmTreeLeaf pv;

    /** Initialize
     *  @param shell Shell to use for displayed dialog
     *  @param pv Alarm PV
     */
    public DurationAction(final Shell shell, final AlarmTreeLeaf pv)
    {
        this.shell = shell;
        this.pv = pv;
        setText(pv.getDuration());
        setImageDescriptor(Activator.getImageDescriptor("icons/clock.png")); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        MessageDialog.openInformation(shell, Messages.Duration,
                NLS.bind(Messages.DurationMsgFmt,
                         new Object[]
                         {
                            pv.getDescription(),
                            pv.getTimestampText(),
                            pv.getDuration()
                         }));
    }
}
