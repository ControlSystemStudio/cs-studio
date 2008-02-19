
/*
 *  ClientRequest.java, v1.0, 2005-12-22
 *
 *  Copyright (c) 2005 Markus Möller
 *  Deutsches Elektronen-Synchrotron DESY, Hamburg 
 *  Notkestraße 85, 22607 Hamburg, Germany
 * 
 *  All rights reserved.
 *
 */

package org.csstudio.diag.interconnectionServer.server;

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
import javax.jms.Session;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.server.InterconnectionServer.TagValuePairs;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

//import de.desy.jms.server.InterconnectionServer.TagValuePairs;

public class ClientRequest extends Thread
{
    private DatagramSocket      socket          = null;
    private DatagramPacket      packet          = null;
    // private Session             alarmSession, logSession, putLogSession         = null;
    // private Destination         alarmDestination, logDestination, putLogDestination     = null;
    private Connection          alarmConnection, logConnection, putLogConnection    = null;
    // private MessageProducer		alarmSender, logSender, putLogSender	= null;
    //private MessageProducer     sender          = null;
    //private MapMessage          message         = null;
    private Statistic			statistic		= null;
    public Statistic.StatisticContent  statisticContent = null;
    public TagList 				tagList			= null;
    InterconnectionServer icServer = null;
    private boolean				RESET_HIGHEST_UNACKNOWLEDGED_ALARM_TRUE	= true;
    private boolean				RESET_HIGHEST_UNACKNOWLEDGED_ALARM_FALSE = false;
    
	/* 
	 * 
	 public ClientRequest( InterconnectionServer icServer, DatagramSocket d, DatagramPacket p, Session jmsAlarmSession, Destination jmsAlarmDestination, MessageProducer jmsAlarmSender, 
			Session jmsLogSession, Destination jmsLogDestination, MessageProducer jmsLogSender, 
			Session jmsPutLogSession, Destination jmsPutLogDestination, MessageProducer jmsPutLogSender)
			*/
	public ClientRequest( InterconnectionServer icServer, DatagramSocket d, DatagramPacket p, Connection alarmConnection, Connection logConnection,Connection puLogConnection)
	{
        this.icServer = icServer;
		this.socket       = d;
		this.packet       = p;
		/*
		this.alarmSession      = jmsAlarmSession;
		this.logSession      = jmsLogSession;
		this.putLogSession      = jmsPutLogSession;
        this.alarmDestination  = jmsAlarmDestination;
        this.logDestination  = jmsLogDestination;
        this.putLogDestination  = jmsPutLogDestination;
        this.alarmSender = jmsAlarmSender;
        this.logSender = jmsLogSender;
        this.putLogSender = jmsPutLogSender;
        */
		this.alarmConnection = alarmConnection;
		this.logConnection = logConnection;
		this.putLogConnection = puLogConnection;
		
        this.statistic	  = Statistic.getInstance();
        this.tagList 	  = TagList.getInstance();
        
		this.start();
	}
	
