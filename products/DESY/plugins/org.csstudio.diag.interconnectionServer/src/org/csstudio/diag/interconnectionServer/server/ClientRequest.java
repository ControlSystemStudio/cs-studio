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

import javax.annotation.CheckForNull;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.DuplicateMessageDetector;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.IDuplicateMessageHandler;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.IocMessage;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.IocMessageParser;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.TagList;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.TagValuePair;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread created for each message which arrives from the IOC
 * Initial implementation by Markus Moeller.
 *
 * @author Matthias Clausen
 */
public class ClientRequest implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ClientRequest.class);


    private String				packetData 		= null;
	private DatagramSocket      socket          = null;
    private IIocConnectionManager iocConnectionManager = null;
    private IocConnection  iocConnection = null;
    private IInterconnectionServer icServer = null;
    private final int statusMessageDelay = 0;
	private final InetAddress _replyAddress;
	private final int _replyPort;
	private final int _length;
	private static Map<String, DuplicateMessageDetector>
			_duplicateMessageDetectors =
				new HashMap<String, DuplicateMessageDetector>();
	private GregorianCalendar _startTime = null;

	public ClientRequest( final String packetData, final DatagramSocket d,
			final InetAddress replyAddress, final int replyPort, final int packetLength, final GregorianCalendar startTime)
	{
        this.icServer = ServiceLocator.getService(IInterconnectionServer.class);
        this.iocConnectionManager = ServiceLocator.getService(IIocConnectionManager.class);

        this.packetData = packetData;
		this.socket = d;
		_replyAddress = replyAddress;
        _replyPort = replyPort;
        _length = packetLength;

        this._startTime = startTime;
	}

	public void run() {
	    String hostName = null;
	    String hostAndPort = null;
	    MapMessage message = null;

        GregorianCalendar parseTime = new GregorianCalendar();

        final IPreferencesService prefs = Platform.getPreferencesService();
	    final int jmsTimeToLiveAlarmsInt = prefs.getInt(
	    		Activator.getDefault().getPluginId(),
	    		PreferenceConstants.JMS_TIME_TO_LIVE_ALARMS, 0, null);
		final int jmsTimeToLiveLogsInt = prefs.getInt(
				Activator.getDefault().getPluginId(),
				PreferenceConstants.JMS_TIME_TO_LIVE_LOGS, 0, null);
		final int jmsTimeToLivePutLogsInt = prefs.getInt(
				Activator.getDefault().getPluginId(),
				PreferenceConstants.JMS_TIME_TO_LIVE_PUT_LOGS, 0, null);

		/*
		 * do we want to write out message indicators?
		 */
		final String showMessageIndicator = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.SHOW_MESSAGE_INDICATOR, "", null);
		boolean showMessageIndicatorB = false;
		if ( showMessageIndicator !=null && showMessageIndicator.equals("true")) {
			showMessageIndicatorB = true;
		}


        /*
         * increase statistic counter
         */
        icServer.getClientRequestTheadCollector().incrementValue();

        // write out some statistics

        // 2009-07-06 MCL
        // change statistic ID from hostName to hostAddress
        //
        try {
            iocConnection = iocConnectionManager.getIocConnection( _replyAddress, _replyPort);
        } catch (final NamingException e1) {
            LOG.error("IOC connection failed for this address " + _replyAddress + ", port " + _replyPort, e1);
            return;
        } catch (LdapServiceException e) {
            LOG.error("IOC connection failed for this address " + _replyAddress + ", port " + _replyPort, e);
        }
        iocConnection.setTime( true);

        if (iocConnection.isDisabled()) {
        	// If the IOC connection is disabled, all messages from the IOC are
        	// ignored.
        	return;
        }

        iocConnection.setLastMessageSize( _length);
        hostName = iocConnection.getNames().getHostName();
        hostAndPort = hostName + ":" + _replyPort;
        /*
		 * find logical name of IOC by the IP address
		 * do NOT check on the LDAP server if the name was already found...
		 */
