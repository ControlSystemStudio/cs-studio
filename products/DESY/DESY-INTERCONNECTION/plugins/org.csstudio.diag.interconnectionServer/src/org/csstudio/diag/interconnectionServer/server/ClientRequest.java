/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
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

package org.csstudio.diag.interconnectionServer.server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.DuplicateMessageDetector;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.IDuplicateMessageHandler;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.IocMessage;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.IocMessageParser;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.TagList;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.TagValuePair;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Thread created for each message which arrives from the IOC
 * Initial implementation by Markus Moeller.
 * 
 * @author Matthias Clausen
 */
public class ClientRequest implements Runnable
{
    private String				packetData 		= null;
	private DatagramSocket      socket          = null;
    private IocConnectionManager			statistic		= null;
    private IocConnection  statisticContent = null;
    private InterconnectionServer icServer = null;
    private boolean				RESET_HIGHEST_UNACKNOWLEDGED_ALARM_TRUE	= true;
    private boolean				RESET_HIGHEST_UNACKNOWLEDGED_ALARM_FALSE = false;
    private int statusMessageDelay = 0;
	private final InetAddress _address;
	private final int _port;
	private final int _length;
	private static Map<String, DuplicateMessageDetector>
			_duplicateMessageDetectors =
				new HashMap<String, DuplicateMessageDetector>();
    
	public ClientRequest( InterconnectionServer icServer, String packetData,
			DatagramSocket d, InetAddress replyAddress, int replyPort, int packetLength)
	{
        this.icServer = icServer;
        this.packetData = packetData;
		this.socket = d;
		_address = replyAddress;
        _port = replyPort;
        _length = packetLength;
		
        this.statistic	  = IocConnectionManager.getInstance();
	}
	
	public void run()
	{
	    String hostName = null;
	    String hostAndPort = null;
	    MapMessage message = null;
        
        hostName = _address.getHostName();
        /*
         * in case the host name is null
         * keep the IP address instead
         */
        if ( hostName == null) {
        	hostName = _address.getHostAddress();
        }
        hostAndPort = hostName + ":" + _port;
        GregorianCalendar parseTime = new GregorianCalendar();

        IPreferencesService prefs = Platform.getPreferencesService();
	    int jmsTimeToLiveAlarmsInt = prefs.getInt(
	    		Activator.getDefault().getPluginId(),
	    		PreferenceConstants.JMS_TIME_TO_LIVE_ALARMS, 0, null);
		int jmsTimeToLiveLogsInt = prefs.getInt(
				Activator.getDefault().getPluginId(),
				PreferenceConstants.JMS_TIME_TO_LIVE_LOGS, 0, null);
		int jmsTimeToLivePutLogsInt = prefs.getInt(
				Activator.getDefault().getPluginId(),
				PreferenceConstants.JMS_TIME_TO_LIVE_PUT_LOGS, 0, null);

		/*
		 * do we want to write out message indicators?
		 */
		String showMessageIndicator = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.SHOW_MESSAGE_INDICATOR, "", null); 
		boolean showMessageIndicatorB = false;
		if ( (showMessageIndicator !=null) && showMessageIndicator.equals("true")) {
			showMessageIndicatorB = true;
		}

        
        /*
         * increase stistic counter
         */
        icServer.getClientRequestTheadCollector().incrementValue();
        
        // write out some statistics
        //
        statisticContent = statistic.getIocConnection(hostName, _port);
        statisticContent.setTime( true);
        statisticContent.setLastMessageSize( _length);
        /*
		 * find logical name of IOC by the IP address
		 * do NOT check on the LDAP server if the name was already found...
		 */
        if ( statisticContent.getLogicalIocName() == null) {
        	/*
        	 * new IOC - ask LDAP for logical name
        	 */
        	String[] iocNames = LdapSupport.getInstance().getLogicalIocName ( _address.getHostAddress(), hostName);
        	statisticContent.setLogicalIocName( iocNames[0]);
        	/*
        	 * save ldapIocName 
        	 */
        	System.out.println("ClientRequest:  ldapIocName = " + iocNames[1]);
        	statisticContent.setLdapIocName(iocNames[1]);
        }
        
        
        Vector<TagValuePair> tagValuePairs	= new Vector<TagValuePair>();
        Hashtable<String,String> tagValue = new Hashtable<String,String>();	// could replace the Vector above
        
