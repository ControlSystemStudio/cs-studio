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
package org.csstudio.config.savevalue.service;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

/**
 * Custom socket factory that creates clients sockets with short (5 seconds)
 * timeouts. The server sockets returned by this factory use the default
 * timeout.
 * 
 * @author Joerg Rathlev
 */
public class SocketFactory implements RMIClientSocketFactory,
		RMIServerSocketFactory, Serializable {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -5374474110426849095L;
	
	/**
	 * The timeout value, in milliseconds.
	 */
	private static final int TIMEOUT = 5000;
	
	/**
	 * {@inheritDoc}
	 */
	public final Socket createSocket(final String host, final int port) throws IOException {
		Socket s = new Socket(host, port);
		s.setSoTimeout(TIMEOUT);
		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	public final ServerSocket createServerSocket(final int port) throws IOException {
		ServerSocket s = new ServerSocket(port);
		return s;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final int hashCode() {
		return getClass().hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final boolean equals(final Object o) {
		return (getClass() == o.getClass());
	}

}
