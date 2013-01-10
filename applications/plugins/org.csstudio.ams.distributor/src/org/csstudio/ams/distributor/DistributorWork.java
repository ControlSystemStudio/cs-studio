
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

package org.csstudio.ams.distributor;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.csstudio.ams.AMSException;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.Utils;
import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.ams.dbAccess.configdb.AggrFilterActionDAO;
import org.csstudio.ams.dbAccess.configdb.AggrUserGroupDAO;
import org.csstudio.ams.dbAccess.configdb.AggrUserGroupTObject;
import org.csstudio.ams.dbAccess.configdb.AggrUserGroupUserTObject;
import org.csstudio.ams.dbAccess.configdb.FilterActionDAO;
import org.csstudio.ams.dbAccess.configdb.FilterActionTObject;
import org.csstudio.ams.dbAccess.configdb.FilterDAO;
import org.csstudio.ams.dbAccess.configdb.FilterTObject;
import org.csstudio.ams.dbAccess.configdb.MessageChainDAO;
import org.csstudio.ams.dbAccess.configdb.MessageChainTObject;
import org.csstudio.ams.dbAccess.configdb.MessageDAO;
import org.csstudio.ams.dbAccess.configdb.TopicDAO;
import org.csstudio.ams.dbAccess.configdb.TopicTObject;
import org.csstudio.ams.dbAccess.configdb.UserDAO;
import org.csstudio.ams.dbAccess.configdb.UserGroupDAO;
import org.csstudio.ams.dbAccess.configdb.UserGroupTObject;
import org.csstudio.ams.dbAccess.configdb.UserGroupUserDAO;
import org.csstudio.ams.dbAccess.configdb.UserGroupUserTObject;
import org.csstudio.ams.dbAccess.configdb.UserTObject;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.utility.jms.consumer.JmsRedundantConsumer;
import org.csstudio.utility.jms.publisher.JmsMultiplePublisher;
import org.csstudio.utility.jms.sharedconnection.SharedJmsConnections;
import org.eclipse.jface.preference.IPreferenceStore;

/*- FIXME Frage klaeren, warum das T_AMS_JMS immer in user feld steht,
 *  auch dieser Connector nicht angesteuert wird??? */