        DuplicateMessageDetector duplicateMessageDetector =
        	getDuplicateMessageDetectorForIoc(statisticContent.getLogicalIocName());

        IocMessageParser parser = new IocMessageParser();
		IocMessage iocMessage = parser.parse(packetData);
		duplicateMessageDetector.checkAndRemember(iocMessage);
		putIocMessageDataIntoLegacyDataStructures(iocMessage, tagValue,
				tagValuePairs);
        
		if (iocMessage.isValid()) {
			CentralLogger.getInstance().debug(this, "Packet parsed as valid IocMessage: " + iocMessage);
        	
        	parseTime = new GregorianCalendar();
        	boolean status	= true;
        	
        	//
        	// ok we successfully parsed whatever was sent
        	//
        	// now we'll send it to the jms server
        	//
        	
        	//
    		// set beacon time locally (set to current time - retrigger)
        	// to be performed in ANY case !!
    		//
    		statisticContent.setBeaconTime();	// set beacon ONLY for beacon messages!

    		IIocMessageSender sender = new SocketMessageSender(_address, _port, socket);
    		Session session;
        	
        	switch (TagList.getMessageType(iocMessage.getMessageTypeString())) {
        	
        	case TagList.ALARM_MESSAGE:				// compatibility with old version
        	case TagList.ALARM_STATUS_MESSAGE:		// compatibility with old version
        	case TagList.EVENT_MESSAGE:				// the real thing!
        		//
        		// ALARM jms server
        		//
        		
        		session = null;
        		try {
        			session = icServer.createJmsSession();
        			Destination alarmDestination = session.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer alarmSender = session.createProducer( alarmDestination);
                	alarmSender.setDeliveryMode(DeliveryMode.PERSISTENT);
                	alarmSender.setTimeToLive(jmsTimeToLiveAlarmsInt);
                	
            		message = session.createMapMessage();
            		prepareTypedJmsMessage(message, tagValuePairs, iocMessage.getMessageTypeString());
            		alarmSender.send(message);
            		alarmSender.close();
            		
            		icServer.getJmsMessageWriteCollector().setValue(LegacyUtil.timeSince(parseTime));
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			icServer.checkSendMessageErrorCount();
                    System.out.println("ClientRequest : send ALARM message : *** EXCEPTION *** : " + jmse.getMessage());
                } finally {
                	if (session != null) {
                		try {
							session.close();
						} catch (JMSException e) {
							CentralLogger.getInstance().warn(this, "Failed to close JMS session", e);
						}
                	}
                }
        		//
        		// just send a reply
        		// BUT wait to give the queues a chance to empty (depending on the size of the write vector in the LDAP engine)
        		//
        		if ( (Engine.getInstance().getWriteVector().size() >=0) && (Engine.getInstance().getWriteVector().size() < PreferenceProperties.MAX_TIME_DELAY_FOR_STATUS_MESSSAGES)) {
        			statusMessageDelay = Engine.getInstance().getWriteVector().size();
        		} else {
        			statusMessageDelay = PreferenceProperties.MAX_TIME_DELAY_FOR_STATUS_MESSSAGES;
        		}
        		 
        		try {
    				Thread.sleep( statusMessageDelay);
    			} catch (InterruptedException e) {
    				// TODO: handle exception
    			}
   				ReplySender.send(iocMessage.getMessageId(), status, sender);

        		updateLdapEntry( tagValue, RESET_HIGHEST_UNACKNOWLEDGED_ALARM_TRUE);
        		
        		if (showMessageIndicatorB) {
        			System.out.print("A");
        		}
        		break;
        		
        	case TagList.STATUS_MESSAGE:
        		//
        		// ALARM just a list of ALL alarm states from the IOC - status messages do NOT get displayed in the ALARM view
        		// they are important for the LDAP-Trees currently under display in the CSS-Alarm-Tree views!!!
        		//
        		session = null;
        		try {
        			session = icServer.createJmsSession();
        			Destination alarmDestination = session.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer alarmSender = session.createProducer( alarmDestination);
                	alarmSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	alarmSender.setTimeToLive( jmsTimeToLiveAlarmsInt);
                	
            		message = session.createMapMessage();
            		prepareTypedJmsMessage(message, tagValuePairs, iocMessage.getMessageTypeString());
            		alarmSender.send(message);
            		alarmSender.close();
            		
            		icServer.getJmsMessageWriteCollector().setValue(LegacyUtil.timeSince(parseTime));
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			icServer.checkSendMessageErrorCount();
                    System.out.println("ClientRequest : send ALARM message : *** EXCEPTION *** : " + jmse.getMessage());
                } finally {
                	if (session != null) {
                		try {
							session.close();
						} catch (JMSException e) {
							CentralLogger.getInstance().warn(this, "Failed to close JMS session", e);
						}
                	}
                }
        		//
        		// just send a reply
        		// BUT wait to give the queues a chance to empty (depending on the size of the write vector in the LDAP engine)
        		//
        		if ( (Engine.getInstance().getWriteVector().size() >=0) && (Engine.getInstance().getWriteVector().size() < PreferenceProperties.MAX_TIME_DELAY_FOR_STATUS_MESSSAGES)) {
        			statusMessageDelay = Engine.getInstance().getWriteVector().size();
        		} else {
        			statusMessageDelay = PreferenceProperties.MAX_TIME_DELAY_FOR_STATUS_MESSSAGES;
        		}
        		 
        		try {
    				Thread.sleep( statusMessageDelay);
    			} catch (InterruptedException e) {
    				// TODO: handle exception
    			}
    			
    			ReplySender.send(iocMessage.getMessageId(), status, sender);

        		//
        		// time to update the LDAP server entry
        		//

        		updateLdapEntry( tagValue, RESET_HIGHEST_UNACKNOWLEDGED_ALARM_FALSE);
        		
        		if (showMessageIndicatorB) {
        			System.out.print("AS");
        		}

        		break;
        		
        	case TagList.SNL_LOG_MESSAGE:  // TODO send SNL log to its own topic
        	case TagList.SYSTEM_LOG_MESSAGE:
        	case TagList.APPLICATION_LOG_MESSAGE:
        		

        		//
        		// LOG jms server
        		//
        		session = null;
        		try {
        			session = icServer.createJmsSession();
        			Destination logDestination = session.createTopic( PreferenceProperties.JMS_LOG_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer logSender = session.createProducer( logDestination);
                	logSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	logSender.setTimeToLive( jmsTimeToLiveLogsInt);
                	/*
                	 * XXX: This creates a JMS message with TYPE set to one
                	 * of 'sysLog', 'sysMsg' or 'appLog', but none of these
                	 * types are actually defined for JMS messages (see comment
                	 * in class TagList).
                	 */
                    message = session.createMapMessage();
                    prepareTypedJmsMessage(message, tagValuePairs, iocMessage.getMessageTypeString());
            		logSender.send(message);
            		logSender.close();
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			icServer.checkSendMessageErrorCount();
                } finally {
                	if (session != null) {
                		try {
							session.close();
						} catch (JMSException e) {
							CentralLogger.getInstance().warn(this, "Failed to close JMS session", e);
						}
                	}
                }
        		//
        		// just send a reply
        		//
        		ReplySender.send(iocMessage.getMessageId(), status, sender);
        		updateLdapEntry( tagValue, RESET_HIGHEST_UNACKNOWLEDGED_ALARM_FALSE);
        		
        		if (showMessageIndicatorB) {
        			System.out.print("S");
        		}
        		break;
        		
        	case TagList.BEACON_MESSAGE:
        		// IOC versions that use this message type should no longer be
        		// in use anywhere.
        		CentralLogger.getInstance().warn(this,
        				"Received message with TYPE=beacon, " +
        				"message type not supported! (ioc: " +
        				statisticContent.getLogicalIocName() +
        				"; message: " + iocMessage + ")");

        	case TagList.BEACON_MESSAGE_SELECTED:
        		//
        		// just send a reply
        		//
        		ReplySender.send(iocMessage.getMessageId(), status, sender);
        		
        		/*
        		 * OK - we are selected - so:
        		 * just in case we previously set the channel to disconnected - we'll have to update all alarm states
        		 * -> trigger the IOC to send all alarms!
        		 */
        		if ( statisticContent.isDidWeSetAllChannelToDisconnect()) {
        			/*
        			 * yes - did set all channel to disconnect
        			 * we'll have to get all alarm-states from the IOC
        			 * 
        			 * XXX: This will send the command to the same port to which
        			 * a reply would be sent. All other users of SendCommandToIoc
        			 * send the command to the command port configured in the
        			 * preferences.
        			 */
        			SendCommandToIoc sendCommandToIoc = new SendCommandToIoc( hostName, _port, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
    				icServer.getCommandExecutor().execute(sendCommandToIoc);
    				statisticContent.setGetAllAlarmsOnSelectChange(false);	// we set the trigger to get the alarms...
    				statisticContent.setDidWeSetAllChannelToDisconnect(false);
    				CentralLogger.getInstance().info(this, "IOC Connected and selected again - previously channels were set to disconnect - get an update on all alarms!");
        		}
        		
        		/*
        		 * we are selected!
        		 * in case we were not selected before - we'll ask the IOC for an update on ALL the alarm states
        		 */
        		if (!statisticContent.isSelectState()) {
        			//remember we're selected
        			statisticContent.setSelectState(true);
        			
        			String selectMessage = "SELECTED";
        			if ( statisticContent.wasPreviousBeaconWithinThreeBeaconTimeouts()) {
        				selectMessage = "SELECTED - switch over";
        			}
        			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					icServer.getLocalHostName() + ":" + statisticContent.getLogicalIocName() + ":selectState",					// name
        					icServer.getLocalHostName(), 														// value
        					JmsMessage.SEVERITY_NO_ALARM, 										// severity
        					selectMessage, 														// status
        					hostName, 															// host
        					null, 																// facility
        					"virtual channel");	
        			// send command to IOC - get ALL alarm states
        			/*
        			 * if we received beacons within the last two beacon timeout periods we 'probably' did not loose any messages
        			 * this is a switch over from one IC-Server to another and thus
        			 * we DO NOT have to ask for an update on all alarms! 
        			 */
        			if ( ! statisticContent.wasPreviousBeaconWithinThreeBeaconTimeouts() &&
        					statisticContent.isGetAllAlarmsOnSelectChange()) {
        				/*
	        			 * XXX: This will send the command to the same port to which
	        			 * a reply would be sent. All other users of SendCommandToIoc
	        			 * send the command to the command port configured in the
	        			 * preferences.
        				 */
        				SendCommandToIoc sendCommandToIoc = new SendCommandToIoc( hostName, _port, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
        				icServer.getCommandExecutor().execute(sendCommandToIoc);
        				statisticContent.setGetAllAlarmsOnSelectChange(false);	// we set the trigger to get the alarms...
        				CentralLogger.getInstance().info(this, "This is a fail over from one IC-Server to this one - get an update on all alarms!");
        			} else {
        				CentralLogger.getInstance().info(this, "Just a switch over from one IC-Server to this one - no need to get an update on all alarms!");
        			}
        		}
        		
        		
        		//
        		// generate system log message if connection state changed
        		//
        		if ( !statisticContent.getConnectState()) {
        			//
        			// connect state changed!
        			//
        			statisticContent.setConnectState (true);
        			statisticContent.setTimeReConnected();
        			/*
        			 * start IocChangeState thread
        			 */
        			new IocChangedState (statisticContent, true);
        			
        			/*
        			 * create JMS sender
        			 * 
        			 */
        			session = null;
        			try {
        				session = icServer.createJmsSession();
                    	MapMessage logMessage = session.createMapMessage();
						prepareJmsMessageLogNewClientConnected(logMessage, hostAndPort);
						icServer.sendLogMessage(logMessage, session);

            			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
            					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
            					icServer.getLocalHostName() + ":" + hostName + ":connectState",					// name
            					icServer.getLocalHostName(), 														// value
            					JmsMessage.SEVERITY_NO_ALARM, 										// severity
            					"CONNECTED", 														// status
            					hostName, 															// host
            					null, 																// facility
            					"virtual channel");	
            		}
            		catch(JMSException jmse)
                    {
            			status = false;
            			icServer.checkSendMessageErrorCount();
                    } finally {
                    	if (session != null) {
                    		try {
								session.close();
							} catch (JMSException e) {
								CentralLogger.getInstance().warn(this, "Failed to close JMS session", e);
							}
                    	}
                    }
        		}
        		if (showMessageIndicatorB) {
        			System.out.print("B");
        		}
        		break;
        		
        	case TagList.IOC_SYSTEM_MESSAGE:
        		
        		// TODO: handle system messages other than "switchOver"

        		ReplySender.send(iocMessage.getMessageId(), status, sender);
        		
        		boolean isSwitchOverMessage = iocMessage.contains("TEXT")
        				&& "switchOver".equals(iocMessage.getItem("TEXT").getValue());
        		
        		/*
        		 * we are selected!
        		 * in case we were not selected before - we'll ask the IOC for an update on ALL the alarm states
        		 */
        		if (isSwitchOverMessage) {
        			//remember we're selected
        			statisticContent.setSelectState(true);

        			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					icServer.getLocalHostName() + ":" + statisticContent.getLogicalIocName() + ":selectState",					// name
        					icServer.getLocalHostName(), 														// value
        					JmsMessage.SEVERITY_NO_ALARM, 										// severity
        					"SELECTED - switch over", 												// status
        					hostName, 															// host
        					null, 																// facility
        					"virtual channel");	
        			// do NOT send command to IOC - get ALL alarm states
//        			new SendCommandToIoc( statisticId, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
//        			icServer.getCommandExecutor().execute(sendCommandToIoc);
        		}
        		
        		
        		//
        		// do NOT generate system log message : connection state my not have changed changed
        		//
        		
        		if (showMessageIndicatorB) {
        			System.out.print("SO");
        		}
        		break;
        		
        	case TagList.BEACON_MESSAGE_NOT_SELECTED:
        		//
        		// just send a reply
        		//
        		ReplySender.send(iocMessage.getMessageId(), status, sender);
        		
        		/*
        		 * we are not selected any more
        		 * in case we were selected before - we'll have to create a JMS message
        		 */
        		if ( statisticContent.isSelectState()) {
        			//remember we're not selected any more
        			statisticContent.setSelectState(false);

        			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					icServer.getLocalHostName() + ":" + statisticContent.getLogicalIocName() + ":selectState",	// name
        					icServer.getLocalHostName(), 														// value
        					JmsMessage.SEVERITY_MINOR, 											// severity
        					"NOT-SELECTED", 													// status
        					hostName, 															// host
        					null, 																// facility
        					"virtual channel");	
        		}
        		//
        		// generate system log message if connection state changed
        		//
        		if ( !statisticContent.getConnectState()) {
        			//
        			// connect state changed!
        			//
        			statisticContent.setConnectState (true);
        			statisticContent.setTimeReConnected();
        			/*
        			 * start IocChangeState thread
        			 */
        			new IocChangedState (statisticContent, true);
        			
        			/*
        			 * create JMS sender
        			 * 
        			 */
        			session = null;
        			try {
        				session = icServer.createJmsSession();
                    	MapMessage logMessage = session.createMapMessage();
						prepareJmsMessageLogNewClientConnected(logMessage, hostAndPort);
						icServer.sendLogMessage(logMessage, session);

            			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
            					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
            					icServer.getLocalHostName() + ":" + hostName + ":connectState",					// name
            					icServer.getLocalHostName(), 														// value
            					JmsMessage.SEVERITY_NO_ALARM, 										// severity
            					"CONNECTED", 														// status
            					hostName, 															// host
            					null, 																// facility
            					"virtual channel");	

            		}
            		catch(JMSException jmse)
                    {
            			status = false;
            			icServer.checkSendMessageErrorCount();
                    } finally {
                    	if (session != null) {
                    		try {
								session.close();
							} catch (JMSException e) {
								CentralLogger.getInstance().warn(this, "Failed to close JMS session", e);
							}
                    	}
                    }
        		}
        		if (showMessageIndicatorB) {
        			System.out.print("B");
        		}
        		break;
        		
