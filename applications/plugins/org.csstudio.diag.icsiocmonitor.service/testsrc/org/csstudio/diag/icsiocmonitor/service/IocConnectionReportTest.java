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

package org.csstudio.diag.icsiocmonitor.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class IocConnectionReportTest {

	@Test
	public void testConstructorAndGetters() throws Exception {
		List<IocConnectionReportItem> items = new ArrayList<IocConnectionReportItem>();
		items.add(new IocConnectionReportItem("ioc1.example.com", "ioc1", IocConnectionState.CONNECTED));
		IocConnectionReport r = new IocConnectionReport("server", items);
		assertEquals("server", r.getReportingServer());
		assertEquals(1, r.getItems().size());
		IocConnectionReportItem item0 = r.getItems().get(0);
		assertEquals("ioc1.example.com", item0.getIocHostname());
		assertEquals("ioc1", item0.getIocName());
		assertEquals(IocConnectionState.CONNECTED, item0.getConnectionState());
	}
	
	@Test(expected = NullPointerException.class)
	public void testReportingServerIsNull() throws Exception {
		new IocConnectionReport(null, Collections.<IocConnectionReportItem>emptyList());
	}
	
	@Test(expected = NullPointerException.class)
	public void testIocStatesIsNull() throws Exception {
		new IocConnectionReport("server", null);
	}
}
