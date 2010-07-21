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

import org.csstudio.platform.logging.CentralLogger;

/** Queue that receives Runnables and executes them.
 *  <p>
 *  Doesn't start a thread to actually handle the work,
 *  only provides a thread-safe way to add Runnables
 *  and to execute them.
 *  @author Kay Kasemir
 */
public class WorkQueue
{
    /** Task queue */
    final Queue<Runnable> tasks = new LinkedList<Runnable>();
    
    /** Thread that executes the queue. Set on first access */
    private Thread thread;
    
    /** Add a task to the queue */
    public void add(final Runnable task)
    {
        synchronized (tasks)
        {
            tasks.add(task);
            tasks.notifyAll();
        }
    }

    /** Execute queued tasks. If there are none, wait a little, then check again.
     *  @param millisecs Time to wait
     */
    public void execute(final int millisecs)
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
                CentralLogger.getInstance().getLogger(this).error(ex);
                ex.printStackTrace();
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
    @SuppressWarnings("nls")
    public void assertOnThread()
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