public class DistributorWork extends Thread implements AmsConstants,
		MessageListener {

	// TODO: Replace it with an enum!
    /** Derby database connection */
	private java.sql.Connection localAppDb;

    /** HSQL in-memory cache connection */
    private java.sql.Connection memoryCacheDb;

	// (application db)

	// jms internal communication
	// --- Sender connection ---

	// private JmsMultipleProducer amsSender;
	private JmsMultiplePublisher amsSender;

	// --- Receiver connection ---
	// private JmsRedundantReceiver amsReceiver;
	private JmsRedundantConsumer amsReceiver;
	
	// jms external communication
	private Context extContext = null;
	private ConnectionFactory extFactory = null;
	private Connection extConnection = null;
	private Session extSession = null;

	private MessageProducer extPublisherAlarm = null;

	/** Container that holds the information about the connector topics. */
	private final ConnectorTopicContainer topicContainer;

	private boolean bStop = false;
	private final boolean bStoppedClean = false;

	private final ConfigurationSynchronizer synchronizer;

	public DistributorWork(final java.sql.Connection localDatabaseConnection,
	                       final java.sql.Connection cacheDatabaseConnection,
			               final ConfigurationSynchronizer synch) {
	    
		localAppDb = localDatabaseConnection;
		memoryCacheDb = cacheDatabaseConnection;
		this.synchronizer = synch;
		
		// Create the container that holds the information about the connector
		// topics.
		topicContainer = new ConnectorTopicContainer();

		// Initialize JMS connections for sending messages to distributors
		boolean success = initJmsInternal();
		if (!success) {
			throw new RuntimeException(
					"Failed to initialize internal JMS connections");
		}
		success = initJmsExternal();
		if (!success) {
			throw new RuntimeException(
					"Failed to initialize external JMS connections");
		}		
	}

	@Override
	public void onMessage(final Message message) {
		synchronized (synchronizer) {
			try {
				workOnMessage(message);
			} catch (final Exception e) {
				Log.log(this, Log.ERROR, e);
			}
		}
	}

	@Override
	public void run() {
		int iErr = ErrorState.STAT_OK.getStateNumber();
		Log.log(this, Log.INFO, "start distributor work");
		bStop = false;

		while (bStop == false) {
			try {
				sleep(1);

				synchronized (synchronizer) {
					iErr = ErrorState.STAT_OK.getStateNumber();

					// WorkOnReplyMessage 2 / 3 (reply or change status)
					if (iErr == ErrorState.STAT_OK.getStateNumber()) {
						Message message = null;

						try {
							message = amsReceiver.receive("amsSubscriberReply");
						} catch (final Exception e) {
							Log.log(this, Log.FATAL,
									"could not receive from internal jms", e);
							iErr = ErrorState.STAT_ERR_JMSCON_INT
									.getStateNumber();
						}

						if (message != null) {
							iErr = responseMsg(message); // response 1
							// messages, other
							// in the next run
						}
					}
					// WorkOnMessageChain 3 / 3
					if (iErr == ErrorState.STAT_OK.getStateNumber()) {
						final List<Integer> keyList = MessageChainDAO
								.selectKeyList(memoryCacheDb, MESSAGECHAIN_WORK);

						final Iterator<Integer> iter = keyList.iterator();
						while (iter.hasNext()) {
							final Integer val = iter.next();
							if (val != null) {
								iErr = workOnMessageChain(val.intValue());
								if (iErr != ErrorState.STAT_OK.getStateNumber()) {
									break; // error: exit while
								}
							}
						}
					}
				}
			} catch (final Exception e) {
				Log.log(this, Log.FATAL, e);
			}
		}

		closeJmsExternal();
		closeJmsInternal();

		Log.log(this, Log.INFO, "Distributor is leaving.");
	}

	//
	// End: run
	// //////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////
	// Start: init & close (internal JMS, external JMS)
	//

	private boolean initJmsInternal() {

		final IPreferenceStore storeAct = AmsActivator.getDefault()
				.getPreferenceStore();

		final boolean durable = Boolean.parseBoolean(storeAct
				.getString(AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE));

		try {
            amsSender = new JmsMultiplePublisher(SharedJmsConnections.sharedSenderConnection());
        } catch (Exception e) {
            Log.log (Log.ERROR, "Cannot create JMS multiple producer: " + e.getMessage());
            return false;
        }
		
		/* SMS Connector */

		String topicName = storeAct
				.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_SMS_CONNECTOR);
		if (amsSender.addMessageProducer("amsPublisherSms", topicName) == false) {
			Log.log(this, Log.ERROR, "Cannot create amsPublisherSms");
			return false;
		}
		boolean full = storeAct
				.getBoolean(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TOPIC_SMS_CONNECTOR_FORWARD);
		topicContainer.addConnectorTopic(new ConnectorTopic(topicName,
				"SmsConnector", full));

		/* JMS Connector */

		topicName = storeAct
				.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR);
		if (amsSender.addMessageProducer("amsPublisherJms", topicName) == false) {
			Log.log(this, Log.ERROR, "Cannot create amsPublisherJms");
			return false;
		}
		full = storeAct
				.getBoolean(AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR_FORWARD);
		topicContainer.addConnectorTopic(new ConnectorTopic(topicName,
				"JmsConnector", full));

		/* Email Connector */

		topicName = storeAct
				.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR);
		if (amsSender.addMessageProducer("amsPublisherMail", topicName) == false) {
			Log.log(this, Log.ERROR, "Cannot create amsPublisherMail");
			return false;
		}
		full = storeAct
				.getBoolean(AmsPreferenceKey.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR_FORWARD);
		topicContainer.addConnectorTopic(new ConnectorTopic(topicName,
				"EMailConnector", full));

		/* Voicemail Connector */

		topicName = storeAct
				.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR);
		if (amsSender.addMessageProducer("amsPublisherVoiceMail", topicName) == false) {
			Log.log(this, Log.ERROR, "Cannot create amsPublisherVoiceMail");
			return false;
		}
		full = storeAct
				.getBoolean(AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR_FORWARD);
		topicContainer.addConnectorTopic(new ConnectorTopic(topicName,
				"VoicemailConnector", full));

		boolean success = true;

		try {

			amsReceiver = new JmsRedundantConsumer(SharedJmsConnections.sharedReceiverConnections());
			if (!amsReceiver.isConnected()) {
			    Log.log(this, Log.FATAL, "Cannot create redundant consumer.");
			    return false;
			}
			
			// The topic T_AMS_DISTRIBUTE is now used by the connection that is created in the
			// application class. The messages are processed by the method onMessage()
//			success = amsReceiver
//					.createRedundantSubscriber(
//							"amsSubscriberDist",
//							storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_DISTRIBUTOR),
//							storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TSUB_DISTRIBUTOR),
//							durable);
//
//			if (success == false) {
//				Log.log(this, Log.FATAL, "could not create amsSubscriberDist");
//				return false;
//			}

			success = amsReceiver.createRedundantSubscriber(
					"amsSubscriberReply",
					storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_REPLY),
					storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TSUB_REPLY),
					durable);

			if (success == false) {
				Log.log(this, Log.FATAL, "could not create amsSubscriberReply");
			}

		} catch (final Exception e) {
			Log.log(this, Log.FATAL, "could not init internal Jms", e);
		}

		return success;
	}

	public void closeJmsInternal() {

		Log.log(this, Log.INFO, "Exiting internal jms communication");

		// -- Close receiver connection ---
		if (amsReceiver != null) {
			amsReceiver.closeAll();
		}

		// -- Close sender connection ---
		amsSender.closeAll();

		Log.log(this, Log.INFO, "jms internal communication closed");
	}

	private boolean initJmsExternal() {

		try {

			final IPreferenceStore storeAct = AmsActivator.getDefault()
					.getPreferenceStore();

			final Hashtable<String, String> properties = new Hashtable<String, String>();
			properties
					.put(Context.INITIAL_CONTEXT_FACTORY,
							storeAct.getString(AmsPreferenceKey.P_JMS_EXTERN_CONNECTION_FACTORY_CLASS));
			properties
					.put(Context.PROVIDER_URL,
							storeAct.getString(AmsPreferenceKey.P_JMS_EXTERN_SENDER_PROVIDER_URL));
			extContext = new InitialContext(properties);

			extFactory = (ConnectionFactory) extContext
					.lookup(storeAct
							.getString(AmsPreferenceKey.P_JMS_EXTERN_CONNECTION_FACTORY));
			extConnection = extFactory.createConnection();

			// ADDED BY: Markus Moeller, 25.05.2007
			extConnection.setClientID("DistributorWorkSenderExternal");

			extSession = extConnection.createSession(false,
					Session.CLIENT_ACKNOWLEDGE);
			
			// TODO: ATTENTION!!!
			// We have to get the topic for re-insert the alarm message from a different
			// preference entry! AmsPreferenceKey.P_JMS_EXT_TOPIC_ALARM is not usable
			// because it could be contain one OR MORE alarm topics (ALARM, SNL_LOG, ...).
			// In case of re-insertion of an alarm chain message, it will be sent to ALL
			// topics!!!! This will cause more message chains.
			
			extPublisherAlarm = extSession
					.createProducer(extSession.createTopic(storeAct
							.getString(AmsPreferenceKey.P_JMS_EXT_TOPIC_ALARM_REINSERT)));
			if (extPublisherAlarm == null) {
				Log.log(this, Log.FATAL, "could not create extPublisherAlarm");
				return false;
			}

			extConnection.start();

			return true;
		} catch (final Exception e) {
			Log.log(this, Log.FATAL, "could not init external Jms", e);
		}

		return false;
	}

	public void closeJmsExternal() {
		Log.log(this, Log.INFO, "exiting external jms communication");

		if (extPublisherAlarm != null) {
			try {
				extPublisherAlarm.close();
			} catch (final JMSException e) {
				Log.log(this, Log.WARN, e);
			} finally {
				extPublisherAlarm = null;
			}
		}

		if (extSession != null) {
			try {
				extSession.close();
			} catch (final JMSException e) {
				Log.log(this, Log.WARN, e);
			} finally {
				extSession = null;
			}
		}
		if (extConnection != null) {
			try {
				extConnection.stop();
			} catch (final JMSException e) {
				Log.log(this, Log.WARN, e);
			}
		}
		if (extConnection != null) {
			try {
				extConnection.close();
			} catch (final JMSException e) {
				Log.log(this, Log.WARN, e);
			} finally {
				extConnection = null;
			}
		}
		if (extContext != null) {
			try {
				extContext.close();
			} catch (final NamingException e) {
				Log.log(this, Log.WARN, e);
			} finally {
				extContext = null;
			}
		}

		Log.log(this, Log.INFO, "jms external communication closed");
	}

	/**
	 * Sets the boolean variable that controlls the main loop to true
	 */
	public synchronized void stopWorking() {
		bStop = true;
	}

	/**
	 * Returns the shutdown state.
	 * 
	 * @return True, if the shutdown have occured clean otherwise false
	 */
	public boolean stoppedClean() {
		return bStoppedClean;
	}

	private void publishToConnectorSms(final String text, final String addr)
			throws JMSException {
		final MapMessage msg = amsSender.createMapMessage();
		msg.setString(MSGPROP_RECEIVERTEXT, text);
		msg.setString(MSGPROP_RECEIVERADDR, addr);
		amsSender.sendMessage("amsPublisherSms", msg);
	}

	/**
	 * Sends the message to the JMS-Connector.
	 * 
	 * @param text
	 *            The Message
	 * @param topic
	 *            The JMS-Destination-Topic
	 * @throws JMSException
	 */
	private void publishToConnectorJms(final String text, final String topic,
			final HashMap<String, String> map) throws JMSException {
		final MapMessage msg = amsSender.createMapMessage();

		msg.setString(MSGPROP_RECEIVERTEXT, text);
		msg.setString(MSGPROP_RECEIVERADDR, topic);

		// TODO: Add the alarm message here!!!
		if (map != null) {
			if (!map.isEmpty()) {
				// The marker for a message containing the origin alarm message
				msg.setString(MSGPROP_EXTENDED_MESSAGE, "true");

				String key;
				Iterator<String> keys = map.keySet().iterator();
				while (keys.hasNext()) {
					key = keys.next();
					msg.setString(key, map.get(key));
				}

				key = null;
				keys = null;
			}
		}

		amsSender.sendMessage("amsPublisherJms", msg);

		Log.log(Log.INFO,
				"DistributorWork.publishToConnectorJms(): Message sent via amsPublisherJms.send([text=\""
						+ text + "\", topic=\"" + topic + "\"]);");
	}

	private void publishToConnectorMail(final String text, final String addr,
			final String username) throws JMSException {
		final MapMessage msg = amsSender.createMapMessage();
		msg.setString(MSGPROP_RECEIVERTEXT, text);
		msg.setString(MSGPROP_RECEIVERADDR, addr);
		Log.log(Log.INFO,
				"DistributorWork.publishToConnectorMail() -1- addr="
						+ msg.getString(MSGPROP_RECEIVERADDR) + ", username="
						+ msg.getString(MSGPROP_SUBJECT_USERNAME));
		msg.setString(MSGPROP_SUBJECT_USERNAME, username);
		Log.log(Log.INFO,
				"DistributorWork.publishToConnectorMail() -2- addr="
						+ msg.getString(MSGPROP_RECEIVERADDR) + ", username="
						+ msg.getString(MSGPROP_SUBJECT_USERNAME));
		amsSender.sendMessage("amsPublisherMail", msg);
	}

	private void publishToConnectorVoiceMail(final String text,
			final String addr, final int texttype) throws JMSException {
		publishToConnectorVoiceMail(text, addr, "", texttype);
	}

	private void publishToConnectorVoiceMail(final String text,
			final String addr, final String chainIdAndPos, final int texttype)
			throws JMSException {
		final MapMessage msg = amsSender.createMapMessage();
		msg.setString(MSGPROP_RECEIVERTEXT, text);
		msg.setString(MSGPROP_RECEIVERADDR, addr);
		msg.setString(MSGPROP_MESSAGECHAINID_AND_POS, chainIdAndPos);
		msg.setString(MSGPROP_TEXTTYPE, "" + texttype);
		msg.setString(MSGPROP_GROUP_WAIT_TIME, "0");
		amsSender.sendMessage("amsPublisherVoiceMail", msg);
	}

	private void publishToConnectorVoiceMail(final String text,
			final String addr, final String chainIdAndPos, final int texttype,
			final Date nextActTime) throws JMSException {
		final MapMessage msg = amsSender.createMapMessage();
		msg.setString(MSGPROP_RECEIVERTEXT, text);
		msg.setString(MSGPROP_RECEIVERADDR, addr);
		msg.setString(MSGPROP_MESSAGECHAINID_AND_POS, chainIdAndPos);
		msg.setString(MSGPROP_TEXTTYPE, "" + texttype);
		msg.setString(MSGPROP_GROUP_WAIT_TIME, getTimeString(nextActTime));
		amsSender.sendMessage("amsPublisherVoiceMail", msg);
	}

	private boolean acknowledge(final Message msg) {
		try {
			msg.acknowledge();
			return true;
		} catch (final Exception e) {
			Log.log(this, Log.FATAL, "could not acknowledge", e);
		}
		return false;
	}

	//
	// End: init & close (Derby DB, internal JMS, external JMS)
	// //////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////
	// Start: Distribute Message
	//

	private int workOnMessage(final Message message) throws Exception {

		int iErr = ErrorState.STAT_OK.getStateNumber();

		Log.log(this, Log.DEBUG, "Enter workOnMessage()");
		try {
			if (!(message instanceof MapMessage)) {
				Log.log(this, Log.WARN, "Got unknown message " + message);
			} else {
				final MapMessage msg = (MapMessage) message;
				Utils.logMessage("DistributorWork receives MapMessage", msg);

				final String val = msg.getString(MSGPROP_COMMAND);
				if (val != null && val.equals(MSGVALUE_TCMD_RELOAD_CFG_START)) {
					synchronizer.requestSynchronization();
				} else {
					iErr = distributeMessage(msg);
					if (iErr == ErrorState.STAT_FALSE.getStateNumber()) {
						Log.log(this, Log.WARN,
								"Could not distributeMessage, handle as O.K.");
						return ErrorState.STAT_OK.getStateNumber(); // handle as
																	// O.K.
					}
				}
			}
		} catch (final JMSException e) {
			Log.log(this, Log.FATAL, "Could not workOnMessage", e);
			return ErrorState.STAT_ERR_JMSCON_INT.getStateNumber();
		}

		if (iErr == ErrorState.STAT_OK.getStateNumber()) // only if rplStart()
															// or
		// distributeMessage()
		// successful
		{ // and if no instanceof MapMessage, too
			if (!acknowledge(message)) {
				// session
				Log.log(this, Log.DEBUG, "Acknowledge of message failed.");
				return ErrorState.STAT_ERR_JMSCON_INT.getStateNumber();
			}
		}

		Log.log(this, Log.DEBUG, "Leaving workOnMessage()");
		return iErr;
	}

	private int distributeMessage(final MapMessage msg) throws Exception {

		Log.log(this, Log.DEBUG, "Enter distributeMessage()");
		try {
			final int iFilterId = Integer.parseInt(msg
					.getString(MSGPROP_FILTERID));
			final FilterTObject filter = FilterDAO.select(memoryCacheDb,
					iFilterId);

			HistoryWriter.logMessage(localAppDb, msg, filter, iFilterId);

			final String description = "Message filtered by " + iFilterId
					+ " - "
					+ (filter == null ? "filter not there" : filter.getName())
					+ "." + " Msg: " + Utils.getMessageString(msg);
			Log.log(Log.INFO, /* history.getHistoryID() + ". " + */
					description);
			// + " actiontype=" + history.getActionType()
			// + " user=" + history.getUserName()
			// + " via " + history.getDestType()
			// + " dest= " + history.getDestAdress());

			final List<?> fActions = AggrFilterActionDAO.select(memoryCacheDb,
					iFilterId);

			int iMessageId = -1;
			int iWorked = ErrorState.STAT_FALSE.getStateNumber();

			final Iterator<?> iter = fActions.iterator();
			while (iter.hasNext()) {
				final FilterActionTObject fa = (FilterActionTObject) iter
						.next();

				if (fa.getFilterActionTypeRef() == FILTERACTIONTYPE_SMS_GR
						|| fa.getFilterActionTypeRef() == FILTERACTIONTYPE_VM_GR
						|| fa.getFilterActionTypeRef() == FILTERACTIONTYPE_MAIL_GR) {

					// ADDED BY Markus Moeller, 2007-11-12
					// Blocking non-active groups
					final AggrUserGroupTObject userGroup = AggrUserGroupDAO
							.selectList(memoryCacheDb, fa.getReceiverRef());
					if (userGroup.getUsergroup().getIsActive() != 0) {
						if (iMessageId == -1) {
							iMessageId = MessageDAO.insert(localAppDb, msg,
									true);
							// Make sure that message IDs are identical in both DBs
							MessageDAO.insertWithMessageId(memoryCacheDb, msg, iMessageId);
						}

						final MessageChainTObject messageChainObject = new MessageChainTObject(
								-1, iMessageId, filter.getFilterID(),
								fa.getFilterActionID(), -1, null, null,
								MESSAGECHAIN_WORK, null);
						MessageChainDAO.insert(localAppDb, messageChainObject);
						MessageChainDAO.insert(memoryCacheDb, messageChainObject);

						if (iWorked == ErrorState.STAT_FALSE.getStateNumber()) {
							iWorked = ErrorState.STAT_OK.getStateNumber();
						}
					} else {
						HistoryWriter.logHistoryGroupBlocked(
								localAppDb,
								msg,
								"Send to group with reply",
								fa.getFilterActionTypeRef(),
								userGroup.getUsergroup(),
								0,
								0,
								TopicDAO.select(memoryCacheDb,
										fa.getReceiverRef()));
					}
				} else {
					final int iErr = sendMessage(msg, filter, fa); // throws
					// Exception
					if (iErr == ErrorState.STAT_OK.getStateNumber()
							|| iErr == ErrorState.STAT_FALSE.getStateNumber()) {
						if (iWorked == ErrorState.STAT_FALSE.getStateNumber()) {
							iWorked = iErr;
						}
					} else {
						return iErr;
					}
				}// else
			}// while

			Log.log(this, Log.DEBUG, "Leaving distributeMessage()");
			return iWorked;
		} catch (final JMSException e) {
			Log.log(this, Log.FATAL, "failed to sendMessage", e);
			return ErrorState.STAT_ERR_JMSCON_INT.getStateNumber();
		} catch (final SQLException e) {
			Log.log(this, Log.FATAL, "failed to sendMessage", e);
			return ErrorState.STAT_ERR_APPLICATION_DB_SEND.getStateNumber();
		} catch (final AMSException e) {
			Log.log(this, Log.FATAL, "failed to sendMessage, delete message", e);
			return ErrorState.STAT_FALSE.getStateNumber(); // error, delete
															// message
		}
	}

	private String prepareMessageText(final MapMessage mapMsg,
			final FilterTObject filter, final FilterActionTObject fa,
			final MessageChainTObject nextChain) throws Exception // INCLUDING -
																	// AMSException
	{
		String text = fa.getMessage();
		if (text == null) {
			text = filter.getDefaultMessage();
			if (text == null) {
				text = "";
			}
		}

		final String placeHolder = new String("$");
		final int len = placeHolder.length();
		final StringBuffer sbText = new StringBuffer(text);
		String key = null;
		int idxFirst = 0;
		int idxSecond = 0;

		while (true) {
			idxFirst = sbText.indexOf(placeHolder, idxFirst); // Search for
			// placeHolder
			if (idxFirst < 0) {
				break;
			}

			idxSecond = idxFirst + len;
			idxSecond = sbText.indexOf(placeHolder, idxSecond); // Search for
			// another
			// placeHolder
			if (idxSecond < 0) {
				break;
			}

			key = sbText.substring(idxFirst + len, idxSecond);
			if (key != null) {
				key = key.toUpperCase(); // error tolerance: someone typed in
				// small letter
			}

			if (key != null && key.indexOf(" ") < 0 && key.length() > 0) // error
			// tolerance:
			// $HOST$
			// text
			// $
			// text
			// $VALUE$
			{ // \ /
				try // do not recognize as placeholder if blank is included
				{
					final String value = mapMsg.getString(key);
					if (value != null) {
						sbText.replace(idxFirst, idxSecond + len, value); // Replace
						// keyText
						// with
						// value
						// (with
						// both
						// placeHolder)
						idxFirst += value.length(); // add length of new text to
						// first position
						continue;
					}
				} catch (final Exception ex) {
					// Can be ignored
				}
			}
			idxFirst = idxSecond; // start at next $
		}

		if (nextChain != null) // add MessageChain for Reply
		{
			sbText.append(" MsgNo="); // the same format for all connectors
			sbText.append(prepareMessageNumber(nextChain.getMessageChainID(),
					nextChain.getReceiverPos()));
		}

		return sbText.toString();
	}

	private String prepareMessageNumber(final int iChainID, final int iChainPos)
			throws Exception // INCLUDING - AMSException
	{
		int iRlen = ("" + iChainPos).length();
		if (iRlen > MSG_POS_LENGTH_FOR_MSGPROP) {
			throw new AMSException("MessageChain ReceiverPos=" + iChainPos
					+ " has more chars than > MSG_POS_LENGTH_FOR_MSGPROP="
					+ MSG_POS_LENGTH_FOR_MSGPROP);
		}

		final StringBuffer sb = new StringBuffer();
		sb.append(iChainID);
		while (iRlen++ < MSG_POS_LENGTH_FOR_MSGPROP) {
			// until len == MSG_POS_LENGTH_FOR_MSGPROP
			sb.append('0'); // fill with leading zeros (1 -> 001)
		}
		sb.append(iChainPos);

		return sb.toString();
	}

	private int sendMessage(final MapMessage mapMsg,
			final FilterTObject filter, final FilterActionTObject fa)
			throws Exception // INCLUDING -
	// SQLException,
	// JMSException,
	// AMSException
	{
		final int faTypeRef = fa.getFilterActionTypeRef();

		if (faTypeRef >= FILTERACTIONTYPE_TOPICDEST) {
			sendMessageToDefaultTopic(mapMsg);
			return ErrorState.STAT_OK.getStateNumber(); // All O.K.
			// return sendMessageToTopic(mapMsg, faTypeRef); // to free topic
		} else if (faTypeRef == FILTERACTIONTYPE_SMS
				|| faTypeRef == FILTERACTIONTYPE_VM
				|| faTypeRef == FILTERACTIONTYPE_MAIL
				|| faTypeRef == FILTERACTIONTYPE_TO_JMS) {
			final String text = prepareMessageText(mapMsg, filter, fa, null);
			return sendMessageToConnector(mapMsg, text, fa); // to user
		} else if (faTypeRef == FILTERACTIONTYPE_SMS_G
				|| faTypeRef == FILTERACTIONTYPE_VM_G
				|| faTypeRef == FILTERACTIONTYPE_MAIL_G) {
			final String text = prepareMessageText(mapMsg, filter, fa, null);
			return sendMessageToUserGroup(mapMsg, text, fa); // to group
		} else {
			throw new AMSException(
					"Configuration is invalid. FilterActionType=" + faTypeRef);
		}
	}

	//
	// End: Distribute Message
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Send the message to a default topic which was configured as default
	 * destination in preference page.
	 * 
	 * @param message
	 *            The message to be send
	 */
	private void sendMessageToDefaultTopic(final Message message) {
		// TODO: create configuration, send
		Log.log(this, Log.WARN,
				"method sendMessageToDefaultTopic(Message message) not implemented yet!");
	}

	private int sendMessageToConnector(final MapMessage mapMsg,
			final String text, final FilterActionTObject fa) throws Exception // INCLUDING
																				// -
	// SQLException,
	// JMSException,
	// AMSException
	{
		ConnectorTopic ct = null;
		HashMap<String, String> map = null;

		final UserTObject user = UserDAO.select(memoryCacheDb,
				fa.getReceiverRef());
		if (fa.getFilterActionTypeRef() != FILTERACTIONTYPE_TO_JMS
				&& user.getActive() == 0) {
			Log.log(Log.WARN, "User not active: " + user.getUserID()
					+ " in FilterAction: " + fa.getFilterActionID());
			return ErrorState.STAT_FALSE.getStateNumber();
		}
		TopicTObject topic = null;
		switch (fa.getFilterActionTypeRef()) {
		case FILTERACTIONTYPE_TO_JMS:
			topic = TopicDAO.select(memoryCacheDb, fa.getReceiverRef());
			ct = topicContainer
					.getConnectorTopicByConnectorName("JmsConnector");
			if (ct.isFullMessageReceiver()) {
				map = this.getMessageContent(mapMsg);
			}

			publishToConnectorJms(text, topic.getTopicName(), map);
			break;

		case FILTERACTIONTYPE_SMS:
			publishToConnectorSms(text, user.getMobilePhone()); // SMS
			break;

		case FILTERACTIONTYPE_VM:
			publishToConnectorVoiceMail(text, user.getPhone(),
					TextType.ALARM_WOCONFIRM.getTextTypeNumber()); // VoiceMail
			break;

		case FILTERACTIONTYPE_MAIL:
			publishToConnectorMail(text, user.getEmail(), user.getName()); // E-Mail
			break;

		default:
			throw new AMSException(
					"Configuration is invalid. FilterActionType="
							+ fa.getFilterActionTypeRef());
		}

		HistoryWriter.logHistorySend(localAppDb, mapMsg, text,
				fa.getFilterActionTypeRef(), user, null, -1, 0, 0, topic);
		return ErrorState.STAT_OK.getStateNumber();
	}

	private HashMap<String, String> getMessageContent(final MapMessage message) {
		final HashMap<String, String> map = new HashMap<String, String>();
		String key = null;

		try {
			final Enumeration<?> list = message.getMapNames();
			while (list.hasMoreElements()) {
				key = (String) list.nextElement();
				map.put(key, message.getString(key));
			}
		} catch (final JMSException jmse) {
			map.clear();
		}

		return map;
	}

	private int sendMessageToUserGroup(final MapMessage mapMsg,
			final String text, final FilterActionTObject fa) throws Exception // INCLUDING
																				// -
	// SQLException,
	// JMSException,
	// AMSException
	{
		int iOneSended = ErrorState.STAT_FALSE.getStateNumber();
		final AggrUserGroupTObject userGroup = AggrUserGroupDAO.selectList(
				memoryCacheDb, fa.getReceiverRef());

		// If user group is NOT active...
		if (userGroup.getUsergroup().getIsActive() == 0) {
			HistoryWriter.logHistoryGroupBlocked(localAppDb, mapMsg, text,
					fa.getFilterActionTypeRef(), userGroup.getUsergroup(), 0,
					0, TopicDAO.select(memoryCacheDb, fa.getReceiverRef()));

			iOneSended = ErrorState.STAT_GROUP_BLOCKED.getStateNumber(); // ==
																			// STAT_OK

			return iOneSended;
		}

		final Iterator<?> iter = userGroup.getUsers().iterator();
		while (iter.hasNext()) {
			final AggrUserGroupUserTObject aUser = (AggrUserGroupUserTObject) iter
					.next();
			final UserTObject user = aUser.getUser();

			if (aUser.getUserGroupUser().getActive() == 0) {
				Log.log(Log.WARN,
						"UserGroupRel not active: User " + user.getUserID()
								+ " of Group "
								+ aUser.getUserGroupUser().getUserGroupRef()
								+ " in FilterAction: " + fa.getFilterActionID());
				continue;
			}
			if (aUser.getUser().getActive() == 0) {
				Log.log(Log.WARN, "User not active: " + user.getUserID()
						+ " in FilterAction: " + fa.getFilterActionID());
				continue;
			}

			switch (fa.getFilterActionTypeRef()) {
			case FILTERACTIONTYPE_SMS_G:
				publishToConnectorSms(text, user.getMobilePhone()); // SMS
				break;
			case FILTERACTIONTYPE_VM_G:
				publishToConnectorVoiceMail(text, user.getPhone(),
						TextType.ALARM_WOCONFIRM.getTextTypeNumber()); // VoiceMail
				break;
			case FILTERACTIONTYPE_MAIL_G:
				publishToConnectorMail(text, user.getEmail(), user.getName());// E-Mail
				break;
			default:
				throw new AMSException(
						"Configuration is invalid. FilterActionType="
								+ fa.getFilterActionTypeRef());
			}

			HistoryWriter.logHistorySend(localAppDb, mapMsg, text,
					fa.getFilterActionTypeRef(), aUser.getUser(),
					userGroup.getUsergroup(), -1, 0, 0,
					TopicDAO.select(memoryCacheDb, fa.getReceiverRef()));
			iOneSended = ErrorState.STAT_OK.getStateNumber();
		}

		return iOneSended;
	}

	//
	// End: Send Message
	// //////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////
	// Start: Reply & ChangeStatus
	//

	private int responseMsg(final Message message) throws Exception {
		int iErr = ErrorState.STAT_OK.getStateNumber();
		try {
			if (!(message instanceof MapMessage)) {
				Log.log(this, Log.WARN, "Got unknown message " + message);
			} else {
				final MapMessage msg = (MapMessage) message;
				Utils.logMessage("DistributorWork receives MapMessage", msg);

				iErr = responseMessage(msg);
				if (iErr == ErrorState.STAT_FALSE.getStateNumber()) {
					Log.log(this, Log.WARN,
							"MapMessage not accepted => delete it");
					iErr = ErrorState.STAT_OK.getStateNumber(); // handle as
																// O.K.
				}
			}
		} catch (final SQLException e) {
			Log.log(this, Log.FATAL, "could not responseMessage", e);
			return ErrorState.STAT_ERR_APPLICATION_DB_SEND.getStateNumber();
		} catch (final JMSException e) {
			Log.log(this, Log.FATAL, "could not responseMessage", e);
			return ErrorState.STAT_ERR_JMSCON_INT.getStateNumber();
		}

		if (iErr == ErrorState.STAT_OK.getStateNumber()) // only if
															// responseMessage()
		// successful
		{ // and if no instanceof MapMessage, too
			if (!acknowledge(message)) {
				// session
				return ErrorState.STAT_ERR_JMSCON_INT.getStateNumber();
			}
		}
		return iErr;
	}

	private int responseMessage(final MapMessage msg) throws Exception // INCLUDING
	// -
	// JMSException,
	// SQLException,
	// InterruptedException
	{
		final String strChainIdAndPos = msg
				.getString(MSGPROP_MESSAGECHAINID_AND_POS);
		final String strGroupNum = msg.getString(MSGPROP_CHANGESTAT_GROUPNUM);

		final String replyType = msg.getString(MSGPROP_REPLY_TYPE);
		final String replyAdress = msg.getString(MSGPROP_REPLY_ADRESS);

		/*
		 * TEST String name = null; Enumeration<?> list = msg.getMapNames();
		 * while(list.hasMoreElements()) { name = (String)list.nextElement();
		 * System.out.println(name + " = " + msg.getString(name)); }
		 * System.out.println();
		 */

		if (strChainIdAndPos != null) {
			int chainID = 0;
			int chainPos = 0;
			try {
				if (!strChainIdAndPos.equals("#")) {
					if (strChainIdAndPos.length() < MSG_POS_LENGTH_FOR_MSGPROP + 1) {
						throw new NumberFormatException(
								"strChainIdAndPos.length() < "
										+ (MSG_POS_LENGTH_FOR_MSGPROP + 1));
					}

					final int posInStr = strChainIdAndPos.length()
							- MSG_POS_LENGTH_FOR_MSGPROP;// chars used for
					// pos
					chainPos = Integer.parseInt(strChainIdAndPos
							.substring(posInStr));
					chainID = Integer.parseInt(strChainIdAndPos.substring(0,
							posInStr));
				}
			} catch (final NumberFormatException e) {
				// Log.log(Log.FATAL, "Message Reply: ChainIdAndPos is not a
				// valid number!", nfEx);
				// only warn
				Log.log(Log.WARN,
						"Message Reply: ChainIdAndPos is not a valid number!");
				return ErrorState.STAT_FALSE.getStateNumber();
			}

			final String confirmCode = msg.getString(MSGPROP_CONFIRMCODE);

			if (strChainIdAndPos.equals("#")) {
				return replyAllMessageChain(confirmCode, replyType, replyAdress);
			}

			return replyMessageChain(null, chainID, chainPos, confirmCode,
					replyType, replyAdress);
		} else if (strGroupNum != null) {
			final String strUserNum = msg.getString(MSGPROP_CHANGESTAT_USERNUM);
			final String strStatus = msg.getString(MSGPROP_CHANGESTAT_STATUS);
			final String strAction = msg.getString(MSGPROP_CHANGESTAT_ACTION);
			int groupNum = 0;
			int userNum = 0;
			short status = 0;
			final boolean changeGroupState = strAction
					.compareToIgnoreCase("group") == 0;

			try {
				groupNum = Integer.parseInt(strGroupNum);
				userNum = Integer.parseInt(strUserNum);
				Log.log(Log.INFO,
						"DistributorWork.responseMessage(): + groupNum="
								+ groupNum + ", userNum=" + userNum);
				status = Short.parseShort(strStatus);
			} catch (final NumberFormatException e) {
				// Log.log(Log.FATAL, "Message Change Status: first three values
				// have to be numeric!");
				// only warn
				Log.log(Log.WARN,
						"Message Change Status: first three values have to be numeric!");
				return ErrorState.STAT_FALSE.getStateNumber();
			}

			String reason = msg.getString(MSGPROP_CHANGESTAT_REASON);
			reason = reason.trim();

			final String statusCode = msg
					.getString(MSGPROP_CHANGESTAT_STATUSCODE);
			String txt = null;
			if (changeGroupState) {
				txt = "G*" + groupNum + "*" + userNum + "*" + status + "*";
			} else {
				txt = groupNum + "*" + userNum + "*" + status + "*";
			}

			if (!(status == 0 || status == 1)) {
				Log.log(Log.WARN, "unknown state: " + status + " for msg "
						+ txt);
				final UserTObject userTmp = UserDAO.select(memoryCacheDb,
						userNum);
				if (userTmp != null
						&& userTmp.getStatusCode().equalsIgnoreCase(statusCode))// NOT_OK
				// -
				// main
				// system
				// temporarly
				// not
				// available
				{
					HistoryWriter.logHistoryChangeStatus(localAppDb, userTmp,
							"Unknown state.", null, txt, status, reason,
							replyType, replyAdress);
					sendChangeStatusConfirmation(userTmp, txt
							+ MSGCODE_UNKNOWN_STATUS, replyType, replyAdress,
							TextType.STATUSCHANGE_NOK.getTextTypeNumber());
				} else {
					HistoryWriter.logHistoryChangeStatus(localAppDb, null,
							"Unknown state.", null, txt, status, reason,
							replyType, replyAdress);
				}
				return ErrorState.STAT_FALSE.getStateNumber(); // wrong request
			}

			boolean bBreak = false;

			// If the user want to change the group state...
			if (changeGroupState == true) {
				java.sql.Connection oraDb = null;
				// UserTObject userOra = null;
				UserGroupTObject groupOra = null;

				try // try finally
				{
					for (int i = 0; i < 3; i++) {
						try {
							oraDb = AmsConnectionFactory.getConfigurationDB(); // throws
							// ClassNotFoundException,
							// SQLException
						} catch (final Exception e) {
							sleep(10000);
							AmsConnectionFactory.closeConnection(oraDb); // don't
							// forget
							// to
							// close
							// connection
							oraDb = null;
							continue; // try max 3 times (with continue below)
						}

						if (oraDb == null) {
							sleep(10000); // no close needed, it's null
							continue; // try max 3 times (with continue below)
						}

						try {
							groupOra = changeGroupState(oraDb,
									Arrays.asList(oraDb), groupNum, userNum,
									status, statusCode, reason, txt, replyType,
									replyAdress);
						} catch (final Exception e) {
							sleep(10000);
							AmsConnectionFactory.closeConnection(oraDb); // don't
							// forget
							// to
							// close
							// connection
							oraDb = null;
							groupOra = null;
							continue; // try max 3 times (with continue above)
						}

						if (groupOra != null) // in main system deleted =>
												// reload
						// follows
						{
							try {
								final UserTObject user1 = UserDAO.select(
										memoryCacheDb, userNum);
								final UserGroupTObject group2 = changeGroupState(
										memoryCacheDb, Arrays.asList(
												memoryCacheDb, localAppDb),
										groupNum, userNum, status, statusCode,
										reason, txt, replyType, replyAdress);
								if (group2 != null) // OK - all well done
								{
									final UserGroupTObject ug = UserGroupDAO
											.select(memoryCacheDb, groupNum);
									HistoryWriter.logHistoryChangeStatus(
											localAppDb, null, null, ug, txt,
											status, reason, replyType,
											replyAdress);
									sendChangeGroupStatusConfirmation(user1,
											ug, "Group state changed: " + txt
													+ MSGCODE_OK, replyType,
											replyAdress,
											TextType.STATUSCHANGE_OK
													.getTextTypeNumber());
									Log.log(Log.INFO, txt + MSGCODE_OK);
									return ErrorState.STAT_OK.getStateNumber(); // All
																				// O.K.
								}
							} catch (final SQLException e) {
								Log.log(this, Log.FATAL, e);
							}
							HistoryWriter
									.logHistoryChangeStatus(
											localAppDb,
											null,
											"Critical error => can update local db => replicate configuration.",
											null, txt, status, reason,
											replyType, replyAdress);
							Log.log(Log.FATAL,
									"critical error => can update local db => replicate configuration "
											+ txt);
						}
						bBreak = true; // normal exit
						break; // exit for
					}// for
				} finally {
					AmsConnectionFactory.closeConnection(oraDb); // don't forget
					// to close
					// connection
					oraDb = null;
					groupOra = null;
				}

			} else // the user want to change the user state
			{
				java.sql.Connection oraDb = null;
				UserTObject userOra = null;
				try // try finally
				{
					for (int i = 0; i < 3; i++) {
						try {
							oraDb = AmsConnectionFactory.getConfigurationDB(); // throws
							// ClassNotFoundException,
							// SQLException
						} catch (final Exception e) {
							sleep(10000);
							AmsConnectionFactory.closeConnection(oraDb); // don't
							// forget
							// to
							// close
							// connection
							oraDb = null;
							continue; // try max 3 times (with continue below)
						}

						if (oraDb == null) {
							sleep(10000); // no close needed, it's null
							continue; // try max 3 times (with continue below)
						}

						try {
							userOra = changeStatus(oraDb, Arrays.asList(oraDb),
									groupNum, userNum, status, statusCode,
									reason, txt, replyType, replyAdress);
						} catch (final Exception e) {
							sleep(10000);
							AmsConnectionFactory.closeConnection(oraDb); // don't
							// forget
							// to
							// close
							// connection
							oraDb = null;
							userOra = null;
							continue; // try max 3 times (with continue above)
						}

						if (userOra != null) // in main system deleted => reload
						// follows
						{
							try {
								final UserTObject user2 = changeStatus(
										memoryCacheDb, Arrays.asList(
												memoryCacheDb, localAppDb),
										groupNum, userNum, status, statusCode,
										reason, txt, replyType, replyAdress);
								if (user2 != null) // OK - all well done
								{
									final UserGroupTObject ug = UserGroupDAO
											.select(memoryCacheDb, groupNum);
									HistoryWriter.logHistoryChangeStatus(
											localAppDb, user2, null, ug, txt,
											status, reason, replyType,
											replyAdress);
									sendChangeStatusConfirmation(user2, txt
											+ MSGCODE_OK, replyType,
											replyAdress,
											TextType.STATUSCHANGE_OK
													.getTextTypeNumber());
									Log.log(Log.INFO, txt + MSGCODE_OK);
									return ErrorState.STAT_OK.getStateNumber(); // All
																				// O.K.
								}
							} catch (final SQLException e) {
								Log.log(this, Log.FATAL, e);
							}
							HistoryWriter
									.logHistoryChangeStatus(
											localAppDb,
											null,
											"Critical error => can update local db => replicate configuration.",
											null, txt, status, reason,
											replyType, replyAdress);
							Log.log(Log.FATAL,
									"critical error => can update local db => replicate configuration "
											+ txt);
						}
						bBreak = true; // normal exit
						break; // exit for
					}// for
				} finally {
					AmsConnectionFactory.closeConnection(oraDb); // don't forget
					// to close
					// connection
					oraDb = null;
					userOra = null;
				}

				if (bBreak == false) // if no connection to configuration
				// database
				{
					Log.log(this,
							Log.FATAL,
							"Could not changeStatus: temporary no connection to configuration database for msg "
									+ txt);
					final UserTObject userTmp = UserDAO.select(memoryCacheDb,
							userNum);
					HistoryWriter.logHistoryChangeStatus(localAppDb, userTmp,
							"No connection to config db.", null, txt, status,
							reason, replyType, replyAdress);
					if (userTmp != null) {
						// available
						sendChangeStatusConfirmation(userTmp, txt
								+ MSGCODE_NO_MAIN_SYSTEM, replyType,
								replyAdress,
								TextType.STATUSCHANGE_NOK.getTextTypeNumber());
					}
				}
			} // if(changeGroupState == true) ... else
		} // else if (strGroupNum != null)
		return ErrorState.STAT_FALSE.getStateNumber();
	}

	private UserTObject changeStatus(final java.sql.Connection readConnection,
			List<java.sql.Connection> writeConnections, final int groupNum,
			final int userNum, final short status, final String statusCode,
			final String reason, final String txt, final String replyType,
			final String replyAdress) throws Exception // INCLUDING
	// -
	// SQLException
	// (Oracle
	// DB
	// or
	// Derby
	// DB)
	{
		final UserTObject user = UserDAO.select(readConnection, userNum);
		if (user == null) {
			HistoryWriter.logHistoryChangeStatus(localAppDb, null, "UserID="
					+ userNum + " not found.", null, txt, status, reason,
					replyType, replyAdress);
			Log.log(Log.FATAL, "User not found: " + userNum + " for msg " + txt);// do
																					// not
																					// send
																					// back
																					// to
																					// user
																					// if
																					// not
			// registered
			return null; // no user to send to
		}

		if (!user.getStatusCode().equalsIgnoreCase(statusCode)) // consitent
		// state, but do
		// not publish
		// statuscode
		{
			HistoryWriter.logHistoryChangeStatus(localAppDb, user,
					"Status code does not match.", null, txt, status, reason,
					replyType, replyAdress);
			Log.log(Log.FATAL, "status code does not match for msg " + txt);
			return null;
		}

		final UserGroupTObject ug = UserGroupDAO.select(readConnection,
				groupNum);
		if (ug == null) {
			HistoryWriter.logHistoryChangeStatus(localAppDb, user, "GroupID="
					+ groupNum + " not found.", null, txt, status, reason,
					replyType, replyAdress);
			Log.log(Log.WARN, "no group: " + groupNum + " for msg " + txt);

			// never coming twice here
			sendChangeStatusConfirmation(user, txt + MSGCODE_NO_GROUP,
					replyType, replyAdress,
					TextType.STATUSCHANGE_NOK.getTextTypeNumber());
			return null;
		}

		for (int i = 0; i < 3; i++) // if update failed try max 3 times
		{
			final UserGroupUserTObject ugu = UserGroupUserDAO.select(
					readConnection, groupNum, userNum);
			if (ugu == null) {
				HistoryWriter.logHistoryChangeStatus(localAppDb, user,
						"User not in group.", ug, txt, status, reason,
						replyType, replyAdress);
				Log.log(Log.WARN, "user not in group for msg " + txt);
				// never coming twice here, nok not in group
				sendChangeStatusConfirmation(user, txt + MSGCODE_NOT_IN_GROUP,
						replyType, replyAdress,
						TextType.STATUSCHANGE_NOK.getTextTypeNumber());
				return null;
			}
			if (ugu.getActive() == status) {
				Log.log(Log.WARN, "status already set for msg, handle as ok "
						+ txt);
				return user;
			}
			
			// This condition is checked if one tries to change his/her status
			// via SMS to inactive.
			if (status == 0) // only check if want to set 0 - Inactive
			{
				int iActiveCount = 0;
				final Iterator<?> iter = UserGroupUserDAO.selectList(
						readConnection, groupNum).iterator();
				while (iter.hasNext()) {
					if (((AggrUserGroupUserTObject) iter.next())
							.getUserGroupUser().getActive() == 1) {
						// Inactive,
						// 1 -
						// Active
						// (group
						// ownership)
						iActiveCount++; // count active user in group
					}
				}
				if (ug.getMinGroupMember() >= iActiveCount) {
					HistoryWriter.logHistoryChangeStatus(
							localAppDb,
							user,
							"Min user count reached min="
									+ ug.getMinGroupMember() + ".", ug, txt,
							status, reason, replyType, replyAdress);
					Log.log(Log.WARN,
							"min user count reached min="
									+ ug.getMinGroupMember() + " for msg "
									+ txt);

					// never coming twice here
					// NOT_OK - min count of user reached
					sendChangeStatusConfirmation(user, txt
							+ MSGCODE_MIN_USER_REACHED, replyType, replyAdress,
							TextType.STATUSCHANGE_NOK.getTextTypeNumber());
					return null;
				}
			}

			if (reason.length() > 0) {
				ugu.setActiveReason(reason);
			}

			ugu.setActive(status); // set status in UserGroupUserTObject
			boolean updateSuccessful = true;
			for (java.sql.Connection writeConnection : writeConnections) {
				updateSuccessful &= UserGroupUserDAO.update(writeConnection,
						ugu);
			}
			if (updateSuccessful) {
				return user; // status updated
			}
		}// for

		HistoryWriter
				.logHistoryChangeStatus(
						localAppDb,
						user,
						"failed to update status (tried 3 times, Data changed or deleted!).",
						ug, txt, status, reason, replyType, replyAdress);
		Log.log(Log.WARN, "failed to update status (tried 3 times) for msg "
				+ txt + " (Data changed or deleted!)");
		return null;
	}

	private UserGroupTObject changeGroupState(
			final java.sql.Connection readConnection,
			List<java.sql.Connection> writeConnections, final int groupNum,
			final int userNum, final short status, final String statusCode,
			final String reason, final String txt, final String replyType,
			final String replyAdress) throws Exception {
		UserGroupUserTObject groupUser = null;
		UserTObject user = null;

		// Check whether or not the user is a member of the group
		groupUser = UserGroupUserDAO.select(readConnection, groupNum, userNum);
		if (groupUser == null) {
			HistoryWriter.logHistoryChangeStatus(localAppDb, null, "GroupID="
					+ groupNum + " or UserID=" + userNum + " not found.", null,
					txt, status, reason, replyType, replyAdress);
			Log.log(Log.FATAL, "User=" + userNum + " or group=" + groupNum
					+ " not found");// do not send back to user if not
			// registered
			return null; // no user to send to
		}

		// Get the user
		user = UserDAO.select(readConnection, userNum);
		if (user == null) {
			HistoryWriter.logHistoryChangeStatus(localAppDb, user, "UserID="
					+ userNum + " not found.", null, txt, status, reason,
					replyType, replyAdress);
			Log.log(Log.FATAL, "User=" + userNum + " not found");// do not send
																	// back to
																	// user if
																	// not

			return null;
		}

		// Check the status code
		if (!user.getStatusCode().equalsIgnoreCase(statusCode)) // consitent
		// state, but do
		// not publish
		// statuscode
		{
			HistoryWriter.logHistoryChangeStatus(localAppDb, user,
					"Status code does not match.", null, txt, status, reason,
					replyType, replyAdress);
			Log.log(Log.FATAL, "status code does not match for user=" + userNum);

			return null;
		}

		final UserGroupTObject userGroup = UserGroupDAO.select(readConnection,
				groupNum);
		if (userGroup == null) {
			HistoryWriter.logHistoryChangeStatus(localAppDb, user, "GroupID="
					+ groupNum + " not found.", userGroup, txt, status, reason,
					replyType, replyAdress);
			Log.log(Log.WARN, "no group: " + groupNum);

			// never coming twice here
			sendChangeStatusConfirmation(user, txt + MSGCODE_NO_GROUP,
					replyType, replyAdress,
					TextType.STATUSCHANGE_NOK.getTextTypeNumber());
			return null;
		}

		for (int i = 0; i < 3; i++) // if update failed try max 3 times
		{
			final UserGroupTObject ug = UserGroupDAO.select(readConnection,
					groupNum);
			if (ug == null) {
				HistoryWriter.logHistoryChangeStatus(localAppDb, user,
						"User not in group.", ug, txt, status, reason,
						replyType, replyAdress);
				Log.log(Log.WARN, "group not found ");
				// never coming twice here, nok not in group
				sendChangeGroupStatusConfirmation(user, ug, txt
						+ MSGCODE_NOT_IN_GROUP, replyType, replyAdress,
						TextType.STATUSCHANGE_NOK.getTextTypeNumber());
				return null;
			}
			if (ug.getIsActive() == status) {
				Log.log(Log.WARN, "status already set for msg, handle as ok ");
				return ug;
			}

			if (status == 0) // only check if want to set 0 - Inactive
			{
				int iActiveCount = 0;
				final Iterator<?> iter = UserGroupUserDAO.selectList(
						readConnection, groupNum).iterator();
				while (iter.hasNext()) {
					if (((AggrUserGroupUserTObject) iter.next())
							.getUserGroupUser().getActive() == 1) {
						// Inactive,
						// 1 -
						// Active
						// (group
						// ownership)
						iActiveCount++; // count active user in group
					}
				}
			}

			ug.setIsActive(status); // set status in UserGroupUserTObject
			boolean updateSuccessful = true;
			for (java.sql.Connection writeConnection : writeConnections) {
				updateSuccessful &= UserGroupDAO.update2(writeConnection, ug);
			}
			if (updateSuccessful) {
				return ug; // status updated
			}
		}// for

		HistoryWriter
				.logHistoryChangeStatus(
						localAppDb,
						user,
						"failed to update group status (tried 3 times, Data changed or deleted!).",
						userGroup, txt, status, reason, replyType, replyAdress);
		Log.log(Log.WARN, "failed to update status (tried 3 times) for msg "
				+ txt + " (Data changed or deleted!)");

		return null;
	}

	private void sendReplyConfirmationForUser(final UserTObject user,
			final boolean bOk, final String error, final int msgChainId,
			final int msgChainPos, final String replyType,
			final String originator) {
		String txt = null;
		String addr = "";
		try {
			int type = TextType.ALARMCONFIRM_NOK.getTextTypeNumber();
			if (bOk) {
				txt = "Alarm confirmation successful for MsgNo="
						+ prepareMessageNumber(msgChainId, msgChainPos);
				type = TextType.ALARMCONFIRM_OK.getTextTypeNumber();
			} else {
				txt = "Alarm confirmation rejected for MsgNo="
						+ prepareMessageNumber(msgChainId, msgChainPos)
						+ " Error: " + error;
			}

			if (originator != null) {
				txt += " (confirmation initiated from " + originator + ")";
			}

			if (replyType.equals(MSG_REPLY_TYPE_SMS)) {
				addr = " with MobilePhoneNumber=" + user.getMobilePhone();
				publishToConnectorSms(txt, user.getMobilePhone()); // SMS
				Log.log(this, Log.INFO, "Reply Confirmation:'" + txt
						+ "' send to user=" + user.getName() + " via sms ("
						+ user.getMobilePhone() + ")");
			} else if (replyType.equals(MSG_REPLY_TYPE_EMAIL)) {
				addr = " with Email=" + user.getEmail();
				publishToConnectorMail(txt, user.getEmail(), user.getName());// E-Mail
				Log.log(this, Log.INFO, "Reply Confirmation:'" + txt
						+ "' send to user=" + user.getName() + " via email("
						+ user.getEmail() + ")");
			} else if (replyType.equals(MSG_REPLY_TYPE_VOICEMAIL)) {
				addr = " with PhoneNumber=" + user.getPhone();
				publishToConnectorVoiceMail(txt, user.getPhone(), type); // VoiceMail
				Log.log(this, Log.INFO, "Reply Confirmation:'" + txt
						+ "' send to user=" + user.getName()
						+ " via voicemail (" + user.getPhone() + ")");
			}
		} catch (final Exception ex) {
			Log.log(this,
					Log.WARN,
					"failed to send reply confirmation message to user="
							+ user.getName() + addr, ex);
		}
	}

	/**
	 * Send a reply message for an JMS topic.
	 * 
	 * @param topic
	 *            The current JMS topic, where the message is sent to
	 * @param msgChainId
	 *            The id of the message chain
	 * @param msgChainPos
	 *            The current position in the message chain
	 * @param originator
	 *            The initiator of the confirmation
	 */
	private void sendReplyConfirmationForJms(final TopicTObject topic,
			final int msgChainId, final int msgChainPos, final String originator) {
		String txt = null;
		String addr = "";
		try {
			txt = "Alarm confirmation successful for MsgNo="
					+ prepareMessageNumber(msgChainId, msgChainPos);

			if (originator != null) {
				txt += " (confirmation initiated from " + originator + ")";
			}

			addr = " with Topic=" + topic.getTopicName();
			publishToConnectorJms(txt, topic.getTopicName(), null);
			Log.log(this, Log.INFO, "Reply Confirmation:'" + txt
					+ "' send to topic=" + topic.getTopicName() + " via JMS("
					+ topic.getTopicName() + ")");
		} catch (final Exception ex) {
			Log.log(this, Log.WARN,
					"failed to send reply confirmation message to topic="
							+ topic.getTopicName() + addr, ex);
		}
	}

	private void sendChangeStatusConfirmation(final UserTObject user,
			String originalText, final String replyType, final String originator,
			final int texttype) {
	    String txt = new String(originalText);
		String addr = "";
		try {
			if (originator != null) {
				txt += " (status change initiated from " + originator + ")";
			}

			if (replyType.equals(MSG_REPLY_TYPE_SMS)) {
				addr = " with MobilePhoneNumber=" + user.getMobilePhone();
				publishToConnectorSms(txt, user.getMobilePhone()); // SMS
			} else if (replyType.equals(MSG_REPLY_TYPE_JMS)) {

				// TopicTObject topic = TopicDAO.select(conDb, fa
				// .getReceiverRef()); TODO
				// addr = " with Topic=" + topic.getName();
				// publishToConnectorJms(txt, topic.getName()); // JMS
			} else if (replyType.equals(MSG_REPLY_TYPE_EMAIL)) {
				addr = " with Email=" + user.getEmail();
				publishToConnectorMail(txt, user.getEmail(), user.getName()); // E-Mail
			} else if (replyType.equals(MSG_REPLY_TYPE_VOICEMAIL)) {
				addr = " with PhoneNumber=" + user.getPhone();
				publishToConnectorVoiceMail(txt, user.getPhone(), texttype); // VoiceMail
			} else {
				throw new AMSException("Invalid MSGPROP_REPLY_TYPE="
						+ replyType);
			}
		} catch (final Exception ex) {
			Log.log(this, Log.WARN,
					"failed to send change status confirmation message to user="
							+ user.getName() + addr, ex);
		}
	}

	private void sendChangeGroupStatusConfirmation(final UserTObject user,
			final UserGroupTObject group, String originalText, final String replyType,
			final String originator, final int texttype) {
	    String txt = new String(originalText);
		String addr = "";
		try {
			if (originator != null) {
				txt += " (group status change initiated from " + originator
						+ ")";
			}

			if (replyType.equals(MSG_REPLY_TYPE_SMS)) {
				addr = " with MobilePhoneNumber=" + user.getMobilePhone();
				// publishToConnectorSms(txt, user.getMobilePhone()); // SMS

				// Get the numbers of all active group members
				Vector<UserTObject> activeUsers = UserGroupUserDAO
						.selectByGroupAndState(memoryCacheDb, group.getID(), 1);

				if (!activeUsers.isEmpty()) {
					UserTObject u = null;

					for (int i = 0; i < activeUsers.size(); i++) {
						u = activeUsers.get(i);
						if (u.getActive() != 0) {
							publishToConnectorSms(txt, u.getMobilePhone());
						}
					}

					u = null;
					activeUsers.clear();
					activeUsers = null;
				}
			} else if (replyType.equals(MSG_REPLY_TYPE_JMS)) {

				// TopicTObject topic = TopicDAO.select(conDb, fa
				// .getReceiverRef()); TODO
				// addr = " with Topic=" + topic.getName();
				// publishToConnectorJms(txt, topic.getName()); // JMS
			} else if (replyType.equals(MSG_REPLY_TYPE_EMAIL)) {
				addr = " with Email=" + user.getEmail();
				publishToConnectorMail(txt, user.getEmail(), user.getName()); // E-Mail
			} else if (replyType.equals(MSG_REPLY_TYPE_VOICEMAIL)) {
				addr = " with PhoneNumber=" + user.getPhone();
				publishToConnectorVoiceMail(txt, user.getPhone(), texttype); // VoiceMail
			} else {
				throw new AMSException("Invalid MSGPROP_REPLY_TYPE="
						+ replyType);
			}
		} catch (final Exception ex) {
			Log.log(this, Log.WARN,
					"failed to send change status confirmation message to user="
							+ user.getName() + addr, ex);
		}
	}

	//
	// End: Reply & ChangeStatus
	// //////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////
	// Start: Message Chain
	//

	private int replyAllMessageChain(final String confirmCode,
			final String replyType, final String replyAdress) throws Exception {
		int iRet = ErrorState.STAT_OK.getStateNumber();

		final List<?> lMc = MessageChainDAO.selectKeyListByReceiverAdress(
				memoryCacheDb, MESSAGECHAIN_WORK, replyAdress);

		final Iterator<?> iter = lMc.iterator();
		while (iter.hasNext()) {
			final MessageChainTObject chainDb = (MessageChainTObject) iter
					.next();
			iRet = replyMessageChain(chainDb, chainDb.getMessageChainID(),
					chainDb.getReceiverPos(), confirmCode, replyType,
					replyAdress);

			if (iRet != ErrorState.STAT_OK.getStateNumber()) {
				return iRet;
			}

		}
		return iRet;
	}

	private int replyMessageChain(final MessageChainTObject chainDbParam,
			final int msgChainId, final int msgChainPos,
			final String confirmCode, final String replyType,
			final String replyAdress) throws Exception // INCLUDING
	// -
	// SQLException,
	// JMSException
	{
		MessageChainTObject chainDb = null;

		if (chainDbParam != null) {
			chainDb = chainDbParam;
		} else {
			chainDb = MessageChainDAO.select(memoryCacheDb, msgChainId);
		}

		FilterActionTObject fa = null;
		AggrUserGroupUserTObject aUser = null;
		AggrUserGroupTObject userGroup = null;

		if (chainDb != null && chainDb.getChainState() == MESSAGECHAIN_WORK) {
			if (chainDb.getReceiverPos() == msgChainPos) {
				fa = FilterActionDAO.select(memoryCacheDb,
						chainDb.getFilterActionRef());
				if (fa != null) {
					if (replyType.equals(MSG_REPLY_TYPE_JMS)) {
						final TopicTObject topic = TopicDAO.select(
								memoryCacheDb, fa.getReceiverRef());
						chainDb.setChainState(MESSAGECHAIN_REPLIED);
						MessageChainDAO.update(localAppDb, chainDb);
						MessageChainDAO.update(memoryCacheDb, chainDb);
						HistoryWriter
								.logHistoryReply(localAppDb, "Reply", null,
										null, topic,
										"Chain replied for ChainId="
												+ msgChainId + ", Pos="
												+ msgChainPos + ".",
										fa.getFilterActionTypeRef(),
										msgChainId, msgChainPos, replyType,
										replyAdress);

						sendReplyConfirmationForJms(topic, msgChainId,
								msgChainPos, replyAdress);
						return ErrorState.STAT_OK.getStateNumber(); // All O.K.
					} else {
						userGroup = AggrUserGroupDAO.select(memoryCacheDb,
								fa.getReceiverRef(), msgChainPos);
						if (userGroup != null) {
							aUser = userGroup.getUsers().get(0);
						}

						String userConfirmCode = aUser.getUser().getConfirmCode();
						if (aUser != null
								&& userConfirmCode != null && userConfirmCode.equals(confirmCode)) {
							chainDb.setChainState(MESSAGECHAIN_REPLIED);
							MessageChainDAO.update(localAppDb, chainDb);
							MessageChainDAO.update(memoryCacheDb, chainDb);

							HistoryWriter.logHistoryReply(localAppDb, "Reply",
									aUser.getUser(), userGroup.getUsergroup(),
									null, "Chain replied for ChainId="
											+ msgChainId + ", Pos="
											+ msgChainPos + ".",
									fa.getFilterActionTypeRef(), msgChainId,
									msgChainPos, replyType, replyAdress);

							sendReplyConfirmationForUser(aUser.getUser(), true,
									null, msgChainId, msgChainPos, replyType,
									replyAdress);
							return ErrorState.STAT_OK.getStateNumber(); // All
																		// O.K.
						}
					}
				}
			}
		}

		String err = "no message chain found";
		if (chainDb != null) {
			err = "system error - configuration changed.";
			// ChainState too old
			// pos wrong
			// fa not there
			if (fa == null) {
				fa = FilterActionDAO.select(memoryCacheDb,
						chainDb.getFilterActionRef());
			}

			if (fa != null
					&& (userGroup == null || userGroup.getUsers().isEmpty())) {
				userGroup = AggrUserGroupDAO.select(memoryCacheDb,
						fa.getReceiverRef(), msgChainPos);
				if (userGroup != null) {
					aUser = userGroup.getUsers().get(0);
				}
			}

			// user not there
			// confirm code wrong
			if (aUser != null && fa != null) {
				if (chainDb.getChainState() != MESSAGECHAIN_WORK) {
					err = "message chain not in work.";
				} else if (chainDb.getReceiverPos() != msgChainPos) {
					err = "user not in time interval.";
				} else if (aUser.getUser().getConfirmCode() == null) {
					err = "use has no confirmation code.";
				} else if(!aUser.getUser().getConfirmCode()
						.equals(confirmCode)) {
					err = "wrong confirmation code.";
				}

				sendReplyConfirmationForUser(aUser.getUser(), false, err,
						msgChainId, msgChainPos, replyType, replyAdress);
			}
		} else {
			Log.log(Log.FATAL, "Message Reply: ChainID '" + msgChainId
					+ "' not found.");
		}

		HistoryWriter.logHistoryReply(localAppDb, "Reply Err",
				(aUser == null ? null : aUser.getUser()),
				(userGroup == null ? null : userGroup.getUsergroup()), null,
				"Reply not accepted for ChainId=" + msgChainId + ", Pos="
						+ msgChainPos + " Error: " + err,
				(fa == null ? 0 : fa.getFilterActionTypeRef()), msgChainId,
				msgChainPos, replyType, replyAdress);

		return ErrorState.STAT_OK.getStateNumber(); // All O.K.
	}

	private int workOnMessageChain(final int msgChainId) throws Exception {
		MessageChainTObject msgChain = null;
		try {
			msgChain = MessageChainDAO.select(memoryCacheDb, msgChainId);
			if (msgChain == null) {
				return ErrorState.STAT_OK.getStateNumber(); // handle as O.K.
															// (no error)
			}
			if (msgChain.getChainState() != MESSAGECHAIN_WORK) {
				return ErrorState.STAT_OK.getStateNumber(); // handle as O.K.
															// (no error)
			}
			if (msgChain.getNextActTime() != null) {
				if (System.currentTimeMillis() < msgChain.getNextActTime()
						.getTime()) {
					return ErrorState.STAT_OK.getStateNumber(); // handle as
																// O.K. (no
					// error)
				}
			}

			// msg is not send to internal topic, only for text preparing
			// if chain is not replied then send the msg to extern alarm topic
			MapMessage extMsg = null;
			try {
				extMsg = extSession.createMapMessage();
				MessageDAO.select(memoryCacheDb, msgChain.getMessageRef(),
						extMsg);
			} catch (final JMSException e) // JMSException (STAT_ERR_JMSCON_EXT)
			{
				Log.log(this,
						Log.FATAL,
						"workOnMessageChain: could not extSession.createMapMessage",
						e);
				return ErrorState.STAT_ERR_JMSCON_EXT.getStateNumber();
			}

			final FilterTObject filter = FilterDAO.select(memoryCacheDb,
					msgChain.getFilterRef());
			final FilterActionTObject filterAction = FilterActionDAO.select(
					memoryCacheDb, msgChain.getFilterActionRef());

			AggrUserGroupUserTObject aNextUser = null;
			AggrUserGroupTObject userGroup = null;
			boolean bOneActive = false; // if no one possible break up complete
			// chain

			if (filterAction != null && filter != null) {
				userGroup = AggrUserGroupDAO.selectList(memoryCacheDb,
						filterAction.getReceiverRef()); // ggf. eine Topic-ID!

				final Iterator<?> iter = userGroup.getUsers().iterator();
				while (iter.hasNext()) {
					final AggrUserGroupUserTObject aUser = (AggrUserGroupUserTObject) iter
							.next();

					if (aUser.getUserGroupUser().getActive() == 0) {
						Log.log(Log.WARN, "UserGroupRel not active: User "
								+ aUser.getUser().getUserID() + " of Group "
								+ aUser.getUserGroupUser().getUserGroupRef()
								+ " in FilterAction: " + filterAction.getFilterActionID());
						continue;
					}
					if (aUser.getUser().getActive() == 0) {
						Log.log(Log.WARN, "User not active: "
								+ aUser.getUser().getUserID()
								+ " in FilterAction: " + filterAction.getFilterActionID());
						continue;
					}

					bOneActive = true;

					if (-1 == aUser.getUserGroupUser().getPos()) {
						// => config
						// Error
						throw new AMSException(
								"Config Error: next UserPos == -1 == aUser.getUserGroupUser().getPos()");
					}

					if (msgChain.getReceiverPos() < aUser.getUserGroupUser()
							.getPos())// act < next
					{
						aNextUser = aUser;

						msgChain.setReceiverPos(aNextUser.getUserGroupUser()
								.getPos());
						final long currentTime = System.currentTimeMillis();
						msgChain.setSendTime(new Date(currentTime));
						msgChain.setNextActTime(new Date(currentTime
								+ userGroup.getUsergroup().getTimeOutSec()
								* 1000));

						break;
					}
				}
			}

			if (aNextUser != null) // send next
			{
				int iPref = 0;
				final String text = prepareMessageText(extMsg, filter, filterAction,
						msgChain); // throws
				// no
				// JMSException
				// (STAT_ERR_JMSCON_EXT)
				final String chainIdAndPos = prepareMessageNumber(
						msgChain.getMessageChainID(), msgChain.getReceiverPos());

				final UserTObject user = aNextUser.getUser();
				switch (user.getPrefAlarmingTypeRR()) {
				case USERFILTERALARMTYPE_SMS:
					publishToConnectorSms(text, user.getMobilePhone()); // SMS
					msgChain.setReceiverAdress(user.getMobilePhone());
					iPref = USERFILTERALARMTYPE_SMS;
					break;
				case USERFILTERALARMTYPE_JMS:
					final TopicTObject topic = TopicDAO.select(memoryCacheDb,
							filterAction.getReceiverRef());
					publishToConnectorJms(text, topic.getTopicName(), null); // JMS
					msgChain.setReceiverAdress(topic.getTopicName());
					iPref = USERFILTERALARMTYPE_JMS;
					break;
				case USERFILTERALARMTYPE_VM:
					publishToConnectorVoiceMail(text, user.getPhone(),
							chainIdAndPos,
							TextType.ALARM_WCONFIRM.getTextTypeNumber()); // VoiceMail
					iPref = USERFILTERALARMTYPE_VM;
					break;
				case USERFILTERALARMTYPE_MAIL:
					publishToConnectorMail(text, user.getEmail(),
							user.getName());// E-Mail
					iPref = USERFILTERALARMTYPE_MAIL;
					break;
				default:
					switch (filterAction.getFilterActionTypeRef()) {
					case FILTERACTIONTYPE_SMS_GR:
						publishToConnectorSms(text, user.getMobilePhone());// SMS
						msgChain.setReceiverAdress(user.getMobilePhone());
						break;
					case FILTERACTIONTYPE_VM_GR:
						publishToConnectorVoiceMail(text, user.getPhone(),
								chainIdAndPos,
								TextType.ALARM_WCONFIRM.getTextTypeNumber(),
								msgChain.getNextActTime());// VoiceMail
						break;
					case FILTERACTIONTYPE_MAIL_GR:
						publishToConnectorMail(text, user.getEmail(),
								user.getName());// E-Mail
						break;
					default:
						throw new AMSException(
								"Configuration is invalid. FilterActionType="
										+ filterAction.getFilterActionTypeRef());
					}
					break;
				}

				MessageChainDAO.update(localAppDb, msgChain);
				MessageChainDAO.update(memoryCacheDb, msgChain);

				try {
					HistoryWriter
							.logHistorySend(
									localAppDb,
									extMsg,
									text,
									filterAction.getFilterActionTypeRef(),
									user,
									(userGroup != null ? userGroup
											.getUsergroup() : null), msgChain
											.getReceiverPos(), msgChain
											.getMessageChainID(), iPref,
									TopicDAO.select(memoryCacheDb,
											filterAction.getReceiverRef()));
				} catch (final JMSException e) // JMSException
				// (STAT_ERR_JMSCON_EXT)
				{
					Log.log(this, Log.FATAL,
							"workOnMessageChain: could not logHistorySend", e);
					return ErrorState.STAT_ERR_JMSCON_EXT.getStateNumber();
				}
			} else {
				String err = "Chain failed for ChainId="
						+ msgChain.getMessageChainID() + ", Pos="
						+ msgChain.getReceiverPos()
						+ " Reason: nobody replied the chain.";
				if (filterAction == null || filter == null) // log if error only
				{
					err = "Chain failed for ChainId="
							+ msgChain.getMessageChainID() + ", Pos="
							+ msgChain.getReceiverPos()
							+ " Error: system error - configuration changed.";
				} else if (bOneActive == false) {
					err = "Chain broken up for ChainId="
							+ msgChain.getMessageChainID()
							+ ", Pos="
							+ msgChain.getReceiverPos()
							+ " Error: system error - configuration changed - no active user anymore.";
				}

				HistoryWriter.logHistoryReply(localAppDb, "Failed", null, null,
						null, err,
						(filterAction == null ? 0 : filterAction.getFilterActionTypeRef()),
						msgChain.getMessageChainID(),
						msgChain.getReceiverPos(), null, null);

				if (bOneActive == true || filterAction == null || filter == null) {
					try {
						extMsg.setString(MSGPROP_REINSERTED, "TRUE"); // store
						// it
						// new
						// to
						// alarm
						// topic
						extPublisherAlarm.send(extMsg);
					} catch (final JMSException e) // JMSException
					// (STAT_ERR_JMSCON_EXT)
					{
						Log.log(this,
								Log.FATAL,
								"workOnMessageChain: could not send to extPublisherAlarm",
								e);
						return ErrorState.STAT_ERR_JMSCON_EXT.getStateNumber();
					}
				}

				msgChain.setChainState(MESSAGECHAIN_FAILED); // delete old
				MessageChainDAO.update(localAppDb, msgChain);
				MessageChainDAO.update(memoryCacheDb, msgChain);
			}
			return ErrorState.STAT_OK.getStateNumber(); // All O.K.
		} catch (final SQLException e) {
			Log.log(this, Log.FATAL, "could not workOnMessageChain", e);
			return ErrorState.STAT_ERR_APPLICATION_DB.getStateNumber();
		} catch (final JMSException e) {
			Log.log(this, Log.FATAL, "could not workOnMessageChain", e);
			return ErrorState.STAT_ERR_JMSCON_INT.getStateNumber();
		} catch (final AMSException e) {
			Log.log(this,
					Log.FATAL,
					"could not workOnMessageChain, set message chain to failed",
					e);
			msgChain.setChainState(MESSAGECHAIN_FAILED);
			try {
				MessageChainDAO.update(localAppDb, msgChain);
				MessageChainDAO.update(memoryCacheDb, msgChain);
			} catch (final SQLException ex) {
				Log.log(this, Log.FATAL, "could not update message chain", ex);
				return ErrorState.STAT_ERR_APPLICATION_DB.getStateNumber();
			}
			return ErrorState.STAT_OK.getStateNumber(); // handle as O.K.,
														// continue with
			// work
		}
	}

	public String getTimeString(final java.util.Date date) {
		return String.valueOf(date.getTime());
	}

	//
	// End: Message Chain
	// //////////////////////////////////////////////////////////////////////////////

}