//        if ( statisticContent.getLogicalIocName() == null) {
//        	/*
//        	 * new IOC - ask LDAP for logical name
//        	 */
//        	String[] iocNames = LdapSupport.getInstance().getLogicalIocName ( _address.getHostAddress(), hostName);
//        	statisticContent.setLogicalIocName( iocNames[0]);
//        	/*
//        	 * save ldapIocName
//        	 */
//        	LOG.trace("ClientRequest:  ldapIocName = " + iocNames[1]);
//        	statisticContent.setLdapIocName(iocNames[1]);
//        }


        final Vector<TagValuePair> tagValuePairs	= new Vector<TagValuePair>();
        final Hashtable<String,String> tagValue = new Hashtable<String,String>();	// could replace the Vector above

        final DuplicateMessageDetector duplicateMessageDetector =
        	getDuplicateMessageDetectorForIoc(iocConnection.getNames().getLogicalIocName());

        final IocMessageParser parser = new IocMessageParser();
		final IocMessage iocMessage = parser.parse(packetData);
		duplicateMessageDetector.checkAndRemember(iocMessage);
		putIocMessageDataIntoLegacyDataStructures(iocMessage, tagValue,
				tagValuePairs);

		if (iocMessage.isValid()) {
			LOG.debug("Packet parsed as valid IocMessage: " + iocMessage);

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
    		iocConnection.setBeaconTime();	// set beacon ONLY for beacon messages!

    		final IIocMessageSender sender = new SocketMessageSender(_replyAddress, _replyPort, socket);
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
        			final Destination alarmDestination = session.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	final MessageProducer alarmSender = session.createProducer( alarmDestination);
                	alarmSender.setDeliveryMode(DeliveryMode.PERSISTENT);
                	alarmSender.setTimeToLive(jmsTimeToLiveAlarmsInt);

            		message = session.createMapMessage();
            		prepareTypedJmsMessage(message, tagValuePairs, iocMessage.getMessageTypeString());
            		alarmSender.send(message);
            		alarmSender.close();

            		icServer.getJmsMessageWriteCollector().setValue(LegacyUtil.timeSince(parseTime));
        		}
        		catch(final JMSException jmse)
                {
        			status = false;
        			icServer.countJmsSendMessageErrorAndReconnectIfTooManyErrors();
                    LOG.warn("ClientRequest : send ALARM message : *** EXCEPTION *** : ", jmse);
                } finally {
                	tryToCloseSession(session);
                }
        		//
        		// just send a reply
        		// BUT wait to give the queues a chance to empty (depending on the size of the write vector in the LDAP engine)
        		//

                // FIXME (jpenning, bknerr) : obsolete LDAP access stuff