	public void run()
	{
	    DatagramPacket 	newPacket       = null;
	    InetAddress    	address         = null;
	    String			hostName		= null;
	    String         	daten           = null;
	    String 			ldapIocName 	= null;
	    String         	answerString    = null;
	    String[]       	attribute       = null;
	    int            	length          = 0;
	    int            	port            = 0;
	    String			statisticId		= null;
	    boolean 		received		= true;
	    MapMessage      message         = null;
	    MessageProducer sender          = null;
	    Session         alarmSession	= null;
	    Session         logSession		= null;
	    Session         putLogSession	= null;
        
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
        GregorianCalendar afterJmsSendTime = new GregorianCalendar();
        GregorianCalendar afterUdpAcknowledgeTime = new GregorianCalendar();
        GregorianCalendar afterLdapWriteTime = new GregorianCalendar();

//		//get properties from xml store.
//		XMLStore store = XMLStore.getInstance();
//		String jmsTimeToLiveAlarms = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"jmsTimeToLiveAlarms", false);
//		String jmsTimeToLiveLogs = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"jmsTimeToLiveLogs", false);
//		String jmsTimeToLivePutLogs = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"jmsTimeToLivePutLogs", false);

        IPreferencesService prefs = Platform.getPreferencesService();
	    String jmsTimeToLiveAlarms = prefs.getString(Activator.getDefault().getPluginId(),
	    		"jmsTimeToLiveAlarms", "", null);  
	    String jmsTimeToLiveLogs = prefs.getString(Activator.getDefault().getPluginId(),
	    		"jmsTimeToLiveLogs", "", null);  
	    String jmsTimeToLivePutLogs = prefs.getString(Activator.getDefault().getPluginId(),
	    		"jmsTimeToLivePutLogs", "", null);  
	    
        int jmsTimeToLiveAlarmsInt = Integer.parseInt(jmsTimeToLiveAlarms);
		int jmsTimeToLiveLogsInt = Integer.parseInt(jmsTimeToLiveLogs);
		int jmsTimeToLivePutLogsInt = Integer.parseInt(jmsTimeToLivePutLogs);

        
        /*
         * increase stistic counter
         */
        icServer.getClientRequestTheadCollector().incrementValue();
        
        ///System.out.println("Time: - start 		= " + dateToString(new GregorianCalendar()));
        //
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

        
        //
        // you need debug information?
        //
        if (false) {
        	
        	daten = new String(packet.getData(), 0, length);
        	
            System.out.println("--------------------------------------------------------------------------------\n");
            System.out.println("Adresse:                  " + address.toString());
            System.out.println("Port:                     " + port);
            System.out.println("Länge der Daten im Paket: " + length);
            System.out.println("Länge des Datenstrings:   " + daten.length());
            System.out.println("Der String:\n" + daten + "\n");
        
            StringTokenizer tok = new StringTokenizer(daten, PreferenceProperties.DATA_TOKENIZER);
            
            System.out.println("Anzahl der Token: " + tok.countTokens() + "\n");
        }
        
        
        if ( parseMessage(  tagValue, tagValuePairs, id, type, statisticId)) {
        	
        	parseTime = new GregorianCalendar();
        	//System.out.println("Time: - after parse 	= " + dateToString(new GregorianCalendar()));
        	boolean status	= true;
        	
        	//
        	// ok we successfully parsed whatever was sent
        	//
        	// now we'll send it to the jms server
        	//
        	
        	switch (TagList.getInstance().getMessageType( type.getValue())) {
        	
        	case TagList.ALARM_MESSAGE:				// compatibility with old version
        	case TagList.ALARM_STATUS_MESSAGE:		// compatibility with old version
        	case TagList.EVENT_MESSAGE:				// the real thing!
        		//
        		// ALARM jms server
        		//
        		//System.out.print("a");
        		try {
        			

        			alarmSession = alarmConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    // Create the destination (Topic or Queue)
        			Destination alarmDestination = alarmSession.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer alarmSender = alarmSession.createProducer( alarmDestination);
                	alarmSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	alarmSender.setTimeToLive( jmsTimeToLiveAlarmsInt);
                	
            		//sender = alarmSession.createProducer(alarmDestination);
            		//System.out.println("Time-ALARM: - after sender= 	= " + dateToString(new GregorianCalendar()));
                    //message = alarmSession.createMapMessage();
            		message = icServer.prepareTypedJmsMessage( alarmSession.createMapMessage(), tagValuePairs, type);
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
        		//
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		afterUdpAcknowledgeTime = new GregorianCalendar();
        		///System.out.println("Time-ALARM: - after send UDP reply	= " + dateToString(new GregorianCalendar()));
        		//
        		// time to update the LDAP server entry
        		//
        		//System.out.print("aLs");
        		updateLdapEntry( tagValue, RESET_HIGHEST_UNACKNOWLEDGED_ALARM_TRUE);
        		
        		//
        		// set beacon time locally
        		//
        		statisticContent.setBeaconTime();
        		
        		//System.out.print("aLe");
        		afterLdapWriteTime = new GregorianCalendar();
        		
        		//checkPerformance( parseTime, afterJmsSendTime, afterUdpAcknowledgeTime, afterLdapWriteTime);
        		System.out.print("A");
        		break;
        		
        	case TagList.STATUS_MESSAGE:
        		//
        		// ALARM just a list of ALL alarm states from the IOC - status messages do NOT get displayed in the ALARM view
        		// they are important for the LDAP-Trees currently under display in the CSS-Alarm-Tree views!!!
        		//
        		try {
        			
        			alarmSession = alarmConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    // Create the destination (Topic or Queue)
        			Destination alarmDestination = alarmSession.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer alarmSender = alarmSession.createProducer( alarmDestination);
                	alarmSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	alarmSender.setTimeToLive( jmsTimeToLiveAlarmsInt);
                	
            		//sender = alarmSession.createProducer(alarmDestination);
            		//System.out.println("Time-ALARM: - after sender= 	= " + dateToString(new GregorianCalendar()));
                    //message = alarmSession.createMapMessage();
            		message = icServer.prepareTypedJmsMessage( alarmSession.createMapMessage(), tagValuePairs, type);
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
        		//
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);

        		//
        		// time to update the LDAP server entry
        		//

        		updateLdapEntry( tagValue, RESET_HIGHEST_UNACKNOWLEDGED_ALARM_FALSE);
        		
        		//
        		// set beacon time locally
        		//
        		statisticContent.setBeaconTime();
        		
        		System.out.print("AS");
        		break;
        		
        	case TagList.SYSTEM_LOG_MESSAGE:
        	case TagList.APPLICATION_LOG_MESSAGE:
        		

        		//
        		// LOG jms server
        		//
        		try{
        			logSession = logConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    // Create the destination (Topic or Queue)
        			Destination logDestination = logSession.createTopic( PreferenceProperties.JMS_LOG_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer logSender = logSession.createProducer( logDestination);
                	logSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	logSender.setTimeToLive( jmsTimeToLiveLogsInt);
        			// sender = logSession.createProducer(logDestination);
                    //message = logSession.createMapMessage();
                    message = icServer.prepareTypedJmsMessage( logSession.createMapMessage(), tagValuePairs, type);
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
        		System.out.print("S");
        		break;
        		
        	case TagList.BEACON_MESSAGE:
        		//
        		// just send a reply
        		//
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		///System.out.println("Time-Beacon: - after send UDP reply	=  " + dateToString(new GregorianCalendar()));
        		//
        		// set beacon time locally
        		//
        		statisticContent.setBeaconTime();
        		/*
        		 * since we do not know whether we are selected...
        		 * ... we have to ask the IOC
        		 * Ask IOC every BEACON_ASK_IF_SELECTED_COUNTER beacon
        		 * In case the select state changes - we'll ask the IOC for ALL alarm states
        		 * This is handled in the SendCommandToIoc class
        		 */
        		if (statisticContent.getSelectStateCounter() > PreferenceProperties.BEACON_ASK_IF_SELECTED_COUNTER) {
        			new SendCommandToIoc( statisticId, PreferenceProperties.COMMAND_SEND_STATUS);
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
        			/*
        			 * start IocChangeState thread
        			 */
        			new IocChangedState (statisticContent.getHost(), statisticContent.getIpAddress(), statisticContent.getLogicalIocName(), statisticContent.getLdapIocName(), true);
        			
        			
        			/*
        			 * create JMS sender
        			 * 
        			 */
        			try{
            			logSession = logConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                        // Create the destination (Topic or Queue)
            			Destination logDestination = logSession.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                        // Create a MessageProducer from the Session to the Topic or Queue
                    	MessageProducer logSender = logSession.createProducer( logDestination);
                    	logSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                    	logSender.setTimeToLive(jmsTimeToLiveLogsInt);
                    	icServer.sendLogMessage( icServer.prepareJmsMessage ( logSession.createMapMessage(), icServer.jmsLogMessageNewClientConnected( statisticId)));
                    	
                    	logSender.close();
            		}
            		catch(JMSException jmse)
                    {
            			status = false;
            			icServer.checkSendMessageErrorCount();
                        //System.out.println("ClientRequest : send LOG message : *** EXCEPTION *** : " + jmse.getMessage());
                    }
        		}
        		//System.out.print("B");
        		break;
        		
        	case TagList.BEACON_MESSAGE_SELECTED:
        		//
        		// just send a reply
        		//
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		///System.out.println("Time-Beacon: - after send UDP reply	=  " + dateToString(new GregorianCalendar()));
        		//
        		// set beacon time locally
        		//
        		statisticContent.setBeaconTime();
        		/*
        		 * we are selected!
        		 * in case we were not selected before - we'll ask the IOC for an update on ALL the alarm states
        		 */
        		if (!statisticContent.isSelectState()) {
        			//remember we're selected
        			statisticContent.setSelectState(true);
        			/*
        			 * get host name of interconnection server
        			 */
        			String localHostName = null;
        			try {
        				java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
        				localHostName = localMachine.getHostName();
        			}
        			catch (java.net.UnknownHostException uhe) { 
        			}
        			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					localHostName + ":" + statisticContent.getLogicalIocName() + ":selectState",					// name
        					localHostName, 														// value
        					JmsMessage.SEVERITY_NO_ALARM, 										// severity
        					"SELECTED", 														// status
        					hostName, 															// host
        					null, 																// facility
        					"virtual channel", 											// text
        					null);	
        			// send command to IOC - get ALL alarm states
        			new SendCommandToIoc( statisticId, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
        		}
        		
        		
        		//
        		// generate system log message if connection state changed
        		//
        		if ( !statisticContent.connectState) {
        			//
        			// connect state changed!
        			//
        			statisticContent.setConnectState (true);
        			/*
        			 * start IocChangeState thread
        			 */
        			new IocChangedState (statisticContent.getHost(), statisticContent.getIpAddress(), statisticContent.getLogicalIocName(), statisticContent.getLdapIocName(), true);
        			
        			/*
        			 * create JMS sender
        			 * 
        			 */
        			try{
            			logSession = logConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                        // Create the destination (Topic or Queue)
            			Destination logDestination = logSession.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                        // Create a MessageProducer from the Session to the Topic or Queue
                    	MessageProducer logSender = logSession.createProducer( logDestination);
                    	logSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                    	logSender.setTimeToLive(jmsTimeToLiveLogsInt);
                    	icServer.sendLogMessage( icServer.prepareJmsMessage ( logSession.createMapMessage(), icServer.jmsLogMessageNewClientConnected( statisticId)));

                    	/*
            			 * get host name of interconnection server
            			 */
            			String localHostName = null;
            			try {
            				java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            				localHostName = localMachine.getHostName();
            			}
            			catch (java.net.UnknownHostException uhe) { 
            			}
            			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
            					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
            					localHostName + ":" + hostName + ":connectState",					// name
            					localHostName, 														// value
            					JmsMessage.SEVERITY_NO_ALARM, 										// severity
            					"CONNECTED", 														// status
            					hostName, 															// host
            					null, 																// facility
            					"virtual channel", 											// text
            					null);	
                    	logSender.close();
            		}
            		catch(JMSException jmse)
                    {
            			status = false;
            			icServer.checkSendMessageErrorCount();
                        //System.out.println("ClientRequest : send LOG message : *** EXCEPTION *** : " + jmse.getMessage());
                    }
        		}
        		//System.out.print("B");
        		break;
        		
        	case TagList.BEACON_MESSAGE_NOT_SELECTED:
        		//
        		// just send a reply
        		//
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		///System.out.println("Time-Beacon: - after send UDP reply	=  " + dateToString(new GregorianCalendar()));
        		//
        		// set beacon time locally
        		//
        		statisticContent.setBeaconTime();
        		/*
        		 * we are not selected any more
        		 * in case we were selected before - we'll have to create a JMS message
        		 */
        		if ( statisticContent.isSelectState()) {
        			//remember we're not selected any more
        			statisticContent.setSelectState(false);
        			/*
        			 * get host name of interconnection server
        			 */
        			String localHostName = null;
        			try {
        				java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
        				localHostName = localMachine.getHostName();
        			}
        			catch (java.net.UnknownHostException uhe) { 
        			}
        			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					localHostName + ":" + statisticContent.getLogicalIocName() + ":selectState",	// name
        					localHostName, 														// value
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
        			/*
        			 * start IocChangeState thread
        			 */
        			new IocChangedState (statisticContent.getHost(), statisticContent.getIpAddress(), statisticContent.getLogicalIocName(), statisticContent.getLdapIocName(), true);
        			
        			/*
        			 * create JMS sender
        			 * 
        			 */
        			try{
            			logSession = logConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                        // Create the destination (Topic or Queue)
            			Destination logDestination = logSession.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                        // Create a MessageProducer from the Session to the Topic or Queue
                    	MessageProducer logSender = logSession.createProducer( logDestination);
                    	logSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                    	logSender.setTimeToLive(jmsTimeToLiveLogsInt);
                    	icServer.sendLogMessage( icServer.prepareJmsMessage ( logSession.createMapMessage(), icServer.jmsLogMessageNewClientConnected( statisticId)));
                    	/*
            			 * get host name of interconnection server
            			 */
            			String localHostName = null;
            			try {
            				java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            				localHostName = localMachine.getHostName();
            			}
            			catch (java.net.UnknownHostException uhe) { 
            			}
            			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
            					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
            					localHostName + ":" + hostName + ":connectState",					// name
            					localHostName, 														// value
            					JmsMessage.SEVERITY_NO_ALARM, 										// severity
            					"CONNECTED", 														// status
            					hostName, 															// host
            					null, 																// facility
            					"virtual channel", 											// text
            					null);	
                    	logSender.close();
            		}
            		catch(JMSException jmse)
                    {
            			status = false;
            			icServer.checkSendMessageErrorCount();
                        //System.out.println("ClientRequest : send LOG message : *** EXCEPTION *** : " + jmse.getMessage());
                    }
        		}
        		//System.out.print("B");
        		break;
        		
        	case TagList.PUT_LOG_MESSAGE:
        		//
        		// PUT-LOG jms server
        		//
        		try {
        			putLogSession = putLogConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    // Create the destination (Topic or Queue)
        			Destination putLogDestination = putLogSession.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

                    // Create a MessageProducer from the Session to the Topic or Queue
                	MessageProducer putLogSender = putLogSession.createProducer( putLogDestination);
                	putLogSender.setDeliveryMode( DeliveryMode.PERSISTENT);
                	putLogSender.setTimeToLive( jmsTimeToLivePutLogsInt);
        			//sender = putLogSession.createProducer(putLogDestination);
                    //message = putLogSession.createMapMessage();
                    message = icServer.prepareTypedJmsMessage( putLogSession.createMapMessage(), tagValuePairs, type);
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
        		System.out.print("P");
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
            	System.out.print("T");
            	break;
            	
        		
        	case TagList.UNKNOWN_MESSAGE:
        		default:
        		status = false;
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		System.out.print("U");
        	}

        }
     
        //System.out.println("ClientRequest : clean up : leave thread" + this.getId());
        icServer.getClientRequestTheadCollector().decrementValue();
        icServer.setSuccessfullJmsSentCountdown(true);
        //System.out.print("Ex");
        
		try {
			if (alarmSession != null) {
				alarmSession.close();
			}
			if (logSession != null) {
				logSession.close();
			}
			if (putLogSession != null) {
				putLogSession.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} 
		finally {
			// final clean up
		}
	}
	
	private boolean parseMessage ( Hashtable<String,String> tagValue, Vector<TagValuePairs> tagValuePairs, TagValuePairs tag, TagValuePairs type, String statisticId) {
		boolean success = false;
		String[] attribute = null;
		boolean gotTag 		= false;
    	boolean gotId 		= false;
    	String	timeStamp = null;
		
		String daten = new String(this.packet.getData(), 0, this.packet.getLength());
		//System.out.println("Message: " + daten);
		//
		// just in case we should use another data format in the future
		// here's the place to implement anoher parser
		//

		StringTokenizer tok = new StringTokenizer(daten, PreferenceProperties.DATA_TOKENIZER);
        
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
		System.out.println( "Time to send JMS message      : " + timeDifference);
		
		timeDifference = gregorianTimeDifference( afterJmsSendTime, afterUdpAcknowledgeTime);
		System.out.println( "Time to acknowledge message   : " + timeDifference);
		
		timeDifference = gregorianTimeDifference( afterUdpAcknowledgeTime, afterLdapWriteTime);
		System.out.println( "Time to add LDAP write request: " + timeDifference);
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
