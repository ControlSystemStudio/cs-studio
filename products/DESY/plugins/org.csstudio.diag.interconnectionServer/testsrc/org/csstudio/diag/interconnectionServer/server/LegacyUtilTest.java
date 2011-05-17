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

package org.csstudio.diag.interconnectionServer.server;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;

import org.junit.Test;


/**
 * @author Joerg Rathlev
 *
 */
public class LegacyUtilTest {

	@Test
	public void testGregorianTimeDifference() throws Exception {
		GregorianCalendar t1 = new GregorianCalendar(2000, 0, 1, 12, 00, 00);
		GregorianCalendar t2 = new GregorianCalendar(2000, 0, 1, 12, 01, 00);
		assertEquals(60000, LegacyUtil.gregorianTimeDifference(t1, t2));
	}
	
	@Test
	public void testFormatDate() throws Exception {
		GregorianCalendar cal = new GregorianCalendar(2000, 0, 1, 12, 00, 00);
		assertEquals("2000-01-01 12:00:00.000", LegacyUtil.formatDate(cal.getTime()));
		
		cal.add(GregorianCalendar.MILLISECOND, 1);
		assertEquals("2000-01-01 12:00:00.001", LegacyUtil.formatDate(cal.getTime()));

		cal.add(GregorianCalendar.MILLISECOND, 99);
		assertEquals("2000-01-01 12:00:00.100", LegacyUtil.formatDate(cal.getTime()));
	}
}
