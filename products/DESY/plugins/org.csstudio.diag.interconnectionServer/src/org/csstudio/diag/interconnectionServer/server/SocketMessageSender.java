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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.naming.NamingException;

/**
 * Sends messages to an IOC via a datagram socket.
 *
 * @author Joerg Rathlev
 */
public class SocketMessageSender implements IIocMessageSender {

	private final InetAddress _address;
	private final DatagramSocket _socket;
	private final int _port;

	/**
	 * Creates a new message sender which will send messages to the specified
	 * address over the specified socket.
	 *
	 * @param address
	 *            the destination address for messages.
	 * @param port
	 *            the port.
	 * @param socket
	 *            the socket.
	 */
	public SocketMessageSender(final InetAddress address, final int port,
			final DatagramSocket socket) {
		_address = address;
		_socket = socket;
		_port = port;
	}

	/**
	 * {@inheritDoc}
	 * @throws NamingException
	 */
	public void send(final String message) throws NamingException {
		final byte[] networkMessage = (message + "\0").getBytes();
		final DatagramPacket packet = new DatagramPacket(networkMessage,
				networkMessage.length, _address, _port);
		try {
			_socket.send(packet);

			// TODO: should not have to call getter here. Refactor!
			IocConnectionManager.INSTANCE.getIocConnection(_address, _port).setTime(false); // false = sent
		} catch (final IOException e) {
			// XXX: Is this enough error handling?
			IocConnectionManager.INSTANCE.getIocConnection(_address, _port).incrementErrorCounter();
		}
	}

}
