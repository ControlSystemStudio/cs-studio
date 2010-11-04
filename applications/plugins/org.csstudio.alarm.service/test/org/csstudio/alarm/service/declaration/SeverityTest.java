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
package org.csstudio.alarm.service.declaration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Joerg Rathlev
 */
public class SeverityTest {

    @Test
    public void testGetLowest() {
        Assert.assertEquals(EpicsSeverity.UNKNOWN, EpicsSeverity.getLowest());
    }

	@Test
	public void testIsAlarm() throws Exception {
		// All severities except NO_ALARM are alarms.
		assertFalse(EpicsSeverity.NO_ALARM.isAlarm());
		assertTrue(EpicsSeverity.INVALID.isAlarm());
		assertTrue(EpicsSeverity.MINOR.isAlarm());
		assertTrue(EpicsSeverity.MAJOR.isAlarm());
	}

	@Test
	public void testOrder() throws Exception {

	    // FIXME (jpenning) (bknerr) : Don't rely on ordering of enums : ANTIPATTERN

		// The order should be:
		// INVALID >  MAJOR > MINOR > NO_ALARM

		assertTrue(EpicsSeverity.INVALID.compareTo(EpicsSeverity.INVALID) == 0);
		assertTrue(EpicsSeverity.INVALID.compareTo(EpicsSeverity.MAJOR) > 0);
		assertTrue(EpicsSeverity.INVALID.compareTo(EpicsSeverity.MINOR) > 0);
		assertTrue(EpicsSeverity.INVALID.compareTo(EpicsSeverity.NO_ALARM) > 0);

		assertTrue(EpicsSeverity.MAJOR.compareTo(EpicsSeverity.INVALID) < 0);
		assertTrue(EpicsSeverity.MAJOR.compareTo(EpicsSeverity.MAJOR) == 0);
		assertTrue(EpicsSeverity.MAJOR.compareTo(EpicsSeverity.MINOR) > 0);
		assertTrue(EpicsSeverity.MAJOR.compareTo(EpicsSeverity.NO_ALARM) > 0);

		assertTrue(EpicsSeverity.MINOR.compareTo(EpicsSeverity.INVALID) < 0);
		assertTrue(EpicsSeverity.MINOR.compareTo(EpicsSeverity.MAJOR) < 0);
		assertTrue(EpicsSeverity.MINOR.compareTo(EpicsSeverity.MINOR) == 0);
		assertTrue(EpicsSeverity.MINOR.compareTo(EpicsSeverity.NO_ALARM) > 0);

		assertTrue(EpicsSeverity.NO_ALARM.compareTo(EpicsSeverity.INVALID) < 0);
		assertTrue(EpicsSeverity.NO_ALARM.compareTo(EpicsSeverity.MAJOR) < 0);
		assertTrue(EpicsSeverity.NO_ALARM.compareTo(EpicsSeverity.MINOR) < 0);
		assertTrue(EpicsSeverity.NO_ALARM.compareTo(EpicsSeverity.NO_ALARM) == 0);
	}

	@Test
	public void testParseSeverity() throws Exception {
		assertSame(EpicsSeverity.MAJOR, EpicsSeverity.parseSeverity("MAJOR"));
		assertSame(EpicsSeverity.MINOR, EpicsSeverity.parseSeverity("MINOR"));
		assertSame(EpicsSeverity.INVALID, EpicsSeverity.parseSeverity("INVALID"));
		assertSame(EpicsSeverity.NO_ALARM, EpicsSeverity.parseSeverity("NO_ALARM"));

		// unknown strings will return UNKNOWN
		assertSame(EpicsSeverity.UNKNOWN, EpicsSeverity.parseSeverity("foo"));
	}
}
