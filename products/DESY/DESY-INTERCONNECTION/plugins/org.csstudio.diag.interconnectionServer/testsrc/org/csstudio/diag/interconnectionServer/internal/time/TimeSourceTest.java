/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.diag.interconnectionServer.internal.time;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Test for the abstract class {@link TimeSource} and for its concrete
 * subclass {@link StubTimeSource}.
 * 
 * @author Joerg Rathlev
 */
public class TimeSourceTest {
	
	@Test
	public void testConstructor() throws Exception {
		StubTimeSource ts = new StubTimeSource(12345);
		assertEquals(12345, ts.now());
		
		// Wait a few milliseconds, check that it still returns the same time
		Thread.sleep(5);
		assertEquals(12345, ts.now());
	}
	
	@Test
	public void testStubSetTime() throws Exception {
		StubTimeSource ts = new StubTimeSource(12345);
		assertEquals(12345, ts.now());
		ts.setTime(67890);
		assertEquals(67890, ts.now());
	}
	
	@Test
	public void testMillisecondsSince() throws Exception {
		StubTimeSource ts = new StubTimeSource(100);
		assertEquals(100, ts.millisecondsSince(0));
		ts.setTime(1234567890);
		assertEquals(81, ts.millisecondsSince(1234567809));
	}

}
