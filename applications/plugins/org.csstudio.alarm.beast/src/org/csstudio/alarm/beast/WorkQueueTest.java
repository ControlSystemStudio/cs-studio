/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import static org.junit.Assert.*;

import org.junit.Test;

/** Very simplistic WorkQueue JUnit test
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WorkQueueTest
{
    private String result = "";
    
    @Test
    public void testExecute() throws Exception
    {
        final WorkQueue queue = new WorkQueue();
        queue.add(new Runnable()
        {
            public void run()
            {
                System.out.println("Hello");
                result += "Hello";
            }
        });
        queue.add(new Runnable()
        {
            public void run()
            {
                System.out.println("Goodbye");    
                result += "Goodbye";
            }
        });
        queue.execute(1000);
        assertEquals("HelloGoodbye", result);
        
        // Should be on the same thread
        queue.assertOnThread();
        
        // Detect error when called from wrong thread
        final Thread thread = new Thread(new Runnable()
        {
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
}
