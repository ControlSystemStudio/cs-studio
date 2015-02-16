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

package org.csstudio.platform.internal.utility.jms.sharedconnection;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.platform.utility.jms.IConnectionMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

/**
 * Helper class for implementing classes that support being monitored with
 * {@link IConnectionMonitor}. Instances of this class keep track of a list of
 * connection monitors that have been added and can send notifications to those
 * listeners.
 * 
 * @author Joerg Rathlev
 */
class ConnectionMonitorSupport {

	private List<IConnectionMonitor> _monitors;
	
	/**
	 * Creates a new <code>ConnectionMonitorSupport</code> instance.
	 */
	public ConnectionMonitorSupport() {
		// A CopyOnWriteArrayList is optimized for frequent, concurrent reads
		// and infrequent writes, which makes it a good choice for lists of
		// listeners.
		_monitors = new CopyOnWriteArrayList<IConnectionMonitor>();
	}

	/**
	 * Adds a monitor to the list of monitors.
	 * 
	 * @param monitor
	 *            a monitor.
	 */
	public void addMonitor(IConnectionMonitor monitor) {
		_monitors.add(monitor);
	}

	/**
	 * Removes the specified monitor from the list of monitors.
	 * 
	 * @param monitor
	 *            the monitor to remove.
	 */
	public void removeMonitor(IConnectionMonitor monitor) {
		_monitors.remove(monitor);
	}
	
	/**
	 * Calls the <code>onConnected</code> method on all listeners.
	 * 
	 * @see IConnectionMonitor#onConnected()
	 */
	public void fireConnectedEvent() {
		for (final IConnectionMonitor monitor : _monitors) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// nothing to do
				}
				public void run() throws Exception {
					monitor.onConnected();
				}
			});
		}
	}
	
	/**
	 * Calls the <code>onDisconnected</code> method on all listeners.
	 * 
	 * @see IConnectionMonitor#onDisconnected()
	 */
	public void fireDisconnectedEvent() {
		for (final IConnectionMonitor monitor : _monitors) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// nothing to do
				}
				public void run() throws Exception {
					monitor.onDisconnected();
				}
			});
		}
	}
	
}
