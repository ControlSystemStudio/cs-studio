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

package org.csstudio.platform.utility.jms.sharedconnection;

import org.csstudio.platform.utility.jms.IConnectionMonitor;

/**
 * <p>
 * Allows a client to control a <code>MessageListener</code> which uses the
 * shared JMS connection services. Clients can use this interface to monitor the
 * availability of the underlying JMS connections, and to close the session
 * when they no longer want to receive any messages.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Joerg Rathlev
 */
public interface IMessageListenerSession {

	/**
	 * Closes this message listener session. Clients will no longer receive any
	 * messages after they have closed this session.
	 */
	void close();

	/**
	 * Adds a connection monitor which monitors the availability of the
	 * underlying JMS connections.
	 * 
	 * @param monitor
	 *            a connection monitor.
	 */
	void addMonitor(IConnectionMonitor monitor);

	/**
	 * Removes the specified connection monitor from this session.
	 * 
	 * @param monitor
	 *            the monitor to remove.
	 */
	void removeMonitor(IConnectionMonitor monitor);

	/**
	 * Returns whether all of the underlying JMS connections are currently
	 * active.
	 * 
	 * @return <code>true</code> if all underlying connections are started and
	 *         not interrupted, <code>false</code> otherwise.
	 */
	boolean isActive();
}
