/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.apputil.ui.elog.ElogDialog;
import org.csstudio.apputil.ui.elog.SendToElogActionHelper;
import org.csstudio.logbook.ILogbook;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that sends selected alarms to elog
 *  @author Kay Kasemir
 */
public class SendToElogAction extends SendToElogActionHelper
{
    final private Shell shell;
    final private List<AlarmTreeLeaf> alarms;

    /** Initialize action
     *  @param alarms Alarms to acknowledge when action runs
     */
    public SendToElogAction(final Shell shell, final List<AlarmTreeLeaf> alarms)
    {
        this.shell = shell;
        this.alarms = alarms;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
    	final String alarm_info = AlarmTextHelper.createAlarmInfoText(alarms);
        try
        {
            final ElogDialog dialog = new ElogDialog(shell,
                    Messages.SendToElogAction_Message,
                    Messages.SendToElogAction_InitialTitle,
                    alarm_info, null)
            {
                @Override
                public void makeElogEntry(final String logbook_name,
                        final String user, final String password,
                        final String title, final String body, final String images[]) throws Exception
                {
                    final ILogbook logbook = getLogbook_factory()
                                    .connect(logbook_name, user, password);
                    try
                    {
                        logbook.createEntry(title, body, images);
                    }
                    finally
                    {
                        logbook.close();
                    }
                }
            };
            dialog.open();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, Messages.Error,
                    NLS.bind(Messages.SendToElogAction_ErrorFmt, ex.getMessage()));
        }
    }
}
