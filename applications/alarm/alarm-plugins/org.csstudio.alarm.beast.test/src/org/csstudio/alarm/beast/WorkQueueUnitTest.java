/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

/** Very simplistic WorkQueue JUnit test
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WorkQueueUnitTest
{
    private String result = "";

    @Test
    public void testExecute() throws Exception
    {
        final WorkQueue queue = new WorkQueue();
        queue.execute(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("Hello");
                result += "Hello";
            }
        });
        queue.execute(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("Goodbye");
                result += "Goodbye";
            }
        });
        queue.performQueuedCommands(1000);
        assertEquals("HelloGoodbye", result);

        // Should be on the same thread
        queue.assertOnThread();

        // Detect error when called from wrong thread
        final Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    queue.assertOnThread();
                    fail("Queue didn't notice access from wrong thread");
                }
                catch (Throwable ex)
                {
                    System.out.println(ex.getMessage());
                    assertTrue(ex.getMessage().contains("WrongThread"));
                }
            }
        }, "WrongThread");
        thread.start();
        thread.join();
    }

    @Test
    public void testExecuteWithoutDelay() throws Exception
    {
        final WorkQueue queue = new WorkQueue();
        queue.execute(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("Hello");
                result += "Hello";
            }
        });
        // Execute queued commands
        queue.performQueuedCommands();
        assertEquals("Hello", result);

        // Should be on the same thread
        queue.assertOnThread();

        // Empty queue means almost no delay
        final long start = System.currentTimeMillis();
        queue.performQueuedCommands();
        final long end = System.currentTimeMillis();
        final double seconds = (end-start)/1000.0;
        assertEquals(0.0, seconds, 0.01);
    }

    // Meant to run in JProfiler, used to
    // determine queue performance
    @Ignore
    @Test
    public void profileQueuePerformance()
    {
        final WorkQueue queue = new WorkQueue();

        while (true)
        {
            for (int dup=0; dup<5; ++dup)
                for (int i=0; i<10; ++i)
                {
                    queue.execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // NOP
                        }
                    });
                }
            queue.performQueuedCommands();
        }
    }
}
