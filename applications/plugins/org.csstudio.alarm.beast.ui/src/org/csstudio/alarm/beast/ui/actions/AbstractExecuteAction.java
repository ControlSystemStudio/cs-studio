/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.io.IOException;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.ui.CommandExecutorThread;
import org.csstudio.alarm.beast.ui.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

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

        final MessageConsoleStream console_out = getConsole().newMessageStream();
        console_out.println(getText() + ": (" + dir + ") '" + command + "'");
        try
        {
            console_out.close();
        }
        catch (IOException e)
        {
            // Ignored
        }

        new ExecuteActionThread(dir).start();
    }

    /** Get a console in the Eclipse Console View for dumping the output
     *  of invoked alarm actions.
     *  <p>
     *  Code based on
     *  http://wiki.eclipse.org/FAQ_How_do_I_write_to_the_console_from_a_plug-in%3F
     *
     *  @return MessageConsole, newly created or one that already existed.
     */
    private MessageConsole getConsole()
    {
        final ConsolePlugin plugin = ConsolePlugin.getDefault();
        final IConsoleManager manager = plugin.getConsoleManager();
        final IConsole[] consoles = manager.getConsoles();
        for (int i = 0; i < consoles.length; i++)
           if (CONSOLE_NAME.equals(consoles[i].getName()))
              return (MessageConsole) consoles[i];
        //no console found, so create a new one
        final MessageConsole myConsole =
            new MessageConsole(CONSOLE_NAME, this.getImageDescriptor());
        // There is no default console buffer limit in chars or lines?
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=46871
        // 2k char limit, keep 1k
        myConsole.setWaterMarks(1024, 2048);
        manager.addConsoles(new IConsole[]{myConsole});
        return myConsole;
    }
}
