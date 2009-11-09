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
public class AlarmTest {
	
	@Test
	public void testAlarm() throws Exception {
		Alarm alarm = new Alarm("foo", Severity.MINOR);
		assertEquals("foo", alarm.getObjectName());
		assertEquals(Severity.MINOR, alarm.getSeverity());
	}
	
	@Test
	public void testMajorIsHigherSeverityThanMinor() throws Exception {
		Alarm major = new Alarm("foo", Severity.MAJOR);
		Alarm minor = new Alarm("foo", Severity.MINOR);
		
		assertTrue(major.severityHigherThan(minor));
		assertFalse(minor.severityHigherThan(major));
	}
	
	@Test
	public void testSeverityComparison() throws Exception {
		Alarm noalarm = new Alarm("foo", Severity.NO_ALARM);
		Alarm minor = new Alarm("foo", Severity.MINOR);
		Alarm major = new Alarm("foo", Severity.MAJOR);
		Alarm invalid = new Alarm("foo", Severity.INVALID);
		
		assertTrue(minor.severityHigherThan(noalarm));
		assertTrue(major.severityHigherThan(minor));
		assertTrue(invalid.severityHigherThan(major));

		// All alarms have a higher severity than nothing (null)
		assertTrue(noalarm.severityHigherThan(null));
		assertTrue(minor.severityHigherThan(null));
		assertTrue(major.severityHigherThan(null));
		assertTrue(invalid.severityHigherThan(null));
	}
}
