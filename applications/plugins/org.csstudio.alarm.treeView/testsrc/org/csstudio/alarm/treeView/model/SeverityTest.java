/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.alarm.treeView.model;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Joerg Rathlev
 */
public class SeverityTest {

	@Test
	public void testIsAlarm() throws Exception {
		// All severities except NO_ALARM are alarms.		
		assertFalse(Severity.NO_ALARM.isAlarm());
		assertTrue(Severity.INVALID.isAlarm());
		assertTrue(Severity.MINOR.isAlarm());
		assertTrue(Severity.MAJOR.isAlarm());
	}

	@Test
	public void testOrder() throws Exception {
		// The order should be:
		// MAJOR > MINOR > INVALID > NO_ALARM
		
		assertTrue(Severity.MAJOR.compareTo(Severity.MAJOR) == 0);
		assertTrue(Severity.MAJOR.compareTo(Severity.MINOR) > 0);
		assertTrue(Severity.MAJOR.compareTo(Severity.INVALID) > 0);
		assertTrue(Severity.MAJOR.compareTo(Severity.NO_ALARM) > 0);

		assertTrue(Severity.MINOR.compareTo(Severity.MAJOR) < 0);
		assertTrue(Severity.MINOR.compareTo(Severity.MINOR) == 0);
		assertTrue(Severity.MINOR.compareTo(Severity.INVALID) > 0);
		assertTrue(Severity.MINOR.compareTo(Severity.NO_ALARM) > 0);
		
		assertTrue(Severity.INVALID.compareTo(Severity.MAJOR) < 0);
		assertTrue(Severity.INVALID.compareTo(Severity.MINOR) < 0);
		assertTrue(Severity.INVALID.compareTo(Severity.INVALID) == 0);
		assertTrue(Severity.INVALID.compareTo(Severity.NO_ALARM) > 0);

		assertTrue(Severity.NO_ALARM.compareTo(Severity.MAJOR) < 0);
		assertTrue(Severity.NO_ALARM.compareTo(Severity.MINOR) < 0);
		assertTrue(Severity.NO_ALARM.compareTo(Severity.INVALID) < 0);
		assertTrue(Severity.NO_ALARM.compareTo(Severity.NO_ALARM) == 0);
	}
	
	@Test
	public void testParseSeverity() throws Exception {
		assertSame(Severity.MAJOR, Severity.parseSeverity("MAJOR"));
		assertSame(Severity.MINOR, Severity.parseSeverity("MINOR"));
		assertSame(Severity.INVALID, Severity.parseSeverity("INVALID"));
		assertSame(Severity.NO_ALARM, Severity.parseSeverity("NO_ALARM"));
		
		// unknown strings will return NO_ALARM
		assertSame(Severity.NO_ALARM, Severity.parseSeverity("foo"));
	}
}
