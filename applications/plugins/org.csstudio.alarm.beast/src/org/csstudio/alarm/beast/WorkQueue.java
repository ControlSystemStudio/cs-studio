/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;

/** Queue that receives {@link Runnable}s and executes them.
 *  <p>
 *  An {@link Executor} that does <b>not</b> create new threads,
 *  but queues commands for execution by an already existing
 *  thread.
 *  <p>
 *  For example used to re-direct execution of commands to a 'main'
 *  thread, to assert that all interactions with a certain resource
 *  happen on the same thread.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WorkQueue implements Executor
{
    /** Task queue */
    final Queue<Runnable> tasks = new LinkedList<Runnable>();

    /** Thread that executes the queue. Set on first access */
    private Thread thread;

    /** @return Number of currently queued commands on the work queue */
    public int size()
    {
    	synchronized (tasks)
        {
        	return tasks.size();
        }
    }
    
    /** Add a command to the queue
     *  @param command Command to be executed
     *  @see Executor#execute(Runnable)
     */
    @Override
    public void execute(final Runnable command)
    {
        synchronized (tasks)
        {
            tasks.add(command);
            tasks.notifyAll();
        }
    }

    /** Perform queued commands, return when done.
     *  Returns 'immediately' if there are no queued commands.
     */
    public void perform_queued_commands()
    {
        Runnable task;
        // Execute all tasks on queue
        synchronized (tasks)
        {
            task = tasks.poll();
        }
        while (task != null)
        {
            try
            {
                task.run();
            }
            catch (Throwable ex)
            {
                Activator.getLogger().log(Level.SEVERE, "Work Queue Exception", ex);
            }
            synchronized (tasks)
            {
                task = tasks.poll();
            }
        }
    }

    /** Perform queued commands. If there are none, wait a little, then check again.
     *  Meant to be called in a 'main' loop, i.e. always from the same thread,
     *  using the delay to keep the loop from using all CPU, yet also not waiting indefinitely
     *  to allow termination checks.
     *
     *  @param millisecs Time to wait
     */
    public void perform_queued_commands(final int millisecs)
    {
        assertOnThread();
        Runnable task;
        // Wait in case there aren't any tasks in the queue
        synchronized (tasks)
        {
            task = tasks.poll();
            if (task == null)
            {
                try
                {
                    tasks.wait(millisecs);
                }
                catch (InterruptedException ex)
                {
                    return;
                }
                task = tasks.poll();
            }
        }
        // Execute all tasks on queue
        while (task != null)
        {
            try
            {
                task.run();
            }
            catch (Throwable ex)
            {
                Activator.getLogger().log(Level.SEVERE, "Work Queue Exception", ex);
            }
            synchronized (tasks)
            {
                task = tasks.poll();
            }
        }
    }

    /** Assert that the work queue is executed by the same original thread
     *  @throws Error if called from thread other than the initial work queue thread
     */
    public synchronized void assertOnThread()
    {
        if (thread == null)
        {
            thread = Thread.currentThread();
            return;
        }
        if (thread != Thread.currentThread())
            throw new Error("Work queue thread changed from " +
                    thread.getName() + " to " + Thread.currentThread().getName());
    }
}
