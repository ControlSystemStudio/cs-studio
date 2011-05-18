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

package org.csstudio.alarm.treeview.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class AlarmUnitTest {

	private final Date t1 = new Date(0);
	private final Date t2 = new Date(1);

	@Test
	public void testAlarm() throws Exception {
		final Alarm alarm = new Alarm("foo", EpicsAlarmSeverity.MINOR, t1);
		assertEquals("foo", alarm.getObjectName());
		assertEquals(EpicsAlarmSeverity.MINOR, alarm.getSeverity());
	}

	@Test
	public void testEventtimeComparison() throws Exception {
		final Alarm a1 = new Alarm("foo", EpicsAlarmSeverity.MINOR, t1);
		final Alarm a2 = new Alarm("foo", EpicsAlarmSeverity.MINOR, t2);

		assertTrue(a2.occuredAfter(a1));

		assertTrue(a1.occuredAfter(null));
		assertTrue(a2.occuredAfter(null));
	}
}
