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

package org.csstudio.diag.icsiocmonitor.ui.internal.model;

import static org.junit.Assert.*;

import org.csstudio.diag.icsiocmonitor.service.IocConnectionState;
import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class IocStateTest {

	@Test
	public void testContructionAndIocName() throws Exception {
		IocState s = new IocState("ioc");
		assertEquals("ioc", s.getIocName());
	}
	
	@Test
	public void testSetAndGetIocConnectionState() throws Exception {
		IocState s = new IocState("ioc");
		s.setIcsConnectionState("ics1", IocConnectionState.CONNECTED);
		s.setIcsConnectionState("ics2", IocConnectionState.DISCONNECTED);
		assertEquals(IocConnectionState.CONNECTED, s.getIcsConnectionState("ics1"));
		assertEquals(IocConnectionState.DISCONNECTED, s.getIcsConnectionState("ics2"));
		
		// State of a server which is not set explicitly is DISCONNECTED:
		assertEquals(IocConnectionState.DISCONNECTED, s.getIcsConnectionState("foo"));
	}
	
	@Test
	public void testGetSelectedInterconnectionServer() throws Exception {
		IocState s = new IocState("ioc");
		s.setIcsConnectionState("ics1", IocConnectionState.CONNECTED);
		s.setIcsConnectionState("ics2", IocConnectionState.CONNECTED_SELECTED);
		assertEquals("ics2", s.getSelectedInterconnectionServer());

		s.setIcsConnectionState("ics2", IocConnectionState.CONNECTED);
		assertNull(s.getSelectedInterconnectionServer());
	}
}
