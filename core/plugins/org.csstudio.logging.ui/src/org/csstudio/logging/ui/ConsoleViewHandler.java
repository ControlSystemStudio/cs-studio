/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.ui;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.csstudio.logging.LogFormatter;
import org.csstudio.logging.Preferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/** Log handler that displays messages in the Eclipse Console view.
 *  @author Kay Kasemir
 *  @author Alexander Will - Author of org.csstudio.platform.ui.internal.console.Console that was used with Log4j
 */
public class ConsoleViewHandler extends Handler
{
    /** Flag to check for multiple instances */
    private static boolean have_console = false;

    /** Connection to Console View */
    final private MessageConsole console;

    /** Printable, color-coded stream of the <code>console</code> */
    final private MessageConsoleStream severe_stream, warning_stream, info_stream, basic_stream;

    /** Add console view to the (root) logger.
     *  <p>
     *  To be called from Eclipse application's
     *  <code>WorkbenchWindowAdvisor.postWindowCreate()</code>.
     *  Calling it earlier is not possible because the necessary
     *  console view infrastructure is not available, yet.
     *  <p>
     *  Calling it much later means log messages are lost.
     *  <p>
     *  There is no good reason to add more than one ConsoleViewHandler.
     *  Could implement this as a singleton work around accidental addition
     *  of multiple ConsoleViewHandlers, but using IllegalStateException
     *  to help expose such errors in the application logic.
     *
     *  @throws IllegalStateException when called more than once
     */
    @SuppressWarnings("nls")
    public static synchronized void addToLogger()
    {
        if (have_console)
            throw new IllegalStateException("ConsoleViewHandler has already been added to root logger");
        final ConsoleViewHandler handler = new ConsoleViewHandler();
        try
        {
            handler.setFormatter(new LogFormatter(Preferences.getDetail()));
        }
        catch (Throwable ex)
        {
            Logger.getLogger(ConsoleViewHandler.class.getName())
                .log(Level.WARNING, "Cannot configure console view: {0}", ex.getMessage());
            return;
        }
        Logger.getLogger("").addHandler(handler);
        have_console = true;
    }

    /** Initialize, hook into console view
     *
     *  Private to prevent multiple instances
     *  @see #addToLogger()
     */
    private ConsoleViewHandler()
    {
        // Allocate a console for text messages.
        console = new MessageConsole(Messages.ConsoleView_Title, null);
        // Values are from https://bugs.eclipse.org/bugs/show_bug.cgi?id=46871#c5
        console.setWaterMarks(80000, 100000);

        // Add to the 'Console' View
        final ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
        consolePlugin.getConsoleManager().addConsoles(
                new IConsole[] { console });

        // Route StreamHandler's output to the MessageConsole.
        // Could this deadlock because StreamHandler.publish() is
        // synchronized, and the final ConsoleView update is on the
        // one and only GUI thread?
        //
        // According to MessageConsole javadoc,
        // "Text written to streams is buffered and processed in a Job",
        // so we assume it's decoupled by the MessageConsole buffer & Job
        severe_stream = console.newMessageStream();
        warning_stream = console.newMessageStream();
        info_stream = console.newMessageStream();
        basic_stream = console.newMessageStream();

        // Setting the color of a stream while it's in use is hard:
        // Has to happen on SWT UI thread, and changes will randomly
        // affect only the next message or the whole Console View.
        // Using different streams for the color-coded message levels
        // seem to work OK.
        final Display display = Display.getCurrent();
        if (display != null)
        {
            severe_stream.setColor(display.getSystemColor(SWT.COLOR_MAGENTA));
            warning_stream.setColor(display.getSystemColor(SWT.COLOR_RED));
            info_stream.setColor(display.getSystemColor(SWT.COLOR_BLUE));
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void publish(final LogRecord record)
    {
        if (! isLoggable(record))
            return;

        final String msg;
        try
        {
            msg = getFormatter().format(record);
        }
        catch (Exception ex)
        {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }

        try
        {
            // Change of color must happen on GUI thread
            getStream(record.getLevel()).print(msg);
        }
        catch (Exception ex)
        {
            reportError(null, ex, ErrorManager.WRITE_FAILURE);
            return;
        }
    }

    /** @param level Message {@link Level}
     *  @return Suggested stream for that Level
     */
    private MessageConsoleStream getStream(final Level level)
    {
        if (level.intValue() >= Level.SEVERE.intValue())
            return severe_stream;
        else if (level.intValue() >= Level.WARNING.intValue())
            return warning_stream;
        else if (level.intValue() >= Level.INFO.intValue())
            return info_stream;
        else
            return basic_stream;
    }

    /** {@inheritDoc} */
    @Override
    public void flush()
    {
        try
        {
            severe_stream.flush();
            warning_stream.flush();
            info_stream.flush();
            basic_stream.flush();
        }
        catch (Exception ex)
        {
            reportError(null, ex, ErrorManager.FLUSH_FAILURE);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws SecurityException
    {
        // Remove from 'Console' view
        console.clearConsole();
        final ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
        consolePlugin.getConsoleManager().removeConsoles(
                new IConsole[] { console });

        // With Eclipse 3.6.1, when Eclipse is shutting down,
        // closing the stream will cause org.eclipse.ui.internal.console.IOConsolePartitioner
        // to start a shutdown Job, which fails because the JobManager
        // is already down...
        try
        {
             severe_stream.close();
             warning_stream.close();
             info_stream.close();
             basic_stream.close();
        }
        catch (Exception ex)
        {   // Ignore errors because we're shutting down anyway
            ex = null;
        }
    }
}
