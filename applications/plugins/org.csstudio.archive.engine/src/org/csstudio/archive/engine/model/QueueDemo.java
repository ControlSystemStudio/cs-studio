/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

import org.csstudio.apputil.ringbuffer.RingBuffer;
import org.junit.Test;

/** Simple benchmark of queues for the SampleBuffer.
 *  <p>
 *  ArrayBlockingQueue was originally used as the safest approach,
 *  but RingBuffer is about twice as fast.
 *  <p>
 *  <pre>
 * ArrayBlockingQueue:     25915005 values in 10.00 sec =    2591500.5 vals/sec
 * LinkedList        :     44003817 values in 10.00 sec =    4400381.7 vals/sec
 * ArrayList         :     48088144 values in 10.00 sec =    4808814.4 vals/sec
 * RingBuffer        :     48864698 values in 10.00 sec =    4886469.8 vals/sec
 *  </pre>
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class QueueDemo
{
    private static final long RUNTIME = 10 * 1000l;

    /** ArrayBlockingQueue:
     *  Add to end, remove head, and queue already handles the synchronization.
     */
    @Test
    public void testBlockingQueue() throws Exception
    {
        final ArrayBlockingQueue<Integer> queue =
            new ArrayBlockingQueue<Integer>(100);

        final long start = System.currentTimeMillis();
        final long run = start + RUNTIME;
        int i = 0;
        while (run > System.currentTimeMillis())
        {
            queue.add(Integer.valueOf(i));
            final Integer val = queue.take();
            assertEquals(i, val.intValue());
            ++i;
        }
        final long end = System.currentTimeMillis();
        final double secs = (end - start) / 1000.0;
        System.out.format(
                "ArrayBlockingQueue: %12d values in %.2f sec = %12.1f vals/sec\n",
                i, secs, i / secs);
    }

    /** Linked list: Add to end, remove head */
    @Test
    public void testLinkedList() throws Exception
    {
        final LinkedList<Integer> queue = new LinkedList<Integer>();
    
        final long start = System.currentTimeMillis();
        final long run = start + RUNTIME;
        int i = 0;
        while (run > System.currentTimeMillis())
        {
            synchronized (queue)
            {
                queue.add(Integer.valueOf(i));
            }
            synchronized (queue)
            {
                final Integer val = queue.poll();
                assertEquals(i, val.intValue());
            }
            ++i;
        }
        final long end = System.currentTimeMillis();
        final double secs = (end - start) / 1000.0;
        System.out.format(
                "LinkedList        : %12d values in %.2f sec = %12.1f vals/sec\n",
                i, secs, i / secs);
    }

    /** Array list: Add to end, remove head */
    @Test
    public void testArrayList() throws Exception
    {
        final ArrayList<Integer> queue = new ArrayList<Integer>(100);

        final long start = System.currentTimeMillis();
        final long run = start + RUNTIME;
        int i = 0;
        while (run > System.currentTimeMillis())
        {
            synchronized (queue)
            {
                queue.add(Integer.valueOf(i));
            }
            synchronized (queue)
            {
                final int val = queue.size() > 0 ? queue.remove(0) : -1;
                assertEquals(i, val);
            }
            ++i;
        }
        final long end = System.currentTimeMillis();
        final double secs = (end - start) / 1000.0;
        System.out.format(
                "ArrayList         : %12d values in %.2f sec = %12.1f vals/sec\n",
                i, secs, i / secs);
    }

    /** Array list: Add to end, remove head */
    @Test
    public void testRingBuffer() throws Exception
    {
        final RingBuffer<Integer> queue = new RingBuffer<Integer>(100);

        final long start = System.currentTimeMillis();
        final long run = start + RUNTIME;
        int i = 0;
        while (run > System.currentTimeMillis())
        {
            synchronized (queue)
            {
                queue.add(Integer.valueOf(i));
            }
            synchronized (queue)
            {
                final Integer val = queue.remove();
                final int number = val == null ? -1 : val.intValue();
                assertEquals(i, number);
            }
            ++i;
        }
        final long end = System.currentTimeMillis();
        final double secs = (end - start) / 1000.0;
        System.out.format(
                "RingBuffer        : %12d values in %.2f sec = %12.1f vals/sec\n",
                i, secs, i / secs);
    }

}
