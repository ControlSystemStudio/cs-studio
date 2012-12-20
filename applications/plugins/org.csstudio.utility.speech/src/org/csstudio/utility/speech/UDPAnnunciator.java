/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.speech;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/** Annnuciator implementation that sends
 *  text to a UDP server, which then
 *  performs the annunciations.
 *  
 *  <p>This can be tested with 'netcat',
 *  listening on a port for UDP messages
 *  and printing them out:
 *  <pre>
 *   nc -l -p 6543 -u
 *  </pre>
 *  
 *  @author Kay Kasemir
 */
public class UDPAnnunciator extends BaseAnnunciator
{
	private DatagramSocket socket;
	private InetAddress address;
	private int port;

	/** Initialize
	 *  @param host Destination host or empty for broadcast
	 *  @param port Destination port
	 *  @throws Exception on error
	 */
	public UDPAnnunciator(final String host, final int port) throws Exception
	{
		socket = new DatagramSocket();
		socket.setBroadcast(true);
		address = InetAddress.getByName(host);
		this.port = port;
	}

	@Override
	public void say(final String something) throws Exception
	{
		final String text = applyTranslations(something + "\n"); //$NON-NLS-1$
		
		final byte[] bytes = text.getBytes();
		DatagramPacket packet = new DatagramPacket(
				bytes, bytes.length,
				address, port);
		socket.send(packet);
	}

	@Override
	public void close()
	{
		socket.close();
	}
}
