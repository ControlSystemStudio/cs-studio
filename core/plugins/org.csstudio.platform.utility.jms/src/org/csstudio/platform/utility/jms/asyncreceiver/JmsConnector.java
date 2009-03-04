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

package org.csstudio.platform.utility.jms.asyncreceiver;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.MessageListener;

import org.csstudio.platform.utility.jms.Activator;
import org.csstudio.platform.utility.jms.IConnectionMonitor;
import org.csstudio.platform.utility.jms.preferences.PreferenceConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Manages the connections to the JMS servers.
 * 
 * @author Joerg Rathlev
 */
public final class JmsConnector {

	/**
	 * The logger used by this object.
	 */
	// private final CentralLogger _log = CentralLogger.getInstance();
	/**
	 * The alarm message listener.
	 */
	private MessageListener _listener;

	/**
	 * The connection monitors attached to this connector.
	 */
	private List<IConnectionMonitor> _connectionMonitors;

	/**
	 * The JMS connections held by this connector.
	 */
	private JmsConnection[] _connections;

	/**
	 * Whether this connector is connected, that is, whether all of its
	 * underlying JMS connections are connected.
	 */
	private boolean _connected;

	/**
	 * Creates a new JMS connector.
	 */
	public JmsConnector(MessageListener listener) {
		_listener = listener;
		_connectionMonitors = new CopyOnWriteArrayList<IConnectionMonitor>();
		_connections = new JmsConnection[2];
		_connected = false;
	}

	/**
	 * Adds the specified connection monitor to this connector.
	 * 
	 * @param monitor
	 *            the connection monitor.
	 */
	public void addConnectionMonitor(final IConnectionMonitor monitor) {
		_connectionMonitors.add(monitor);
	}

	/**
	 * Removes the specified connection monitor from this connector.
	 * 
	 * @param monitor
	 *            the connection monitor.
	 */
	public void removeConnectionMonitor(final IConnectionMonitor monitor) {
		_connectionMonitors.remove(monitor);
	}

	/**
	 * Notifies the connection monitors that the connection was established.
	 */
	private void fireConnectedEvent() {
		for (IConnectionMonitor monitor : _connectionMonitors) {
			monitor.onConnected();
		}
	}

	/**
	 * Notifies the connection monitors that the connection was closed or
	 * interrupted.
	 */
	private void fireDisconnectedEvent() {
		for (IConnectionMonitor monitor : _connectionMonitors) {
			monitor.onDisconnected();
		}
	}

	/**
	 * Called by the connection of this connector when their connection state
	 * changes. Updates the connection state of this connector and notifies the
	 * connection monitors if the state has changed.
	 */
	void onConnectionStateChanged() {
		boolean connectedNow = true;
		for (JmsConnection connection : _connections) {
			connectedNow &= (connection != null ? connection.isConnected()
					: false);
		}

		// If this connector was connected but is not connected now, or if it
		// was not connected but is connected now, fire the appropriate event.
		if (_connected && !connectedNow) {
			// _log.debug(this, "Connection state changed to disconnected.");
			fireDisconnectedEvent();
		} else if (!_connected && connectedNow) {
			// _log.debug(this, "Connection state changed to connected.");
			fireConnectedEvent();
		}
		_connected = connectedNow;
	}

	/**
	 * Connects to the JMS servers. This method blocks until the connection to
	 * both servers is established.
	 * 
	 * @param monitor
	 *            a progress monitor. Connecting to the first and second JMS
	 *            server are reported as subtasks to the monitor.
	 * @throws JmsConnectionException
	 *             if the connection could not be established.
	 */
	public void connect(String[] topics,
			String url1, String url2) throws JmsConnectionException {
		IPreferencesService prefs = Platform.getPreferencesService();
		// String[] topics = prefs.getString(Activator.PLUGIN_ID,
		// PreferenceConstants.JMS_TOPICS, "", null).split(",");
		if (url1 == null) {
			url1 = prefs.getString(Activator.PLUGIN_ID,
					PreferenceConstants.JMS_URL_PRIMARY, "", null);
		}
		if (url2 == null) {
			url2 = prefs.getString(Activator.PLUGIN_ID,
					PreferenceConstants.JMS_URL_SECONDARY, "", null);
		}
		_connections[0] = new JmsConnection(this, url1, topics, _listener);
		try {
			_connections[0].start();
		} catch (JmsConnectionException e) {
			// _log.error(this,
			// "Could not establish JMS connection to first broker.", e);
			throw e;
		}

		_connections[1] = new JmsConnection(this, url2, topics, _listener);
		try {
			_connections[1].start();
		} catch (JmsConnectionException e) {
			// _log.error(this,
			// "Could not establish JMS connection to second broker.", e);
			// Close the first connection before rethrowing.
			_connections[0].close();
			throw e;
		}
	}

	/**
	 * Disconnects from the JMS servers.
	 */
	public void disconnect() {
		// _log.debug(this, "Closing JMS connections.");
		for (int i = 0; i < 2; i++) {
			if (_connections[i] != null) {
				_connections[i].close();
				_connections[i] = null;
			}
		}
		// _listener.stop();
	}

}
