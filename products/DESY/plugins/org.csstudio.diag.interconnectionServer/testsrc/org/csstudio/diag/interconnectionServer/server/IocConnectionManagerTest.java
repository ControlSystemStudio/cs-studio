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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.net.InetAddress;

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class IocConnectionManagerTest {

	@Test
	@Ignore("Test used dedicated methods in production code.")
	public void testGetIocConnection() throws Exception {
//		IIocDirectory directory = Mockito.mock(IIocDirectory.class);
//		Mockito.when(directory.getLogicalIocName(InetAddress.getByName("127.0.0.1"), "localhost"))
//			.thenReturn(new String[] {"logicalName", "ldapName"});

	    // FIXME (bknerr) : rewrite the test that it does not utilise dedicated 'test' methods in the
	    // production code
		final IocConnectionManager cm = IocConnectionManager.INSTANCE;
		final InetAddress ipAddress = InetAddress.getByName("127.0.0.1");
		final String hostname = ipAddress.getHostName();
		final IocConnection conn = cm.getIocConnection(ipAddress, 123);
		assertNotNull(conn);
		assertEquals(hostname, conn.getHost());
		assertEquals(123, conn.getPort());
		assertEquals("logicalName", conn.getLogicalIocName());
		assertEquals("ldapName", conn.getLdapIocName());

		// Multiple requests must return the same IocConnection instance
		assertSame(conn, cm.getIocConnection(ipAddress, 123));
	}

}
