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

import org.csstudio.platform.utility.jms.Activator;
import org.csstudio.platform.utility.jms.preferences.PreferenceConstants;
import org.csstudio.platform.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Service which manages a shared connection for sending JMS messages. The
 * settings for the connection are read from the preferences of the JMS Utility
 * plug-in.
 * 
 * @author Joerg Rathlev
 */
public class SharedSenderConnectionService {
	
	private MonitorableSharedConnection _connection;

	/**
	 * {@inheritDoc}
	 */
	public ISharedConnectionHandle sharedConnection() throws JMSException {
		createSharedConnectionIfNecessary();
		return _connection.createHandle();
	}

	/**
	 * Creates and starts the shared connection if it has not been created yet.
	 */
	private synchronized void createSharedConnectionIfNecessary() {
		if (_connection == null) {
			IPreferencesService prefs = Platform.getPreferencesService();
			String jmsUrl = prefs.getString(Activator.PLUGIN_ID,
					PreferenceConstants.SENDER_BROKER_URL,
					// FIXME: the preference initializer does not seem to work
					"failover:(tcp://krykjmsb.desy.de:64616,tcp://krykjmsa.desy.de:62616)?maxReconnectDelay=5000",
					null);
			_connection = new MonitorableSharedConnection(jmsUrl);
		}
	}
}
