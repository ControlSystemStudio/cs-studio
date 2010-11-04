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

import org.csstudio.domain.desy.alarm.epics.EpicsAlarm;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Joerg Rathlev
 */
public class SeverityTest {

    @Test
    public void testGetLowest() {
        Assert.assertEquals(EpicsAlarm.UNKNOWN, EpicsAlarm.getLowest());
    }

	@Test
	public void testIsAlarm() throws Exception {
		// All severities except NO_ALARM are alarms.
		assertFalse(EpicsAlarm.NO_ALARM.isAlarm());
		assertTrue(EpicsAlarm.INVALID.isAlarm());
		assertTrue(EpicsAlarm.MINOR.isAlarm());
		assertTrue(EpicsAlarm.MAJOR.isAlarm());
	}

	@Test
	public void testOrder() throws Exception {

	    // FIXME (jpenning) (bknerr) : Don't rely on ordering of enums : ANTIPATTERN

		// The order should be:
		// INVALID >  MAJOR > MINOR > NO_ALARM

		assertTrue(EpicsAlarm.INVALID.compareTo(EpicsAlarm.INVALID) == 0);
		assertTrue(EpicsAlarm.INVALID.compareTo(EpicsAlarm.MAJOR) > 0);
		assertTrue(EpicsAlarm.INVALID.compareTo(EpicsAlarm.MINOR) > 0);
		assertTrue(EpicsAlarm.INVALID.compareTo(EpicsAlarm.NO_ALARM) > 0);

		assertTrue(EpicsAlarm.MAJOR.compareTo(EpicsAlarm.INVALID) < 0);
		assertTrue(EpicsAlarm.MAJOR.compareTo(EpicsAlarm.MAJOR) == 0);
		assertTrue(EpicsAlarm.MAJOR.compareTo(EpicsAlarm.MINOR) > 0);
		assertTrue(EpicsAlarm.MAJOR.compareTo(EpicsAlarm.NO_ALARM) > 0);

		assertTrue(EpicsAlarm.MINOR.compareTo(EpicsAlarm.INVALID) < 0);
		assertTrue(EpicsAlarm.MINOR.compareTo(EpicsAlarm.MAJOR) < 0);
		assertTrue(EpicsAlarm.MINOR.compareTo(EpicsAlarm.MINOR) == 0);
		assertTrue(EpicsAlarm.MINOR.compareTo(EpicsAlarm.NO_ALARM) > 0);

		assertTrue(EpicsAlarm.NO_ALARM.compareTo(EpicsAlarm.INVALID) < 0);
		assertTrue(EpicsAlarm.NO_ALARM.compareTo(EpicsAlarm.MAJOR) < 0);
		assertTrue(EpicsAlarm.NO_ALARM.compareTo(EpicsAlarm.MINOR) < 0);
		assertTrue(EpicsAlarm.NO_ALARM.compareTo(EpicsAlarm.NO_ALARM) == 0);
	}

	@Test
	public void testParseSeverity() throws Exception {
		assertSame(EpicsAlarm.MAJOR, EpicsAlarm.parseAlarm("MAJOR"));
		assertSame(EpicsAlarm.MINOR, EpicsAlarm.parseAlarm("MINOR"));
		assertSame(EpicsAlarm.INVALID, EpicsAlarm.parseAlarm("INVALID"));
		assertSame(EpicsAlarm.NO_ALARM, EpicsAlarm.parseAlarm("NO_ALARM"));

		// unknown strings will return UNKNOWN
		assertSame(EpicsAlarm.UNKNOWN, EpicsAlarm.parseAlarm("foo"));
	}
}
