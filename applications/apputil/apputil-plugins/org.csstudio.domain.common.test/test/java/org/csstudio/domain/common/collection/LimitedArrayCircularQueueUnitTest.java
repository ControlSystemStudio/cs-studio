/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.common.collection;

import java.util.Collections;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test for {@link LimitedArrayCircularQueue}.
 *
 * @author bknerr
 * @since 12.10.2011
 */
public class LimitedArrayCircularQueueUnitTest {

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidConstruction1() {
        new LimitedArrayCircularQueue<Object>(0);
    }
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidConstruction2() {
        new LimitedArrayCircularQueue<Object>(-1);
    }
    @Test(expected=NoSuchElementException.class)
    public void testValidConstructionOfEmpty() {
        final LimitedArrayCircularQueue<Double> queue = new LimitedArrayCircularQueue<Double>(1);
        Assert.assertTrue(queue.remainingCapacity() == 1);
        Assert.assertTrue(queue.isEmpty());
        Assert.assertFalse(queue.contains(Double.valueOf(1.0)));
        Assert.assertNull(queue.peek());
        Assert.assertNull(queue.poll());
        Assert.assertNull(queue.get(0));
        queue.element();
    }
    @Test(expected=NoSuchElementException.class)
    public void testAddForSize1() {
        final LimitedArrayCircularQueue<Double> queue =
            new LimitedArrayCircularQueue<Double>(1);
        final Double firstElem = Double.valueOf(1.0);
        queue.add(firstElem);
        Assert.assertFalse(queue.isEmpty());
        Assert.assertTrue(queue.remainingCapacity() == 0);
        Assert.assertTrue(queue.contains(firstElem));
        Assert.assertEquals(firstElem, queue.peek());
        Assert.assertEquals(firstElem, queue.element());
        Assert.assertEquals(firstElem, queue.get(0));
        Assert.assertEquals(firstElem, queue.poll());
        Assert.assertTrue(queue.remainingCapacity() == 1);

        Assert.assertTrue(queue.isEmpty());
        Assert.assertNull(queue.peek());
        Assert.assertNull(queue.poll());
        Assert.assertNull(queue.get(0));
        queue.element();
    }
    @Test(expected=NoSuchElementException.class)
    public void testAddOfferForSize1Circular() {
        final LimitedArrayCircularQueue<Double> queue =
            new LimitedArrayCircularQueue<Double>(1);
        final Double firstElem = Double.valueOf(1.0);
        queue.offer(firstElem);
        Assert.assertFalse(queue.isEmpty());
        Assert.assertTrue(queue.size() == 1);
        Assert.assertTrue(queue.contains(firstElem));

        final Double secondElem = Double.valueOf(2.0);
        queue.add(secondElem);
        Assert.assertTrue(queue.remainingCapacity() == 0);
        Assert.assertFalse(queue.isEmpty());
        Assert.assertTrue(queue.size() == 1);
        Assert.assertFalse(queue.contains(firstElem));
        Assert.assertTrue(queue.contains(secondElem));
        Assert.assertEquals(secondElem, queue.element());
        Assert.assertEquals(secondElem, queue.peek());
        Assert.assertEquals(secondElem, queue.get(0));
        Assert.assertFalse(queue.isEmpty());
        Assert.assertEquals(secondElem, queue.poll());
        Assert.assertTrue(queue.isEmpty());
        Assert.assertNull(queue.get(1));
        queue.element();
    }

    @Test
    public void testAdd4CircularOverride() {
        final LimitedArrayCircularQueue<Number> queue =
            new LimitedArrayCircularQueue<Number>(4);
        Assert.assertTrue(queue.remainingCapacity() == 4);

        final Integer fst = Integer.valueOf(1);
        final Double sec = Double.valueOf(1.0);
        final Short trd = Short.valueOf((short) 1);
        final Byte frt = Byte.valueOf((byte) 1);
        queue.add(fst);
        queue.offer(sec);
        Assert.assertTrue(queue.remainingCapacity() == 2);
        queue.offer(trd);
        queue.add(frt);
        Assert.assertTrue(queue.remainingCapacity() == 0);
        Assert.assertFalse(queue.isEmpty());
        Assert.assertTrue(queue.size() == 4);
        Assert.assertEquals(fst, queue.get(0));
        Assert.assertEquals(sec, queue.get(1));
        Assert.assertEquals(trd, queue.get(2));
        Assert.assertEquals(frt, queue.get(3));

        final Long fth = Long.valueOf(1L);
        queue.add(fth);
        Assert.assertTrue(queue.size() == 4);

        Assert.assertEquals(sec, queue.get(0));
        Assert.assertEquals(trd, queue.get(1));
        Assert.assertEquals(frt, queue.get(2));
        Assert.assertEquals(fth, queue.get(3));
    }

    @Test
    public void testRemovePollCapacity() {
        final LimitedArrayCircularQueue<Number> queue =
            new LimitedArrayCircularQueue<Number>(2);
        final Integer fst = Integer.valueOf(1);
        queue.addAll(Collections.singleton(fst));
        Assert.assertFalse(queue.isEmpty());
        Assert.assertTrue(queue.size() == 1);
        Assert.assertTrue(queue.contains(fst));
        Assert.assertTrue(queue.remainingCapacity() == 1);

        Assert.assertEquals(fst, queue.remove());

        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(queue.size() == 0);
        Assert.assertNull(queue.get(0));
        Assert.assertNull(queue.get(1));

        Assert.assertTrue(queue.remainingCapacity() == 2);

        final Integer sec = Integer.valueOf(2);
        queue.addAll(Lists.newArrayList(fst, sec));
        Assert.assertTrue(queue.remainingCapacity() == 0);
        Assert.assertFalse(queue.isEmpty());
        Assert.assertTrue(queue.size() == 2);
        Assert.assertFalse(queue.contains(new Object()));

        Assert.assertEquals(fst, queue.remove());
        Assert.assertTrue(queue.remainingCapacity() == 1);
        Assert.assertFalse(queue.isEmpty());
        Assert.assertTrue(queue.size() == 1);
        Assert.assertEquals(sec, queue.poll());

        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(queue.size() == 0);
        Assert.assertNull(queue.peek());
    }

    @Test
    public void testCapacity() {
        final LimitedArrayCircularQueue<Number> queue =
            new LimitedArrayCircularQueue<Number>(4);
        Assert.assertTrue(queue.remainingCapacity() == 4);
        final Integer fst = Integer.valueOf(1);
        final Double sec = Double.valueOf(1.0);
        final Short trd = Short.valueOf((short) 1);
        final Byte frt = Byte.valueOf((byte) 1);
        queue.add(fst);
        queue.offer(sec);
        queue.offer(trd);
        queue.add(frt);
        Assert.assertTrue(queue.remainingCapacity() == 0);

        queue.setCapacity(5);
        Assert.assertTrue(queue.remainingCapacity() == 1);
        final Byte fth = Byte.valueOf((byte) 5);
        queue.add(fth);
        final Byte sth = Byte.valueOf((byte) 6);
        queue.add(sth);
        Assert.assertTrue(queue.remainingCapacity() == 0);

        Assert.assertEquals(sec, queue.get(0));
        Assert.assertEquals(sth, queue.get(4));

        queue.setCapacity(2);
        Assert.assertEquals(fth, queue.get(0));
        Assert.assertEquals(sth, queue.get(1));
        queue.remove();
        Assert.assertTrue(queue.remainingCapacity() == 1);

        queue.setCapacity(1);
        Assert.assertEquals(sth, queue.get(0));
        Assert.assertTrue(queue.remainingCapacity() == 0);
    }

}
