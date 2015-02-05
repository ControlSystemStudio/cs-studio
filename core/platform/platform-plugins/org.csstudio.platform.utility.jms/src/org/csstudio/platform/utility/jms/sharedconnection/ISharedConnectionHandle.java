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

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.csstudio.platform.utility.jms.IConnectionMonitor;

/**
 * <p>
 * Handle which provides access to a shared JMS connection. Clients can use this
 * handle to create sessions using the shared connection.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Joerg Rathlev
 */
public interface ISharedConnectionHandle {

	/**
	 * Creates a session object for the shared connection. It is the
	 * responsibility of the client to close the session when it is no longer
	 * needed.
	 * 
	 * @param transacted
	 *            indicates whether the session is transacted.
	 * @param acknowledgeMode
	 *            indicates whether the consumer or the client will acknowledge
	 *            any messages it receives; ignored if the session is
	 *            transacted. Legal values are
	 *            <code>Session.AUTO_ACKNOWLEDGE</code>,
	 *            <code>Session.CLIENT_ACKNOWLEDGE</code>, and
	 *            <code>Session.DUPS_OK_ACKNOWLEDGE</code>.
	 * @return a newly created session.
	 * @throws JMSException
	 *             if the shared <code>Connection</code> object fails to create
	 *             a session due to some internal error or lack of support for
	 *             the specific transaction and acknowledgement mode.
	 * @see Connection#createSession(boolean, int)
	 */
	Session createSession(boolean transacted, int acknowledgeMode)
			throws JMSException;

	/**
	 * Releases this handle. Clients should call this method when they no longer
	 * need the shared connection. Clients should explicitly close all sessions
	 * before calling this method.
	 */
	void release();

	/**
	 * Adds a connection monitor to the shared connection represented by this
	 * handle.
	 * 
	 * @param monitor
	 *            a connection monitor.
	 */
	void addMonitor(IConnectionMonitor monitor);

	/**
	 * Removes the specified connection monitor from the shared connection
	 * represented by this handle.
	 * 
	 * @param monitor
	 *            the monitor to remove.
	 */
	void removeMonitor(IConnectionMonitor monitor);

	/**
	 * Returns whether the shared connection represented by this handle is
	 * started and not interrupted.
	 * 
	 * @return <code>true</code> if the shared connection is started and not
	 *         interrupted, <code>false</code> otherwise.
	 */
	boolean isActive();
}
