package org.csstudio.alarm.service.declaration;
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


import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Joerg Rathlev
 */
public class SeverityUnitTest {

    @Test
    public void testGetLowest() {
        Assert.assertEquals(EpicsAlarmSeverity.UNKNOWN, EpicsAlarmSeverity.getLowest());
    }

	@Test
	public void testOrder() throws Exception {

	    // FIXME (jpenning) (bknerr) : Don't rely on ordering of enums : ANTIPATTERN

		// The order should be:
		// INVALID >  MAJOR > MINOR > NO_ALARM

		assertTrue(EpicsAlarmSeverity.INVALID.compareTo(EpicsAlarmSeverity.INVALID) == 0);
		assertTrue(EpicsAlarmSeverity.INVALID.compareTo(EpicsAlarmSeverity.MAJOR) > 0);
		assertTrue(EpicsAlarmSeverity.INVALID.compareTo(EpicsAlarmSeverity.MINOR) > 0);
		assertTrue(EpicsAlarmSeverity.INVALID.compareTo(EpicsAlarmSeverity.NO_ALARM) > 0);

		assertTrue(EpicsAlarmSeverity.MAJOR.compareTo(EpicsAlarmSeverity.INVALID) < 0);
		assertTrue(EpicsAlarmSeverity.MAJOR.compareTo(EpicsAlarmSeverity.MAJOR) == 0);
		assertTrue(EpicsAlarmSeverity.MAJOR.compareTo(EpicsAlarmSeverity.MINOR) > 0);
		assertTrue(EpicsAlarmSeverity.MAJOR.compareTo(EpicsAlarmSeverity.NO_ALARM) > 0);

		assertTrue(EpicsAlarmSeverity.MINOR.compareTo(EpicsAlarmSeverity.INVALID) < 0);
		assertTrue(EpicsAlarmSeverity.MINOR.compareTo(EpicsAlarmSeverity.MAJOR) < 0);
		assertTrue(EpicsAlarmSeverity.MINOR.compareTo(EpicsAlarmSeverity.MINOR) == 0);
		assertTrue(EpicsAlarmSeverity.MINOR.compareTo(EpicsAlarmSeverity.NO_ALARM) > 0);

		assertTrue(EpicsAlarmSeverity.NO_ALARM.compareTo(EpicsAlarmSeverity.INVALID) < 0);
		assertTrue(EpicsAlarmSeverity.NO_ALARM.compareTo(EpicsAlarmSeverity.MAJOR) < 0);
		assertTrue(EpicsAlarmSeverity.NO_ALARM.compareTo(EpicsAlarmSeverity.MINOR) < 0);
		assertTrue(EpicsAlarmSeverity.NO_ALARM.compareTo(EpicsAlarmSeverity.NO_ALARM) == 0);
	}

	@Test
	public void testParseSeverity() throws Exception {
		assertSame(EpicsAlarmSeverity.MAJOR, EpicsAlarmSeverity.parseSeverity("MAJOR"));
		assertSame(EpicsAlarmSeverity.MINOR, EpicsAlarmSeverity.parseSeverity("MINOR"));
		assertSame(EpicsAlarmSeverity.INVALID, EpicsAlarmSeverity.parseSeverity("INVALID"));
		assertSame(EpicsAlarmSeverity.NO_ALARM, EpicsAlarmSeverity.parseSeverity("NO_ALARM"));

		// unknown strings will return UNKNOWN
		assertSame(EpicsAlarmSeverity.UNKNOWN, EpicsAlarmSeverity.parseSeverity("foo"));
	}
}
