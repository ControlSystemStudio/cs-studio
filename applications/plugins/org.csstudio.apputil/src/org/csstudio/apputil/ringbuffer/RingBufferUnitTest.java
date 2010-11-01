/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ringbuffer;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of the RingBuffer
 *  @author Kay Kasemir
 */
public class RingBufferUnitTest
{
    @Test
    public void testRingBuffer() throws Exception
    {
        final RingBuffer<Integer> ring = new RingBuffer<Integer>(5);
        assertTrue(ring.isEmpty());
        assertFalse(ring.isFull());
        
        // Add/remove one item
        ring.add(1);
        assertFalse(ring.isEmpty());
        assertEquals(Integer.valueOf(1), ring.remove());
        assertNull(ring.remove());
        assertTrue(ring.isEmpty());

        // Add 4 items
        for (int i=1; i<=4; ++i)
            ring.add(i);
        assertFalse(ring.isFull());
        
        // Fill with 5th element
        ring.add(5);
        assertTrue(ring.isFull());
        
        // Fill to 10, but ring only remembers the last 5 items
        for (int i=6; i<10; ++i)
            ring.add(i);
        assertTrue(ring.isFull());
        dump(ring);
        assertEquals(5, ring.size());
        assertEquals(Integer.valueOf(5), ring.get(0));
        assertEquals(Integer.valueOf(9), ring.get(4));

        // Changing the capacity will preserve those items
        final int cap = ring.getCapacity() * 2;
        ring.setCapacity(cap);
        assertFalse(ring.isFull());
        assertEquals(cap, ring.getCapacity());
        assertEquals(5, ring.size());
        assertEquals(Integer.valueOf(5), ring.get(0));
        assertEquals(Integer.valueOf(9), ring.get(4));
        // .. but now we can add more
        ring.add(10);
        assertEquals(6, ring.size());
        assertEquals(Integer.valueOf(5), ring.get(0));
        assertEquals(Integer.valueOf(10), ring.get(5));

        Integer item = ring.remove();
        while (item != null)
        {
            System.out.println(item);
            item = ring.remove();
        }
    }

    private void dump(final RingBuffer<Integer> ring)
    {
        final int N = ring.size();
        for (int i=0; i<N; ++i)
            System.out.println(ring.get(i));
    }
}
