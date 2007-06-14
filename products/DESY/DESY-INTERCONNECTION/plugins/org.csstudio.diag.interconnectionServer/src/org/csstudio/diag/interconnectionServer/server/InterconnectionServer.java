package org.csstudio.diag.interconnectionServer.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.statistic.Collector;

/**
 * This version uses <code>DatagramSockets</code> instead of Sockets.
 * @author  Markus Moeller
 * @version 0.2
 *
 */

public class InterconnectionServer
{
    private static InterconnectionServer		thisServer = null;
    private String                      instanceName    = null;    
    private DatagramSocket              serverSocket    = null; 
    private DatagramSocket              serverCommandSocket    = null; 
    private Session                     alarmSession, logSession, putLogSession	        	= null;
	private Destination                 alarmDestination, logDestination, putLogDestination = null;
	private MessageProducer				alarmSender, logSender, putLogSender		= null;
	private boolean 					primaryServerUsed = true;
	private int							sendMessageErrorCount	= 0;
	//private static int					errorContNamingException = 0;
    
    public final String NAME    = "IcServer";
    public final String VERSION = " 0.5";
    public final String BUILD   = " - BUILD 09.02.2007 17:00";
    
    private int sendCommandId = PreferenceProperties.SENT_START_ID;
    
    private Collector	jmsMessageWriteCollector = null;
    private Collector	clientRequestTheadCollector = null;
    
    
    synchronized public boolean setupConnections ( )
    {
    	Hashtable<String, String>   properties      = null;
    	Context                     alarmContext, logContext, putLogContext      	   	= null;
    	ConnectionFactory           alarmFactory, logFactory, putLogFactory        		= null;
    	Connection                  alarmConnection, logConnection, putLogConnection    = null;
    	PrintStream                 newout          = null;
        int							errorContNamingException = 0;
        
        
        // Den Namen dieser Instanz setzen (wird für JMS benötigt)
        instanceName = PreferenceProperties.PROCESS_NAME;

        //
        // remember how often we came here
        //
        Statistic.getInstance().incrementNumberOfJmsServerFailover();
        
        properties = new Hashtable<String, String>();
        
        properties.put(Context.INITIAL_CONTEXT_FACTORY, PreferenceProperties.JMS_CONTEXT_FACTORY);
        //
        // choose to start the primary - or the secondary JMS server
        //
        if ( primaryServerUsed) {
        	properties.put(Context.PROVIDER_URL, PreferenceProperties.PRIMARY_JMS_URL);
        	CentralLogger.getInstance().info(this, "Connect PRIMARY to Active-MQ-Server: " + PreferenceProperties.PRIMARY_JMS_URL);
        	System.out.println( "Connect PRIMARY to Active-MQ-Server: " + PreferenceProperties.PRIMARY_JMS_URL);
        	this.primaryServerUsed = false;
        } else {
        	properties.put(Context.PROVIDER_URL, PreferenceProperties.SECONDARY_JMS_URL);
        	CentralLogger.getInstance().info(this, "Connect SECONDARY to Active-MQ-Server: " + PreferenceProperties.SECONDARY_JMS_URL);
        	System.out.println( "Connect SECONDARY to Active-MQ-Server: " + PreferenceProperties.SECONDARY_JMS_URL);
        	this.primaryServerUsed = true;
        }

        /*
         * not used in our environment
         * 
        properties.put(Context.SECURITY_PRINCIPAL, "admin");
        properties.put(Context.SECURITY_CREDENTIALS, "openjms");
         */
        
        //
        // setup ALARM connection
        //
        try
        {
        	
        	// Create a Connection
        	/*
        	 * *
        	 * * Using new activeMQ Server
        	 * */
        	alarmContext     = new InitialContext(properties);            
            alarmFactory     = (ConnectionFactory)alarmContext.lookup("ConnectionFactory");
            alarmConnection  = alarmFactory.createConnection();
            alarmConnection.start();
        	
        	// Create a Session
        	this.alarmSession = alarmConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
        	this.alarmDestination = this.alarmSession.createTopic( PreferenceProperties.JMS_ALARM_CONTEXT);

            // Create a MessageProducer from the Session to the Topic or Queue
        	this.alarmSender = this.alarmSession.createProducer(this.alarmDestination);
        	// this.alarmSender.setDeliveryMode( DeliveryMode.NON_PERSISTENT);
       
        	
            
        }
        catch(NamingException ne)
        {
        	CentralLogger.getInstance().warn( this, NAME + " create JMS " + PreferenceProperties.JMS_ALARM_CONTEXT + " connection : *** NamingException *** : " + ne.getMessage());
            //System.out.println( NAME + " create JMS " + PreferenceProperties.JMS_ALARM_CONTEXT + " connection : *** NamingException *** : " + ne.getMessage());
            errorContNamingException++;
        }
        
        catch(JMSException jmse)
        {
        	CentralLogger.getInstance().warn( this, NAME + " create JMS " + PreferenceProperties.JMS_ALARM_CONTEXT + " connection : *** JMSException *** : " + jmse.getMessage());
            //System.out.println( NAME + " create JMS " + PreferenceProperties.JMS_ALARM_CONTEXT + " connection : *** JMSException *** : " + jmse.getMessage());
        }
        
        //
        // setup LOG connection
        //
        try
        {
        	// Create a Connection
        	/*
        	 *
        	 * Using new activeMQ Server
        	 * 
        	 */
        	logContext     = new InitialContext(properties);            
            logFactory     = (ConnectionFactory)logContext.lookup("ConnectionFactory");
            logConnection  = logFactory.createConnection();
            logConnection.start();
        	
        	// Create a Session
        	this.logSession = logConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
        	this.logDestination = this.logSession.createTopic( PreferenceProperties.JMS_LOG_CONTEXT);

            // Create a MessageProducer from the Session to the Topic or Queue
        	this.logSender = this.logSession.createProducer(this.logDestination);
        	// this.logSender.setDeliveryMode( DeliveryMode.NON_PERSISTENT);
        }
        
        catch(NamingException ne)
        {
        	CentralLogger.getInstance().warn( this, NAME + " create JMS " + PreferenceProperties.JMS_LOG_CONTEXT + " connection : *** NamingException *** : " + ne.getMessage());
        	// System.out.println( NAME + " create JMS " + PreferenceProperties.JMS_LOG_CONTEXT + " connection : *** NamingException *** : " + ne.getMessage());
            errorContNamingException++;
        }

        catch(JMSException jmse)
        {
            System.out.println(NAME + " create JMS " + PreferenceProperties.JMS_LOG_CONTEXT + " connection : *** JMSException *** : " + jmse.getMessage());
        }
        
        //
        // setup PUT-LOG connection
        //
        try
        {
        	// Create a Connection
        	/*
        	 *
        	 * Using new activeMQ Server
        	 * 
        	 */
        	putLogContext     = new InitialContext(properties);            
            putLogFactory     = (ConnectionFactory)putLogContext.lookup("ConnectionFactory");
            putLogConnection  = putLogFactory.createConnection();
            putLogConnection.start();
        	
        	// Create a Session
        	this.putLogSession = putLogConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
        	this.putLogDestination = this.putLogSession.createTopic( PreferenceProperties.JMS_PUT_LOG_CONTEXT);

            // Create a MessageProducer from the Session to the Topic or Queue
        	this.putLogSender = this.putLogSession.createProducer(this.putLogDestination);
        	// this.putLogSender.setDeliveryMode( DeliveryMode.NON_PERSISTENT);
        }

        catch(NamingException ne)
        {
            System.out.println( NAME + " create JMS " + PreferenceProperties.JMS_PUT_LOG_CONTEXT + " connection : *** NamingException *** : " + ne.getMessage());
            errorContNamingException++;
        }
        catch(JMSException jmse)
        {
            System.out.println(NAME + " create JMS " + PreferenceProperties.JMS_PUT_LOG_CONTEXT + " connection : *** JMSException *** : " + jmse.getMessage());
        }
        
        //
        // return succes
        //
        if ( errorContNamingException == 0) {
        	return true;
        } else {
        	return false;
        }
	}
    
