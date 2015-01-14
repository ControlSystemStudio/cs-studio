/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import gov.aps.jca.Context;

import java.util.LinkedList;
import java.util.logging.Level;

/** JCA command pump, added for two reasons:
 *  <ol>
 *  <li>JCA callbacks can't directly send JCA commands
 *      without danger of a deadlock, at least not with JNI
 *      and the "DirectRequestDispatcher".
 *  <li>Instead of calling 'flushIO' after each command,
 *      this thread allows for a few requests to queue up,
 *      then periodically pumps them out with only a final
 *      'flush'
 *  </ol>
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class JCACommandThread extends Thread
{
    /** Delay between queue inspection.
     *  Longer delay results in bigger 'batches',
     *  which is probably good, but also increases the
     *  latency.
     */
    final private static long DELAY_MILLIS = 100;

    /** The JCA Context */
    final Context jca_context;

    /** Command queue.
     *  <p>
     *  SYNC on access
     */
    final private LinkedList<Runnable> command_queue =
                                                new LinkedList<Runnable>();

    /** Maximum size that command_queue reached at runtime */
    private int max_size_reached = 0;

    /** Flag to tell thread to run or quit */
    private boolean run = false;

    /** Construct, but don't start the thread.
     *  @param jca_context
     *  @see #start()
     */
    public JCACommandThread(final Context jca_context)
    {
        super("JCA Command Thread");
        this.jca_context = jca_context;
    }

    /** Version of <code>start</code> that may be called multiple times.
     *  <p>
     *  The thread must only be started after the first PV has been
     *  created. Otherwise, if flush is called without PVs, JNI JCA
     *  reports pthread errors.
     *  <p>
     *  NOP when already running
     */
    @Override
    public synchronized void start()
    {
        if (run)
            return;
        run = true;
        super.start();
    }


    /** Stop the thread and wait for it to finish */
    void shutdown()
    {
        run = false;
        try
        {
            join();
        }
        catch (InterruptedException ex)
        {
            Activator.getLogger().log(Level.WARNING, "JCACommandThread shutdown", ex);
        }
        Activator.getLogger().log(Level.FINE, "JCACommandThread queue reached up to {0} entries", max_size_reached);
    }

    /** Add a command to the queue.
     *  TODO add some cap on the command queue? At least for value updates?
     *  @param command
     */
    void addCommand(final Runnable command)
    {
        synchronized (command_queue)
        {
            // New maximum queue length (+1 for the one about to get added)
            if (command_queue.size() >= max_size_reached)
                max_size_reached = command_queue.size() + 1;
            command_queue.addLast(command);
        }
    }

    /** @return Oldest queued command or <code>null</code> */
    private Runnable getCommand()
    {
        synchronized (command_queue)
        {
            if (command_queue.size() > 0)
                return command_queue.removeFirst();
        }
        return null;
    }

    @Override
    public void run()
    {
        while (run)
        {
            // Execute all the commands currently queued...
            Runnable command = getCommand();
            while (command != null)
            {   // Execute one command
                try
                {
                    command.run();
                }
                catch (Throwable ex)
                {
                    Activator.getLogger().log(Level.WARNING, "JCACommandThread exception", ex);
                }
                // Get next command
                command = getCommand();
            }
            // Flush.
            // Once, after executing all the accumulated commands.
            // Even when the command queue was empty,
            // there may be stuff worth flushing.
            try
            {
                jca_context.flushIO();
            }
            catch (Throwable ex)
            {
                Activator.getLogger().log(Level.WARNING, "JCA Flush exception", ex);
            }
            // Then wait.
            try
            {
                Thread.sleep(DELAY_MILLIS);
            }
            catch (InterruptedException ex)
            { /* don't even ignore */ }
        }
    }
}