        	case TagList.PUT_LOG_MESSAGE:
        		//
        		// PUT-LOG jms server
        		//
        		session = null;
        		try {
                    // Create the destination (Topic or Queue)
        			session = icServer.createJmsSession();
        			Destination putLogDestination = session.createTopic(PreferenceProperties.JMS_PUT_LOG_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer putLogSender = session.createProducer( putLogDestination);
                	putLogSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	putLogSender.setTimeToLive( jmsTimeToLivePutLogsInt);
                    message = session.createMapMessage();
                    prepareTypedJmsMessage(message, tagValuePairs, iocMessage.getMessageTypeString());
            		putLogSender.send(message);
            		putLogSender.close();
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			icServer.checkSendMessageErrorCount();
                    System.out.println("ClientRequest : send ALARM message : *** EXCEPTION *** : " + jmse.getMessage());
                } finally {
        			if (session != null) {
						try {
							session.close();
						} catch (JMSException e) {
							CentralLogger.getInstance().warn(this, "Failed to close JMS session", e);
						}
					}
        		}
        		ReplySender.send(iocMessage.getMessageId(), status, sender);
        		if (showMessageIndicatorB) {
        			System.out.print("P");
        		}
        		break;
        	case TagList.TEST_COMMAND:
            	if (showMessageIndicatorB) {
        			System.out.print("T");
        		}
            	break;
        	case TagList.UNKNOWN_MESSAGE:
        		default:
        		status = false;
        		ReplySender.send(iocMessage.getMessageId(), status, sender);
        		if (showMessageIndicatorB) {
        			System.out.print("U");
        		}
        	}

        }
     
        icServer.getClientRequestTheadCollector().decrementValue();
	}

	/**
	 * Prepares a JMS message for logging that a new client was connected. (At
	 * least, that is what I think this method does.)
	 * 
	 * @param message
	 *            the message to prepare.
	 * @param host
	 *            the host name.
	 */
	static void prepareJmsMessageLogNewClientConnected(MapMessage message,
			String host) {
		/*
		 * Implementation note: this method is based on the former method
		 * InterconnectionServer#jmsLogMessageNewClientConnected.
		 */
		
		try {
			// XXX: According to Markus, "SysLog" is not a defined type for JMS
			// messages. See the comment in TagList for a list of defined JMS
			// message types.
			message.setString("TYPE", "SysLog");
			Date eventtime = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat(PreferenceProperties.JMS_DATE_FORMAT);
			message.setString("EVENTTIME", dateFormat.format(eventtime));
			message.setString("TEXT", "new log client connected");
			message.setString("HOST", host);
			message.setString("STATUS", "on");
			message.setString("SEVERITY", "NO_ALARM");
		} catch (JMSException e) {
			// XXX: This basically swallows the exception instead of informing
			//        the caller. This method must throw an exception so the
			//        caller knows about the error and does not simply continue
			//        and use an uninitialized message!
			
			// TODO: make it a log message
			System.out.println("ClientRequest : prepareJmsMessage : *** EXCEPTION *** : " + e.getMessage());
		}
	}

	/**
	 * Prepares the fields of a JMS message by adding a type field and one field
	 * for each tag value pair passed to this method.
	 * 
	 * XXX: Note that this method simply sets the TYPE field of the JMS message
	 * to whatever TYPE was received from the IOC. This method does not check
	 * whether the type is a valid type for a JMS message.
	 * 
	 * @param message
	 *            the JMS message to prepare.
	 * @param tagValuePairs
	 *            the tag value pairs. For each tag value pair contained in this
	 *            collection, the corresponding field in the JMS message will be
	 *            set.
	 * @param type
	 *            the message type.
	 */
	// TODO: package-private for testing. Should be refactored!
	static void prepareTypedJmsMessage(MapMessage message,
			Vector<TagValuePair> tagValuePairs, String type) {
		try {
			message.setString("TYPE", type);
			for (TagValuePair pair : tagValuePairs) {
				message.setString(pair.getTag(), pair.getValue());
			}
		}
		catch(JMSException jmse)
		{
			// XXX: This basically swallows the exception instead of informing
			//        the caller. This method must throw an exception so the
			//        caller knows about the error and does not simply continue
			//        and use an uninitialized message!
			
			// TODO: make it a log message
			System.out.println("ClientRequest : prepareJmsMessage : *** EXCEPTION *** : " + jmse.getMessage());
		}
	}

	/**
	 * Returns the duplicate message detector that should be used for the given
	 * IOC.
	 * 
	 * @param ioc
	 *            the IOC.
	 * @return the duplicate message detector.
	 */
	// TODO: public for testing. Should be refactored!
	// TODO: this method also should not be static but in some IOC connection scope
	public static DuplicateMessageDetector getDuplicateMessageDetectorForIoc(
			final String ioc) {
		DuplicateMessageDetector result = _duplicateMessageDetectors.get(ioc);
		if (result == null) {
			result = new DuplicateMessageDetector(new IDuplicateMessageHandler() {
				public void duplicateMessageDetected(IocMessage first,
						IocMessage duplicate) {
					CentralLogger.getInstance().warn(ClientRequest.class,
							"DUPLICATED Message from [" + ioc + "] OLD: " + first + " NEW: " + duplicate);
				}
			});
			_duplicateMessageDetectors.put(ioc, result);
		}
		return result;
	}

	/**
	 * Puts the items contained in the specified message into various data
	 * structures that are used in the old, messy ICS code.
	 * 
	 * @param message
	 *            the message received from the IOC.
	 * @param tagValue
	 *            a hash table which will be filled by this method with all
	 *            tag/value pairs contained in the message.
	 * @param tagValuePairs
	 *            a vector which will be filled with all tag/value pairs parsed
	 *            by this method, except ID and TYPE.
	 */
	// TODO: public for testing. Should be refactored!
	public static void putIocMessageDataIntoLegacyDataStructures(
			IocMessage message, Hashtable<String, String> tagValue,
			Vector<TagValuePair> tagValuePairs) {
		for (TagValuePair item : message.getItems()) {
			String tag = item.getTag();
			String value = item.getValue();
			
			// put the item into the hashtable of all items 
			tagValue.put(tag, value);
			
			if (TagList.getTagType(tag) == TagList.TAG_TYPE_ID) {
				// ignore (this is just so that the tag is not put into the
				// tagValuePairs structure)
			} else if (TagList.getTagType(tag) == TagList.TAG_TYPE_TYPE) {
				// ignore (this is just so that the tag is not put into the
				// tagValuePairs structure)
			} else {
				tagValuePairs.add(item);
			}
		}
		
		if (!message.contains("EVENTTIME")) {
			TagValuePair eventtime = generateEventtimeFor(message);
			message.addItem(eventtime);
			tagValuePairs.add(eventtime);
		}
	}

	/**
	 * Generates an event time for the specified message. The time value will be
	 * taken from the CREATETIME item if the message contains one, otherwise,
	 * the current system time will be used. The generated item is not added to
	 * the message by this method.
	 * 
	 * @param message
	 *            the message.
	 * @return a tag value pair containing the EVENTTIME for the message.
	 */
	private static TagValuePair generateEventtimeFor(IocMessage message) {
		String value;
		if (message.contains("CREATETIME")) {
			value = message.getItem("CREATETIME").getValue();
		} else {
	        value = LegacyUtil.formatDate(new Date());
		}
		
		return new TagValuePair("EVENTTIME", value);
	}

	/**
	 * Update the LDAP database.
	 * Analyse the tag/value pairs in tagValue (must contain at least: NAME and SEVERITY
	 * If time is omitted - localTime will be used.
	 * @param tagValue Hashtable with tag/value pairs.
	 * @param resetHighestUnacknowledgedAlarm True/False defines whether - or not to reset the highest unacknowledged alarm in the LDAP database.
	 * 
	 */
	private void updateLdapEntry ( Hashtable<String,String> tagValue, boolean resetHighestUnacknowledgedAlarm) {
		//
		// find necessary entries and activate ldapUpdateMethod
		//
		String channel,status,severity,timeStamp = null;
		///System.out.println("tagValue : " + tagValue.toString());		
		
		if ( tagValue.containsKey("NAME") && tagValue.containsKey("SEVERITY")) {
			
			channel = tagValue.get("NAME");
			severity = tagValue.get("SEVERITY");
			
			/*
			 * is severity set?
			 */
			if ( tagValue.containsKey("STATUS")) {
				status = tagValue.get("STATUS");
			} else {
				status = "unknown";
			}

			/*
			 * TODO: if we decide to use separate fields for event and create-time this is he place to change it!
			 */
			if ( tagValue.containsKey("EVENTTIME")) {
				timeStamp = tagValue.get("EVENTTIME");
			} else if ( tagValue.containsKey("CREATETIME")){
				timeStamp = tagValue.get("CREATETIME");
			} else {
				// no time available
				SimpleDateFormat sdf = new SimpleDateFormat( PreferenceProperties.JMS_DATE_FORMAT);
		        java.util.Date currentDate = new java.util.Date();
		        timeStamp = sdf.format(currentDate);
			}
			
			/*
			 * change the epicsAlarmHighUnAckn field in the LDAP server?
			 */
			
			if ( resetHighestUnacknowledgedAlarm ) {
				/*
				 * check for actual alarm state
				 */
				String currentSeverity = Engine.getInstance().getAttriebute(channel, Engine.ChannelAttribute.epicsAlarmHighUnAckn);
				CentralLogger.getInstance().debug( this, "Channel: " + channel + " current severity: " + currentSeverity + "[" +getSeverityEnum(currentSeverity)+ "]" + " new severity: " + severity + "[" +getSeverityEnum(severity)+ "]");
				
				if ( getSeverityEnum(severity) > getSeverityEnum(currentSeverity)) {
					/*
					 * new highest alarm!
					 * set highest unacknowledged alarm to new severity
					 * else we keep the highest unacknowledged alarm as it is
					 * the highest unacknowledged alarm will be removed if an acknowledge from the alarm table, alarm tree view 
					 * - or other applications will be set to ""
					 */
					Engine.getInstance().addLdapWriteRequest ("epicsAlarmHighUnAckn", channel, severity);
				}
			}
			
			//
			// send values to LDAP engine
			//
			Engine.getInstance().addLdapWriteRequest( "epicsAlarmSeverity", channel, severity);
			Engine.getInstance().addLdapWriteRequest( "epicsAlarmStatus", channel, status);
			Engine.getInstance().addLdapWriteRequest( "epicsAlarmTimeStamp", channel, timeStamp);		
			
		} else {
			//System.out.println("### - cannot write to LDAP done for NAME= " + tagValue.get("NAME"));
		}
		
	}
	/**
	 * return severity - as number -
	 * - INVALID = 5
	 * - NO_ALARM = 0
	 * - MINOR = 1
	 * - MAJOR = 2
	 * - NONE = -1
	 * @param severity
	 * @return
	 */
	private int getSeverityEnum ( String severity) {
		int severityAsNumber = 0;
		if ( (severity != null) && severity.length()> 0) {
			if (severity.startsWith( "INVALID")) {
				severityAsNumber = 5;
			} else if (severity.startsWith( "INVALID")) {
				severityAsNumber = 5;
			} else if (severity.startsWith( "NO_ALARM")) {
				severityAsNumber = 0;
			} else if (severity.startsWith( "MINOR")) {
				severityAsNumber = 1;
			} else if (severity.startsWith( "MAJOR")) {
				severityAsNumber = 2;
			} else if (severity.startsWith( "NONE")) {
				severityAsNumber = -1;
			} else {
				severityAsNumber = 0;
			}
		}
		return severityAsNumber;
	}
}
