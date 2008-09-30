package org.csstudio.apputil.ringbuffer;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of the RingBuffer
 *  @author Kay Kasemir
 */
public class RingBufferTest
{
    @Test
    public void testRingBuffer() throws Exception
    {
        final RingBuffer<Integer> ring = new RingBuffer<Integer>(5);
        assertTrue(ring.isEmpty());
        
        ring.add(1);
        assertFalse(ring.isEmpty());
        assertEquals(new Integer(1), ring.remove());
        assertNull(ring.remove());
        assertTrue(ring.isEmpty());
        
        for (int i=1; i<10; ++i)
            ring.add(i);
        
        dump(ring);
        final int cap = ring.getCapacity() * 2;
        ring.setCapacity(cap);
        assertEquals(cap, ring.getCapacity());
        
        Integer item = ring.remove();
        while (item != null)
        {
            System.out.println(item);
            item = ring.remove();
        }
    }

    private void dump(RingBuffer<Integer> ring)
    {
        final int N = ring.size();
        for (int i=0; i<N; ++i)
            System.out.println(ring.get(i));
    }
}