    private InterconnectionServer() {
    	// absicherung
    }
    
    public static InterconnectionServer getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( thisServer == null) {
			synchronized (InterconnectionServer.class) {
				if (thisServer == null) {
					thisServer = new InterconnectionServer();
				}
			}
		}
		return thisServer;
	}
    
    public int executeMe()
    {
        DatagramPacket  packet      = null;
        ClientRequest   newClient   = null;
        int             result      = 0;
        boolean         quit        = false;
        byte 			buffer[]	=  new byte[ PreferenceProperties.BUFFER_ZIZE];

        /*
         * if not started from 'getInstance' ...
         */
        
        if ( thisServer == null) {
			synchronized (InterconnectionServer.class) {
				if (thisServer == null) {
					thisServer = this;
				}
			}
		}
        
        /*
         * set up collectors (statistic)
         */
        jmsMessageWriteCollector = new Collector();
        jmsMessageWriteCollector.setApplication("IC-Server");
        jmsMessageWriteCollector.setDescriptor("Time to write JMS message");
        jmsMessageWriteCollector.setContinuousPrint(true);
        jmsMessageWriteCollector.getAlarmHandler().setDeadband(5.0);
        jmsMessageWriteCollector.getAlarmHandler().setHighAbsoluteLimit(500.0);	// 500ms
        jmsMessageWriteCollector.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
        /*
         * set up collectors (statistic)
         */
        clientRequestTheadCollector = new Collector();
        clientRequestTheadCollector.setApplication("IC-Server");
        clientRequestTheadCollector.setDescriptor("Number of Client request Threads");
        clientRequestTheadCollector.setContinuousPrint(true);
        clientRequestTheadCollector.getAlarmHandler().setDeadband(5.0);
        clientRequestTheadCollector.getAlarmHandler().setHighAbsoluteLimit( PreferenceProperties.CLIENT_REQUEST_THREAD_MAX_NUMBER_ALARM_LIMIT);	// 500ms
        clientRequestTheadCollector.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
        
        if ( ! setupConnections()){
        	//
        	// try alternate JMS server (only once here...)
        	//
        	setupConnections();
        }
        
        System.out.println("\n" + NAME + VERSION + BUILD + "\nInternal Name: " + instanceName);

        try
        {
            serverSocket = new DatagramSocket( PreferenceProperties.DATA_PORT_NUMBER);
            //TODO: create message - successfully up and running
        }
        catch(IOException ioe)
        {
        	System.out.println(NAME + VERSION + " ** ERROR ** : Socket konnte nicht initialisiert werden. Port: " + PreferenceProperties.DATA_PORT_NUMBER);
        	System.out.println("\n" + NAME + VERSION + " *** EXCEPTION *** : " + ioe.getMessage());
            
            return -1;
        }
        
        
    
        // TODO: Abbruchbedingung einfügen
        //       z.B. Receiver für Queue COMMAND einfügen
        while(!quit)
        {
            try
            {
                packet = new DatagramPacket( buffer, buffer.length);

                serverSocket.receive(packet);
                
                newClient = new ClientRequest( this, serverSocket, packet, alarmSession, alarmDestination, alarmSender, 
                		logSession, logDestination, logSender, putLogSession, putLogDestination, putLogSender);
            }
            catch(IOException ioe)
            {
                System.out.println("\n" + NAME + VERSION + " *** IOException *** : " + ioe.getMessage());         
            }
        }
                
        serverSocket.close();
        
        return result;
    }
    
    public void checkSendMessageErrorCount () {
    	//
    	// if sendMessageErrorCount 
    	//
    	this.sendMessageErrorCount++;
    	if ( this.sendMessageErrorCount > 
    	Statistic.getInstance().getNumberOfJmsServerFailover()*PreferenceProperties.ERROR_COUNT_BEFORE_SWITCH_JMS_SERVER) {
    		//
    		// try another JMS server
    		//
    		// wait for ERROR_COUNT_BEFORE_SWITCH_JMS_SERVER
    		//
    		// increase waiting time proportional to the number of failovers ( getNumberOfJmsServerFailover())
    		//
    		setupConnections();
    		
    	}
    }
    
    
    public boolean sendLogMessage ( MapMessage message) {
    	boolean status = true;
    	try{
			MessageProducer sender = logSession.createProducer(logDestination);
	        //message = logSession.createMapMessage();
	        //message = jmsLogMessageNewClientConnected( statisticId);
			sender.send(message);
			sender.close();
			return status;
		}
		catch(JMSException jmse)
	    {
			status = false;
	        System.out.println("ClientRequest : send NewClientConnected-LOG message : *** EXCEPTION *** : " + jmse.getMessage());
	        return status;
	    }
	}
    
    public MapMessage prepareTypedJmsMessage ( MapMessage message, Vector<TagValuePairs> tagValuePairs, TagValuePairs type) {
		// first the type information
		try {
			TagValuePairs localPair;
			message.setString(type.getTag(), type.getValue());
			for (Enumeration el=tagValuePairs.elements(); el.hasMoreElements(); ) {
				localPair = (TagValuePairs)el.nextElement();
				message.setString(localPair.getTag(), localPair.getValue());
			}
		}
	    catch(JMSException jmse)
	    {
	    	// TODO: make it a log message
	        System.out.println("ClientRequest : prepareJmsMessage : *** EXCEPTION *** : " + jmse.getMessage());
	    }  
		return message;
	}
	
	public MapMessage prepareJmsMessage ( MapMessage message, Vector<TagValuePairs> tagValuePairs) {
		// first the type information
		try {
			TagValuePairs localPair;
			for (Enumeration el=tagValuePairs.elements(); el.hasMoreElements(); ) {
				localPair = (TagValuePairs)el.nextElement();
				message.setString(localPair.getTag(), localPair.getValue());
			}
		}
	    catch(JMSException jmse)
	    {
	    	// TODO: make it a log message
	        System.out.println("ClientRequest : prepareJmsMessage : *** EXCEPTION *** : " + jmse.getMessage());
	    }  
		return message;
	}
	
	public Vector jmsLogMessageNewClientConnected ( String statisticId) {
		// first the type information
		Vector <TagValuePairs> result = new Vector<TagValuePairs>();
		
		TagValuePairs newTagValuePair =  new TagValuePairs ( "TYPE", "SysLog");
		result.add(newTagValuePair);
		Calendar gregorsDate = new GregorianCalendar();
		Date d = gregorsDate.getTime();
		SimpleDateFormat df = new SimpleDateFormat( PreferenceProperties.JMS_DATE_FORMAT );			
		newTagValuePair =  new TagValuePairs ( "EVENTTIME", df.format(d));
		result.add(newTagValuePair);
		newTagValuePair =  new TagValuePairs ( "TEXT", "new log client connected");
		result.add(newTagValuePair);
		newTagValuePair =  new TagValuePairs ( "HOST", statisticId);
		result.add(newTagValuePair);
		newTagValuePair =  new TagValuePairs ( "STATUS", "on");
		result.add(newTagValuePair);
		
		return result;
	}
	
	public Vector jmsLogMessageLostClientConnection ( String statisticId) {
		// first the type information
		Vector <TagValuePairs >result = null;
		
		TagValuePairs newTagValuePair =  new TagValuePairs ( "TYPE", "SysLog");
		result.add(newTagValuePair);
		Calendar gregorsDate = new GregorianCalendar();
		Date d = gregorsDate.getTime();
		SimpleDateFormat df = new SimpleDateFormat( PreferenceProperties.JMS_DATE_FORMAT );			
		newTagValuePair =  new TagValuePairs ( "EVENTTIME", df.format(d));
		result.add(newTagValuePair);
		newTagValuePair =  new TagValuePairs ( "TEXT", "lost client connection");
		result.add(newTagValuePair);
		newTagValuePair =  new TagValuePairs ( "HOST", statisticId);
		result.add(newTagValuePair);
		newTagValuePair =  new TagValuePairs ( "STATUS", "off");
		result.add(newTagValuePair);
		
		return result;
	}
	
	
	
	public class TagValuePairs {
		//
		// define properties
		//
		String tag	= null;
		String value = null;
		
		public TagValuePairs () {
			
		}
		
		public TagValuePairs ( String tag, String value) {
			this.tag = tag;
			this.value = value;
		}
		
		public void setTag ( String tag) {
			this.tag = tag;
		}
		public String getTag () {
			return this.tag;
		}
		public void setValue ( String value) {
			this.value = value;
		}
		public String getValue () {
			return this.value;
		}
	}



	public Collector getJmsMessageWriteCollector() {
		return jmsMessageWriteCollector;
	}

	public void setJmsMessageWriteCollector(Collector jmsMessageWriteCollector) {
		this.jmsMessageWriteCollector = jmsMessageWriteCollector;
	}

	public Collector getClientRequestTheadCollector() {
		return clientRequestTheadCollector;
	}

	public void setClientRequestTheadCollector(Collector clientRequestTheadCollector) {
		this.clientRequestTheadCollector = clientRequestTheadCollector;
	}

	public int getSendCommandId() {
		return sendCommandId++;
	}

	public void setSendCommandId(int sendCommandId) {
		this.sendCommandId = sendCommandId;
	}
}