//        		if ( Engine.getInstance().getWriteVector().size() >=0 &&
//        		     Engine.getInstance().getWriteVector().size() < PreferenceProperties.MAX_TIME_DELAY_FOR_STATUS_MESSSAGES) {
//        			statusMessageDelay = Engine.getInstance().getWriteVector().size();
//        		} else {
//        			statusMessageDelay = PreferenceProperties.MAX_TIME_DELAY_FOR_STATUS_MESSSAGES;
//        		}

        		try {
    				Thread.sleep( statusMessageDelay);
    			} catch (final InterruptedException e) {
    				// TODO: handle exception
    			}
   				ReplySender.send(iocMessage.getMessageId(), status, sender);

        		if (showMessageIndicatorB) {
        			LOG.trace("A");
        		}
        		// statistic for message reply time
        		icServer.getMessageReplyTimeCollector().setValue(LegacyUtil.timeSince(_startTime));
        		break;

        	case TagList.STATUS_MESSAGE:
        		//
        		// ALARM just a list of ALL alarm states from the IOC - status messages do NOT get displayed in the ALARM view
        		// they are important for the LDAP-Trees currently under display in the CSS-Alarm-Tree views!!!
        		//
        		session = null;
        		try {
        			session = icServer.createJmsSession();
        			final Destination alarmDestination = session.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	final MessageProducer alarmSender = session.createProducer( alarmDestination);
                	alarmSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	alarmSender.setTimeToLive( jmsTimeToLiveAlarmsInt);

            		message = session.createMapMessage();
            		prepareTypedJmsMessage(message, tagValuePairs, iocMessage.getMessageTypeString());
            		alarmSender.send(message);
            		alarmSender.close();

            		icServer.getJmsMessageWriteCollector().setValue(LegacyUtil.timeSince(parseTime));
        		}
        		catch(final JMSException jmse)
                {
        			status = false;
        			icServer.countJmsSendMessageErrorAndReconnectIfTooManyErrors();
                    LOG.warn("ClientRequest : send ALARM message : *** EXCEPTION *** : ", jmse);
                } finally {
                	tryToCloseSession(session);
                }
        		//
        		// just send a reply
        		// BUT wait to give the queues a chance to empty (depending on the size of the write vector in the LDAP engine)
        		//

                // FIXME (jpenning, bknerr) : obsolete LDAP access stuff
