
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
import org.csstudio.ams.dbAccess.configdb.FlagDAO;
import org.csstudio.ams.dbAccess.configdb.HistoryDAO;
import org.csstudio.ams.dbAccess.configdb.HistoryTObject;
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
import org.csstudio.platform.utility.jms.JmsMultipleProducer;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;
import org.eclipse.jface.preference.IPreferenceStore;

/*- FIXME Frage klaeren, warum das T_AMS_JMS immer in user feld steht,
 *  auch dieser Connector nicht angesteuert wird??? */
public class DistributorWork extends Thread implements AmsConstants {
	
    // TODO: Replace it with an enum!
	 // alarm without confirmation
	public static final int TEXTTYPE_ALARM_WOCONFIRM = 1;
	
	 // alarm with confirmation
	public static final int TEXTTYPE_ALARM_WCONFIRM = 2;
	
	 // alarm confirmation ok
	public static final int TEXTTYPE_ALARMCONFIRM_OK = 3;
	
	 // alarm confirmation rejected
	public static final int TEXTTYPE_ALARMCONFIRM_NOK = 4;
	
	// status change ok
	public static final int TEXTTYPE_STATUSCHANGE_OK = 5;
	
	// status change rejected
	public static final int TEXTTYPE_STATUSCHANGE_NOK = 6;

	// private final int CONSUMER_CONNECTIONS = 2;

	private final static int CMD_INIT = 0;
	private final static int CMD_IDLE = 1; // normal work
	private final static int CMD_RPL_START = 2; // after rpl start command from

	// Fmr
	private final static int CMD_RPL_NOTIFY_FMR = 3; // after replication

	private int iCmd = CMD_INIT;

	private DistributorStart application = null;
	private java.sql.Connection conDb = null; // Derby database connection
	// (application db)

	// jms internal communication
	// --- Sender connection ---

	private JmsMultipleProducer amsSender;
	
	// --- Receiver connection ---
	private JmsRedundantReceiver amsReceiver = null;

	// jms external communication
	private Context extContext = null;
	private ConnectionFactory extFactory = null;
	private Connection extConnection = null;
	private Session extSession = null;

	private MessageProducer extPublisherAlarm = null;

	/** Container that holds the information about the connector topics. */
	private ConnectorTopicContainer topicContainer;
	
    private boolean bStop = false;
    private boolean bStoppedClean = false;
    
    private ConfigurationSynchronizer synchronizer;
    private java.sql.Connection masterDb;

	public DistributorWork(DistributorStart ds)
	{
		this.application = ds;
		
		// Create the container that holds the information about the connector topics.
		topicContainer = new ConnectorTopicContainer();
	}

	@Override
    public void run()
	{
		boolean bInitedConDb = false;
		boolean bInitedJmsInt = false;
		boolean bInitedJmsExt = false;
		int iErr = DistributorStart.STAT_OK;
		Log.log(this, Log.INFO, "start distributor work");
        bStop = false;

		while(bStop == false)
		{
			try {
				if (!bInitedConDb) {
					bInitedConDb = initApplicationDb();
					if (bInitedConDb) {
						bInitedConDb = initRplStateFlag(); // get last
                                    					// replication state
                                    					// flag value
						synchronizer = new ConfigurationSynchronizer(conDb, masterDb);
					}

					if (!bInitedConDb) // if one of the two functions return
						// false
						iErr = DistributorStart.STAT_ERR_APPLICATION_DB;
				}

				if (bInitedConDb && !bInitedJmsInt) {
					bInitedJmsInt = initJmsInternal();
					if (!bInitedJmsInt)
						iErr = DistributorStart.STAT_ERR_JMSCON_INT;
				}

				if (bInitedConDb && bInitedJmsInt && !bInitedJmsExt) {
					bInitedJmsExt = initJmsExternal();
					if (!bInitedJmsExt)
						iErr = DistributorStart.STAT_ERR_JMSCON_EXT;
				}

				sleep(1);

				if (bInitedConDb && bInitedJmsInt && bInitedJmsExt) {
					iErr = DistributorStart.STAT_OK;

					Log.log(this, Log.DEBUG, "runs");

					// WorkOnAlarmMessage 1 / 3 (replication start command)
					if (iErr == DistributorStart.STAT_OK && iCmd == CMD_IDLE) {
						Message message = null;

						try {
							// TODO Breakpoint und msg conent analyse.
							message = amsReceiver.receive("amsSubscriberDist");
						} catch (Exception e) {
							Log.log(this, Log.FATAL,
									"could not receive from internal jms", e);
							iErr = DistributorStart.STAT_ERR_JMSCON_INT;
						}

						if (message != null) {
							iErr = workOnMessage(message); // work on 1
							// messages, other
							// in the next run
						}
					}

					// replication
					if (iErr == DistributorStart.STAT_OK
							&& iCmd == CMD_RPL_START)
						iErr = rplExecute();

					if (iErr == DistributorStart.STAT_OK
							&& iCmd == CMD_RPL_NOTIFY_FMR)
						iErr = rplNotifyFmr();

					// WorkOnReplyMessage 2 / 3 (reply or change status)
					if (iErr == DistributorStart.STAT_OK) {
						Message message = null;

						try {
							message = amsReceiver.receive("amsSubscriberReply");
						} catch (Exception e) {
							Log.log(this, Log.FATAL,
									"could not receive from internal jms", e);
							iErr = DistributorStart.STAT_ERR_JMSCON_INT;
						}

						if (message != null) {
							iErr = responseMsg(message); // response 1
							// messages, other
							// in the next run
						}
					}

					// WorkOnMessageChain 3 / 3
					if (iErr == DistributorStart.STAT_OK) {
						List<Integer> keyList = MessageChainDAO.selectKeyList(
								conDb, MESSAGECHAIN_WORK);

						Iterator<Integer> iter = keyList.iterator();
						while (iter.hasNext()) {
							Integer val = iter.next();
							if (val != null) {
								iErr = workOnMessageChain(val.intValue());
								if (iErr != DistributorStart.STAT_OK)
									break; // error: exit while
							}
						}
					}
				}

				if (iErr == DistributorStart.STAT_ERR_APPLICATION_DB_SEND) {
					closeApplicationDb();
					bInitedConDb = false;
					closeJmsInternal(); // recover msg
					bInitedJmsInt = false;
				}

				// if (iErr == DistributorStart.STAT_ERR_FLG_RPL) do close all
				// if (iErr == DistributorStart.STAT_ERR_FLG_BUP) do close all
				if (iErr == DistributorStart.STAT_ERR_APPLICATION_DB
						|| iErr == DistributorStart.STAT_ERR_FLG_RPL
						|| iErr == DistributorStart.STAT_ERR_FLG_BUP) {
					closeApplicationDb();
					bInitedConDb = false;
				}
				if (iErr == DistributorStart.STAT_ERR_JMSCON_INT
						|| iErr == DistributorStart.STAT_ERR_FLG_RPL
						|| iErr == DistributorStart.STAT_ERR_FLG_BUP
						|| iErr == DistributorStart.STAT_ERR_JMSCON_FREE_SEND) // recover
				// msg
				{
					closeJmsInternal();
					bInitedJmsInt = false;
				}
				if (iErr == DistributorStart.STAT_ERR_JMSCON_EXT
						|| iErr == DistributorStart.STAT_ERR_FLG_RPL
						|| iErr == DistributorStart.STAT_ERR_FLG_BUP) {
					closeJmsExternal();
					bInitedJmsExt = false;
				}

				// set status in every loop
				application.setStatus(iErr); // set error status, can be OK if no error
			}
			catch(Exception e)
			{
				application.setStatus(DistributorStart.STAT_ERR_UNKNOWN);
				Log.log(this, Log.FATAL, e);

				closeApplicationDb();
				bInitedConDb = false;

				closeJmsInternal();
				bInitedJmsInt = false;

				closeJmsExternal();
				bInitedJmsExt = false;
			}
		}

        closeJmsExternal();
        closeJmsInternal();
        closeApplicationDb();
        bStoppedClean = true;

        Log.log(this, Log.INFO, "Distributor exited");
	}

	//
	// End: run
	// //////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////
	// Start: init & close (Derby DB, internal JMS, external JMS)
	//

	private boolean initRplStateFlag() throws Exception {
		try {
			short sFlag = FlagDAO.selectFlag(conDb, FLG_RPL);
			switch (sFlag) {
			// FLAGVALUE_SYNCH_FMR_RPL, FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED,
			// other:
			default:
				iCmd = CMD_IDLE; // go to idle if all o.k.
				break;
			case FLAGVALUE_SYNCH_DIST_RPL:
				iCmd = CMD_RPL_START;
				break;
			case FLAGVALUE_SYNCH_DIST_NOTIFY_FMR:
				iCmd = CMD_RPL_NOTIFY_FMR;
				break;
			}
			return true;
		} catch (SQLException e) {
			Log.log(this, Log.FATAL,
					"could not get flag value from application db", e);
		}
		return false;
	}

