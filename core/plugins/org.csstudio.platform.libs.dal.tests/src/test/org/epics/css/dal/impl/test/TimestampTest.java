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

//				System.out.println(t);
//				System.out.println(tm);
//				System.out.println(tn);

		// Big difference is because 'milli' is rounded to 1 second because of missing nanosecond precision from timestamp.
		assertTrue(""+(milli - t.getMilliseconds()),Math.abs(milli - t.getMilliseconds()) < 1000);
		assertTrue(""+(t.getMilliseconds() - tm.getMilliseconds()),Math.abs(t.getMilliseconds() - tm.getMilliseconds()) < 1000);

		assertEquals(nano - (nano / 1000000) * 1000000, tn.getNanoseconds());
		assertEquals(nano / 1000000, tn.getMilliseconds());
		
//		System.out.println(new Timestamp());
//		System.out.println(System.currentTimeMillis());
//		System.out.println(new Timestamp());
//		System.out.println(System.currentTimeMillis());
//		System.out.println(new Timestamp());
//		System.out.println(System.currentTimeMillis());
//		System.out.println(new Timestamp());
//		System.out.println(System.currentTimeMillis());
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
	
	public void testSeconds() {
		Timestamp t = new Timestamp();
		long seconds = t.getMilliseconds()/1000L;
		assertEquals(seconds, t.getSeconds());
	}
	
	public void testCompare() {
		long millis = System.currentTimeMillis();
		long m1 = millis-5;
		long m2 = millis-1;
		long m3 = millis+7;
		long n1 = 1;
		long n2 = 999999;
		
		Timestamp[] t = new Timestamp[7];
		t[0] = new Timestamp(m1,0);
		t[1] = new Timestamp(m2,n2);
		t[2] = new Timestamp(millis,0);
		t[3] = new Timestamp(millis,n1);
		t[4] = new Timestamp(millis,n2);
		t[5] = new Timestamp(m3,0);
		t[6] = new Timestamp(m3,n2);
		
		for (int i = 0; i < t.length-1; i++) {
			assertTrue("Test 1 for index: "+i, t[i].isLessThan(t[i+1]));
			assertTrue("Test 2 for index: "+i, t[i].isLessOrEqual(t[i+1]));
			assertFalse("Test 3 for index: "+i, t[i].isGreaterThan(t[i+1]));
			assertFalse("Test 4 for index: "+i, t[i].isGreaterOrEqual(t[i+1]));
		}
		
		for (int i = t.length-1; i > 0; i--) {
			assertFalse("Test 5 for index: "+i, t[i].isLessThan(t[i-1]));
			assertFalse("Test 6 for index: "+i, t[i].isLessOrEqual(t[i-1]));
			assertTrue("Test 7 for index: "+i, t[i].isGreaterThan(t[i-1]));
			assertTrue("Test 8 for index: "+i, t[i].isGreaterOrEqual(t[i-1]));
		}
		
		Timestamp t2 = new Timestamp(millis,0);
		assertFalse("Test 9: ", t2.isLessThan(t[2]));
		assertTrue("Test 10: ", t2.isLessOrEqual(t[2]));
		assertFalse("Test 11: ", t2.isGreaterThan(t[2]));
		assertTrue("Test 12: ", t2.isGreaterOrEqual(t[2]));
		
	}
	
	public void testToString() {
		long millis = System.currentTimeMillis();
		Timestamp t1 = new Timestamp(millis,0);
		Timestamp t2 = new Timestamp(millis-1,999999);
		Timestamp t3 = new Timestamp(millis,1);
		
		String s1 = t1.toString();
		String s2 = t2.toString();
		String s3 = t3.toString();
		
		String s = (new Timestamp(millis-1,0)).toString();
		
		String s2expected = s.substring(0, s.length()-6)+"999999";
		String s3expected = s1.substring(0, s1.length()-1)+"1";
		
//		System.out.println();
//		System.out.println(s1);
//		System.out.println(s2);
//		System.out.println(s3);
//		System.out.println(s2expected);
//		System.out.println(s3expected);
		
		assertEquals(s2, s2expected);
		assertEquals(s3, s3expected);
	}
	
	public void testToDouble() {
		Timestamp t = new Timestamp(0,0);
		assertEquals(0.0, t.toDouble());
		t = new Timestamp(1000,0);
		assertEquals(1.0, t.toDouble());
		t = new Timestamp(1,1);
		assertEquals(0.001000001, t.toDouble());
	}
	
}

/* __oOo__ */
