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

package org.csstudio.alarm.treeView.jms;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.JMSException;
import javax.jms.Session;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.utility.jms.IConnectionMonitor;
import org.csstudio.platform.utility.jms.sharedconnection.IMessageListenerSession;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;
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
	private final CentralLogger _log = CentralLogger.getInstance();
	
	/**
	 * The alarm message listener.
	 */
	private AlarmMessageListener _listener;
	
	/**
	 * The listener session used to listen to the JMS messages.
	 */
	private IMessageListenerSession _listenerSession;
	
	/**
	 * The connection monitors.
	 */
	private List<IConnectionMonitor> _monitors;
	
	/**
	 * Creates a new JMS connector.
	 */
	public JmsConnector() {
		_listener = new AlarmMessageListener();
		_monitors = new CopyOnWriteArrayList<IConnectionMonitor>();
	}
	
	/**
	 * Sets the tree to which updates will be applied. If set to
	 * <code>null</code>, updates are queued and will be applied later when a
	 * new update target is set by calling this method.
	 * 
	 * @param target
	 *            the root node of the tree, or <code>null</code>.
	 */
	public void setUpdateTarget(final SubtreeNode target) {
		if (target == null) {
			_listener.setUpdater(null);
		} else {
			_listener.setUpdater(new AlarmTreeUpdater(target));
		}
	}

	/**
	 * Adds the specified connection monitor to this connector.
	 * 
	 * @param monitor
	 *            the connection monitor.
	 */
	public void addConnectionMonitor(final IConnectionMonitor monitor) {
		_monitors.add(monitor);
		if (_listenerSession != null) {
			_listenerSession.addMonitor(monitor);
		}
	}

	/**
	 * Removes the specified connection monitor from this connector.
	 * 
	 * @param monitor
	 *            the connection monitor.
	 */
	public void removeConnectionMonitor(final IConnectionMonitor monitor) {
		_monitors.remove(monitor);
		if (_listenerSession != null) {
			_listenerSession.removeMonitor(monitor);
		}
	}

	/**
	 * Connects to the JMS servers. This method blocks until the connection is
	 * established.
	 * 
	 * @throws JmsConnectionException
	 *             if the connection could not be established.
	 */
	public void connect() throws JmsConnectionException {
		IPreferencesService prefs = Platform.getPreferencesService();
		String[] topics = prefs.getString(AlarmTreePlugin.PLUGIN_ID,
				PreferenceConstants.JMS_TOPICS, "", null).split(",");
		
		try {
			_listenerSession = SharedJmsConnections.startMessageListener(
					_listener, topics, Session.AUTO_ACKNOWLEDGE);
			
			// If there already are listeners registered at this connector,
			// these listeners are now added to the listener session and, if the
			// session is active, their onConnected method is called.
			for (IConnectionMonitor monitor : _monitors) {
				_listenerSession.addMonitor(monitor);
				if (_listenerSession.isActive()) {
					monitor.onConnected();
				}
			}
		} catch (JMSException e) {
			_log.error(this,
					"Could not create listener session using the shared JMS connections");
			throw new JmsConnectionException(
					"Could not create listener session using the shared JMS connections", e);
		}
	}
	
	/**
	 * Disconnects from the JMS servers.
	 */
	public void disconnect() {
		_log.debug(this, "Closing JMS connections.");
		_listenerSession.close();
		_listener.stop();
	}

}
