/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.ui;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.csstudio.logging.LogFormatter;
import org.csstudio.logging.Preferences;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/** Log handler that displays messages in the Eclipse Console view.
 *  @author Kay Kasemir
 *  @author Alexander Will - Author of org.csstudio.platform.ui.internal.console.Console
 */
public class ConsoleViewHandler extends Handler
{
    final private MessageConsole console;
    private MessageConsoleStream stream;

    /** Add console view to the (root) logger.
     *
     *  To be called from Eclipse application's
     *  <code>WorkbenchWindowAdvisor.postWindowCreate()</code>.
     *  Calling it earlier is not possible because the necessary
     *  console view infrastructure is not available, yet.
     *
     *  Calling it much later means log messages are lost.
     */
    public static void addToLogger()
    {
        final ConsoleViewHandler handler = new ConsoleViewHandler();
        try
        {
            handler.setFormatter(new LogFormatter(Preferences.getDetail()));
        }
        catch (Throwable ex)
        {
            Logger.getLogger(ConsoleViewHandler.class.getName())
                .log(Level.WARNING, "Cannot configure console view: {0}", ex.getMessage()); //$NON-NLS-1$
            return;
        }
        Logger.getLogger("").addHandler(handler); //$NON-NLS-1$
    }

    /** Initialize, hook into console view
     *
     *  Private to prevent multiple instances
     *  @see #addToLogger()
     */
    private ConsoleViewHandler()
    {
        console = new MessageConsole(Messages.ConsoleView_Title, null);
        // Values are from https://bugs.eclipse.org/bugs/show_bug.cgi?id=46871#c5
        console.setWaterMarks(80000, 100000);

        stream = console.newMessageStream();

        final ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
        consolePlugin.getConsoleManager().addConsoles(
                new IConsole[] { console });
    }

    @Override
    public void publish(final LogRecord record)
    {
        if (! isLoggable(record))
            return;

        final String message = getFormatter().format(record);
        stream.print(message);
    }

    @Override
    public void flush()
    {
        try
        {
            stream.flush();
        }
        catch (IOException e)
        {
            // Ignore
        }
    }

    @Override
    public void close() throws SecurityException
    {
        console.clearConsole();
    }
}
