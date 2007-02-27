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
import java.util.StringTokenizer;
import java.util.Vector;

import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


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
    private Session                     alarmSession, logSession, putLogSession	        	= null;
	private Destination                 alarmDestination, logDestination, putLogDestination = null;
	private MessageProducer				alarmSender, logSender, putLogSender		= null;
	private boolean 					primaryServerUsed = true;
	private int							sendMessageErrorCount	= 0;
	//private static int					errorContNamingException = 0;
    
    public final String NAME    = "IcServer";
    public final String VERSION = " 0.5";
    public final String BUILD   = " - BUILD 09.02.2007 17:00";
    
    /**
     *  Der Konstruktor InterconnectionServer(String) erledigt alle nötigen
     *  Vorbereitungen. Die Queue, die zurzeit benutzt wird, ist ALARM. 
     *  
     * @param name
     */
    
    public InterconnectionServer( ) {
    	
    }
    
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
        // remember how often we came ehre
        //
        Statistic.getInstance().incrementNumberOfJmsServerFailover();
        
        properties = new Hashtable<String, String>();
        
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
        //properties.put(Context.PROVIDER_URL, "rmi://krykelog.desy.de:1099/");
        //
        // choose to start the primary - or the secondary JMS server
        //
        if ( primaryServerUsed) {
        	properties.put(Context.PROVIDER_URL, PreferenceProperties.PRIMARY_JMS_URL);
        	this.primaryServerUsed = false;
        } else {
        	properties.put(Context.PROVIDER_URL, PreferenceProperties.SECONDARY_JMS_URL);
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
            alarmContext     = new InitialContext(properties);            
            alarmFactory     = (ConnectionFactory)alarmContext.lookup("ConnectionFactory");
            alarmConnection  = alarmFactory.createConnection();
            this.alarmSession     = alarmConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Ggf. die Queue ändern
            this.alarmDestination = (Destination)alarmContext.lookup( PreferenceProperties.JMS_ALARM_CONTEXT);
            
            alarmConnection.start();
            
            this.alarmSender = alarmSession.createProducer(alarmDestination);
        }
        catch(NamingException ne)
        {
            System.out.println( NAME + " create JMS ALARM connection : *** NamingException *** : " + ne.getMessage());
            errorContNamingException++;
        }
        catch(JMSException jmse)
        {
            System.out.println(NAME + " create JMS ALARM connection : *** JMSException *** : " + jmse.getMessage());
        }
        
        //
        // setup ALARM connection
        //
        
        try
        {
            logContext     = new InitialContext(properties);            
            logFactory     = (ConnectionFactory)logContext.lookup("ConnectionFactory");
            logConnection  = logFactory.createConnection();
            this.logSession     = logConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Ggf. die Queue ändern
            this.logDestination = (Destination)logContext.lookup( PreferenceProperties.JMS_LOG_CONTEXT);
            
            logConnection.start();
            
            this.logSender = logSession.createProducer(logDestination);
        }
        catch(NamingException ne)
        {
            System.out.println( NAME + " create JMS LOG connection : *** NamingException *** : " + ne.getMessage());
            errorContNamingException++;
        }
        catch(JMSException jmse)
        {
            System.out.println(NAME + " create JMS LOG connection : *** JMSException *** : " + jmse.getMessage());
        }
        
        //
        // setup PU-LOG connection
        //
        
        try
        {
            putLogContext     = new InitialContext(properties);            
            putLogFactory     = (ConnectionFactory)putLogContext.lookup("ConnectionFactory");
            putLogConnection  = putLogFactory.createConnection();
            this.putLogSession     = putLogConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Ggf. die Queue ändern
            this.putLogDestination = (Destination)putLogContext.lookup( PreferenceProperties.JMS_PUT_LOG_CONTEXT);
            
            putLogConnection.start();
            
            this.putLogSender = putLogSession.createProducer(putLogDestination);
        }
        catch(NamingException ne)
        {
            System.out.println( NAME + " create JMS PUT-LOG connection : *** NamingException *** : " + ne.getMessage());
            errorContNamingException++;
        }
        catch(JMSException jmse)
        {
            System.out.println(NAME + " create JMS PUT-LOG connection : *** JMSException *** : " + jmse.getMessage());
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
    
    public static InterconnectionServer getInstance() {
		//
		// get an instance of our sigleton
		//

		return thisServer;
	}
    
    public int executeMe()
    {
        DatagramPacket  packet      = null;
        ClientRequest   newClient   = null;
        int             result      = 0;
        boolean         quit        = false;
        byte 			buffer[]	=  new byte[ PreferenceProperties.BUFFER_ZIZE];

        // Ausgabe in eine Datei umlenken
        /*
        try
        {
            newout = new PrintStream(".\\Me.log");
            System.setOut(newout);
        }
        catch(Exception e) { }
        */
        
        //
        // set up JMS connections
        //
        
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
                
                newClient = new ClientRequest(serverSocket, packet, alarmSession, alarmDestination, alarmSender, 
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

    
    public static void main(String[] args)
    {
        String  n;
        
        if(args.length > 0)
        {
            n = args[0];
        }
        else
        {
            n = "NO NAME";
        }
        
        thisServer = new InterconnectionServer( );
        
        //System.out.println ("vor start init");
        //Timer.Start.init();
        
        //System.out.println ("vor start all timer");
        //Timer.Start.all();

        
        thisServer.executeMe();
        
        System.exit(0);
    }
}
