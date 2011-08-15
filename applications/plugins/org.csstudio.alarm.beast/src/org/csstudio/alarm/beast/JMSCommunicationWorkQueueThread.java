/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.util.concurrent.Executor;

import javax.jms.Session;

/** A {@link JMSCommunicationThread} with {@link WorkQueue}.
 *
 *  Also an {@link Executor}:
 *  Runnables can be added to a work queue, to be executed by the
 *  communication thread.
 *
 *  @author Kay Kasemir
 */
abstract public class JMSCommunicationWorkQueueThread extends JMSCommunicationThread implements Executor
{
    /** Work queue handled by the worker thread that connects to JMS */
    final private WorkQueue queue = new WorkQueue();

    /** Initialize
     *  @param url JMS Server URL
     */
    public JMSCommunicationWorkQueueThread(final String url)
    {
        super(url);
    }

    /** Add task to the work queue of the JMS communication thread.
     *  @param task Task that will be executed by the communication thread.
     *  @see Executor
     */
    @Override
    public void execute(final Runnable task)
    {
        queue.execute(task);
    }

    /** Communicate by executing items on the work queue */
    @Override
    protected void communicate(final Session session) throws Exception
    {
        queue.performQueuedCommands(WORKER_DELAY);
    }
}
