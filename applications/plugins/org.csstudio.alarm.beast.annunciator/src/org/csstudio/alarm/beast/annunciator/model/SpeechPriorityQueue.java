/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import java.util.PriorityQueue;
import java.util.logging.Level;

import org.csstudio.alarm.beast.annunciator.Activator;

/** SpeechPriorityQueue class which implements a PriorityQueue but
 *  changes the add and poll routines.
 *  It uses the PriorityQueues size and clear routines.
 *
 *  @author Delphy Armstrong
 *  @author Kay Kasemir
 *
 *    reviewed by Delphy 1/29/09
 */
public class SpeechPriorityQueue
{
    /** PriorityQueue automatically inserts the messages
     *  in order of severity because MessageWithSeverity
     *  implements Comparable.
     *  <p>
     *  Note that the insertion is mostly from the JMS thread,
     *  while polling is done from the QueueManager thread,
     *  so all access must synchronize on the queue.
     */
    final private PriorityQueue<AnnunciationMessage> queue =
        new PriorityQueue<AnnunciationMessage>();

    /** Add item to the queue and notify everyone.
     *  @param severity Severity of the message
     *  @param message Message text
     */
    public void add(final Severity severity, final String message)
    {
        // Wrap severity and message for queue insertion
        final AnnunciationMessage item = new AnnunciationMessage(severity, message);
        synchronized (queue)
        {
            queue.add(item);
            queue.notifyAll();
        }
    }

    /** @return queue size. */
    public int size()
    {
        synchronized (queue)
        {
            return queue.size();
        }
    }

    /** Clear the queue. */
    public void clear()
    {
        synchronized (queue)
        {
            queue.clear();
        }
    }

    /** Poll the queue and return the next available MessageWithSeverity
     *  or block while none is available.
     *  <p>
     *  This uses the <code>Queue</code> sense of "poll" which
     *  checks for an available item and actually removes it from the queue.
     *
     *  @return Next available item, removed from the queue.
     */
    @SuppressWarnings("nls")
    public AnnunciationMessage poll()
    {
        synchronized (queue)
        {
            while (true)
            {
                final AnnunciationMessage item = queue.poll();
                if (item != null)
                    return item;
                // No item available? Wait, then check again
                try
                {
                    queue.wait();
                }
                catch (InterruptedException e)
                {
                    // Log errors
                    Activator.getLogger().log(Level.WARNING, "Message queue interrupted", e);
                }
            }
        }
    }
}
