/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.ui.CommandExecutorThread;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Base class for Action that executes something
 *  (command, related display, ...).
 *  <p>
 *  When invoked, the command gets executed in the 'command' directory,
 *  configured via preferences.
 *  <p>
 *  The executed command gets written to the console view.
 *  Errors are shown as popup dialog.
 *
 *  @author Kay Kasemir
 */
abstract public class AbstractExecuteAction extends Action
{
    final private static String CONSOLE_NAME = "Alarm Actions"; //$NON-NLS-1$
    final protected Shell shell;
    final protected String command;

    /** CommandExecutorThread for the command, using the wait time
     *  from preferences, displaying errors as dialog.
     *
     *  JProfiler shows that this gets removed by the GC,
     *  but a java.lang.UnixProcess for the external process
     *  remains until the external program exits.
     */
    private class ExecuteActionThread extends CommandExecutorThread
    {
        public ExecuteActionThread(final String dir)
        {
            super(command, dir, Preferences.getCommandCheckTime());
        }

        @Override
        public void error(final int exit_code, final String stderr)
        {
            shell.getDisplay().asyncExec(new Runnable()
            {
                @Override
                public void run()
                {
                    if (shell.isDisposed())
                        return;
                    MessageDialog.openError(shell,
                        Messages.CommandError,
                        NLS.bind(Messages.CommandErrorFmt,
                                 new Object[]{ command, exit_code, stderr}));
                }
            });
        }
    }

    /** Initialize
     *  @param shell Shell to use for error messages etc.
     *  @param icon_name Name of icon
     *  @param label Label to use for action
     *  @param command Command description
     */
    public AbstractExecuteAction(final Shell shell,
            final ImageDescriptor icon,
            final String label, final String command)
    {
        this.shell = shell;
        this.command = command;
        setText(label);
        setImageDescriptor(icon);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public void run()
    {
        final String dir;
        try
        {
            dir = Preferences.getCommandDirectory();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell,
                Messages.CommandError,
                NLS.bind(Messages.CommandErrorFmt,
                         new Object[] { command, "-", ex.getMessage()}));
            return;
        }

        SingleSourcePlugin.getUIHelper().writeToConsole(CONSOLE_NAME,
                getImageDescriptor(),
                getText() + ": (" + dir + ") '" + command + "'");

        new ExecuteActionThread(dir).start();
    }
}
