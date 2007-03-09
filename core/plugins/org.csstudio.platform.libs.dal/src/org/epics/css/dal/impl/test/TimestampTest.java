/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

/**
 *
 */
package org.epics.css.dal.impl.test;

import junit.framework.TestCase;

import org.epics.css.dal.Timestamp;


/**
 * @author ikriznar
 *
 */
public class TimestampTest extends TestCase
{
	public void testNow()
	{
		Timestamp t = new Timestamp();
		long milli = System.currentTimeMillis();
		long nano = System.nanoTime();

		Timestamp tm = new Timestamp(milli, 0);
		Timestamp tn = new Timestamp(0, nano);

		//		System.out.println(t);
		//		System.out.println(tm);
		//		System.out.println(tn);
		assertTrue(Math.abs(milli - t.getMilliseconds()) < 10);
		assertTrue(Math.abs(t.getMilliseconds() - tm.getMilliseconds()) < 10);
		assertTrue(Math.abs(tm.getMilliseconds() - t.getMilliseconds()) < 10);

		assertEquals(nano - (nano / 1000000) * 1000000, tn.getNanoseconds());
		assertEquals(nano / 1000000, tn.getMilliseconds());
	}

	public void testConstructor()
	{
		long m = 0;
		long n = 9999999;

		Timestamp t = new Timestamp(m, n);

		assertEquals("milli= " + m + " nano= " + n, m + n / 1000000,
		    t.getMilliseconds());
		assertEquals("milli= " + m + " nano= " + n,
		    n - (n / 1000000) * 1000000, t.getNanoseconds());

		m = 0;
		n = 1000000;

		t = new Timestamp(m, n);

		assertEquals("milli= " + m + " nano= " + n, 1, t.getMilliseconds());
		assertEquals("milli= " + m + " nano= " + n, 0, t.getNanoseconds());

		m = 10;
		n = 1000001;

		t = new Timestamp(m, n);

		assertEquals("milli= " + m + " nano= " + n, 11, t.getMilliseconds());
		assertEquals("milli= " + m + " nano= " + n, 1, t.getNanoseconds());

		m = 11;
		n = -1;

		t = new Timestamp(m, n);

		assertEquals("milli= " + m + " nano= " + n, 10, t.getMilliseconds());
		assertEquals("milli= " + m + " nano= " + n, 999999, t.getNanoseconds());

		m = 11;
		n = -1000001;

		t = new Timestamp(m, n);

		assertEquals("milli= " + m + " nano= " + n, 9, t.getMilliseconds());
		assertEquals("milli= " + m + " nano= " + n, 999999, t.getNanoseconds());
	}

	public void testRange()
	{
		Timestamp t1 = new Timestamp(0, Long.MAX_VALUE);
		Timestamp t2 = new Timestamp(0, 0);

		System.out.println(t1);
		System.out.println(t2);

		t1 = new Timestamp(Long.MAX_VALUE, 0);
		t2 = new Timestamp(0, 0);

		System.out.println(t1);
		System.out.println(t2);
	}
}

/* __oOo__ */
