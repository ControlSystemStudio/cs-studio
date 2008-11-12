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

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.platform.libs.jms.MessageReceiver;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Preferences;


/**
 * Manages the connections to the JMS server.
 * 
 * @author Joerg Rathlev
 */
public class AlarmQueueSubscriber {
	
	/**
	 * The logger used by this class.
	 */
	private static final CentralLogger LOG = CentralLogger.getInstance();

	/**
	 * The first receiver.
	 */
	private MessageReceiver _receiver1;
	
	/**
	 * The second receiver.
	 */
	private MessageReceiver _receiver2;
	
	/**
	 * The alarm message listener.
	 */
	private AlarmMessageListener _listener;
	
	
	/**
	 * Creates a new alarm queue subscriber.
	 * @param tree the root node of the tree to which updates will be written.
	 */
	public AlarmQueueSubscriber(final SubtreeNode tree) {
		if (tree == null) {
			throw new NullPointerException("tree must not be null");
		}
		
		_listener = new AlarmMessageListener(new AlarmTreeUpdater(tree));
	}
	
	/**
	 * Sets the tree wo which updates will be applied.
	 * @param tree the root node of the tree.
	 */
	public final void setTree(final SubtreeNode tree) {
		// Note: this is called when the refresh feature of the tree view is
		// used. The new tree is read from the LDAP directory, and then the
		// existing listener is switched over to the new tree.
		
		if (tree == null) {
			throw new NullPointerException("tree must not be null");
		}

		_listener.setUpdater(new AlarmTreeUpdater(tree));
	}

	/**
	 * Connects to the JMS servers.
	 */
	public final void openConnection() {
		Preferences prefs = AlarmTreePlugin.getDefault().getPluginPreferences();
		String[] queues = prefs.getString(PreferenceConstants.JMS_QUEUE).split(",");
		try {
			_receiver1 = new MessageReceiver(
					prefs.getString(PreferenceConstants.JMS_CONTEXT_FACTORY_PRIMARY),
					prefs.getString(PreferenceConstants.JMS_URL_PRIMARY),
					queues);
			_receiver1.startListener(_listener);
		} catch (Exception e) {
			LOG.error(this, "Error initializing JMS listener for primary server.", e);
		}
		
		try {
			_receiver2 = new MessageReceiver(
					prefs.getString(PreferenceConstants.JMS_CONTEXT_FACTORY_SECONDARY),
					prefs.getString(PreferenceConstants.JMS_URL_SECONDARY),
					queues);
			_receiver2.startListener(_listener);
		} catch (Exception e) {
			LOG.error(this, "Error initializing JMS listener for secondary server.", e);
		}
	}

	/**
	 * Disconnects from the JMS servers.
	 */
	public final void closeConnection() {
		try {
			if (_receiver1 != null) {
				_receiver1.stopListening();
			}
		} catch (Exception e) {
			LOG.warn(this, "Error stopping primary JMS listener", e);
		}
		
		try {
			if (_receiver2 != null) {
				_receiver2.stopListening();
			}
		} catch (Exception e) {
			LOG.warn(this, "Error stopping secondary JMS listener", e);
		}
	}

}
