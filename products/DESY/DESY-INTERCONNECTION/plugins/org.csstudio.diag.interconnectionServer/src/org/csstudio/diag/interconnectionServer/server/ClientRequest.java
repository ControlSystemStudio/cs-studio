package org.csstudio.diag.interconnectionServer.server;
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


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.diag.interconnectionServer.server.InterconnectionServer.TagValuePairs;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

//import de.desy.jms.server.InterconnectionServer.TagValuePairs;
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
    private DatagramPacket      packet          = null;
    private Statistic			statistic		= null;
    public Statistic.StatisticContent  statisticContent = null;
    public TagList 				tagList			= null;
    InterconnectionServer icServer = null;
    private boolean				RESET_HIGHEST_UNACKNOWLEDGED_ALARM_TRUE	= true;
    private boolean				RESET_HIGHEST_UNACKNOWLEDGED_ALARM_FALSE = false;
    private int statusMessageDelay = 0;
    
	/* 
	 * 
	 public ClientRequest( InterconnectionServer icServer, DatagramSocket d, DatagramPacket p, Session jmsAlarmSession, Destination jmsAlarmDestination, MessageProducer jmsAlarmSender, 
			Session jmsLogSession, Destination jmsLogDestination, MessageProducer jmsLogSender, 
			Session jmsPutLogSession, Destination jmsPutLogDestination, MessageProducer jmsPutLogSender)
			*/
	public ClientRequest( InterconnectionServer icServer, String packetData, DatagramSocket d, DatagramPacket p, Connection alarmConnection, Connection logConnection,Connection puLogConnection)
	{
        this.icServer = icServer;
        this.packetData = packetData;
		this.socket       = d;
		this.packet       = p;
		
        this.statistic	  = Statistic.getInstance();
        this.tagList 	  = TagList.getInstance();
	}
	
	public void run()
	{
	    InetAddress    	address         = null;
	    String			hostName		= null;
	    String         	daten           = null;
	    String[]       	attribute       = null;
	    int            	length          = 0;
	    int            	port            = 0;
	    String			statisticId		= null;
	    boolean 		received		= true;
	    MapMessage      message         = null;
        
        address 	= packet.getAddress();
        hostName 	= address.getHostName();
        /*
         * in case the host name is null
         * keep the IP address instead
         */
        if ( hostName == null) {
        	hostName = address.getHostAddress();
        }
        port 		= packet.getPort();
        length 		= packet.getLength();
        statisticId	= hostName + ":" + port;
        GregorianCalendar parseTime = new GregorianCalendar();

        IPreferencesService prefs = Platform.getPreferencesService();
	    String jmsTimeToLiveAlarms = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.JMS_TIME_TO_LIVE_ALARMS, "", null);  
	    String jmsTimeToLiveLogs = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.JMS_TIME_TO_LIVE_LOGS, "", null);  
	    String jmsTimeToLivePutLogs = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.JMS_TIME_TO_LIVE_PUT_LOGS, "", null);  
	    /*
		 * do we want to write out message indicators?
		 */
		String showMessageIndicator = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.SHOW_MESSAGE_INDICATOR, "", null); 
		boolean showMessageIndicatorB = false;
		if ( (showMessageIndicator !=null) && showMessageIndicator.equals("true")) {
			showMessageIndicatorB = true;
		}
	    
        int jmsTimeToLiveAlarmsInt = Integer.parseInt(jmsTimeToLiveAlarms);
		int jmsTimeToLiveLogsInt = Integer.parseInt(jmsTimeToLiveLogs);
		int jmsTimeToLivePutLogsInt = Integer.parseInt(jmsTimeToLivePutLogs);

        
        /*
         * increase stistic counter
         */
        icServer.getClientRequestTheadCollector().incrementValue();
        
        // write out some statistics
        //
        statisticContent = statistic.getContentObject( statisticId);
        statisticContent.setTime( received);
        statisticContent.setHost( hostName);
        statisticContent.setIpAddress(address.getHostAddress());
        statisticContent.setPort( port);
        statisticContent.setLastMessage( daten);
        statisticContent.setLastMessageSize( length); 
        /*
		 * find logical name of IOC by the IP address
		 * do NOT check on the LDAP server if the name was already found...
		 */
        if ( statisticContent.getLogicalIocName() == null) {
        	/*
        	 * new IOC - ask LDAP for logical name
        	 */
        	String[] iocNames = LdapSupport.getInstance().getLogicalIocName ( address.getHostAddress(), hostName);
        	statisticContent.setLogicalIocName( iocNames[0]);
        	/*
        	 * save ldapIocName 
        	 */
        	System.out.println("ClientRequest:  ldapIocName = " + iocNames[1]);
        	statisticContent.setLdapIocName(iocNames[1]);
        }
        
        
        Vector<TagValuePairs> tagValuePairs	= new Vector<TagValuePairs>();
        Hashtable<String,String> tagValue = new Hashtable<String,String>();	// could replace the Vector above
        TagValuePairs	id		= icServer.new TagValuePairs();
        TagValuePairs	type	= icServer.new TagValuePairs();
        
        
        if ( parseMessage(  packetData, tagValue, tagValuePairs, id, type, statisticId)) {
        	
        	parseTime = new GregorianCalendar();
        	//System.out.println("Time: - after parse 	= " + dateToString(new GregorianCalendar()));
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
        	
        	switch (TagList.getInstance().getMessageType( type.getValue())) {
        	
        	case TagList.ALARM_MESSAGE:				// compatibility with old version
        	case TagList.ALARM_STATUS_MESSAGE:		// compatibility with old version
        	case TagList.EVENT_MESSAGE:				// the real thing!
        		//
        		// ALARM jms server
        		//
        		
        		//System.out.print("a");
        		try {
                    // Create the destination (Topic or Queue)
        			Destination alarmDestination = icServer.getAlarmSession().createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer alarmSender = icServer.getAlarmSession().createProducer( alarmDestination);
                	alarmSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	alarmSender.setTimeToLive( jmsTimeToLiveAlarmsInt);
                	
            		//sender = alarmSession.createProducer(alarmDestination);
            		//System.out.println("Time-ALARM: - after sender= 	= " + dateToString(new GregorianCalendar()));
                    //message = alarmSession.createMapMessage();
            		message = icServer.prepareTypedJmsMessage( icServer.getAlarmSession().createMapMessage(), tagValuePairs, type);
            		///System.out.println("Time-APARM: - after message= 	= " + dateToString(new GregorianCalendar()));
            		
            		///alarmSender.setPriority( 9);
            		///System.out.println("Time-ALARM: - before sender-send 	= " + dateToString(new GregorianCalendar()));
            		alarmSender.send(message);
            		
            		alarmSender.close();
            		
            		//System.out.print("aJs");
            		icServer.getJmsMessageWriteCollector().setValue(gregorianTimeDifference( parseTime, new GregorianCalendar()));
            		//System.out.print("aJe");
            		//System.out.println("Time-ALARM: - after sender-send 	= " + dateToString(new GregorianCalendar()));
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			icServer.checkSendMessageErrorCount();
                    System.out.println("ClientRequest : send ALARM message : *** EXCEPTION *** : " + jmse.getMessage());
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
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		///System.out.println("Time-ALARM: - after send UDP reply	= " + dateToString(new GregorianCalendar()));
        		//
        		// time to update the LDAP server entry
        		//
        		//System.out.print("aLs");
        		updateLdapEntry( tagValue, RESET_HIGHEST_UNACKNOWLEDGED_ALARM_TRUE);
        		
        		//System.out.print("aLe");
        		
        		//checkPerformance( parseTime, afterJmsSendTime, afterUdpAcknowledgeTime, afterLdapWriteTime);
        		if (showMessageIndicatorB) {
        			System.out.print("A");
        		}
        		break;
        		
        	case TagList.STATUS_MESSAGE:
        		//
        		// ALARM just a list of ALL alarm states from the IOC - status messages do NOT get displayed in the ALARM view
        		// they are important for the LDAP-Trees currently under display in the CSS-Alarm-Tree views!!!
        		//
        		try {
        			Destination alarmDestination = icServer.getAlarmSession().createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer alarmSender = icServer.getAlarmSession().createProducer( alarmDestination);
                	alarmSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	alarmSender.setTimeToLive( jmsTimeToLiveAlarmsInt);
                	
            		//sender = alarmSession.createProducer(alarmDestination);
            		//System.out.println("Time-ALARM: - after sender= 	= " + dateToString(new GregorianCalendar()));
                    //message = alarmSession.createMapMessage();
            		message = icServer.prepareTypedJmsMessage( icServer.getAlarmSession().createMapMessage(), tagValuePairs, type);
            		///System.out.println("Time-APARM: - after message= 	= " + dateToString(new GregorianCalendar()));
            		
            		///alarmSender.setPriority( 9);
            		///System.out.println("Time-ALARM: - before sender-send 	= " + dateToString(new GregorianCalendar()));
            		alarmSender.send(message);
            		
            		alarmSender.close();
            		
            		//System.out.print("aJs");
            		icServer.getJmsMessageWriteCollector().setValue(gregorianTimeDifference( parseTime, new GregorianCalendar()));
            		//System.out.print("aJe");
            		//System.out.println("Time-ALARM: - after sender-send 	= " + dateToString(new GregorianCalendar()));
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			icServer.checkSendMessageErrorCount();
                    System.out.println("ClientRequest : send ALARM message : *** EXCEPTION *** : " + jmse.getMessage());
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
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);

        		//
        		// time to update the LDAP server entry
        		//

        		updateLdapEntry( tagValue, RESET_HIGHEST_UNACKNOWLEDGED_ALARM_FALSE);
        		
        		if (showMessageIndicatorB) {
        			System.out.print("AS");
        		}

        		break;
        		
        	case TagList.SYSTEM_LOG_MESSAGE:
        	case TagList.APPLICATION_LOG_MESSAGE:
        		

        		//
        		// LOG jms server
        		//
        		try{
        			Destination logDestination = icServer.getLogSession().createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer logSender = icServer.getLogSession().createProducer( logDestination);
                	logSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	logSender.setTimeToLive( jmsTimeToLiveAlarmsInt);

                	logSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	logSender.setTimeToLive( jmsTimeToLiveLogsInt);
        			// sender = logSession.createProducer(logDestination);
                    //message = logSession.createMapMessage();
                    message = icServer.prepareTypedJmsMessage( icServer.getLogSession().createMapMessage(), tagValuePairs, type);
            		logSender.send(message);
            		logSender.close();
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			icServer.checkSendMessageErrorCount();
                    //System.out.println("ClientRequest : send LOG message : *** EXCEPTION *** : " + jmse.getMessage());
                }
        		//
        		// just send a reply
        		//
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		//
        		// time to update the LDAP server entry
        		//
        		//System.out.print("sLs");
        		updateLdapEntry( tagValue, RESET_HIGHEST_UNACKNOWLEDGED_ALARM_FALSE);
        		
        		//2System.out.print("sLe");
        		if (showMessageIndicatorB) {
        			System.out.print("S");
        		}
        		break;
        		
        	case TagList.BEACON_MESSAGE:
        		//
        		// set beacon time locally (set to current time - retrigger)
        		// set beacon ONLY for beacon messages! (hmmm. this should be already solved - put it ON TOP!
        		//
//        		statisticContent.setBeaconTime();	
        		
        		//
        		// just send a reply
        		//
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		///System.out.println("Time-Beacon: - after send UDP reply	=  " + dateToString(new GregorianCalendar()));

        		/*
        		 * since we do not know whether we are selected...
        		 * ... we have to ask the IOC
        		 * Ask IOC every BEACON_ASK_IF_SELECTED_COUNTER beacon
        		 * In case the select state changes - we'll ask the IOC for ALL alarm states
        		 * This is handled in the SendCommandToIoc class
        		 */
        		if (statisticContent.getSelectStateCounter() > PreferenceProperties.BEACON_ASK_IF_SELECTED_COUNTER) {
        			SendCommandToIoc sendCommandToIoc = new SendCommandToIoc( statisticId, PreferenceProperties.COMMAND_SEND_STATUS);
        			icServer.getCommandExecutor().execute(sendCommandToIoc);
        			statisticContent.setSelectStateCounter(0);
        		} else {
        			/*
        			 * increment counter
        			 */
        			statisticContent.incrementSelectStateCounter();
        		}

        		//
        		// generate system log message if connection state changed
        		//
        		if ( !statisticContent.connectState) {
        			//
        			// connect state changed!
        			//
        			statisticContent.setConnectState (true);
        			statisticContent.setTimeReConnected();
        			/*
        			 * start IocChangeState thread
        			 */
        			new IocChangedState ( statisticId, statisticContent.getHost(), statisticContent.getIpAddress(), statisticContent.getLogicalIocName(), statisticContent.getLdapIocName(), true);
        			
        			
        			/*
        			 * create JMS sender
        			 * 
        			 */
        			try{
                    	icServer.sendLogMessage( icServer.prepareJmsMessage ( icServer.getLogSession().createMapMessage(), icServer.jmsLogMessageNewClientConnected( statisticId)));
            		}
            		catch(JMSException jmse)
                    {
            			status = false;
            			icServer.checkSendMessageErrorCount();
                        //System.out.println("ClientRequest : send LOG message : *** EXCEPTION *** : " + jmse.getMessage());
                    }
        		}
        		if (showMessageIndicatorB) {
        			System.out.print("B");
        		}
        		break;
        		
        	case TagList.BEACON_MESSAGE_SELECTED:
        		//
        		// set beacon time locally (set to current time - retrigger)
        		// set beacon ONLY for beacon messages!
        		//
//        		statisticContent.setBeaconTime();
        		
        		//
        		// just send a reply
        		//
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		///System.out.println("Time-Beacon: - after send UDP reply	=  " + dateToString(new GregorianCalendar()));
        		
        		/*
        		 * OK - we are selected - so:
        		 * just in case we previously set the channel to disconnected - we'll have to update all alarm states
        		 * -> trigger the IOC to send all alarms!
        		 */
        		if ( statisticContent.isDidWeSetAllChannelToDisconnect()) {
        			/*
        			 * yes - did set all channel to disconnect
        			 * we'll have to get all alarm-states from the IOC
        			 */
        			SendCommandToIoc sendCommandToIoc = new SendCommandToIoc( hostName, port, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
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
        					"virtual channel", 											// text
        					null);	
        			// send command to IOC - get ALL alarm states
        			/*
        			 * if we received beacons within the last two beacon timeout periods we 'probably' did not loose any messages
        			 * this is a switch over from one IC-Server to another and thus
        			 * we DO NOT have to ask for an update on all alarms! 
        			 */
        			if ( ! statisticContent.wasPreviousBeaconWithinThreeBeaconTimeouts() &&
        					statisticContent.isGetAllAlarmsOnSelectChange()) {
        				SendCommandToIoc sendCommandToIoc = new SendCommandToIoc( hostName, port, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
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
        		if ( !statisticContent.connectState) {
        			//
        			// connect state changed!
        			//
        			statisticContent.setConnectState (true);
        			statisticContent.setTimeReConnected();
        			/*
        			 * start IocChangeState thread
        			 */
        			new IocChangedState (statisticId, statisticContent.getHost(), statisticContent.getIpAddress(), statisticContent.getLogicalIocName(), statisticContent.getLdapIocName(), true);
        			
        			/*
        			 * create JMS sender
        			 * 
        			 */
        			try{
                    	icServer.sendLogMessage( icServer.prepareJmsMessage ( icServer.getLogSession().createMapMessage(), icServer.jmsLogMessageNewClientConnected( statisticId)));

            			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
            					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
            					icServer.getLocalHostName() + ":" + hostName + ":connectState",					// name
            					icServer.getLocalHostName(), 														// value
            					JmsMessage.SEVERITY_NO_ALARM, 										// severity
            					"CONNECTED", 														// status
            					hostName, 															// host
            					null, 																// facility
            					"virtual channel", 											// text
            					null);	
            		}
            		catch(JMSException jmse)
                    {
            			status = false;
            			icServer.checkSendMessageErrorCount();
                        //System.out.println("ClientRequest : send LOG message : *** EXCEPTION *** : " + jmse.getMessage());
                    }
        		}
        		if (showMessageIndicatorB) {
        			System.out.print("B");
        		}
        		break;
        		
        	case TagList.SWITCH_OVER:
        		//
        		// set beacon time locally (set to current time - retrigger)
        		// set beacon ONLY for beacon messages!
        		//
//        		statisticContent.setBeaconTime();
        		
        		//
        		// the IOC changed state from NOT selected to selected
        		// we do not have to check state and do NOT have to send all alarms!
        		// this message should be sent IMMEDIATELY after a switch over and BEFORE any other messages get generated
        		//
        		
        		//
        		// just send a reply
        		//
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		///System.out.println("Time-Beacon: - after send UDP reply	=  " + dateToString(new GregorianCalendar()));
        		
        		/*
        		 * we are selected!
        		 * in case we were not selected before - we'll ask the IOC for an update on ALL the alarm states
        		 */
//        		if (!statisticContent.isSelectState()) {
        		if ( true) {
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
        					"virtual channel", 													// text
        					null);	
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
        		// set beacon time locally (set to current time - retrigger)
        		// set beacon ONLY for beacon messages!
        		//
//        		statisticContent.setBeaconTime();
        		
        		//
        		// just send a reply
        		//
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		///System.out.println("Time-Beacon: - after send UDP reply	=  " + dateToString(new GregorianCalendar()));
        		
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
        					"virtual channel", 											// text
        					null);	
        		}
        		//
        		// generate system log message if connection state changed
        		//
        		if ( !statisticContent.connectState) {
        			//
        			// connect state changed!
        			//
        			statisticContent.setConnectState (true);
        			statisticContent.setTimeReConnected();
        			/*
        			 * start IocChangeState thread
        			 */
        			new IocChangedState (statisticId, statisticContent.getHost(), statisticContent.getIpAddress(), statisticContent.getLogicalIocName(), statisticContent.getLdapIocName(), true);
        			
        			/*
        			 * create JMS sender
        			 * 
        			 */
        			try{
                    	icServer.sendLogMessage( icServer.prepareJmsMessage ( icServer.getLogSession().createMapMessage(), icServer.jmsLogMessageNewClientConnected( statisticId)));

            			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
            					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
            					icServer.getLocalHostName() + ":" + hostName + ":connectState",					// name
            					icServer.getLocalHostName(), 														// value
            					JmsMessage.SEVERITY_NO_ALARM, 										// severity
            					"CONNECTED", 														// status
            					hostName, 															// host
            					null, 																// facility
            					"virtual channel", 											// text
            					null);	

            		}
            		catch(JMSException jmse)
                    {
            			status = false;
            			icServer.checkSendMessageErrorCount();
                        //System.out.println("ClientRequest : send LOG message : *** EXCEPTION *** : " + jmse.getMessage());
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
        		try {
                    // Create the destination (Topic or Queue)
        			Destination putLogDestination = icServer.getPutLogSession().createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer putLogSender = icServer.getPutLogSession().createProducer( putLogDestination);
                	putLogSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	putLogSender.setTimeToLive( jmsTimeToLivePutLogsInt);
        			//sender = putLogSession.createProducer(putLogDestination);
                    //message = putLogSession.createMapMessage();
                    message = icServer.prepareTypedJmsMessage( icServer.getPutLogSession().createMapMessage(), tagValuePairs, type);
            		putLogSender.send(message);
            		putLogSender.close();
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			icServer.checkSendMessageErrorCount();
                    System.out.println("ClientRequest : send ALARM message : *** EXCEPTION *** : " + jmse.getMessage());
                }
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		if (showMessageIndicatorB) {
        			System.out.print("P");
        		}
        		break;
        		
        	case 4711:
        		//
        		// in case we have to execule something asynchronously...
        		//
            	new ServerCommands (id.getTag(), id.getValue(), tagList.getTagProperties( attribute[0].toString()), socket, packet);
            	break;
        	case TagList.TEST_COMMAND:
        		//
        		// execute command asynchronously
        		//
            	new ServerCommands (id.getTag(), id.getValue(), tagList.getTagProperties( id.getTag()), socket, packet);
            	if (showMessageIndicatorB) {
        			System.out.print("T");
        		}
            	break;
            	
        		
        	case TagList.UNKNOWN_MESSAGE:
        		default:
        		status = false;
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		if (showMessageIndicatorB) {
        			System.out.print("U");
        		}
        	}

        }
     
        //System.out.println("ClientRequest : clean up : leave thread" + this.getId());
        icServer.getClientRequestTheadCollector().decrementValue();
        icServer.setSuccessfullJmsSentCountdown(true);
        //System.out.print("Ex");

	}
	
	private boolean parseMessage ( String packetData, Hashtable<String,String> tagValue, Vector<TagValuePairs> tagValuePairs, TagValuePairs tag, TagValuePairs type, String statisticId) {
		boolean success = false;
		String[] attribute = null;
		boolean gotTag 		= false;
    	boolean gotId 		= false;
    	String	timeStamp = null;
		
		//String daten = new String(this.packet.getData(), 0, this.packet.getLength());
    	
		//System.out.println("Message: " + daten);
		//
		// just in case we should use another data format in the future
		// here's the place to implement anoher parser
		//

		StringTokenizer tok = new StringTokenizer(packetData, PreferenceProperties.DATA_TOKENIZER);
        
		// TODO: make it a logMessage
        ///System.out.println("Anzahl der Token: " + tok.countTokens() + "\n");
        
        if(tok.countTokens() > 0)
        {
                while(tok.hasMoreTokens())
                {
                	String localTok = tok.nextToken();
                	//
                	// parsing Tag=value;Tag1=value1;
                	//
                	
                	//
                	// first make sure that it's a pair
                	// this requires a '=' and at least two more chares like a=b
                	//
                	
                	if ( (localTok !=null) && localTok.contains("=") && (localTok.length() > 2 )) {
                		
                		//
                		// ok seems to be ok to parse further
                		// now make sure that '=' is not the first and not the last char
                		// -> avoid ;=Value;Tag=; combinations
                		
                		if ( (!localTok.endsWith( "=")) && (!localTok.startsWith( "="))) {
	                		attribute = localTok.split("=");
	                		
	                		// TODO: make this a debug message
		                    ///System.out.println(statisticId + " : " + attribute[0] + " := "+ attribute[1]);
		                    //
		                    // fill Hash table in any case
		                    //
		                    tagValue.put(attribute[0].toString(), attribute[1].toString());
		                    
		                    if ( tagList.getTagType( attribute[0].toString()) == PreferenceProperties.TAG_TYPE_IS_ID) {
		                    	tag.setTag(attribute[0].toString());
		                    	tag.setValue(attribute[1].toString());
		                    	gotId = true;
		                    } else if ( tagList.getTagType( attribute[0].toString()) == PreferenceProperties.TAG_TYPE_IS_TYPE) {
		                    	type.setTag(attribute[0].toString());
		                    	type.setValue(attribute[1].toString());
		                    	gotTag = true;
		                    } else {
		                    	/*
		                    	 * ID and type need special treatment - the rest goes here
		                    	 */
		                    	TagValuePairs newTagValuePair =  icServer.new TagValuePairs ( attribute[0].toString(), attribute[1].toString());
		                    	tagValuePairs.add(newTagValuePair);
		                    }
	                	} //if
	                } // if
                } //while
        } // if tok
        /*
		 * check whether we've received a time stamp (any)
		 */
		if ( tagValue.containsKey("EVENTTIME")) {
			// nothing to do
		} else if ( tagValue.containsKey("ID")){
			/*
			 * ANY message MUST contain an ID - so this should be always TRUE
			 * put data into DataStore - this will also check for duplicates!
			 */
			DataStore.getInstance().storeData(tagValue.get("ID"), packetData, statisticContent.getLogicalIocName());
		} else if ( tagValue.containsKey("CREATETIME")){
			/*
			 * if CREATETIME is set -> is it as EVENTTIME
			 */
			timeStamp = tagValue.get("CREATETIME");
			TagValuePairs newTagValuePair =  icServer.new TagValuePairs ( "EVENTTIME", timeStamp);
        	tagValuePairs.add(newTagValuePair);
		} else {
			/*
			 * if no time is set -> create a new EVENTTIME locally
			 */
			SimpleDateFormat sdf = new SimpleDateFormat( PreferenceProperties.JMS_DATE_FORMAT);
	        java.util.Date currentDate = new java.util.Date();
	        timeStamp = sdf.format(currentDate);
	        TagValuePairs newTagValuePair =  icServer.new TagValuePairs ( "EVENTTIME", timeStamp);
        	tagValuePairs.add(newTagValuePair);
		}
		
        if ( gotId && gotTag){
        	success = true;
        	return success;
        }
		return success;
	}
	public String dateToString ( GregorianCalendar gregorsDate) {
		
		//
		// convert Gregorian date into string
		//
		//TODO: use other time format - actually : DD-MM-YYYY
		Date d = gregorsDate.getTime();
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
	    //DateFormat df = DateFormat.getDateInstance();
	    return df.format(d);
	}
	
	public int gregorianTimeDifference ( GregorianCalendar fromTime, GregorianCalendar toTime) {
		//
		// calculate time difference
		//
		Date fromDate = fromTime.getTime();
		Date toDate = toTime.getTime();
		long fromLong = fromDate.getTime();
		long toLong = toDate.getTime();
		long timeDifference = toLong - fromLong;
		int intDiff = (int)timeDifference;
		return intDiff;
	}
	
	public void checkPerformance( GregorianCalendar parseTime, GregorianCalendar afterJmsSendTime, GregorianCalendar afterUdpAcknowledgeTime, GregorianCalendar afterLdapWriteTime) {
		
		int timeDifference;
		timeDifference = gregorianTimeDifference( parseTime, afterJmsSendTime);
//		System.out.println( "Time to send JMS message      : " + timeDifference);
		
		timeDifference = gregorianTimeDifference( afterJmsSendTime, afterUdpAcknowledgeTime);
//		System.out.println( "Time to acknowledge message   : " + timeDifference);
		
		timeDifference = gregorianTimeDifference( afterUdpAcknowledgeTime, afterLdapWriteTime);
//		System.out.println( "Time to add LDAP write request: " + timeDifference);
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
