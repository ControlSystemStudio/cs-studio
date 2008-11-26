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

import java.io.IOException;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.transport.TransportListener;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
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
	private final CentralLogger _log = CentralLogger.getInstance();
	
	/**
	 * The alarm message listener.
	 */
	private AlarmMessageListener _listener;

	/**
	 * The connections held by this object.
	 */
	private Connection[] _connection;

	/**
	 * The sessions held by this object.
	 */
	private Session[] _session;
	
	/**
	 * Creates a new JMS connector.
	 */
	public JmsConnector() {
		_listener = new AlarmMessageListener();
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
	 * Connects to the JMS servers. This method blocks until the connection to
	 * both servers is established.
	 * 
	 * @param monitor
	 *            a progress monitor. Connecting to the first and second JMS
	 *            server are reported as subtasks to the monitor.
	 */
	public void connect(final IProgressMonitor monitor) {
		IPreferencesService prefs = Platform.getPreferencesService();
		String[] topics = prefs.getString(AlarmTreePlugin.PLUGIN_ID,
				PreferenceConstants.JMS_TOPICS, "", null).split(",");
		String factory1 = prefs.getString(AlarmTreePlugin.PLUGIN_ID, PreferenceConstants.JMS_CONTEXT_FACTORY_PRIMARY, "", null);
		String url1 = prefs.getString(AlarmTreePlugin.PLUGIN_ID, PreferenceConstants.JMS_URL_PRIMARY, "", null);
		String factory2 = prefs.getString(AlarmTreePlugin.PLUGIN_ID, PreferenceConstants.JMS_CONTEXT_FACTORY_SECONDARY, "", null);
		String url2 = prefs.getString(AlarmTreePlugin.PLUGIN_ID, PreferenceConstants.JMS_URL_SECONDARY, "", null);
		
		_session = new Session[2];
		_connection = new Connection[2];
		
		// connect to the first server
		monitor.subTask("First server");
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, factory1);
		properties.put(Context.PROVIDER_URL, url1);
		try {
			Context context = new InitialContext(properties);
			ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
			boolean connected = false;
			while (!connected && !monitor.isCanceled()) {
				_log.debug(this, "Trying to connect to first JMS server.");
				connected = connect(factory, topics, 0);
			}
			if (connected) {
				_log.info(this, "Connected to first JMS server.");
			}
		} catch (NamingException e) {
			_log.error(this, "Error getting connection factory for primary JMS server.", e);
		}
		
		// connect to the second server
		monitor.subTask("Second server");
		properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, factory2);
		properties.put(Context.PROVIDER_URL, url2);
		try {
			Context context = new InitialContext(properties);
			ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
			boolean connected = false;
			while (!connected && !monitor.isCanceled()) {
				_log.debug(this, "Trying to connect to second JMS server.");
				connected = connect(factory, topics, 1);
			}
			if (connected) {
				_log.info(this, "Connected to second JMS server.");
			}
		} catch (NamingException e) {
			_log.error(this, "Error getting connection factory for secondary JMS server.", e);
		}
		
		// XXX the following code is for testing purposes only
		try {
			_connection[0].setExceptionListener(new ExceptionListener() {
				public void onException(JMSException exception) {
					System.err.println("JmsConnector: Exception listener of _connection[0] received an exception:");
					exception.printStackTrace();
				}
			});
			System.out.println("JmsConnector: Exception listener added to _connection[0]");
		} catch (JMSException e) {
			System.err.println("JmsConnector: Could not set exception listener on _connection[0]");
		}
		
		ActiveMQConnection conn = (ActiveMQConnection) _connection[0];
		conn.addTransportListener(new TransportListener() {
			public void onCommand(Object command) {
				System.out.println("JmsConnector: TransportListener.onCommand called with: " + command);
			}

			public void onException(IOException error) {
				System.out.println("JmsConnector: TransportListener.onException called with: " + error);
			}

			public void transportInterupted() {
				System.out.println("JmsConnector: TransportListener.transportInterrupted called.");
			}

			public void transportResumed() {
				System.out.println("JmsConnector: TransportListener.transportResumed called.");
			}
		});
	}

	/**
	 * Creates a connection to a JMS server.
	 * 
	 * @param connectionFactory
	 *            the connection factory to use.
	 * @param topics
	 *            the topics to connect the listener to.
	 * @param index
	 *            the index of the server (0 or 1).
	 * @return <code>true</code> if the connection succeeded, <code>false</code>
	 *         otherwise.
	 */
	private boolean connect(final ConnectionFactory connectionFactory,
			final String[] topics, final int index) {
		try {
			_connection[index] = connectionFactory.createConnection();
			_session[index] = _connection[index].createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination[] topics1 = new Destination[2];
			MessageConsumer[] consumers = new MessageConsumer[2];
			for (int i = 0; i < topics.length; i++) {
				topics1[i] = _session[index].createTopic(topics[i]);
				consumers[i] = _session[index].createConsumer(topics1[i]);
				consumers[i].setMessageListener(_listener);
			}
			_connection[index].start();
			return true;
		} catch (JMSException e) {
			_log.warn(this, "Error connecting to JMS server.", e);
			return false;
		}
	}
	
	/**
	 * Disconnects from the JMS servers.
	 */
	public void disconnect() {
		_log.debug(this, "Closing JMS connections.");
		for (int i = 0; i < 2; i++) {
			if (_connection[i] != null) {
				try {
					// Closing the connection will also close the session and
					// the message consumers.
					_connection[i].close();
				} catch (JMSException e) {
					_log.warn(this, "Error while closing JMS connection.", e);
				}
			}
		}
		
		_listener.stop();
		
		// free the arrays
		_connection = null;
		_session = null;
	}

}