	private boolean initApplicationDb() {
		try {
			conDb = AmsConnectionFactory.getApplicationDB();
			masterDb = AmsConnectionFactory.getConfigurationDB();
			if (conDb == null || masterDb == null) {
				Log.log(this, Log.FATAL, "could not init application database");
				return false;
			}
			return true;
		} catch (Exception e) {
			Log.log(this, Log.FATAL, "could not init application database");
		}
		return false;
	}

	public void closeApplicationDb() {
		AmsConnectionFactory.closeConnection(conDb);
		conDb = null;
		Log.log(this, Log.INFO, "application database connection closed");
	}
	
	private boolean initJmsInternal() {
	    
	    IPreferenceStore storeAct = AmsActivator.getDefault().getPreferenceStore();
	    
	    boolean durable = Boolean.parseBoolean(storeAct.getString(AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE));

	    String url = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL);
	    String factory = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY_CLASS);
	    
	    amsSender = new JmsMultipleProducer("DistributorWorkSenderInternal", url, factory);
	    if (amsSender.isConnected() == false) {
	        Log.log(Log.ERROR, "Cannot create JMS multiple producer");
	        return false;
	    }
	    
	    
	    /* Command Topic */
	    
	    String topicName = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_COMMAND);
	    if (amsSender.addMessageProducer("amsPublisherCommand", topicName) == false) {
	        Log.log(this, Log.ERROR, "Cannot create amsPublisherCommand");
	        return false;
	    }
	    	    
	    
	    /* SMS Connector */
	    
	    topicName = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_SMS_CONNECTOR);
        if (amsSender.addMessageProducer("amsPublisherSms", topicName) == false) {
            Log.log(this, Log.ERROR, "Cannot create amsPublisherSms");
            return false;
        }
        boolean full = storeAct.getBoolean(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TOPIC_SMS_CONNECTOR_FORWARD);
        topicContainer.addConnectorTopic(new ConnectorTopic(topicName, "SmsConnector", full));
        
        
        /* JMS Connector */
        
        topicName = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR);
        if (amsSender.addMessageProducer("amsPublisherJms", topicName) == false) {
            Log.log(this, Log.ERROR, "Cannot create amsPublisherJms");
            return false;
        }
        full = storeAct.getBoolean(AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR_FORWARD);
        topicContainer.addConnectorTopic(new ConnectorTopic(topicName, "JmsConnector", full));

        
        /* Email Connector */
        
        topicName = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR);
        if (amsSender.addMessageProducer("amsPublisherMail", topicName) == false) {
            Log.log(this, Log.ERROR, "Cannot create amsPublisherMail");
            return false;
        }
        full = storeAct.getBoolean(AmsPreferenceKey.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR_FORWARD);
        topicContainer.addConnectorTopic(new ConnectorTopic(topicName, "EMailConnector", full));
        
        
        /* Voicemail Connector */
        
        topicName = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR);
        if (amsSender.addMessageProducer("amsPublisherVoiceMail", topicName) == false) {
            Log.log(this, Log.ERROR, "Cannot create amsPublisherVoiceMail");
            return false;
        }
        full = storeAct.getBoolean(AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR_FORWARD);
        topicContainer.addConnectorTopic(new ConnectorTopic(topicName, "VoicemailConnector", full));
        
        

        boolean success = true;
        
        try {
            
            amsReceiver = new JmsRedundantReceiver(
                              "DistributorWorkReceiverInternal",
                              storeAct.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1),
                              storeAct.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2));

            success = amsReceiver.createRedundantSubscriber(
                    "amsSubscriberDist",
                    storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_DISTRIBUTOR),
                    storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TSUB_DISTRIBUTOR),
                    durable);            
   
            if (success == false) {
                Log.log(this, Log.FATAL, "could not create amsSubscriberDist");
                return false;
            }

            success = amsReceiver.createRedundantSubscriber(
                   "amsSubscriberReply",
                   storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_REPLY),
                   storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TSUB_REPLY),
                   durable);
   
            if (success == false) {
                Log.log(this, Log.FATAL, "could not create amsSubscriberReply");
            }

       } catch (Exception e) {
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
			
	        IPreferenceStore storeAct = AmsActivator.getDefault()
					.getPreferenceStore();
						
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties
					.put(
							Context.INITIAL_CONTEXT_FACTORY,
							storeAct
									.getString(AmsPreferenceKey.P_JMS_EXTERN_CONNECTION_FACTORY_CLASS));
			properties
					.put(
							Context.PROVIDER_URL,
							storeAct
									.getString(AmsPreferenceKey.P_JMS_EXTERN_SENDER_PROVIDER_URL));
			extContext = new InitialContext(properties);

			extFactory = (ConnectionFactory) extContext
					.lookup(storeAct
							.getString(AmsPreferenceKey.P_JMS_EXTERN_CONNECTION_FACTORY));
			extConnection = extFactory.createConnection();

			// ADDED BY: Markus Moeller, 25.05.2007
			extConnection.setClientID("DistributorWorkSenderExternal");

			extSession = extConnection.createSession(false,
					Session.CLIENT_ACKNOWLEDGE);

			// CHANGED BY: Markus Moeller, 25.05.2007
			/*
			 * extPublisherAlarm =
			 * extSession.createProducer((Topic)extContext.lookup(
			 * storeAct.getString(SampleService.P_JMS_EXT_TOPIC_ALARM)));
			 */

			extPublisherAlarm = extSession
					.createProducer(extSession
							.createTopic(storeAct
									.getString(AmsPreferenceKey.P_JMS_EXT_TOPIC_ALARM)));
			if (extPublisherAlarm == null) {
				Log.log(this, Log.FATAL, "could not create extPublisherAlarm");
				return false;
			}

			extConnection.start();

			return true;
		} catch (Exception e) {
			Log.log(this, Log.FATAL, "could not init external Jms", e);
		}

		return false;
	}

	public void closeJmsExternal() {
		Log.log(this, Log.INFO, "exiting external jms communication");

		if (extPublisherAlarm != null) {
			try {
				extPublisherAlarm.close();
			} catch (JMSException e) {
				Log.log(this, Log.WARN, e);
			} finally {
				extPublisherAlarm = null;
			}
		}

		if (extSession != null) {
			try {
				extSession.close();
			} catch (JMSException e) {
				Log.log(this, Log.WARN, e);
			} finally {
				extSession = null;
			}
		}
		if (extConnection != null) {
			try {
				extConnection.stop();
			} catch (JMSException e) {
				Log.log(this, Log.WARN, e);
			}
		}
		if (extConnection != null) {
			try {
				extConnection.close();
			} catch (JMSException e) {
				Log.log(this, Log.WARN, e);
			} finally {
				extConnection = null;
			}
		}
		if (extContext != null) {
			try {
				extContext.close();
			} catch (NamingException e) {
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
    public synchronized void stopWorking()
    {
        bStop = true;
    }
    
    /**
     * Returns the shutdown state.
     * 
     * @return True, if the shutdown have occured clean otherwise false
     */
    public boolean stoppedClean()
    {
        return bStoppedClean;
    }

    private void publishToConnectorSms(String text, String addr)
			throws JMSException {
		MapMessage msg = amsSender.createMapMessage();
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
	private void publishToConnectorJms(String text, String topic, HashMap<String, String> map)
			throws JMSException {
		MapMessage msg = amsSender.createMapMessage();
		
		msg.setString(MSGPROP_RECEIVERTEXT, text);
		msg.setString(MSGPROP_RECEIVERADDR, topic);
		
		//TODO: Add the alarm message here!!!
		if(map != null)
		{
		    if(!map.isEmpty())
		    {
		        // The marker for a message containing the origin alarm message
		        msg.setString(MSGPROP_EXTENDED_MESSAGE, "true");
		        
		        String key;
		        Iterator<String> keys = map.keySet().iterator();
		        while(keys.hasNext())
		        {
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

	private void publishToConnectorMail(String text, String addr,
			String username) throws JMSException {
		MapMessage msg = amsSender.createMapMessage();
		msg.setString(MSGPROP_RECEIVERTEXT, text);
		msg.setString(MSGPROP_RECEIVERADDR, addr);
		Log.log(Log.INFO, "DistributorWork.publishToConnectorMail() -1- addr="
				+ msg.getString(MSGPROP_RECEIVERADDR) + ", username="
				+ msg.getString(MSGPROP_SUBJECT_USERNAME));
		msg.setString(MSGPROP_SUBJECT_USERNAME, username);
		Log.log(Log.INFO, "DistributorWork.publishToConnectorMail() -2- addr="
				+ msg.getString(MSGPROP_RECEIVERADDR) + ", username="
				+ msg.getString(MSGPROP_SUBJECT_USERNAME));
		amsSender.sendMessage("amsPublisherMail", msg);
	}

	private void publishToConnectorVoiceMail(String text, String addr,
			int texttype) throws JMSException {
		publishToConnectorVoiceMail(text, addr, "", texttype);
	}

	private void publishToConnectorVoiceMail(String text, String addr,
			String chainIdAndPos, int texttype) throws JMSException {
		MapMessage msg = amsSender.createMapMessage();
		msg.setString(MSGPROP_RECEIVERTEXT, text);
		msg.setString(MSGPROP_RECEIVERADDR, addr);
		msg.setString(MSGPROP_MESSAGECHAINID_AND_POS, chainIdAndPos);
		msg.setString(MSGPROP_TEXTTYPE, "" + texttype);
        msg.setString(MSGPROP_GROUP_WAIT_TIME, "0");
        amsSender.sendMessage("amsPublisherVoiceMail", msg);
	}

    private void publishToConnectorVoiceMail(String text, String addr,
            String chainIdAndPos, int texttype, Date nextActTime) throws JMSException {
        MapMessage msg = amsSender.createMapMessage();
        msg.setString(MSGPROP_RECEIVERTEXT, text);
        msg.setString(MSGPROP_RECEIVERADDR, addr);
        msg.setString(MSGPROP_MESSAGECHAINID_AND_POS, chainIdAndPos);
        msg.setString(MSGPROP_TEXTTYPE, "" + texttype);
        msg.setString(MSGPROP_GROUP_WAIT_TIME, getTimeString(nextActTime));
        amsSender.sendMessage("amsPublisherVoiceMail", msg);
    }

    private boolean acknowledge(Message msg) {
		try {
			msg.acknowledge();
			return true;
		} catch (Exception e) {
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

	private int workOnMessage(Message message) throws Exception {
		int iErr = DistributorStart.STAT_OK;
		try {
			if (!(message instanceof MapMessage))
				Log.log(this, Log.WARN, "got unknown message " + message);
			else {
				MapMessage msg = (MapMessage) message;
				Utils.logMessage("DistributorWork receives MapMessage", msg);

				String val = msg.getString(MSGPROP_COMMAND);
				if (val != null && val.equals(MSGVALUE_TCMD_RELOAD_CFG_START)) {
					iErr = rplStart();
				} else {
					iErr = distributeMessage(msg);
					if (iErr == DistributorStart.STAT_FALSE) {
						Log.log(this, Log.WARN,
								"could not distributeMessage, handle as O.K.");
						return DistributorStart.STAT_OK; // handle as O.K.
					}
				}
			}
		} catch (JMSException e) {
			Log.log(this, Log.FATAL, "could not workOnMessage", e);
			return DistributorStart.STAT_ERR_JMSCON_INT;
		}

		if (iErr == DistributorStart.STAT_OK) // only if rplStart() or
		// distributeMessage()
		// successful
		{ // and if no instanceof MapMessage, too
			if (!acknowledge(message)) // deletes all received messages of the
				// session
				return DistributorStart.STAT_ERR_JMSCON_INT;
		}
		return iErr;
	}

	private int distributeMessage(MapMessage msg) throws Exception {
		try {
			int iFilterId = Integer.parseInt(msg.getString(MSGPROP_FILTERID));
			FilterTObject filter = FilterDAO.select(conDb, iFilterId);

			String msgHost = msg.getString(MSGPROP_HOST);
			String msgProc = msg.getString(MSGPROP_PROCESSID);
			String msgName = msg.getString(MSGPROP_NAME);
			String msgEventTime = msg.getString(MSGPROP_EVENTTIME);
			HistoryTObject history = new HistoryTObject();
			history.setTimeNew(new Date(System.currentTimeMillis()));
			history.setType("Message");
			history.setMsgHost(msgHost);
			history.setMsgProc(msgProc);
			history.setMsgName(msgName);
			history.setMsgEventtime(msgEventTime);

			String description = "Message filtered by " + iFilterId + " - "
					+ (filter == null ? "filter not there" : filter.getName())
					+ "." + " Msg: " + Utils.getMessageString(msg);
			history.setDescription(description);

			HistoryDAO.insert(conDb, history);
			Log.log(Log.INFO, /* history.getHistoryID() + ". " + */
			description);
			// + " actiontype=" + history.getActionType()
			// + " user=" + history.getUserName()
			// + " via " + history.getDestType()
			// + " dest= " + history.getDestAdress());

			List<?> fActions = AggrFilterActionDAO.select(conDb, iFilterId);

			int iMessageID = 0;
			int iWorked = DistributorStart.STAT_FALSE;

			Iterator<?> iter = fActions.iterator();
			while (iter.hasNext()) {
				FilterActionTObject fa = (FilterActionTObject) iter.next();

				if (fa.getFilterActionTypeRef() == FILTERACTIONTYPE_SMS_GR
						|| fa.getFilterActionTypeRef() == FILTERACTIONTYPE_VM_GR
						|| fa.getFilterActionTypeRef() == FILTERACTIONTYPE_MAIL_GR) {
	                
				    // ADDED BY Markus Moeller, 2007-11-12
				    // Blocking non-active groups
				    AggrUserGroupTObject userGroup = AggrUserGroupDAO.selectList(conDb, fa
	                        .getReceiverRef());
	                if(userGroup.getUsergroup().getIsActive() != 0)
	                {
	                    if (iMessageID == 0)
	                        iMessageID = MessageDAO.insert(conDb, msg, true);

	                    MessageChainTObject msgChain = new MessageChainTObject(0,
	                            iMessageID, filter.getFilterID(), fa
	                                    .getFilterActionID(), 0, null, null,
	                            MESSAGECHAIN_WORK, null);
	                    MessageChainDAO.insert(conDb, msgChain);

	                    if (iWorked == DistributorStart.STAT_FALSE)
	                        iWorked = DistributorStart.STAT_OK;
	                }
	                else
	                {
                        HistoryWriter.logHistoryGroupBlocked(conDb, msg, "Send to group with reply", fa.getFilterActionTypeRef(),
                                userGroup.getUsergroup(), 0, 0, TopicDAO.select(conDb, fa.getReceiverRef()));	                    
	                }
				}
				else
				{
					int iErr = sendMessage(msg, filter, fa); // throws
					// Exception
					if (iErr == DistributorStart.STAT_OK
							|| iErr == DistributorStart.STAT_FALSE) {
						if (iWorked == DistributorStart.STAT_FALSE)
							iWorked = iErr;
					} else {
						return iErr;
					}
				}// else
			}// while
			return iWorked;
		} catch (JMSException e) {
			Log.log(this, Log.FATAL, "failed to sendMessage", e);
			return DistributorStart.STAT_ERR_JMSCON_INT;
		} catch (SQLException e) {
			Log.log(this, Log.FATAL, "failed to sendMessage", e);
			return DistributorStart.STAT_ERR_APPLICATION_DB_SEND;
		} catch (AMSException e) {
			Log
					.log(this, Log.FATAL,
							"failed to sendMessage, delete message", e);
			return DistributorStart.STAT_FALSE; // error, delete message
		}
	}

	private String prepareMessageText(MapMessage mapMsg, FilterTObject filter,
			FilterActionTObject fa, MessageChainTObject nextChain)
			throws Exception // INCLUDING - AMSException
	{
		String text = fa.getMessage();
		if(text == null)
		{
			text = filter.getDefaultMessage();
			if(text == null)
			{
			    text = "";
			}
		}
		
		String placeHolder = new String("$");
		int len = placeHolder.length();
		StringBuffer sbText = new StringBuffer(text);
		String key = null;
		int idxFirst = 0;
		int idxSecond = 0;

		while (true) {
			idxFirst = sbText.indexOf(placeHolder, idxFirst); // Search for
			// placeHolder
			if (idxFirst < 0) // placeHolder not found
				break;

			idxSecond = idxFirst + len;
			idxSecond = sbText.indexOf(placeHolder, idxSecond); // Search for
			// another
			// placeHolder
			if (idxSecond < 0) // second placeHolder not found
				break;

			key = sbText.substring(idxFirst + len, idxSecond);
			if (key != null)
				key = key.toUpperCase(); // error tolerance: someone typed in
			// small letter

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
					String value = mapMsg.getString(key);
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
				} catch (Exception ex) {
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

	private String prepareMessageNumber(int iChainID, int iChainPos)
			throws Exception // INCLUDING - AMSException
	{
		int iRlen = ("" + iChainPos).length();
		if (iRlen > MSG_POS_LENGTH_FOR_MSGPROP) {
			throw new AMSException("MessageChain ReceiverPos=" + iChainPos
					+ " has more chars than > MSG_POS_LENGTH_FOR_MSGPROP="
					+ MSG_POS_LENGTH_FOR_MSGPROP);
		}

		StringBuffer sb = new StringBuffer();
		sb.append(iChainID);
		while (iRlen++ < MSG_POS_LENGTH_FOR_MSGPROP)
			// until len == MSG_POS_LENGTH_FOR_MSGPROP
			sb.append('0'); // fill with leading zeros (1 -> 001)
		sb.append(iChainPos);

		return sb.toString();
	}

	private int sendMessage(MapMessage mapMsg, FilterTObject filter,
			FilterActionTObject fa) throws Exception // INCLUDING -
	// SQLException,
	// JMSException,
	// AMSException
	{
		int faTypeRef = fa.getFilterActionTypeRef();

		if (faTypeRef >= FILTERACTIONTYPE_TOPICDEST) {
			sendMessageToDefaultTopic(mapMsg);
			return DistributorStart.STAT_OK; // All O.K.
			// return sendMessageToTopic(mapMsg, faTypeRef); // to free topic
		} else if (faTypeRef == FILTERACTIONTYPE_SMS
				|| faTypeRef == FILTERACTIONTYPE_VM
				|| faTypeRef == FILTERACTIONTYPE_MAIL
				|| faTypeRef == FILTERACTIONTYPE_TO_JMS) {
			String text = prepareMessageText(mapMsg, filter, fa, null);
			return sendMessageToConnector(mapMsg, text, fa); // to user
		} else if (faTypeRef == FILTERACTIONTYPE_SMS_G
				|| faTypeRef == FILTERACTIONTYPE_VM_G
				|| faTypeRef == FILTERACTIONTYPE_MAIL_G) {
			String text = prepareMessageText(mapMsg, filter, fa, null);
			return sendMessageToUserGroup(mapMsg, text, fa); // to group
		} else {
			throw new AMSException(
					"Configuration is invalid. FilterActionType=" + faTypeRef);
		}
	}

	//
	// End: Distribute Message
	// //////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////
	// Start: Replication
	//

	private int rplStart() throws Exception {
		int result = synchronizer.startReplication();
		if (result == DistributorStart.STAT_OK) {
		    iCmd = CMD_RPL_START;
		}
		return result;
	}

	private int rplExecute() throws Exception // INCLUDING -
	// InterruptedException
	{
		int result = DistributorStart.STAT_OK;

		// delete all msg from dist topic subscriber
		Message msg = null;
		Log.log(this, Log.DEBUG, "delete all msg");
		while (null != (msg = amsReceiver.receive("amsSubscriberDist"))) {
			if (!acknowledge(msg)) {
				result = DistributorStart.STAT_ERR_JMSCON_INT;
				break;
			} else {
				result = DistributorStart.STAT_OK;
			}
		}

		if (result != DistributorStart.STAT_OK) {
			return result;
		}

		result = synchronizer.executeReplication();
		if (result == DistributorStart.STAT_OK) {
		    iCmd = CMD_RPL_NOTIFY_FMR;
		}
		return result;
	}

	private int rplNotifyFmr() throws Exception {
		try {
			Log
					.log(this, Log.INFO,
							"send MSGVALUE_TCMD_RELOAD_CFG_END to FMR via Ams Cmd Topic");
			MapMessage msg = amsSender.createMapMessage();
			msg.setString(MSGPROP_TCMD_COMMAND, MSGVALUE_TCMD_RELOAD_CFG_END);
			amsSender.sendMessage("amsPublisherCommand", msg);

			boolean bRet = FlagDAO.bUpdateFlag(conDb, FLG_RPL,
					FLAGVALUE_SYNCH_DIST_NOTIFY_FMR, FLAGVALUE_SYNCH_IDLE);
			if (bRet) {
				iCmd = CMD_IDLE; // end replication
				HistoryWriter.logHistoryRplStart(conDb, false);
			} else {
				Log.log(this, Log.FATAL,
						"update not successful, could not update " + FLG_RPL
								+ " from " + FLAGVALUE_SYNCH_DIST_NOTIFY_FMR
								+ " to " + FLAGVALUE_SYNCH_IDLE);
				return DistributorStart.STAT_ERR_FLG_RPL; // force new
				// initialization,
				// no recover()
				// needed
			}
		} catch (JMSException e) {
			Log.log(this, Log.FATAL, "could not publishReplicateEndToFMgr", e);
			return DistributorStart.STAT_ERR_JMSCON_INT;
		} catch (SQLException e) {
			Log.log(this, Log.FATAL, "could not bUpdateFlag", e);
			return DistributorStart.STAT_ERR_APPLICATION_DB;
		}
		return DistributorStart.STAT_OK;
	}

	/**
	 * Send the message to a default topic which was configured as default
	 * destination in preference page.
	 * 
	 * @param message
	 *            The message to be send
	 */
	private void sendMessageToDefaultTopic(Message message) {
		// TODO: create configuration, send
		Log
				.log(this, Log.WARN,
						"method sendMessageToDefaultTopic(Message message) not implemented yet!");
	}

	private int sendMessageToConnector(MapMessage mapMsg, String text,
			FilterActionTObject fa) throws Exception // INCLUDING -
	// SQLException,
	// JMSException,
	// AMSException
	{
	    ConnectorTopic ct = null;
	    HashMap<String, String> map = null;
	    
		UserTObject user = UserDAO.select(conDb, fa.getReceiverRef());
		if (fa.getFilterActionTypeRef() != FILTERACTIONTYPE_TO_JMS
				&& user.getActive() == 0) {
			Log.log(Log.WARN, "User not active: " + user.getUserID()
					+ " in FilterAction: " + fa.getFilterActionID());
			return DistributorStart.STAT_FALSE;
		}

		switch (fa.getFilterActionTypeRef())
		{
		    case FILTERACTIONTYPE_TO_JMS:
		        TopicTObject topic = TopicDAO.select(conDb, fa.getReceiverRef());
		        ct = topicContainer.getConnectorTopicByConnectorName("JmsConnector");
		        if(ct.isFullMessageReceiver())
		        {
		            map = this.getMessageContent(mapMsg);
		        }
		        
		        publishToConnectorJms(text, topic.getTopicName(), map);
		        break;
		        
		    case FILTERACTIONTYPE_SMS:
		        publishToConnectorSms(text, user.getMobilePhone()); // SMS
		        break;
		        
		    case FILTERACTIONTYPE_VM:
		        publishToConnectorVoiceMail(text, user.getPhone(),
					TEXTTYPE_ALARM_WOCONFIRM); // VoiceMail
		        break;
		
		    case FILTERACTIONTYPE_MAIL:
		        publishToConnectorMail(text, user.getEmail(), user.getName()); // E-Mail
		        break;
		        
		    default:
		        throw new AMSException(
					"Configuration is invalid. FilterActionType="
							+ fa.getFilterActionTypeRef());
		}

		HistoryWriter.logHistorySend(conDb, mapMsg, text, fa.getFilterActionTypeRef(), user,
				null, 0, 0, 0, TopicDAO.select(conDb, fa.getReceiverRef()));
		return DistributorStart.STAT_OK;
	}
	
	private HashMap<String, String> getMessageContent(MapMessage message)
	{
	    HashMap<String, String> map = new HashMap<String, String>();
	    String key = null;
	    
	    try
        {
            Enumeration<?> list = message.getMapNames();
            while(list.hasMoreElements())
            {
                key = (String)list.nextElement();
                map.put(key, message.getString(key));
            }
        }
	    catch(JMSException jmse)
        {
	        map.clear();
        }
	    
	    return map;
	}
	
	private int sendMessageToUserGroup(MapMessage mapMsg, String text,
			FilterActionTObject fa) throws Exception // INCLUDING -
	// SQLException,
	// JMSException,
	// AMSException
	{
		int iOneSended = DistributorStart.STAT_FALSE;
		AggrUserGroupTObject userGroup = AggrUserGroupDAO.selectList(conDb, fa
				.getReceiverRef());
		
		// If user group is NOT active...
		if(userGroup.getUsergroup().getIsActive() == 0)
		{
		    HistoryWriter.logHistoryGroupBlocked(conDb, mapMsg, text, fa.getFilterActionTypeRef(),
	                    userGroup.getUsergroup(), 0, 0, TopicDAO.select(conDb, fa.getReceiverRef()));
	          
	            iOneSended = DistributorStart.STAT_GROUP_BLOCKED; // == STAT_OK

	            return iOneSended;
		}
		
		Iterator<?> iter = userGroup.getUsers().iterator();
		while (iter.hasNext()) {
			AggrUserGroupUserTObject aUser = (AggrUserGroupUserTObject) iter
					.next();
			UserTObject user = aUser.getUser();

			if (aUser.getUserGroupUser().getActive() == 0) {
				Log.log(Log.WARN, "UserGroupRel not active: User "
						+ user.getUserID() + " of Group "
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
						TEXTTYPE_ALARM_WOCONFIRM); // VoiceMail
				break;
			case FILTERACTIONTYPE_MAIL_G:
				publishToConnectorMail(text, user.getEmail(), user.getName());// E-Mail
				break;
			default:
				throw new AMSException(
						"Configuration is invalid. FilterActionType="
								+ fa.getFilterActionTypeRef());
			}

			HistoryWriter.logHistorySend(conDb, mapMsg, text, fa.getFilterActionTypeRef(),
					aUser.getUser(), userGroup.getUsergroup(), 0, 0, 0,
					TopicDAO.select(conDb, fa.getReceiverRef()));
			iOneSended = DistributorStart.STAT_OK;
		}
		
		return iOneSended;
	}

	//
	// End: Send Message
	// //////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////
	// Start: Reply & ChangeStatus
	//

	private int responseMsg(Message message) throws Exception {
		int iErr = DistributorStart.STAT_OK;
		try {
			if (!(message instanceof MapMessage))
				Log.log(this, Log.WARN, "got unknown message " + message);
			else {
				MapMessage msg = (MapMessage) message;
				Utils.logMessage("DistributorWork receives MapMessage", msg);

				iErr = responseMessage(msg);
				if (iErr == DistributorStart.STAT_FALSE) {
					Log.log(this, Log.WARN,
							"MapMessage not accepted => delete it");
					iErr = DistributorStart.STAT_OK; // handle as O.K.
				}
			}
		} catch (SQLException e) {
			Log.log(this, Log.FATAL, "could not responseMessage", e);
			return DistributorStart.STAT_ERR_APPLICATION_DB_SEND;
		} catch (JMSException e) {
			Log.log(this, Log.FATAL, "could not responseMessage", e);
			return DistributorStart.STAT_ERR_JMSCON_INT;
		}

		if (iErr == DistributorStart.STAT_OK) // only if responseMessage()
		// successful
		{ // and if no instanceof MapMessage, too
			if (!acknowledge(message)) // deletes all received messages of the
				// session
				return DistributorStart.STAT_ERR_JMSCON_INT;
		}
		return iErr;
	}

	private int responseMessage(MapMessage msg) throws Exception // INCLUDING
	// -
	// JMSException,
	// SQLException,
	// InterruptedException
	{
		String strChainIdAndPos = msg.getString(MSGPROP_MESSAGECHAINID_AND_POS);
		String strGroupNum = msg.getString(MSGPROP_CHANGESTAT_GROUPNUM);

		String replyType = msg.getString(MSGPROP_REPLY_TYPE);
		String replyAdress = msg.getString(MSGPROP_REPLY_ADRESS);

		/* TEST
		String name = null;
		Enumeration<?> list = msg.getMapNames();
		while(list.hasMoreElements())
		{
		    name = (String)list.nextElement();
		    System.out.println(name + " = " + msg.getString(name));
		}
		System.out.println();
		*/
		
		if (strChainIdAndPos != null) {
			int chainID = 0;
			int chainPos = 0;
			try {
				if (!strChainIdAndPos.equals("#")) {
					if (strChainIdAndPos.length() < (MSG_POS_LENGTH_FOR_MSGPROP + 1))
						throw new NumberFormatException(
								"strChainIdAndPos.length() < "
										+ (MSG_POS_LENGTH_FOR_MSGPROP + 1));

					int posInStr = strChainIdAndPos.length()
							- MSG_POS_LENGTH_FOR_MSGPROP;// chars used for
					// pos
					chainPos = Integer.parseInt(strChainIdAndPos
							.substring(posInStr));
					chainID = Integer.parseInt(strChainIdAndPos.substring(0,
							posInStr));
				}
			} catch (NumberFormatException e) {
				// Log.log(Log.FATAL, "Message Reply: ChainIdAndPos is not a
				// valid number!", nfEx);
				// only warn
				Log.log(Log.WARN,
						"Message Reply: ChainIdAndPos is not a valid number!");
				return DistributorStart.STAT_FALSE;
			}

			String confirmCode = msg.getString(MSGPROP_CONFIRMCODE);

			if (strChainIdAndPos.equals("#"))
				return replyAllMessageChain(confirmCode, replyType, replyAdress);

			return replyMessageChain(null, chainID, chainPos, confirmCode,
					replyType, replyAdress);
		} else if (strGroupNum != null) {
			String strUserNum = msg.getString(MSGPROP_CHANGESTAT_USERNUM);
			String strStatus = msg.getString(MSGPROP_CHANGESTAT_STATUS);
			String strAction = msg.getString(MSGPROP_CHANGESTAT_ACTION);
			int groupNum = 0;
			int userNum = 0;
			short status = 0;
			boolean changeGroupState = (strAction.compareToIgnoreCase("group") == 0);
			
			try {
				groupNum = Integer.parseInt(strGroupNum);
				userNum = Integer.parseInt(strUserNum);
				Log.log(Log.INFO,
						"DistributorWork.responseMessage(): + groupNum="
								+ groupNum + ", userNum=" + userNum);
				status = Short.parseShort(strStatus);
			} catch (NumberFormatException e) {
				// Log.log(Log.FATAL, "Message Change Status: first three values
				// have to be numeric!");
				// only warn
				Log
						.log(Log.WARN,
								"Message Change Status: first three values have to be numeric!");
				return DistributorStart.STAT_FALSE;
			}

			String reason = msg.getString(MSGPROP_CHANGESTAT_REASON);
			reason = reason.trim();

			String statusCode = msg.getString(MSGPROP_CHANGESTAT_STATUSCODE);
			String txt = null;
			if(changeGroupState)
			{
			    txt = "G*" + groupNum + "*" + userNum + "*" + status + "*";
			}
			else
			{
			    txt = groupNum + "*" + userNum + "*" + status + "*";
			}
			
            if (!(status == 0 || status == 1)) {
                Log.log(Log.WARN, "unknown state: " + status + " for msg "
                        + txt);
                UserTObject userTmp = UserDAO.select(conDb, userNum);
                if (userTmp != null
                        && userTmp.getStatusCode().equalsIgnoreCase(statusCode))// NOT_OK
                // -
                // main
                // system
                // temporarly
                // not
                // available
                {
                    HistoryWriter.logHistoryChangeStatus(conDb, userTmp, "Unknown state.",
                            null, txt, status, reason, replyType, replyAdress);
                    sendChangeStatusConfirmation(userTmp, txt
                            + MSGCODE_UNKNOWN_STATUS, replyType, replyAdress,
                            TEXTTYPE_STATUSCHANGE_NOK);
                } else {
                    HistoryWriter.logHistoryChangeStatus(conDb, null, "Unknown state.", null,
                            txt, status, reason, replyType, replyAdress);
                }
                return DistributorStart.STAT_FALSE; // wrong request
            }

            boolean bBreak = false;
            
            // If the user want to change the group state...
			if(changeGroupState == true)
			{
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
                        } catch (Exception e) {
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
                            groupOra = changeGroupState(oraDb, groupNum, userNum,
                                    status, statusCode, reason, txt, replyType, replyAdress);
                        } catch (Exception e) {
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
    
                        if (groupOra != null) // in main system deleted => reload
                        // follows
                        {
                            try {
                                UserTObject user1 = UserDAO.select(conDb, userNum);
                                UserGroupTObject group2 = changeGroupState(conDb, groupNum,
                                        userNum, status, statusCode, reason, txt, replyType, replyAdress);
                                if (group2 != null) // OK - all well done
                                {
                                    UserGroupTObject ug = UserGroupDAO.select(
                                            conDb, groupNum);
                                    HistoryWriter.logHistoryChangeStatus(conDb, null, null, ug,
                                            txt, status, reason, replyType,
                                            replyAdress);
                                    sendChangeGroupStatusConfirmation(user1, ug, "Group state changed: " + txt
                                            + MSGCODE_OK, replyType, replyAdress,
                                            TEXTTYPE_STATUSCHANGE_OK);
                                    Log.log(Log.INFO, txt + MSGCODE_OK);
                                    return DistributorStart.STAT_OK; // All O.K.
                                }
                            } catch (SQLException e) {
                                Log.log(this, Log.FATAL, e);
                            }
                            HistoryWriter.logHistoryChangeStatus(
                                    conDb,
                                    null,
                                    "Critical error => can update local db => replicate configuration.",
                                    null, txt, status, reason, replyType,
                                    replyAdress);
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

			}
			else // the user want to change the user state
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
    					} catch (Exception e) {
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
    						userOra = changeStatus(oraDb, groupNum, userNum,
    								status, statusCode, reason, txt, replyType,
    								replyAdress);
    					} catch (Exception e) {
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
    							UserTObject user2 = changeStatus(conDb, groupNum,
    									userNum, status, statusCode, reason, txt,
    									replyType, replyAdress);
    							if (user2 != null) // OK - all well done
    							{
    								UserGroupTObject ug = UserGroupDAO.select(
    										conDb, groupNum);
    								HistoryWriter.logHistoryChangeStatus(conDb, user2, null, ug,
    										txt, status, reason, replyType,
    										replyAdress);
    								sendChangeStatusConfirmation(user2, txt
    										+ MSGCODE_OK, replyType, replyAdress,
    										TEXTTYPE_STATUSCHANGE_OK);
    								Log.log(Log.INFO, txt + MSGCODE_OK);
    								return DistributorStart.STAT_OK; // All O.K.
    							}
    						} catch (SQLException e) {
    							Log.log(this, Log.FATAL, e);
    						}
    						HistoryWriter.logHistoryChangeStatus(
    								conDb,
    								null,
    								"Critical error => can update local db => replicate configuration.",
    								null, txt, status, reason, replyType,
    								replyAdress);
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
    				Log
    						.log(
    								this,
    								Log.FATAL,
    								"Could not changeStatus: temporary no connection to configuration database for msg "
    										+ txt);
    				UserTObject userTmp = UserDAO.select(conDb, userNum);
    				HistoryWriter.logHistoryChangeStatus(conDb, userTmp,
    						"No connection to config db.", null, txt, status,
    						reason, replyType, replyAdress);
    				if (userTmp != null) // NOT_OK - main system temporarly not
    					// available
    					sendChangeStatusConfirmation(userTmp, txt
    							+ MSGCODE_NO_MAIN_SYSTEM, replyType, replyAdress,
    							TEXTTYPE_STATUSCHANGE_NOK);
    			}
	        } // if(changeGroupState == true) ... else
		} // else if (strGroupNum != null)
		return DistributorStart.STAT_FALSE;
	}

	private UserTObject changeStatus(java.sql.Connection con, int groupNum,
			int userNum, short status, String statusCode, String reason,
			String txt, String replyType, String replyAdress) throws Exception // INCLUDING
	// -
	// SQLException
	// (Oracle
	// DB
	// or
	// Derby
	// DB)
	{
		UserTObject user = UserDAO.select(con, userNum);
		if (user == null) {
			HistoryWriter.logHistoryChangeStatus(conDb, null, "UserID=" + userNum
					+ " not found.", null, txt, status, reason, replyType,
					replyAdress);
			Log
					.log(Log.FATAL, "User not found: " + userNum + " for msg "
							+ txt);// do not send back to user if not
			// registered
			return null; // no user to send to
		}

		if (!user.getStatusCode().equalsIgnoreCase(statusCode)) // consitent
		// state, but do
		// not publish
		// statuscode
		{
			HistoryWriter.logHistoryChangeStatus(conDb, user, "Status code does not match.",
					null, txt, status, reason, replyType, replyAdress);
			Log.log(Log.FATAL, "status code does not match for msg " + txt);
			return null;
		}

		UserGroupTObject ug = UserGroupDAO.select(con, groupNum);
		if (ug == null) {
			HistoryWriter.logHistoryChangeStatus(conDb, user, "GroupID=" + groupNum
					+ " not found.", null, txt, status, reason, replyType,
					replyAdress);
			Log.log(Log.WARN, "no group: " + groupNum + " for msg " + txt);

			// never coming twice here
			sendChangeStatusConfirmation(user, txt + MSGCODE_NO_GROUP,
					replyType, replyAdress, TEXTTYPE_STATUSCHANGE_NOK);
			return null;
		}

		for (int i = 0; i < 3; i++) // if update failed try max 3 times
		{
			UserGroupUserTObject ugu = UserGroupUserDAO.select(con, groupNum,
					userNum);
			if (ugu == null) {
				HistoryWriter.logHistoryChangeStatus(conDb, user, "User not in group.", ug,
						txt, status, reason, replyType, replyAdress);
				Log.log(Log.WARN, "user not in group for msg " + txt);
				// never coming twice here, nok not in group
				sendChangeStatusConfirmation(user, txt + MSGCODE_NOT_IN_GROUP,
						replyType, replyAdress, TEXTTYPE_STATUSCHANGE_NOK);
				return null;
			}
			if (ugu.getActive() == status) {
				Log.log(Log.WARN, "status already set for msg, handle as ok "
						+ txt);
				return user;
			}

			if (status == 0) // only check if want to set 0 - Inactive
			{
				int iActiveCount = 0;
				Iterator<?> iter = UserGroupUserDAO.selectList(con, groupNum)
						.iterator();
				while (iter.hasNext()) {
					if (((AggrUserGroupUserTObject) iter.next())
							.getUserGroupUser().getActive() == 1)// 0 -
						// Inactive,
						// 1 -
						// Active
						// (group
						// ownership)
						iActiveCount++; // count active user in group
				}
				if (ug.getMinGroupMember() >= iActiveCount) {
					HistoryWriter.logHistoryChangeStatus(conDb, user,
							"Min user count reached min="
									+ ug.getMinGroupMember() + ".", ug, txt,
							status, reason, replyType, replyAdress);
					Log.log(Log.WARN, "min user count reached min="
							+ ug.getMinGroupMember() + " for msg " + txt);

					// never coming twice here
					// NOT_OK - min count of user reached
					sendChangeStatusConfirmation(user, txt
							+ MSGCODE_MIN_USER_REACHED, replyType, replyAdress,
							TEXTTYPE_STATUSCHANGE_NOK);
					return null;
				}
			}

			if (reason.length() > 0) // only change reason if new reason
				ugu.setActiveReason(reason);

			ugu.setActive(status); // set status in UserGroupUserTObject
			if (UserGroupUserDAO.update(con, ugu))
				return user; // status updated
		}// for

		HistoryWriter.logHistoryChangeStatus(
				conDb,
				user,
				"failed to update status (tried 3 times, Data changed or deleted!).",
				ug, txt, status, reason, replyType, replyAdress);
		Log.log(Log.WARN, "failed to update status (tried 3 times) for msg "
				+ txt + " (Data changed or deleted!)");
		return null;
	}

	//java.sql.Connection con, int groupNum,
    //int userNum, short status, String statusCode, String reason,
    //String txt, String replyType, String replyAdress
	private UserGroupTObject changeGroupState(java.sql.Connection con, int groupNum,
            int userNum, short status, String statusCode, String reason,
            String txt, String replyType, String replyAdress) throws Exception
	{
	    UserGroupUserTObject groupUser = null;
	    UserTObject user = null;
	    
	    // Check whether or not the user is a member of the group
	    groupUser = UserGroupUserDAO.select(con, groupNum, userNum);
        if (groupUser == null) {
            HistoryWriter.logHistoryChangeStatus(conDb, null, "GroupID=" + groupNum
                    + " or UserID=" + userNum + " not found.", null, txt, status, reason, replyType,
                    replyAdress);
            Log.log(Log.FATAL, "User=" + userNum + " or group=" + groupNum + " not found");// do not send back to user if not
            // registered
            return null; // no user to send to
        }

        // Get the user
        user = UserDAO.select(con, userNum);
        if(user == null)
        {
            HistoryWriter.logHistoryChangeStatus(conDb, user, "UserID=" + userNum + " not found.", null, txt, status, reason, replyType,
                    replyAdress);
            Log.log(Log.FATAL, "User=" + userNum + " not found");// do not send back to user if not
        
            return null;
        }
        
        // Check the status code
        if (!user.getStatusCode().equalsIgnoreCase(statusCode)) // consitent
        // state, but do
        // not publish
        // statuscode
        {
            HistoryWriter.logHistoryChangeStatus(conDb, user, "Status code does not match.",
                    null, txt, status, reason, replyType, replyAdress);
            Log.log(Log.FATAL, "status code does not match for user=" + userNum);
            
            return null;
        }

        UserGroupTObject userGroup = UserGroupDAO.select(con, groupNum);
        if (userGroup == null) {
            HistoryWriter.logHistoryChangeStatus(conDb, user, "GroupID=" + groupNum
                    + " not found.", userGroup, txt, status, reason, replyType,
                    replyAdress);
            Log.log(Log.WARN, "no group: " + groupNum);

            // never coming twice here
            sendChangeStatusConfirmation(user, txt + MSGCODE_NO_GROUP,
                    replyType, replyAdress, TEXTTYPE_STATUSCHANGE_NOK);
            return null;
        }

        for (int i = 0; i < 3; i++) // if update failed try max 3 times
        {
            UserGroupTObject ug = UserGroupDAO.select(con, groupNum);
            if (ug == null) {
                HistoryWriter.logHistoryChangeStatus(conDb, user, "User not in group.", ug,
                        txt, status, reason, replyType, replyAdress);
                Log.log(Log.WARN, "group not found ");
                // never coming twice here, nok not in group
                sendChangeGroupStatusConfirmation(user, ug, txt + MSGCODE_NOT_IN_GROUP,
                        replyType, replyAdress, TEXTTYPE_STATUSCHANGE_NOK);
                return null;
            }
            if (ug.getIsActive() == status) {
                Log.log(Log.WARN, "status already set for msg, handle as ok ");
                return ug;
            }

            if (status == 0) // only check if want to set 0 - Inactive
            {
                int iActiveCount = 0;
                Iterator<?> iter = UserGroupUserDAO.selectList(con, groupNum)
                        .iterator();
                while (iter.hasNext()) {
                    if (((AggrUserGroupUserTObject) iter.next())
                            .getUserGroupUser().getActive() == 1)// 0 -
                        // Inactive,
                        // 1 -
                        // Active
                        // (group
                        // ownership)
                        iActiveCount++; // count active user in group
                }
            }

            ug.setIsActive(status); // set status in UserGroupUserTObject
            if (UserGroupDAO.update2(con, ug))
                return ug; // status updated
        }// for

        HistoryWriter.logHistoryChangeStatus(
                conDb,
                user,
                "failed to update group status (tried 3 times, Data changed or deleted!).",
                userGroup, txt, status, reason, replyType, replyAdress);
        Log.log(Log.WARN, "failed to update status (tried 3 times) for msg "
                + txt + " (Data changed or deleted!)");

	    return null;
	}
	
	private void sendReplyConfirmationForUser(UserTObject user, boolean bOk,
			String error, int msgChainId, int msgChainPos, String replyType,
			String originator) {
		String txt = null;
		String addr = "";
		try {
			int type = TEXTTYPE_ALARMCONFIRM_NOK;
			if (bOk) {
				txt = "Alarm confirmation successful for MsgNo="
						+ prepareMessageNumber(msgChainId, msgChainPos);
				type = TEXTTYPE_ALARMCONFIRM_OK;
			} else {
				txt = "Alarm confirmation rejected for MsgNo="
						+ prepareMessageNumber(msgChainId, msgChainPos)
						+ " Error: " + error;
			}

			if (originator != null)
				txt += " (confirmation initiated from " + originator + ")";

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
		} catch (Exception ex) {
			Log.log(this, Log.WARN,
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
	private void sendReplyConfirmationForJms(TopicTObject topic,
			int msgChainId, int msgChainPos, String originator) {
		String txt = null;
		String addr = "";
		try {
			txt = "Alarm confirmation successful for MsgNo="
					+ prepareMessageNumber(msgChainId, msgChainPos);

			if (originator != null)
				txt += " (confirmation initiated from " + originator + ")";

			addr = " with Topic=" + topic.getTopicName();
			publishToConnectorJms(txt, topic.getTopicName(), null);
			Log.log(this, Log.INFO, "Reply Confirmation:'" + txt
					+ "' send to topic=" + topic.getTopicName() + " via JMS("
					+ topic.getTopicName() + ")");
		} catch (Exception ex) {
			Log.log(this, Log.WARN,
					"failed to send reply confirmation message to topic="
							+ topic.getTopicName() + addr, ex);
		}
	}

	private void sendChangeStatusConfirmation(UserTObject user, String txt,
			String replyType, String originator, int texttype) {
		String addr = "";
		try {
			if (originator != null)
				txt += " (status change initiated from " + originator + ")";

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
			} else
				throw new AMSException("Invalid MSGPROP_REPLY_TYPE="
						+ replyType);
		} catch (Exception ex) {
			Log.log(this, Log.WARN,
					"failed to send change status confirmation message to user="
							+ user.getName() + addr, ex);
		}
	}

    private void sendChangeGroupStatusConfirmation(UserTObject user, UserGroupTObject group, String txt,
            String replyType, String originator, int texttype)
    {
        String addr = "";
        try
        {
            if (originator != null)
                txt += " (group status change initiated from " + originator + ")";

            if (replyType.equals(MSG_REPLY_TYPE_SMS))
            {
                addr = " with MobilePhoneNumber=" + user.getMobilePhone();
                // publishToConnectorSms(txt, user.getMobilePhone()); // SMS
                
                // Get the numbers of all active group members
                Vector<UserTObject> activeUsers = UserGroupUserDAO.selectByGroupAndState(conDb, group.getID(), 1);
                
                if(!activeUsers.isEmpty())
                {
                    UserTObject u = null;
                    
                    for(int i = 0;i < activeUsers.size();i++)
                    {
                        u = activeUsers.get(i);
                        if(u.getActive() != 0)
                        {
                            publishToConnectorSms(txt, u.getMobilePhone());
                        }
                    }
                    
                    u = null;
                    activeUsers.clear();
                    activeUsers = null;
                }
            }
            else if (replyType.equals(MSG_REPLY_TYPE_JMS)) {

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
            } else
                throw new AMSException("Invalid MSGPROP_REPLY_TYPE="
                        + replyType);
        } catch (Exception ex) {
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

	private int replyAllMessageChain(String confirmCode, String replyType,
			String replyAdress) throws Exception {
		int iRet = DistributorStart.STAT_OK;

		List<?> lMc = MessageChainDAO.selectKeyListByReceiverAdress(conDb,
				MESSAGECHAIN_WORK, replyAdress);

		Iterator<?> iter = lMc.iterator();
		while (iter.hasNext()) {
			MessageChainTObject chainDb = (MessageChainTObject) iter.next();
			iRet = replyMessageChain(chainDb, chainDb.getMessageChainID(),
					chainDb.getReceiverPos(), confirmCode, replyType,
					replyAdress);

			if (iRet != DistributorStart.STAT_OK)
				return iRet;

		}
		return iRet;
	}

	private int replyMessageChain(MessageChainTObject chainDbParam,
			int msgChainId, int msgChainPos, String confirmCode,
			String replyType, String replyAdress) throws Exception // INCLUDING
	// -
	// SQLException,
	// JMSException
	{
		MessageChainTObject chainDb = null;

		if (chainDbParam != null)
			chainDb = chainDbParam;
		else
			chainDb = MessageChainDAO.select(conDb, msgChainId);

		FilterActionTObject fa = null;
		AggrUserGroupUserTObject aUser = null;
		AggrUserGroupTObject userGroup = null;
		
		if (chainDb != null && chainDb.getChainState() == MESSAGECHAIN_WORK) {
			if (chainDb.getReceiverPos() == msgChainPos) {
				fa = FilterActionDAO
						.select(conDb, chainDb.getFilterActionRef());
				if (fa != null) {
					if (replyType.equals(MSG_REPLY_TYPE_JMS)) {
						TopicTObject topic = TopicDAO.select(conDb, fa
								.getReceiverRef());
						chainDb.setChainState(MESSAGECHAIN_REPLIED);
						MessageChainDAO.update(conDb, chainDb);
						HistoryWriter.logHistoryReply(conDb, "Reply", null, null, topic,
								"Chain replied for ChainId=" + msgChainId
										+ ", Pos=" + msgChainPos + ".", fa
										.getFilterActionTypeRef(), msgChainId,
								msgChainPos, replyType, replyAdress);

						sendReplyConfirmationForJms(topic, msgChainId,
								msgChainPos, replyAdress);
						return DistributorStart.STAT_OK; // All O.K.
					} else {
						userGroup = AggrUserGroupDAO.select(conDb, fa
								.getReceiverRef(), msgChainPos);
						if (userGroup != null)
							aUser = userGroup.getUsers().get(0);

						if (aUser != null
								&& aUser.getUser().getConfirmCode().equals(
										confirmCode)) {
							chainDb.setChainState(MESSAGECHAIN_REPLIED);
							MessageChainDAO.update(conDb, chainDb);

							HistoryWriter.logHistoryReply(conDb, "Reply", aUser.getUser(),
									userGroup.getUsergroup(), null,
									"Chain replied for ChainId=" + msgChainId
											+ ", Pos=" + msgChainPos + ".", fa
											.getFilterActionTypeRef(),
									msgChainId, msgChainPos, replyType,
									replyAdress);

							sendReplyConfirmationForUser(aUser.getUser(), true,
									null, msgChainId, msgChainPos, replyType,
									replyAdress);
							return DistributorStart.STAT_OK; // All O.K.
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
			if (fa == null)
				fa = FilterActionDAO
						.select(conDb, chainDb.getFilterActionRef());

			if (fa != null
					&& (userGroup == null || userGroup.getUsers().isEmpty())) {
				userGroup = AggrUserGroupDAO.select(conDb, fa.getReceiverRef(),
						msgChainPos);
				if (userGroup != null)
					aUser = userGroup.getUsers().get(0);
			}

			// user not there
			// confirm code wrong
			if (aUser != null && fa != null) {
				if (chainDb.getChainState() != MESSAGECHAIN_WORK)
					err = "message chain not in work.";
				else if (chainDb.getReceiverPos() != msgChainPos)
					err = "user not in time interval.";
				else if (!(aUser.getUser().getConfirmCode().equals(confirmCode)))
					err = "wrong confirmation code.";

				sendReplyConfirmationForUser(aUser.getUser(), false, err,
						msgChainId, msgChainPos, replyType, replyAdress);
			}
		} else
			Log.log(Log.FATAL, "Message Reply: ChainID '" + msgChainId
					+ "' not found.");

		HistoryWriter.logHistoryReply(conDb, "Reply Err", (aUser == null ? null : aUser
				.getUser()), (userGroup == null ? null : userGroup
				.getUsergroup()), null, "Reply not accepted for ChainId="
				+ msgChainId + ", Pos=" + msgChainPos + " Error: " + err,
				(fa == null ? 0 : fa.getFilterActionTypeRef()), msgChainId,
				msgChainPos, replyType, replyAdress);

		return DistributorStart.STAT_OK; // All O.K.
	}

	private int workOnMessageChain(int msgChainId) throws Exception {
		MessageChainTObject msgChain = null;
		try {
			msgChain = MessageChainDAO.select(conDb, msgChainId);
			if (msgChain == null)
				return DistributorStart.STAT_OK; // handle as O.K. (no error)
			if (msgChain.getChainState() != MESSAGECHAIN_WORK)
				return DistributorStart.STAT_OK; // handle as O.K. (no error)
			if (msgChain.getNextActTime() != null)
				if (System.currentTimeMillis() < msgChain.getNextActTime()
						.getTime())// no timeout
					return DistributorStart.STAT_OK; // handle as O.K. (no
			// error)

			// msg is not send to internal topic, only for text preparing
			// if chain is not replied then send the msg to extern alarm topic
			MapMessage extMsg = null;
			try {
				extMsg = extSession.createMapMessage();
				MessageDAO.select(conDb, msgChain.getMessageRef(), extMsg);
			} catch (JMSException e) // JMSException (STAT_ERR_JMSCON_EXT)
			{
				Log
						.log(
								this,
								Log.FATAL,
								"workOnMessageChain: could not extSession.createMapMessage",
								e);
				return DistributorStart.STAT_ERR_JMSCON_EXT;
			}

			FilterTObject filter = FilterDAO.select(conDb, msgChain
					.getFilterRef());
			FilterActionTObject fa = FilterActionDAO.select(conDb, msgChain
					.getFilterActionRef());

			AggrUserGroupUserTObject aNextUser = null;
			AggrUserGroupTObject userGroup = null;
			boolean bOneActive = false; // if no one possible break up complete
			// chain

			if (fa != null && filter != null) {
				userGroup = AggrUserGroupDAO.selectList(conDb, fa
						.getReceiverRef()); // ggf. eine Topic-ID!
				
				Iterator<?> iter = userGroup.getUsers().iterator();
				while (iter.hasNext()) {
					AggrUserGroupUserTObject aUser = (AggrUserGroupUserTObject) iter
							.next();

					if (aUser.getUserGroupUser().getActive() == 0) {
						Log
								.log(Log.WARN, "UserGroupRel not active: User "
										+ aUser.getUser().getUserID()
										+ " of Group "
										+ aUser.getUserGroupUser()
												.getUserGroupRef()
										+ " in FilterAction: "
										+ fa.getFilterActionID());
						continue;
					}
					if (aUser.getUser().getActive() == 0) {
						Log
								.log(Log.WARN, "User not active: "
										+ aUser.getUser().getUserID()
										+ " in FilterAction: "
										+ fa.getFilterActionID());
						continue;
					}

					bOneActive = true;

					if (-1 == aUser.getUserGroupUser().getPos()) // -1 = next
						// => config
						// Error
						throw new AMSException(
								"Config Error: next UserPos == -1 == aUser.getUserGroupUser().getPos()");

					if (msgChain.getReceiverPos() < aUser.getUserGroupUser()
							.getPos())// act < next
					{
						aNextUser = aUser;

						msgChain.setReceiverPos(aNextUser.getUserGroupUser()
								.getPos());
						long currentTime = System.currentTimeMillis();
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
				String text = prepareMessageText(extMsg, filter, fa, msgChain); // throws
				// no
				// JMSException
				// (STAT_ERR_JMSCON_EXT)
				String chainIdAndPos = prepareMessageNumber(msgChain
						.getMessageChainID(), msgChain.getReceiverPos());

				UserTObject user = aNextUser.getUser();
				switch (user.getPrefAlarmingTypeRR()) {
				case USERFILTERALARMTYPE_SMS:
					publishToConnectorSms(text, user.getMobilePhone()); // SMS
					msgChain.setReceiverAdress(user.getMobilePhone());
					iPref = USERFILTERALARMTYPE_SMS;
					break;
				case USERFILTERALARMTYPE_JMS:
					TopicTObject topic = TopicDAO.select(conDb, fa
							.getReceiverRef());
					publishToConnectorJms(text, topic.getTopicName(), null); // JMS
					msgChain.setReceiverAdress(topic.getTopicName());
					iPref = USERFILTERALARMTYPE_JMS;
					break;
				case USERFILTERALARMTYPE_VM:
					publishToConnectorVoiceMail(text, user.getPhone(),
							chainIdAndPos, TEXTTYPE_ALARM_WCONFIRM); // VoiceMail
					iPref = USERFILTERALARMTYPE_VM;
					break;
				case USERFILTERALARMTYPE_MAIL:
					publishToConnectorMail(text, user.getEmail(), user
							.getName());// E-Mail
					iPref = USERFILTERALARMTYPE_MAIL;
					break;
				default:
					switch (fa.getFilterActionTypeRef()) {
					case FILTERACTIONTYPE_SMS_GR:
						publishToConnectorSms(text, user.getMobilePhone());// SMS
						msgChain.setReceiverAdress(user.getMobilePhone());
						break;
					case FILTERACTIONTYPE_VM_GR:
						publishToConnectorVoiceMail(text, user.getPhone(),
								chainIdAndPos, TEXTTYPE_ALARM_WCONFIRM, msgChain.getNextActTime());// VoiceMail
						break;
					case FILTERACTIONTYPE_MAIL_GR:
						publishToConnectorMail(text, user.getEmail(), user
								.getName());// E-Mail
						break;
					default:
						throw new AMSException(
								"Configuration is invalid. FilterActionType="
										+ fa.getFilterActionTypeRef());
					}
					break;
				}

				MessageChainDAO.update(conDb, msgChain);

				try {
					HistoryWriter.logHistorySend(conDb, extMsg, text, fa
							.getFilterActionTypeRef(), user,
							(userGroup != null ? userGroup.getUsergroup()
									: null), msgChain.getReceiverPos(),
							msgChain.getMessageChainID(), iPref, TopicDAO
									.select(conDb, fa.getReceiverRef()));
				} catch (JMSException e) // JMSException
				// (STAT_ERR_JMSCON_EXT)
				{
					Log.log(this, Log.FATAL,
							"workOnMessageChain: could not logHistorySend", e);
					return DistributorStart.STAT_ERR_JMSCON_EXT;
				}
			} else {
				String err = "Chain failed for ChainId="
						+ msgChain.getMessageChainID() + ", Pos="
						+ msgChain.getReceiverPos()
						+ " Reason: nobody replied the chain.";
				if (fa == null || filter == null) // log if error only
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

				HistoryWriter.logHistoryReply(conDb, "Failed", null, null, null, err,
						(fa == null ? 0 : fa.getFilterActionTypeRef()),
						msgChain.getMessageChainID(),
						msgChain.getReceiverPos(), null, null);

				if (bOneActive == true || (fa == null || filter == null)) {
					try {
						extMsg.setString(MSGPROP_REINSERTED, "TRUE"); // store
						// it
						// new
						// to
						// alarm
						// topic
						extPublisherAlarm.send(extMsg);
					} catch (JMSException e) // JMSException
					// (STAT_ERR_JMSCON_EXT)
					{
						Log
								.log(
										this,
										Log.FATAL,
										"workOnMessageChain: could not send to extPublisherAlarm",
										e);
						return DistributorStart.STAT_ERR_JMSCON_EXT;
					}
				}

				msgChain.setChainState(MESSAGECHAIN_FAILED); // delete old
				MessageChainDAO.update(conDb, msgChain);
			}
			return DistributorStart.STAT_OK; // All O.K.
		} catch (SQLException e) {
			Log.log(this, Log.FATAL, "could not workOnMessageChain", e);
			return DistributorStart.STAT_ERR_APPLICATION_DB;
		} catch (JMSException e) {
			Log.log(this, Log.FATAL, "could not workOnMessageChain", e);
			return DistributorStart.STAT_ERR_JMSCON_INT;
		} catch (AMSException e) {
			Log
					.log(
							this,
							Log.FATAL,
							"could not workOnMessageChain, set message chain to failed",
							e);
			msgChain.setChainState(MESSAGECHAIN_FAILED);
			try {
				MessageChainDAO.update(conDb, msgChain);
			} catch (SQLException ex) {
				Log.log(this, Log.FATAL, "could not update message chain", ex);
				return DistributorStart.STAT_ERR_APPLICATION_DB;
			}
			return DistributorStart.STAT_OK; // handle as O.K., continue with
			// work
		}
	}

    public String getTimeString(java.util.Date date)
    {
        return String.valueOf(date.getTime());
    }

	//
	// End: Message Chain
	// //////////////////////////////////////////////////////////////////////////////

}