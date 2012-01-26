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
	
	// Compared these data structures for 'tasks':
	//
	// final Queue<Runnable> tasks = new LinkedList<Runnable>();
	// final LinkedHashMap<Object, Runnable> tasks = new LinkedHashMap<Object, Runnable>();
	//
	// Considered LinkedHashSet, but didn't work:
	// Inserting a 'new' ReplacableRunnable would simply keep
	// an older entry, not update it.

	// JProfile results for 100 tasks, replaced 4 times:
	//
	//                     LinkedList  LinkedHashMap
	// execute                 4us         10us
	// executeReplacable      44us         10us
	// getOldestRunnable       1us         12us
	//
	// Linked list is obviously slower when asked to replaced an existing
	// runnable for the same object because of the linear lookup.
	// But how long is the list going to be?
	// Usually only a few noisy PVs, so maybe just 2 or 3 entries
	// that keep getting replaced.
	// If the test is run with only 10 tasks in the queue,
	// executeReplacable for the LinkedList takes only 11us,
	// i.e. similar to LinkedHashMap
	//
	// -> Simple linked list is probably good enough for the
	// common use case, plus uses less memory.

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
        	// Hash: Use dummy key to add a new element
            // tasks.put(new Object(), command);
        	
        	// Queue:
        	tasks.add(command);
        	
            tasks.notifyAll();
        }
    }

    /** Add a replaceable command to the queue.
     * 
     *  <p>If there is already a replaceable command
     *  on the queue for the same object, it will
     *  be replaced with this one.
     *  
     *  @param command Command to be executed
     *  @see Executor#execute(Runnable)
     */
    public void executeReplacable(final ReplacableRunnable<?> command)
    {
        synchronized (tasks)
        {	// Hash: Adding a ReplacableRunnable will
        	// use that class's equals() to replace
        	// an existing entry for the same object
        	// tasks.put(command, command);
        	
        	// List: Replace old, then add new
        	tasks.remove(command);
        	tasks.add(command);
        	
            tasks.notifyAll();
        }
    }

    /** @return Oldest runnable in the queue or <code>null</code> */
    private Runnable getOldestRunnable()
    {
        synchronized (tasks)
        {
        	// Hash: Need iterator to get oldest
        	//final Iterator<Runnable> iter = tasks.values().iterator();
        	//if (iter.hasNext())
        	//{
        	//	final Runnable result = iter.next();
        	//	iter.remove();
			//	return result;
        	//}
        	//return null;
        	
        	// Queue was designed for this
        	return tasks.poll();
        }
    }
	/** Perform queued commands, return when done.
     *  Returns 'immediately' if there are no queued commands.
     */
    public void performQueuedCommands()
    {
        // Execute all tasks on queue
        Runnable task = getOldestRunnable();
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
            task = getOldestRunnable();
        }
    }

    /** Perform queued commands. If there are none, wait a little, then check again.
     *  Meant to be called in a 'main' loop, i.e. always from the same thread,
     *  using the delay to keep the loop from using all CPU, yet also not waiting indefinitely
     *  to allow termination checks.
     *
     *  @param millisecs Time to wait
     */
    public void performQueuedCommands(final int millisecs)
    {
        assertOnThread();
        Runnable task;
        // Wait in case there aren't any tasks in the queue
        // Do this while sync'ed to not miss
        // a notify.
        synchronized (tasks)
        {
        	task = getOldestRunnable();
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
                task = getOldestRunnable();
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
            task = getOldestRunnable();
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
