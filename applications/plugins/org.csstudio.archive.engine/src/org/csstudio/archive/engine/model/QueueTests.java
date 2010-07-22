package org.csstudio.archive.engine.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Test;

/** Simple benchmark of queues for the SampleBuffer.
 *  <p>
 *  ArrayBlockingQueue is the safest approach, but ArrayList is
 *  twice as fast for simple inserts.
 *  Still, ArrayBlockingQueue already functions as ring-buffer,
 *  so in overwrite situations where speed really matters it's
 *  probably the better choice.
 *  <p>
 *  <pre>
 * ArrayBlockingQueue:     27185109 values in 10.00 sec =    2718510.9 vals/sec
 * LinkedList        :     45739500 values in 10.00 sec =    4573950.0 vals/sec
 * ArrayList         :     50023429 values in 10.00 sec =    5002342.9 vals/sec
 *  </pre>
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class QueueTests
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
            queue.add(new Integer(i));
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
                queue.add(new Integer(i));
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
                queue.add(new Integer(i));
            }
            synchronized (queue)
            {
                final Integer val = queue.size() > 0 ? queue.remove(0) : null;
                assertEquals(i, val.intValue());
            }
            ++i;
        }
        final long end = System.currentTimeMillis();
        final double secs = (end - start) / 1000.0;
        System.out.format(
                "ArrayList         : %12d values in %.2f sec = %12.1f vals/sec\n",
                i, secs, i / secs);
    }
}