//        		if ( Engine.getInstance().getWriteVector().size() >=0 && Engine.getInstance().getWriteVector().size() < PreferenceProperties.MAX_TIME_DELAY_FOR_STATUS_MESSSAGES) {
//        			statusMessageDelay = Engine.getInstance().getWriteVector().size();
//        		} else {
//        			statusMessageDelay = PreferenceProperties.MAX_TIME_DELAY_FOR_STATUS_MESSSAGES;
//        		}

        		try {
    				Thread.sleep( statusMessageDelay);
    			} catch (final InterruptedException e) {
    				// TODO: handle exception
    			}

    			ReplySender.send(iocMessage.getMessageId(), status, sender);

        		if (showMessageIndicatorB) {
        		    LOG.trace("AS");
        		}
        		// statistic for message reply time
        		icServer.getMessageReplyTimeCollector().setValue(LegacyUtil.timeSince(_startTime));

        		break;

        	case TagList.SIM_LOG_MESSAGE:
        		//
        		// SIM
        		// A list of channels which are currently in Simulation mode
        		// purpose: create list for special JMS topic
        		//
        		session = null;
        		try {
        			session = icServer.createJmsSession();
        			final Destination alarmDestination = session.createTopic( PreferenceProperties.JMS_SIM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	final MessageProducer alarmSender = session.createProducer( alarmDestination);
                	alarmSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	alarmSender.setTimeToLive( jmsTimeToLiveAlarmsInt);

            		message = session.createMapMessage();
            		prepareTypedJmsMessage(message, tagValuePairs, iocMessage.getMessageTypeString());
            		alarmSender.send(message);
            		alarmSender.close();

            		icServer.getJmsMessageWriteCollector().setValue(LegacyUtil.timeSince(parseTime));
        		}
        		catch(final JMSException jmse)
                {
        			status = false;
        			icServer.countJmsSendMessageErrorAndReconnectIfTooManyErrors();
                    LOG.warn("ClientRequest : send SIM message : *** EXCEPTION *** : ", jmse);
                } finally {
                	tryToCloseSession(session);
                }
        		//
        		// just send a reply
                //

    			ReplySender.send(iocMessage.getMessageId(), status, sender);

        		break;

        	case TagList.ADIS_LOG_MESSAGE:
        		//
        		// ADIS
        		// A list of channels which are currently in Alarm Disable Mode
        		// purpose: create list for special JMS topic
        		//
        		session = null;
        		try {
        			session = icServer.createJmsSession();
        			final Destination alarmDestination = session.createTopic( PreferenceProperties.JMS_ADIS_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	final MessageProducer alarmSender = session.createProducer( alarmDestination);
                	alarmSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	alarmSender.setTimeToLive( jmsTimeToLiveAlarmsInt);

            		message = session.createMapMessage();
            		prepareTypedJmsMessage(message, tagValuePairs, iocMessage.getMessageTypeString());
            		alarmSender.send(message);
            		alarmSender.close();

            		icServer.getJmsMessageWriteCollector().setValue(LegacyUtil.timeSince(parseTime));
        		}
        		catch(final JMSException jmse)
                {
        			status = false;
        			icServer.countJmsSendMessageErrorAndReconnectIfTooManyErrors();
                    LOG.warn("ClientRequest : send SIM message : *** EXCEPTION *** : ", jmse);
                } finally {
                	tryToCloseSession(session);
                }
        		//
        		// just send a reply
                //

    			ReplySender.send(iocMessage.getMessageId(), status, sender);

        		break;


//        	case TagList.SNL_LOG_MESSAGE:  // TODO send SNL log to its own topic
        	case TagList.SYSTEM_LOG_MESSAGE:
        	case TagList.APPLICATION_LOG_MESSAGE:


        		//
        		// LOG jms server
        		//
        		session = null;
        		try {
        			session = icServer.createJmsSession();
        			final Destination logDestination = session.createTopic( PreferenceProperties.JMS_LOG_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	final MessageProducer logSender = session.createProducer( logDestination);
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
        		catch(final JMSException jmse)
                {
        			status = false;
        			icServer.countJmsSendMessageErrorAndReconnectIfTooManyErrors();
                } finally {
                	tryToCloseSession(session);
                }
        		//
        		// just send a reply
        		//
        		ReplySender.send(iocMessage.getMessageId(), status, sender);

        		if (showMessageIndicatorB) {
        		    LOG.trace("S");
        		}
        		// statistic for message reply time
        		icServer.getMessageReplyTimeCollector().setValue(LegacyUtil.timeSince(_startTime));
        		break;

        	case TagList.BEACON_MESSAGE:
        		// IOC versions that use this message type should no longer be
        		// in use anywhere.
        		LOG.warn("Received message with TYPE=beacon, " +
        				"message type not supported! (ioc: " +
        				iocConnection.getNames().getLogicalIocName() +
        				"; message: " + iocMessage + ")");

                    //$FALL-THROUGH$
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
        		if ( iocConnection.isDidWeSetAllChannelToDisconnect()) {
        			/*
        			 * yes - did set all channel to disconnect
        			 * we'll have to get all alarm-states from the IOC
        			 */
                        try {
                            final IocCommandSender sendCommandToIoc = new IocCommandSender(iocConnection,
                                                                                           PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
                            
                            icServer.getCommandExecutor().execute(sendCommandToIoc);
                            
                            iocConnection.setGetAllAlarmsOnSelectChange(false); // we set the trigger to get the alarms...
                            iocConnection.setDidWeSetAllChannelToDisconnect(false);
                            LOG.info("IOC Connected and selected again - previously channels were set to disconnect - get an update on all alarms!");
                        } catch (final IllegalArgumentException e) {
        		        LOG.error("Creation of command sender failed:\n" + e.getMessage());
        		    }
        		}

        		/*
        		 * we are selected!
        		 * in case we were not selected before - we'll ask the IOC for an update on ALL the alarm states
        		 */
        		if (!iocConnection.isSelectState()) {
        			//remember we're selected
        			iocConnection.setSelectState(true);

        			String selectMessage = "SELECTED";
        			if ( iocConnection.wasPreviousBeaconWithinThreeBeaconTimeouts()) {
        				selectMessage = "SELECTED - switch over";
        			}
        			JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					icServer.getLocalHostName() + ":" + iocConnection.getNames().getLogicalIocName() + ":selectState",					// name
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
        			if ( ! iocConnection.wasPreviousBeaconWithinThreeBeaconTimeouts() &&
        					iocConnection.isGetAllAlarmsOnSelectChange()) {
        				final IocCommandSender sendCommandToIoc = new IocCommandSender(iocConnection, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
        				icServer.getCommandExecutor().execute(sendCommandToIoc);
        				iocConnection.setGetAllAlarmsOnSelectChange(false);	// we set the trigger to get the alarms...
        				LOG.info("This is a fail over from one IC-Server to this one - get an update on all alarms!");
        			} else {
        				LOG.info("Just a switch over from one IC-Server to this one - no need to get an update on all alarms!");
        			}
        		}


        		//
        		// generate system log message if connection state changed
        		//
        		if ( !iocConnection.getConnectState()) {
        			//
        			// connect state changed!
        			//
        			iocConnection.setConnectState (true);
        			iocConnection.setTimeReConnected();
        			/*
        			 * start IocChangeState thread
        			 */
        			new IocChangedState (iocConnection, true);

        			/*
        			 * create JMS sender
        			 *
        			 */
        			session = null;
        			try {
        				session = icServer.createJmsSession();
                    	final MapMessage logMessage = session.createMapMessage();
						prepareJmsMessageLogNewClientConnected(logMessage, hostAndPort);
						icServer.sendLogMessage(logMessage, session);

            			JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
            					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
            					icServer.getLocalHostName() + ":" + hostName + ":connectState",					// name
            					icServer.getLocalHostName(), 														// value
            					JmsMessage.SEVERITY_NO_ALARM, 										// severity
            					"CONNECTED", 														// status
            					hostName, 															// host
            					null, 																// facility
            					"virtual channel");
            		}
            		catch(final JMSException jmse)
                    {
            			status = false;
            			icServer.countJmsSendMessageErrorAndReconnectIfTooManyErrors();
                    } finally {
                    	tryToCloseSession(session);
                    }
        		}
        		if (showMessageIndicatorB) {
        		    LOG.trace("B");
        		}
        		// statistic for beacon reply time
        		icServer.getBeaconReplyTimeCollector().setValue(LegacyUtil.timeSince(_startTime));
        		break;

        	case TagList.IOC_SYSTEM_MESSAGE:

        		// TODO: handle system messages other than "switchOver"

        		ReplySender.send(iocMessage.getMessageId(), status, sender);

        		final boolean isSwitchOverMessage = iocMessage.contains("TEXT")
        				&& "switchOver".equals(iocMessage.getItem("TEXT").getValue());

        		/*
        		 * we are selected!
        		 * in case we were not selected before - we'll ask the IOC for an update on ALL the alarm states
        		 */
        		if (isSwitchOverMessage) {
        			//remember we're selected
        			iocConnection.setSelectState(true);

        			JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					icServer.getLocalHostName() + ":" + iocConnection.getNames().getLogicalIocName() + ":selectState",					// name
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
        		    LOG.trace("SO");
        		}
        		// statistic for message reply time
        		icServer.getMessageReplyTimeCollector().setValue(LegacyUtil.timeSince(_startTime));
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
        		if ( iocConnection.isSelectState()) {
        			//remember we're not selected any more
        			iocConnection.setSelectState(false);

        			JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					icServer.getLocalHostName() + ":" + iocConnection.getNames().getLogicalIocName() + ":selectState",	// name
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
        		if ( !iocConnection.getConnectState()) {
        			//
        			// connect state changed!
        			//
        			iocConnection.setConnectState (true);
        			iocConnection.setTimeReConnected();
        			/*
        			 * start IocChangeState thread
        			 */
        			new IocChangedState (iocConnection, true);

        			/*
        			 * create JMS sender
        			 *
        			 */
        			session = null;
        			try {
        				session = icServer.createJmsSession();
                    	final MapMessage logMessage = session.createMapMessage();
						prepareJmsMessageLogNewClientConnected(logMessage, hostAndPort);
						icServer.sendLogMessage(logMessage, session);

            			JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
            					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
            					icServer.getLocalHostName() + ":" + hostName + ":connectState",					// name
            					icServer.getLocalHostName(), 														// value
            					JmsMessage.SEVERITY_NO_ALARM, 										// severity
            					"CONNECTED", 														// status
            					hostName, 															// host
            					null, 																// facility
            					"virtual channel");

            		}
            		catch(final JMSException jmse)
                    {
            			status = false;
            			icServer.countJmsSendMessageErrorAndReconnectIfTooManyErrors();
                    } finally {
                    	tryToCloseSession(session);
                    }
        		}
        		if (showMessageIndicatorB) {
        		    LOG.trace("B");
        		}
        		// statistic for beacon reply time
        		icServer.getBeaconReplyTimeCollector().setValue(LegacyUtil.timeSince(_startTime));
        		break;

        	case TagList.PUT_LOG_MESSAGE:
        		//
        		// PUT-LOG jms server
        		//
        		session = null;
        		try {
                    // Create the destination (Topic or Queue)
        			session = icServer.createJmsSession();
        			final Destination putLogDestination = session.createTopic(PreferenceProperties.JMS_PUT_LOG_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	final MessageProducer putLogSender = session.createProducer( putLogDestination);
                	putLogSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	putLogSender.setTimeToLive( jmsTimeToLivePutLogsInt);
                    message = session.createMapMessage();
                    prepareTypedJmsMessage(message, tagValuePairs, iocMessage.getMessageTypeString());
            		putLogSender.send(message);
            		putLogSender.close();
        		}
        		catch(final JMSException jmse)
                {
        			status = false;
        			icServer.countJmsSendMessageErrorAndReconnectIfTooManyErrors();
                    LOG.warn("ClientRequest : send ALARM message : *** EXCEPTION *** : ", jmse);
                } finally {
        			tryToCloseSession(session);
        		}
        		ReplySender.send(iocMessage.getMessageId(), status, sender);
        		if (showMessageIndicatorB) {
        		    LOG.trace("P");
        		}
        		// statistic for message reply time
        		icServer.getMessageReplyTimeCollector().setValue(LegacyUtil.timeSince(_startTime));
        		break;

        	case TagList.SNL_LOG_MESSAGE:
        		//
        		// SNL-LOG jms server
        		//
        		session = null;
        		try {
                    // Create the destination (Topic or Queue)
        			session = icServer.createJmsSession();
        			final Destination snlLogDestination = session.createTopic(PreferenceProperties.JMS_SNL_LOG_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	final MessageProducer snlLogSender = session.createProducer( snlLogDestination);
                	snlLogSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	snlLogSender.setTimeToLive( jmsTimeToLivePutLogsInt);
                    message = session.createMapMessage();
                    prepareTypedJmsMessage(message, tagValuePairs, iocMessage.getMessageTypeString());
                    snlLogSender.send(message);
                    snlLogSender.close();
        		}
        		catch(final JMSException jmse)
                {
        			status = false;
        			icServer.countJmsSendMessageErrorAndReconnectIfTooManyErrors();
        			LOG.warn("ClientRequest : send ALARM message : *** EXCEPTION *** : ", jmse);
                } finally {
        			tryToCloseSession(session);
        		}
        		ReplySender.send(iocMessage.getMessageId(), status, sender);
        		if (showMessageIndicatorB) {
        		    LOG.trace("SN");
        		}
        		// statistic for message reply time
        		icServer.getMessageReplyTimeCollector().setValue(LegacyUtil.timeSince(_startTime));
        		break;

        	case TagList.TEST_COMMAND:
            	if (showMessageIndicatorB) {
            	    LOG.trace("T");
        		}
            	break;
        	case TagList.UNKNOWN_MESSAGE:
        		default:
        		status = false;
        		ReplySender.send(iocMessage.getMessageId(), status, sender);
        		if (showMessageIndicatorB) {
        		    LOG.trace("U");
        		}
        	}

        }

        icServer.getClientRequestTheadCollector().decrementValue();

	}

    private void tryToCloseSession(@CheckForNull final Session session) {
        if (session != null) {
        	try {
        		session.close();
        	} catch (final JMSException e) {
        		LOG.warn("Failed to close JMS session", e);
        	}
        }
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
	static void prepareJmsMessageLogNewClientConnected(final MapMessage message,
			final String host) {
		/*
		 * Implementation note: this method is based on the former method
		 * InterconnectionServer#jmsLogMessageNewClientConnected.
		 */

		try {
			// XXX: According to Markus, "SysLog" is not a defined type for JMS
			// messages. See the comment in TagList for a list of defined JMS
			// message types.
			message.setString("TYPE", "SysLog");
			final Date eventtime = new Date();
			final SimpleDateFormat dateFormat = new SimpleDateFormat(PreferenceProperties.JMS_DATE_FORMAT);
			message.setString("EVENTTIME", dateFormat.format(eventtime));
			message.setString("TEXT", "new log client connected");
			message.setString("HOST", host);
			message.setString("STATUS", "on");
			message.setString("SEVERITY", "NO_ALARM");
		} catch (final JMSException e) {
			// XXX: This basically swallows the exception instead of informing
			//        the caller. This method must throw an exception so the
			//        caller knows about the error and does not simply continue
			//        and use an uninitialized message!

			LOG.warn("ClientRequest : prepareJmsMessage : *** EXCEPTION *** : ", e);
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
	static void prepareTypedJmsMessage(final MapMessage message,
			final Vector<TagValuePair> tagValuePairs, final String type) {
		try {
			message.setString("TYPE", type);
			for (final TagValuePair pair : tagValuePairs) {
				message.setString(pair.getTag(), pair.getValue());
			}
		}
		catch(final JMSException jmse)
		{
			// XXX: This basically swallows the exception instead of informing
			//        the caller. This method must throw an exception so the
			//        caller knows about the error and does not simply continue
			//        and use an uninitialized message!
			LOG.warn("ClientRequest : prepareJmsMessage : *** EXCEPTION *** : ", jmse);
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
	// TODO: package scoped for testing. Should be refactored!
	// TODO: this method also should not be static but in some IOC connection scope
	static DuplicateMessageDetector getDuplicateMessageDetectorForIoc(
			final String ioc) {
		DuplicateMessageDetector result = _duplicateMessageDetectors.get(ioc);
		if (result == null) {
			result = new DuplicateMessageDetector(new IDuplicateMessageHandler() {
				@SuppressWarnings("synthetic-access")
                public void duplicateMessageDetected(final IocMessage first,
						final IocMessage duplicate) {
					LOG.warn("DUPLICATED Message from [" + ioc + "] OLD: " + first + " NEW: " + duplicate);
					// increase counter in statistic
					ServiceLocator.getService(IInterconnectionServer.class).getNumberOfDuplicateMessagesCollector().incrementValue();
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
	// TODO: package-scoped for testing. Should be refactored!
	static void putIocMessageDataIntoLegacyDataStructures(
			final IocMessage message, final Hashtable<String, String> tagValue,
			final Vector<TagValuePair> tagValuePairs) {
		for (final TagValuePair item : message.getItems()) {
			final String tag = item.getTag();
			final String value = item.getValue();

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
			final TagValuePair eventtime = generateEventtimeFor(message);
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
	private static TagValuePair generateEventtimeFor(final IocMessage message) {
		String value;
		if (message.contains("CREATETIME")) {
			value = message.getItem("CREATETIME").getValue();
		} else {
	        value = LegacyUtil.formatDate(new Date());
		}

		return new TagValuePair("EVENTTIME", value);
	}

}
