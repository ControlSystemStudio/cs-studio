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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * @author Joerg Rathlev
 *
 */
public class SocketMessageSenderTest {

	@Test
	@Ignore("Test used dedicated methods in production code.")
	public void testSend() throws Exception {
//		IIocDirectory directory = Mockito.mock(IIocDirectory.class);
//		Mockito.when(directory.getLogicalIocName(InetAddress.getByName("127.0.0.1"=, "localhost"))
//			.thenReturn(new String[] {"logicalName", "ldapName"});
		// This is only required to initialize the singleton connection manager
		// instance with the mocked directory implementation.
//		IocConnectionManager.getInstance(directory);

	       // FIXME (bknerr) : rewrite the test that it does not utilise dedicated 'test' methods in the
        // production code
		final InetAddress address = InetAddress.getByName("127.0.0.1");
		final DatagramSocket socket = Mockito.mock(DatagramSocket.class);

		final IIocMessageSender sender = new SocketMessageSender(address, 1234, socket);
		sender.send("Hello, world.");

		final Matcher<DatagramPacket> matchesExpectedDatagram = new BaseMatcher<DatagramPacket>() {

			public boolean matches(final Object item) {
				if (item instanceof DatagramPacket) {
					final DatagramPacket packet = (DatagramPacket) item;
					return packet.getAddress().equals(address)
						&& packet.getPort() == 1234
						&& new String(packet.getData()).equals("Hello, world.\0");
				}
				return false;
			}

			public void describeTo(final Description description) {
				description.appendText("packet with \"Hello, world.\"");
			}
		};
		Mockito.verify(socket).send(Mockito.argThat(matchesExpectedDatagram));
	}
}
