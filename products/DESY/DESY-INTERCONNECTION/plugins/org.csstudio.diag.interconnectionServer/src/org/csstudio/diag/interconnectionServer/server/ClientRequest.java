
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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.diag.interconnectionServer.server.InterconnectionServer.TagValuePairs;
import org.csstudio.utility.ldap.engine.Engine;

//import de.desy.jms.server.InterconnectionServer.TagValuePairs;

public class ClientRequest extends Thread
{
    private DatagramSocket      socket          = null;
    private DatagramPacket      packet          = null;
    private Session             alarmSession, logSession, putLogSession         = null;
    private Destination         alarmDestination, logDestination, putLogDestination     = null;
    private MessageProducer		alarmSender, logSender, putLogSender	= null;
    //private MessageProducer     sender          = null;
    //private MapMessage          message         = null;
    private Statistic			statistic		= null;
    public Statistic.StatisticContent  statisticContent = null;
    public TagList 				tagList			= null;
    
	public ClientRequest( InterconnectionServer icServer, DatagramSocket d, DatagramPacket p, Session jmsAlarmSession, Destination jmsAlarmDestination, MessageProducer jmsAlarmSender, 
			Session jmsLogSession, Destination jmsLogDestination, MessageProducer jmsLogSender, 
			Session jmsPutLogSession, Destination jmsPutLogDestination, MessageProducer jmsPutLogSender)
	{
        this.socket       = d;
		this.packet       = p;
		this.alarmSession      = jmsAlarmSession;
		this.logSession      = jmsLogSession;
		this.putLogSession      = jmsPutLogSession;
        this.alarmDestination  = jmsAlarmDestination;
        this.logDestination  = jmsLogDestination;
        this.putLogDestination  = jmsPutLogDestination;
        this.alarmSender = jmsAlarmSender;
        this.logSender = jmsLogSender;
        this.putLogSender = jmsPutLogSender;
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
	    String         	answerString    = null;
	    String[]       	attribute       = null;
	    int            	length          = 0;
	    int            	port            = 0;
	    String			statisticId		= null;
	    boolean 		received		= true;
	    MapMessage      message         = null;
	    MessageProducer sender          = null;
        
        address 	= packet.getAddress();
        hostName 	= address.getHostName();
        port 		= packet.getPort();
        length 		= packet.getLength();
        statisticId	= hostName + ":" + port;
        GregorianCalendar parseTime = new GregorianCalendar();
        GregorianCalendar afterJmsSendTime = new GregorianCalendar();
        GregorianCalendar afterUdpAcknowledgeTime = new GregorianCalendar();
        GregorianCalendar afterLdapWriteTime = new GregorianCalendar();
        
        /*
         * increase stistic counter
         */
        InterconnectionServer.getInstance().getClientRequestTheadCollector().incrementValue();
        
        ///System.out.println("Time: - start 		= " + dateToString(new GregorianCalendar()));
        //
        // write out some statistics
        //
        statisticContent = statistic.getContentObject( statisticId);
        statisticContent.setTime( received);
        statisticContent.setHost( hostName);
        statisticContent.setPort( port);
        statisticContent.setLastMessage( daten);
        statisticContent.setLastMessageSize( length); 
        
        Vector<TagValuePairs> tagValuePairs	= new Vector<TagValuePairs>();
        Hashtable<String,String> tagValue = new Hashtable<String,String>();	// could replace the Vector above
        TagValuePairs	id		= InterconnectionServer.getInstance().new TagValuePairs();
        TagValuePairs	type	= InterconnectionServer.getInstance().new TagValuePairs();

        
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
        	case TagList.ALARM_MESSAGE:
        	case TagList.EVENT_MESSAGE:
        	case TagList.ALARM_STATUS_MESSAGE:
        		//
        		// ALARM jms server
        		//
        		//System.out.print("a");
        		try {
            		//sender = alarmSession.createProducer(alarmDestination);
            		//System.out.println("Time-ALARM: - after sender= 	= " + dateToString(new GregorianCalendar()));
                    //message = alarmSession.createMapMessage();
            		message = InterconnectionServer.getInstance().prepareTypedJmsMessage( alarmSession.createMapMessage(), tagValuePairs, type);
            		///System.out.println("Time-APARM: - after message= 	= " + dateToString(new GregorianCalendar()));
            		
            		alarmSender.setPriority( 9);
            		///System.out.println("Time-ALARM: - before sender-send 	= " + dateToString(new GregorianCalendar()));
            		alarmSender.send(message);
            		message = null;
            		//System.out.print("aJs");
            		InterconnectionServer.getInstance().getJmsMessageWriteCollector().setValue(gregorianTimeDifference( parseTime, new GregorianCalendar()));
            		//System.out.print("aJe");
            		//System.out.println("Time-ALARM: - after sender-send 	= " + dateToString(new GregorianCalendar()));
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			InterconnectionServer.getInstance().checkSendMessageErrorCount();
                    System.out.println("ClientRequest : send ALARM message : *** EXCEPTION *** : " + jmse.getMessage());
                }
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		afterUdpAcknowledgeTime = new GregorianCalendar();
        		///System.out.println("Time-ALARM: - after send UDP reply	= " + dateToString(new GregorianCalendar()));
        		//
        		// time to update the LDAP server entry
        		//
        		//System.out.print("aLs");
        		updateLdapEntry( tagValue);
        		//System.out.print("aLe");
        		afterLdapWriteTime = new GregorianCalendar();
        		
        		//checkPerformance( parseTime, afterJmsSendTime, afterUdpAcknowledgeTime, afterLdapWriteTime);
        		System.out.print("A");
        		break;
        		
        	case TagList.STATUS_MESSAGE:
        	case TagList.SYSTEM_LOG_MESSAGE:
        	case TagList.APPLICATION_LOG_MESSAGE:
        		//
        		// LOG jms server
        		//
        		try{
        			// sender = logSession.createProducer(logDestination);
                    //message = logSession.createMapMessage();
                    message = InterconnectionServer.getInstance().prepareTypedJmsMessage( logSession.createMapMessage(), tagValuePairs, type);
            		logSender.send(message);
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			InterconnectionServer.getInstance().checkSendMessageErrorCount();
                    //System.out.println("ClientRequest : send LOG message : *** EXCEPTION *** : " + jmse.getMessage());
                }
        		ServerCommands.sendMesssage( ServerCommands.prepareMessage( id.getTag(), id.getValue(), status), socket, packet);
        		//
        		// time to update the LDAP server entry
        		//
        		//System.out.print("sLs");
        		updateLdapEntry( tagValue);
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
        		Statistic.getInstance().getContentObject(statisticId).setBeaconTime();
        		//
        		// generate system log message if connection state changed
        		//
        		if ( !Statistic.getInstance().getContentObject(statisticId).connectState) {
        			//
        			// connect state changed!
        			//
        			Statistic.getInstance().getContentObject(statisticId).setConnectState (true);
        			//
        			// try to send message (if logSession already available 
        			//
        			if ( logSession != null) {
        				try{
                			InterconnectionServer.getInstance().sendLogMessage( InterconnectionServer.getInstance().prepareJmsMessage ( logSession.createMapMessage(), InterconnectionServer.getInstance().jmsLogMessageNewClientConnected( statisticId)));
                			}
                			catch(JMSException jmse)
                            {
                    			status = false;
                                System.out.println("ClientRequest : send NewClientConnected-LOG message : *** EXCEPTION *** : " + jmse.getMessage());
                            }
        			} else {
        				//
        				// cannot send log message -> print locally
        				//
        				System.out.println("ClientRequest : NO logSession available => change connection state");
        			}
        			
        			/*
        			Statistic.getInstance().getContentObject(statisticId).setConnectState (true);
        			try{
            			sender = logSession.createProducer(logDestination);
                        message = logSession.createMapMessage();
                        message = jmsLogMessageNewClientConnected( statisticId);
                		sender.send(message);
            		}
            		catch(JMSException jmse)
                    {
            			status = false;
                        System.out.println("ClientRequest : send NewClientConnected-LOG message : *** EXCEPTION *** : " + jmse.getMessage());
                    }
                    */
        		}
        		System.out.print("B");
        		break;
        		
        	case TagList.PUT_LOG_MESSAGE:
        		//
        		// PUT-LOG jms server
        		//
        		try {
        			//sender = putLogSession.createProducer(putLogDestination);
                    //message = putLogSession.createMapMessage();
                    message = InterconnectionServer.getInstance().prepareTypedJmsMessage( putLogSession.createMapMessage(), tagValuePairs, type);
            		putLogSender.send(message);
        		}
        		catch(JMSException jmse)
                {
        			status = false;
        			InterconnectionServer.getInstance().checkSendMessageErrorCount();
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

        // clean up
        try
        {
            if (sender != null) {
            	sender.close();
            	System.out.println("clean up");
            }
            message = null;
        }
        catch(JMSException jmse)
        {
            System.out.println("ClientRequest : clean up : *** EXCEPTION *** : " + jmse.getMessage());
        }         
        //System.out.println("ClientRequest : clean up : leave thread" + this.getId());
        InterconnectionServer.getInstance().getClientRequestTheadCollector().decrementValue();
	}
	
	private boolean parseMessage ( Hashtable<String,String> tagValue, Vector<TagValuePairs> tagValuePairs, TagValuePairs tag, TagValuePairs type, String statisticId) {
		boolean success = false;
		String[] attribute = null;
		boolean gotTag 		= false;
    	boolean gotId 		= false;
		
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
		                    	TagValuePairs newTagValuePair =  InterconnectionServer.getInstance().new TagValuePairs ( attribute[0].toString(), attribute[1].toString());
		                    	tagValuePairs.add(newTagValuePair);
		                    }
	                	} //if
	                } // if
                } //while
        } // if tok
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
	
	
	private void updateLdapEntry ( Hashtable<String,String> tagValue) {
		//
		// find necessary entries and activate ldapUpdateMethod
		//
		String channel,status,severity,timeStamp = null;
		///System.out.println("tagValue : " + tagValue.toString());		
		
		if ( tagValue.containsKey("NAME") && tagValue.containsKey("STATUS") && tagValue.containsKey("SEVERITY") && (tagValue.containsKey("EVENTTIME") || tagValue.containsKey("CREATETIME"))) {
			
			channel = tagValue.get("NAME");
			status = tagValue.get("STATUS");
			severity = tagValue.get("SEVERITY");
			/*
			 * TODO: if we decide to use separate fields for event and create-time zhis is he place to change it!
			 */
			if ( tagValue.containsKey("EVENTTIME")) {
				timeStamp = tagValue.get("EVENTTIME");
			} else {
				timeStamp = tagValue.get("CREATETIME");
			}
			//
			// send values to LDAP engine
			//
			Engine.getInstance().addLdapWriteRequest( "epicsAlarmSeverity", channel, severity);
			Engine.getInstance().addLdapWriteRequest( "epicsAlarmStatus", channel, status);
			Engine.getInstance().addLdapWriteRequest( "epicsAlarmTimeStamp", channel, timeStamp);		
			
			//System.out.println("### - write to LDAP done for NAME= " + channel);
			//
			// change time stamp written time (for now we use: epicsAlarmAcknTimeStamp)
			//
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S");
	        java.util.Date currentDate = new java.util.Date();
	        String eventTime = sdf.format(currentDate);
	        /*
	         * TODO: we need a new field in LDAP for the time when we actually wrote the message
	         * for noew we leave it out
	         */
	        //Engine.getInstance().addLdapWriteRequest( "epicsAlarmAcknTimeStamp", channel, eventTime);		
		} else {
			//System.out.println("### - cannot write to LDAP done for NAME= " + tagValue.get("NAME"));
		}
		
	}
	
}
