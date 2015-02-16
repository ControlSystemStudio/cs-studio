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

import javax.jms.JMSException;
import javax.jms.MessageListener;

import org.csstudio.platform.utility.jms.Activator;
import org.csstudio.platform.utility.jms.preferences.PreferenceConstants;
import org.csstudio.platform.utility.jms.sharedconnection.IMessageListenerSession;
import org.csstudio.platform.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Service which manages shared connections for receiving JMS messages. The
 * settings for the connection are read from the preferences of the JMS Utility
 * plug-in.
 * 
 * @author Joerg Rathlev
 */
public class SharedReceiverConnectionService {
	
	/*
	 * The current implementation is hard-coded to use two connections.
	 * TODO: implement 1..n connections.
	 */
	
	private final MonitorableSharedConnection _connection1;
	private final MonitorableSharedConnection _connection2;
	
	/**
	 * Creates the service.
	 */
	public SharedReceiverConnectionService() {
		IPreferencesService prefs = Platform.getPreferencesService();
		String jmsUrl1 = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.RECEIVER_BROKER_URL_1,
				"", null);
		String jmsUrl2 = prefs.getString(Activator.PLUGIN_ID,
				PreferenceConstants.RECEIVER_BROKER_URL_2,
				"", null);
		_connection1 = new MonitorableSharedConnection(jmsUrl1);
		_connection2 = new MonitorableSharedConnection(jmsUrl2);
	}

	/**
	 * Returns handles to the shared connections.
	 * 
	 * @return handles to the shared connections.
	 * @throws JMSException
	 *             if one of the underlying shared connections could not be
	 *             created or started due to an internal error.
	 */
	public ISharedConnectionHandle[] sharedConnections() throws JMSException {
		ISharedConnectionHandle[] result = new ISharedConnectionHandle[2];
		result[0] = _connection1.createHandle();
		try {
			result[1] = _connection2.createHandle();
		} catch (JMSException e) {
			// An exception occured when trying to create the second connection.
			// Clean up the first connection, then rethrow the exception.
			result[0].release();
			throw e;
		}
		return result;
	}

	/**
	 * Starts a message listener that will listen on the shared receiver
	 * connections.
	 * 
	 * @param listener
	 *            the listener.
	 * @param topics
	 *            the topics to subscribe to.
	 * @param acknowledgeMode
	 *            the JMS session acknowledgement mode. Legal values are
	 *            <code>Session.AUTO_ACKNOWLEDGE</code>,
	 *            <code>Session.CLIENT_ACKNOWLEDGE</code>, and
	 *            <code>Session.DUPS_OK_ACKNOWLEDGE</code>.
	 * @return An <code>IMessageListenerSession</code> which can be used to
	 *         control the listener session.
	 * @throws JMSException
	 *             if an internal error occured in the underlying JMS provider.
	 */
	public IMessageListenerSession startSharedConnectionMessageListener(
			MessageListener listener, String[] topics, int acknowledgeMode)
			throws JMSException {
		return MultiConnectionReceiver.createListenerSession(
				sharedConnections(), listener, topics, acknowledgeMode);
		// TODO: The shared connection handles used by the listener session are
		// never released. That should be changed.
	}

}
