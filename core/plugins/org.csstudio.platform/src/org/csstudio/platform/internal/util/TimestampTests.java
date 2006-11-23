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
package org.csstudio.platform.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.csstudio.platform.util.ITimestamp;
import org.csstudio.platform.util.TimestampFactory;
import org.junit.Test;

/**
 * Tests of the {@link Timestamp} class.
 * 
 * @author Kay Kasemir
 */
public final class TimestampTests {
	/**
	 * Test method for
	 * {@link Timestamp#fromPieces(int, int, int, int, int, int, long)}.
	 * 
	 */
	@Test
	public void testTimestamp() {
		// Basic conversions from/to pieces and strings
		ITimestamp t1 = Timestamp.fromPieces(1990, 1, 18, 13, 30, 20, 0);
		long[] pieces = t1.toPieces();
		assertEquals(pieces[0], 1990L);
		assertEquals(pieces[1], 1L);
		assertEquals(pieces[2], 18L);
		assertEquals(pieces[3], 13L);
		assertEquals(pieces[4], 30L);
		assertEquals(pieces[5], 20L);
		assertEquals(pieces[6], 0L);
		String s1 = t1.toString();
		String pattern = "YYYY/MM/DD HH:MM:SS.000000000";
		// 1990/01/18 13:30:10.000000000
		assertEquals(pattern.length(), s1.length());
		assertEquals("1990/01/18 13:30:20.000000000", s1);

		// Compare only fromPieces -> toString
		// since that includes toPieces.

		// 1 nanosec
		t1 = Timestamp.fromPieces(1990, 1, 18, 13, 30, 20, 1);
		s1 = t1.toString();
		assertEquals("1990/01/18 13:30:20.000000001", s1);

		// half a sec
		t1 = Timestamp.fromPieces(1990, 1, 18, 13, 30, 20, 500000000);
		s1 = t1.toString();
		assertEquals("1990/01/18 13:30:20.500000000", s1);

		// other millenium
		t1 = Timestamp.fromPieces(2005, 12, 31, 23, 59, 00, 500000000);
		s1 = t1.toString();
		assertEquals("2005/12/31 23:59:00.500000000", s1);

		// almost one sec
		t1 = Timestamp.fromPieces(2005, 12, 31, 23, 59, 00, 999999999);
		s1 = t1.toString();
		assertEquals("2005/12/31 23:59:00.999999999", s1);

		// Start of year
		t1 = Timestamp.fromPieces(2006, 1, 1, 00, 00, 00, 0);
		s1 = t1.toString();
		assertEquals("2006/01/01 00:00:00.000000000", s1);

		// End of year
		t1 = Timestamp.fromPieces(2006, 12, 31, 23, 59, 59, 999999999);
		s1 = t1.toString();
		assertEquals("2006/12/31 23:59:59.999999999", s1);
	}

	/**
	 * Test method for {@link Timestamp#fromString(String)}.
	 * 
	 * @throws Exception
	 *             an exception
	 */
	@Test
	public void testParser() throws Exception {
		ITimestamp t1;
		String s1;

		t1 = Timestamp.fromString("2006/01/01");
		s1 = t1.toString();
		assertEquals("2006/01/01 00:00:00.000000000", s1);

		t1 = Timestamp.fromString("2006/01/01 12:10");
		s1 = t1.toString();
		assertEquals("2006/01/01 12:10:00.000000000", s1);

		t1 = Timestamp.fromString("2006/01/18 12:10:34");
		s1 = t1.toString();
		assertEquals("2006/01/18 12:10:34.000000000", s1);
	}

	/**
	 * Test method for {@link Timestamp#equals(Object)}.
	 * 
	 */
	@Test
	public void testEquality() {
		// Basic conversions from/to pieces and strings
		Timestamp a = Timestamp.fromPieces(1990, 1, 18, 13, 30, 20, 0);
		Timestamp b = Timestamp.fromPieces(1990, 1, 18, 13, 30, 20, 0);
		Timestamp c = TimestampFactory.now();

		assertTrue(a.equals(a));
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));

		assertTrue(a.isGreaterOrEqual(a));
		assertTrue(a.isGreaterOrEqual(b));
		assertTrue(b.isGreaterOrEqual(a));

		assertTrue(c.isGreaterOrEqual(a));
		assertTrue(c.isGreaterOrEqual(b));
		assertTrue(c.isGreaterOrEqual(c));

		assertTrue(c.isGreaterThan(a));
		assertTrue(c.isGreaterThan(b));
		assertFalse(c.isGreaterThan(c));

		assertFalse(a.equals(c));
		assertFalse(b.equals(c));
		assertFalse(c.equals(a));
	}
}
