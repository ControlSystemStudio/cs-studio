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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/** Log handler that displays messages in the Eclipse Console view.
 *
 *  <p>The {@link MessageConsoleStream} description mentions buffering
 *  and appears thread-safe, but lockups have been observed
 *  if multiple threads try to access the console streams.
 *  This handler therefore logs in the UI thread, using the {@link Display}
 *  that was available on initialization.
 *  This is suitable for RCP, but not RAP.
 *
 *  @author Kay Kasemir
 *  @author Alexander Will - Author of org.csstudio.platform.ui.internal.console.Console that was used with Log4j
 */
public class ConsoleViewHandler extends Handler
{
    /** Flag to prevent multiple instances */
    private static boolean have_console = false;

    /** Display used for performing console access in the UI thread */
    final Display display;

    /** Connection to Console View.
     *  Set to <code>null</code> when console support shuts down
     */
    private volatile MessageConsole console;

    /** Printable, color-coded stream of the <code>console</code> */
    final private MessageConsoleStream severe_stream, warning_stream, info_stream, basic_stream;

    private Color severeColor, warningColor, infoColor, basicColor;

    /** Add console view to the (root) logger.
     *  <p>
     *  To be called from Eclipse application's
     *  <code>WorkbenchWindowAdvisor.postWindowCreate()</code>.
     *  Calling it earlier is not possible because the necessary
     *  console view infrastructure is not available, yet.
     *  <p>
     *  Calling it much later means log messages are lost.
     *  <p>
     *  Only the first call has an effect.
     *  Subsequent calls as they can happen when opening multiple windows
     *  of the same Eclipse instance will have no effect.
     */
    @SuppressWarnings("nls")
    public static synchronized void addToLogger()
    {
        if (have_console)
            return;

        try
        {
            // RAP has one (pseudo-)display per web client.
            // Cannot obtain one shared display as on RCP.
            if (SWT.getPlatform().startsWith("rap"))
                throw new Exception("Console logging not supported on RAP");

            final Display display = Display.getCurrent();
            if (display == null)
                throw new Exception("No display");

            final ConsoleViewHandler handler = new ConsoleViewHandler(display);
            handler.setFormatter(new LogFormatter(Preferences.getDetail()));
            Logger.getLogger("").addHandler(handler);
        }
        catch (Throwable ex)
        {
            Logger.getLogger(ConsoleViewHandler.class.getName())
                .log(Level.WARNING, "Cannot configure console view: {0}", ex.getMessage());
            return;
        }
        have_console = true;
    }

    /** Initialize, hook into console view
     *
     *  Private to prevent multiple instances
     *  @see #addToLogger()
     */
    private ConsoleViewHandler(final Display display)
    {
        this.display = display;

        // Allocate a console for text messages.
        console = new MessageConsole(Messages.ConsoleView_Title, null);
        // Values are from https://bugs.eclipse.org/bugs/show_bug.cgi?id=46871#c5
        console.setWaterMarks(80000, 100000);

        // Add to the 'Console' View
        final ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
        consolePlugin.getConsoleManager().addConsoles(
                new IConsole[] { console });

        // Streams to the MessageConsole
        severe_stream = console.newMessageStream();
        warning_stream = console.newMessageStream();
        info_stream = console.newMessageStream();
        basic_stream = console.newMessageStream();

        // Setting the color of a stream while it's in use is hard:
        // Has to happen on SWT UI thread, and changes will randomly
        // affect only the next message or the whole Console View.
        // Using different streams for the color-coded message levels
        // seem to work OK.

        severeColor = new Color(display, org.csstudio.logging.ui.Preferences.getColorSevere());
        warningColor = new Color(display, org.csstudio.logging.ui.Preferences.getColorWarning());
        infoColor = new Color(display, org.csstudio.logging.ui.Preferences.getColorInfo());
        basicColor = new Color(display, org.csstudio.logging.ui.Preferences.getColorBasic());

        severe_stream.setColor(severeColor);
        warning_stream.setColor(warningColor);
        info_stream.setColor(infoColor);
        basic_stream.setColor(basicColor);

        // Suppress log messages when the Eclipse console system shuts down.
        // Unclear how to best do that. Console plugin will 'remove' all consoles on shutdown,
        // so listen for that. But it actually closes the console streams just before that,
        // so a 'publish' call could arrive exactly between the console system shutting down
        // and the 'consolesRemoved' event that notifies us about that fact, resulting
        // in an exception inside 'publish' when it tries to write to the dead console stream.
        consolePlugin.getConsoleManager().addConsoleListener(new IConsoleListener()
        {
            @Override
            public void consolesAdded(final IConsole[] consoles)
            {
                // NOP
            }

            @Override
            public void consolesRemoved(final IConsole[] consoles)
            {
                // Check if it's this console
                for (IConsole console : consoles)
                {
                    if (console == ConsoleViewHandler.this.console)
                    {   // Mark as closed/detached
                        ConsoleViewHandler.this.console = null;
                        return;
                    }
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void publish(final LogRecord record)
    {
        if (! isLoggable(record))
            return;

        // Format message
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

        // Print in UI thread to avoid lockups
        if (display.isDisposed())
            return;
        display.asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // Console might already be closed/detached
                    if (console == null)
                        return;
                    final MessageConsoleStream stream = getStream(record.getLevel());
                    if (stream.isClosed())
                        return;
                    // During shutdown, error is possible because 'document' of console view
                    // was already closed. Unclear how to check for that.
                    stream.print(msg);
                }
                catch (Exception ex)
                {
                    reportError(null, ex, ErrorManager.WRITE_FAILURE);
                }
            }
        });
    }

    /** @param level Message {@link Level}
     *  @return Suggested stream for that Level or <code>null</code>
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
        // Flush in UI thread to avoid lockups
        display.asyncExec(new Runnable()
        {
            @Override
            public void run()
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
        });
    }

    /** Usually called by JRE when Logger shuts down, i.e.
     *  way after the Eclipse shutdown has already closed
     *  the console view
     */
    @Override
    public void close() throws SecurityException
    {
        basicColor.dispose();
        infoColor.dispose();
        severeColor.dispose();
        warningColor.dispose();
        // Mark as detached from console
        final MessageConsole console_copy = console;
        if (console_copy == null)
            return;
        console = null;
        // Remove from 'Console' view
        console_copy.clearConsole();
        final ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
        consolePlugin.getConsoleManager().removeConsoles(
                new IConsole[] { console_copy });
    }
}
