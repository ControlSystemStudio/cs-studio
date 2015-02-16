/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import org.junit.Test;

/** JUnit demo of the Queue Manager.
 *
 *  @author Kay Kasemir
 *  @author Katia Danilova
 *  @author Delphy Armstrong
 *
 *    reviewed by Delphy 1/29/09
 */
public class QueueManagerDemo
{
    final private JMSAnnunciatorListener listener = new JMSAnnunciatorListener()
    {
        @Override
        public void performedAnnunciation(final AnnunciationMessage annunciation)
        {
            System.out.println(annunciation);
        }

        @Override
        public void annunciatorError(final Throwable ex)
        {
            ex.printStackTrace();
        }
    };

    @Test
    @SuppressWarnings("nls")
    public void main() throws Exception
    {
        // Initialize Severities, queue, QueueManager
        Severity.initialize("MAJOR,MINOR,INFO");
        final SpeechPriorityQueue queue = new SpeechPriorityQueue();
        final QueueManager queue_manager = new QueueManager(listener, queue, null, 5);
        queue_manager.start();

        // Allow QueueManager to start up
        Thread.sleep(3000);

        // Initial Message comes out right away
        queue.add(Severity.forInfo(), "Initial Message");

        // Queuing several messages where the higher priority arrives later
        queue.add(Severity.fromString("INFO"), ".. and then the info message");
        queue.add(Severity.fromString("MAJOR"), "This should come next");
        // Wait for messages to be processed
        while (queue.size() > 0)
            Thread.sleep(100);
        Thread.sleep(2000);

        // Create burst of messages by adding many to the queue
        queue.add(Severity.fromString("MAJOR"), "Burst of messages");
        for (int i = 0; i < 10; ++i)
            queue.add(Severity.fromString("MAJOR"), "all ignored");
        queue.add(Severity.fromString("MINOR"), "  ! important messages get through");
        for (int i = 0; i < 10; ++i)
            queue.add(Severity.fromString("MAJOR"), "all ignored");
        queue.add(Severity.fromString("MINOR"), "  ! like this one");
        // Wait for QueueManager to handle that burst
        while (queue.size() > 0)
            Thread.sleep(100);

        // Single message
        Thread.sleep(5000);
        System.out.println("Adding final");
        queue.add(Severity.fromString("INFO"), "Back to normal.");

        // Wait for that message to be processed
        while (queue.size() > 0)
            Thread.sleep(100);

        // Ask queue manager to exit
        queue_manager.stop();
    }
}

