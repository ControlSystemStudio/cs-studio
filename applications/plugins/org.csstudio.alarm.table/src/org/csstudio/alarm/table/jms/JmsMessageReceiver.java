/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.alarm.table.jms;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.utility.jms.sharedconnection.IMessageListenerSession;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;

/**
 * This class handles the receiver jms connection (via the shared jsm connection
 * in css platform) New messages were added to the model (_messageList).
 * 
 * @author jhatje
 * 
 */
public class JmsMessageReceiver implements MessageListener {

	/**
	 * List of messages displayed in the table.
	 */
	JMSMessageList _messageList;

	/**
	 * JMS Session for the listener
	 */
	IMessageListenerSession _listenerSession;

	public JmsMessageReceiver(JMSMessageList messageList) {
		this._messageList = messageList;
	}

	/**
	 * A new message is received. Add it to the model.
	 */
	public void onMessage(final Message message) {
		if (message == null) {
			JmsLogsPlugin.logError("Message == null");
		}
		try {
			if (message instanceof TextMessage) {
				JmsLogsPlugin.logError("received message is not a map message");
			} else if (message instanceof MapMessage) {
				final MapMessage mm = (MapMessage) message;
				_messageList.addJMSMessage(mm);
				CentralLogger.getInstance().debug(this, "received map message");
			} else {
				JmsLogsPlugin.logError("received message is an unknown type");
			}
		} catch (Exception e) {
			e.printStackTrace();
			JmsLogsPlugin.logException("JMS error: ", e); //$NON-NLS-1$
		}
	}

	/**
	 * Start jms message listener. If there is a previous session active (the
	 * user has edited the topics) it will be closed and a new session is
	 * created.
	 * 
	 * @param _deafultTopicSet
	 *            JMS topics to be monitored
	 */
	public void initializeJMSConnection(String defaultTopicSet) {
		String[] topicList = null;
		if ((defaultTopicSet == null) || (defaultTopicSet.length() == 0)) {
			CentralLogger.getInstance().error(this,
					"Could not initialize JMS Listener. JMS topics == NULL!");
		} else {
			topicList = defaultTopicSet.split(",");
		}
		try {
			if ((_listenerSession != null) && (_listenerSession.isActive())) {
				_listenerSession.close();
				_listenerSession = null;
			}
			_listenerSession = SharedJmsConnections.startMessageListener(this,
					topicList, Session.AUTO_ACKNOWLEDGE);
			CentralLogger.getInstance()
					.info(
							this,
							"Initialize JMS connection with topics: "
									+ defaultTopicSet);
		} catch (JMSException e) {
			CentralLogger.getInstance().error(this,
					"JMS Connection error: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			CentralLogger.getInstance().error(
					this,
					"JMS Connection error, invalid arguments: "
							+ e.getMessage());
		}
	}
}
