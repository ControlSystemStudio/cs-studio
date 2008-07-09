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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.statistic.Collector;
import org.csstudio.platform.statistic.CollectorSupervisor;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * This version uses <code>DatagramSockets</code> instead of Sockets.
 * @author  Markus Moeller 
 * @version 1.0.1
 *
 */

public class InterconnectionServer
{
    private static InterconnectionServer		thisServer = null;
    private String                      instanceName    = null;    
    private DatagramSocket              serverSocket    = null; 
    private DatagramSocket              serverCommandSocket    = null; 
    private Session                     alarmSession, logSession, putLogSession	        	= null;
    private Connection                  alarmConnection, logConnection, putLogConnection    = null;
	private Destination                 alarmDestination, logDestination, putLogDestination = null;
	private MessageProducer				alarmSender, logSender, putLogSender		= null;
	private boolean 					primaryServerUsed = true;
	private int							sendMessageErrorCount	= 0;
	public int 							successfullJmsSentCountdown = PreferenceProperties.CLIENT_REQUEST_THREAD_UNSUCCESSSFULL_COUNTDOWN;
	private	volatile boolean         	quit        = false;
	private int messageCounter = 0;
	private String						localHostName = "defaultLocalHost";
	private BeaconWatchdog				beaconWatchdog = null;
	//private static int					errorContNamingException = 0;
    
    public final String NAME    = "IcServer";
    public final String VERSION = " 1.1.2";
    public final String BUILD   = " - BUILD 26.06.2008";
    
    //set in constructor from xml preferences
    private int sendCommandId;			// PreferenceProperties.SENT_START_ID
    
    // Thread Executor
    private ExecutorService executor;
    private ExecutorService commandExecutor;
    
    private Collector	jmsMessageWriteCollector = null;
    private Collector	clientRequestTheadCollector = null;
    private Collector	numberOfMessagesCollector = null;
    private Collector	numberOfIocFailoverCollector = null;
	
    /**
     * Indicates while-loop is running
     */
    private volatile boolean running;
    
    
    synchronized public boolean setupConnections ( )
    {
    	Hashtable<String, String>   properties      = null;
    	Context                     alarmContext, logContext, putLogContext      	   	= null;
    	ConnectionFactory           alarmFactory, logFactory, putLogFactory        		= null;
    	//Connection                  alarmConnection, logConnection, putLogConnection    = null;
    	PrintStream                 newout          = null;
        int							errorContNamingException = 0;
        ActiveMQConnectionFactory connectionFactory = null;
        boolean activeMqIsActive = false;
        
        // Den Namen dieser Instanz setzen (wird für JMS benötigt)
        instanceName = PreferenceProperties.PROCESS_NAME;

        //
        // remember how often we came here
        //
        Statistic.getInstance().incrementNumberOfJmsServerFailover();
        
//		//get properties from xml store.
//		XMLStore store = XMLStore.getInstance();
//		String jmsContextFactory = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"jmsContextFactory", false);
//		String primaryJmsUrl = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"primaryJmsUrl", false);
//		String secondaryJmsUrl = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"secondaryJmsUrl", false);

        IPreferencesService prefs = Platform.getPreferencesService();
	    String jmsContextFactory = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.JMS_CONTEXT_FACTORY, "", null);  
	    String primaryJmsUrl = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.PRIMARY_JMS_URL, "", null);
	    String connectionClientId = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.CONNECTION_CLIENT_ID, "", null);  
	    /*
	     * do not use the secondary URL any more
	     *
	    String secondaryJmsUrl = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.SECONDARY_JMS_URL, "", null);  
        */
        
        properties = new Hashtable<String, String>();
        
        properties.put(Context.INITIAL_CONTEXT_FACTORY, jmsContextFactory);
        //
        // choose to start the primary - or the secondary JMS server
        //
        //if ( primaryServerUsed) {
        	if ( (jmsContextFactory) != null && jmsContextFactory.toUpperCase().equals("ACTIVEMQ")) {
        		connectionFactory = new ActiveMQConnectionFactory(primaryJmsUrl);
        		activeMqIsActive = true;
        		CentralLogger.getInstance().info(this, "Connect PRIMARY to Active-MQ-Server: " + primaryJmsUrl);
//        		System.out.println( "Connect PRIMARY to Active-MQ-Server: " + primaryJmsUrl);
        	} else {
        		properties.put(Context.PROVIDER_URL, primaryJmsUrl);
        		CentralLogger.getInstance().info(this, "Connect PRIMARY to JMS-Server: " + primaryJmsUrl);
//        		System.out.println( "Connect PRIMARY to JMS-Server: " + primaryJmsUrl);
        	}

//        	this.primaryServerUsed = false;
//        } else {
//        	if ( (jmsContextFactory) != null && jmsContextFactory.toUpperCase().equals("ACTIVEMQ")) {
//        		connectionFactory = new ActiveMQConnectionFactory(primaryJmsUrl);
//        		activeMqIsActive = true;
//        		CentralLogger.getInstance().info(this, "Connect SECONDARY to Active-MQ-Server: " + secondaryJmsUrl);
////        		System.out.println( "Connect SECONDARY to Active-MQ-Server: " + secondaryJmsUrl);
//        	} else {
//        		properties.put(Context.PROVIDER_URL, secondaryJmsUrl);
//        		CentralLogger.getInstance().info(this, "Connect SECONDARY to JMS-Server: " + secondaryJmsUrl);
////        		System.out.println( "Connect SECONDARY to JMS-Server: " + secondaryJmsUrl);
//        	}
//        	this.primaryServerUsed = true;
//        }

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
        	if ( activeMqIsActive) {
        		/*
        		 * using ActiveMQ
        		 */
                this.alarmConnection = connectionFactory.createConnection();
                this.alarmConnection.setClientID(connectionClientId + "Alarm-" + getLocalHostName());
                this.alarmConnection.start();
        	} else {
        		/*
        		 * using standard JMS
        		 */
        		alarmContext     = new InitialContext(properties);            
                alarmFactory     = (ConnectionFactory)alarmContext.lookup("ConnectionFactory");
                this.alarmConnection  = alarmFactory.createConnection();
                this.alarmConnection.start();
        	}
        	
        	setAlarmSession(alarmConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)); // TODO: CLIENT_ACKNOWLEDGE??
            
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
        	if ( activeMqIsActive) {
        		/*
        		 * using ActiveMQ
        		 */
        		this.logConnection = connectionFactory.createConnection();
        		this.logConnection.setClientID(connectionClientId + "Log-" + getLocalHostName());
        		this.logConnection.start();
        	} else {
        		/*
        		 * using standard JMS
        		 */
        		logContext     = new InitialContext(properties);            
                logFactory     = (ConnectionFactory)logContext.lookup("ConnectionFactory");
                this.logConnection  = logFactory.createConnection();
                this.logConnection.start();
        	}
        	setLogSession(logConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)); // TODO: CLIENT_ACKNOWLEDGE??

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
        	if ( activeMqIsActive) {
        		/*
        		 * using ActiveMQ
        		 */
        		this.putLogConnection = connectionFactory.createConnection();
        		this.putLogConnection.setClientID(connectionClientId + "PutLog-" + getLocalHostName());
        		this.putLogConnection.start();
        	} else {
        		/*
        		 * using standard JMS
        		 */
        		putLogContext     = new InitialContext(properties);            
                putLogFactory     = (ConnectionFactory)putLogContext.lookup("ConnectionFactory");
                this.putLogConnection  = putLogFactory.createConnection();
                this.putLogConnection.start();
        	}
        	setPutLogSession(putLogConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)); // TODO: CLIENT_ACKNOWLEDGE??

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
    
    public void cleanupJms () {
		try {
			/*
			 * disconnect from JMS
			 */
			this.alarmSession.close();
			this.alarmConnection.close();
			
			this.logSession.close();
			this.logConnection.close();
			
			this.putLogSession.close();
			this.putLogConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}    	
    	
    }
    
    private void disconnectFromIocs() {
    	String[] listOfNodes = Statistic.getInstance().getNodeNameArray();
    	
//		//get properties from xml store.
//		XMLStore store = XMLStore.getInstance();
//		String commandPortNumber = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"commandPortNumber", false);

        IPreferencesService prefs = Platform.getPreferencesService();
	    String commandPortNumber = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.COMMAND_PORT_NUMBER, "", null);  
        

		
		int commandPortNum = Integer.parseInt(commandPortNumber);

    	for ( int i=0; i<listOfNodes.length; i++ ) {
    		CentralLogger.getInstance().warn(this, "InterconnectionServer: disconnect from IOC: " + listOfNodes[i]);
    		SendCommandToIoc sendCommandToIoc = new SendCommandToIoc( listOfNodes[i], commandPortNum, PreferenceProperties.COMMAND_DISCONNECT);
    		getCommandExecutor().execute(sendCommandToIoc);
    	}
    }
    
    public boolean stopIcServer () {
    	boolean success = true;
    	CentralLogger.getInstance().warn(this, "Atempt to stop InterconnectionServer");
    	/*
    	 * exit main loop
    	 */
    	setQuit(true);
    	while(this.running) {
//    		Thread.yield();
//    		try {
//				Thread.sleep( 1000);	// wait until 
//			} catch (InterruptedException e) {
//				// TODO: handle exception
//			}
    	}
    	
    	return success;
    }
    
    public void restartJms () {
    	/*
    	 * 
    	 */
    	CentralLogger.getInstance().warn(this, "InterconnectionServer: Restart JMS connections! ");
    	disconnectFromIocs();
    	cleanupJms();
    	setupConnections();
    }
    
    private InterconnectionServer() {
    	// absicherung
    	
//    	//get properties from xml preferences
//    	XMLStore store = XMLStore.getInstance();
//		String sentStartID = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"sentStartID", false);

//		//create a defaultscope for the plugin. Otherwise the preference initialzier
//		//will be called AFTER StartupService and the LoginCallbackhandler
//		//has no preference values.
//		IEclipsePreferences prefsx = new DefaultScope().getNode(
//				Activator.getDefault().getPluginId());
		IEclipsePreferences prefs = new DefaultScope().getNode(
				Activator.getDefault().getPluginId());

//		prefs.put(PreferenceConstants.XMPP_USER_NAME, "icserver-alarm");
//		prefs.put(PreferenceConstants.XMPP_PASSWORD, "icserver");
//		prefs.put(PreferenceConstants.DATA_PORT_NUMBER, "18324");
//		prefs.put(PreferenceConstants.COMMAND_PORT_NUMBER, "18325");
//		prefs.put(PreferenceConstants.SENT_START_ID, "5000000");
//		prefs.put(PreferenceConstants.JMS_CONTEXT_FACTORY, "ACTIVEMQ");
//		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_ALARMS, "3600000");
//		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_LOGS, "600000");
//		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_PUT_LOGS, "3600000");
//		prefs.put(PreferenceConstants.PRIMARY_JMS_URL, "failover:(tcp://krynfs.desy.de:62616,tcp://krykjmsb.desy.de:64616)?maxReconnectDelay=500,maxReconnectAttempts=50");
//		prefs.put(PreferenceConstants.SECONDARY_JMS_URL	, "failover:(tcp://krykjmsb.desy.de:64616,tcp://krynfs.desy.de:62616)?maxReconnectDelay=500,maxReconnectAttempts=50");

    	
        IPreferencesService prefService = Platform.getPreferencesService();
	    sendCommandId = prefService.getInt(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.SENT_START_ID, 0, null);  
//		sendCommandId = Integer.parseInt(sentStartID);

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
        byte 			buffer[]	= null;
        boolean receiveEnabled = true;

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
		 * get host name of interconnection server
		 */
		setLocalHostName("localHost-ND");
		try {
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			setLocalHostName( localMachine.getHostName());
		}
		catch (java.net.UnknownHostException uhe) { 
		}
        
        /*
         * set up collectors (statistic)
         */
        jmsMessageWriteCollector = new Collector();
        jmsMessageWriteCollector.setApplication("IC-Server-" + getLocalHostName());
        jmsMessageWriteCollector.setDescriptor("Time to write JMS message");
        jmsMessageWriteCollector.setContinuousPrint(false);
        jmsMessageWriteCollector.setContinuousPrintCount(1000.0);
        jmsMessageWriteCollector.getAlarmHandler().setDeadband(5.0);
        jmsMessageWriteCollector.getAlarmHandler().setHighAbsoluteLimit(500.0);	// 500ms
        jmsMessageWriteCollector.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
        /*
         * set up collectors (statistic)
         */
        clientRequestTheadCollector = new Collector();
        clientRequestTheadCollector.setApplication("IC-Server-" + getLocalHostName());
        clientRequestTheadCollector.setDescriptor("Number of Client request Threads");
        clientRequestTheadCollector.setContinuousPrint(false);
        clientRequestTheadCollector.setContinuousPrintCount(1000.0);
        clientRequestTheadCollector.getAlarmHandler().setDeadband(5.0);
        clientRequestTheadCollector.getAlarmHandler().setHighAbsoluteLimit( PreferenceProperties.CLIENT_REQUEST_THREAD_MAX_NUMBER_ALARM_LIMIT);	// 500ms
        clientRequestTheadCollector.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
        clientRequestTheadCollector.setHardLimit( PreferenceProperties.MAX_NUMBER_OF_CLIENT_THREADS);
        /*
         * set up collectors (statistic)
         */
        numberOfMessagesCollector = new Collector();
        numberOfMessagesCollector.setApplication("IC-Server-" + getLocalHostName());
        numberOfMessagesCollector.setDescriptor("Number of Messages received");
        /*
         * set up collectors (statistic)
         */
        numberOfIocFailoverCollector = new Collector();
        numberOfIocFailoverCollector.setApplication("IC-Server-" + getLocalHostName());
        numberOfIocFailoverCollector.setDescriptor("Number of IOC failover");
        
        if ( ! setupConnections()){
        	//
        	// try alternate JMS server (only once here...)
        	//
        	setupConnections();
        }
        
//        System.out.println("\n" + NAME + VERSION + BUILD + "\nInternal Name: " + instanceName);

//		//get properties from xml store.
//		XMLStore store = XMLStore.getInstance();
//		String dataPortNumber = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"dataPortNumber", false);

        IPreferencesService prefs = Platform.getPreferencesService();
	    String dataPortNumber = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.DATA_PORT_NUMBER, "", null);  
		
		int dataPortNum = Integer.parseInt(dataPortNumber);
		
		//
		// check beacon timeout of connections to IOCs beaconTimeout
		//
	    String beaconTimeout = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.BEACON_TIMEOUT, "", null); 
	    int beaconTimeoutI = Integer.parseInt(beaconTimeout);
	    this.beaconWatchdog = new BeaconWatchdog(beaconTimeoutI);  // mS
		/*
		 * do we want to write out message indicators?
		 */
		String showMessageIndicator = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.SHOW_MESSAGE_INDICATOR, "", null); 
		boolean showMessageIndicatorB = false;
		if ( (showMessageIndicator !=null) && showMessageIndicator.equals("true")) {
			showMessageIndicatorB = true;
		}
		
		//
		// create thread pool using the Executor Service
		//
	    String numberofReadThreadsS = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.NUMBER_OF_READ_THREADS, "", null); 
	    int numberofReadThreads = Integer.parseInt(numberofReadThreadsS);
	    
	    this.setExecutor( Executors.newFixedThreadPool(numberofReadThreads));
	    CentralLogger.getInstance().info( this, "IC-Server create Read Thread Pool with " + numberofReadThreads + " threads"); 
	    
		//
		// create command thread pool using the Executor Service
		//
	    String numberofCommandThreadsS = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.NUMBER_OF_COMMAND_THREADS, "", null); 
	    int numberofCommandThreads = Integer.parseInt(numberofCommandThreadsS);
	    
	    this.setCommandExecutor( Executors.newFixedThreadPool(numberofCommandThreads));
	    CentralLogger.getInstance().info( this, "IC-Server create Command Thread Pool with " + numberofCommandThreads + " threads"); 
		
		
        try
        {
            serverSocket = new DatagramSocket( dataPortNum );
            //TODO: create message - successfully up and running
        }
        catch(IOException ioe)
        {
        	System.out.println(NAME + VERSION + " ** ERROR ** : Socket konnte nicht initialisiert werden. Port: " + dataPortNum);
        	System.out.println("\n" + NAME + VERSION + " *** EXCEPTION *** : " + ioe.getMessage());
            
            return -1;
        }
        
        /*
         * create thread group for the thread which will be created here:
         */
        ThreadGroup somegroup = new ThreadGroup ("ClientRequests");
    
        // TODO: Abbruchbedingung einfügen
        //       z.B. Receiver für Queue COMMAND einfügen
        
        running = true;
        
        while(!isQuit())
        {
        	/*
        	 * count the number of messages and write out one line after each 100 messages
        	 */
        	if ( showMessageIndicatorB && (messageCounter++ > 100)){
        		messageCounter = 0;
        		System.out.println ("100 messages complete");
        	}
        	
        	/*
        	 * check for the number of existing threads
        	 * do not create new threads
        	 * - send disconnect message to all currently connected IOCs (once)
        	 * - wait one second
        	 * - try again
        	 */
        	if ( clientRequestTheadCollector.getActualValue().getValue() > clientRequestTheadCollector.getHardLimit()) {
        		
        		if ( receiveEnabled) {
        			CentralLogger.getInstance().warn( this, "Maximum number of client threads (" + clientRequestTheadCollector.getHardLimit() + ") reached - STOP creating new threads");
        			/*
        			 * send command to IOC's: I want to disconnect!
        			 */
        			disconnectFromIocs();
            		/*
            		 * print out overview of current statistic information
            		 */
            		System.out.println(CollectorSupervisor.getInstance().getCollectionAsString());
            		receiveEnabled = false;
        		}
        		/*
        		 * wait - otherwise we run in an undefinite loop
        		 */
            	try {
               		Thread.sleep(1000);
               	}
               	catch (InterruptedException  e) {
               		e.printStackTrace();
               	}
        	} else {
        		try
                {
        			/*
        			 * always a 'fresh' buffer!
        			 * buffer can be overwritten if a new message arrives before we've copied the contents!!!
        			 */
        			buffer	=  new byte[ PreferenceProperties.BUFFER_ZIZE];
                    packet = new DatagramPacket( buffer, buffer.length);

                    serverSocket.receive(packet);
                    
                    /*
                     * unpack the packet here!
                     * if we do this way down in the thread - it might be overwritten!!
                     */
                    
                    String packetData = new String(packet.getData(), 0, packet.getLength());
                    
                    /* 
                     * 
                    newClient = new ClientRequest( this, serverSocket, packet, alarmSession, alarmDestination, alarmSender, 
                    		logSession, logDestination, logSender, putLogSession, putLogDestination, putLogSender);
                    		*/
                    newClient = new ClientRequest( this, packetData, serverSocket, packet, alarmConnection, logConnection, putLogConnection);
                    
                    /*
                     * execute runnable by thread pool executor
                     */
                    this.getExecutor().execute(newClient);
                    
                    /*
                     * now watchdog within thread pool
                     */
                    // new ClientWatchdog ( newClient, PreferenceProperties.CLIENT_REQUEST_THREAD_TIMEOUT);
                    
                    // increment statistics
                    numberOfMessagesCollector.incrementCount();
                }
                catch(IOException ioe)
                {
                    System.out.println("\n" + NAME + VERSION + " *** IOException *** : " + ioe.getMessage());         
                }
        		receiveEnabled = true;
        	}
        }
        
        CentralLogger.getInstance().warn( this, "InterconnectionServer: leaving main loop - to STOP");

        CentralLogger.getInstance().warn( this, "InterconnectionServer: waiting for client threads to stop");
        /**
         * wait until all client threads are stopped
         * all? - well leave a mismatch of 10
         */
        while( getClientRequestTheadCollector().getActualValue().getValue() > 10) Thread.yield();

        CentralLogger.getInstance().warn( this, "InterconnectionServer: all but " + getClientRequestTheadCollector().getActualValue().getValue() + "  clients threads stopped, closing connections...");
        
    	disconnectFromIocs();
    	cleanupJms();
//    	TODO
    	Engine.getInstance().setRunning( false); // - gracefully stop LDAP engine
    	
    	// shutdown thread pool
    	this.getExecutor().shutdownNow();
    	this.getCommandExecutor().shutdownNow();
    	
    	// shutdown beacon watchdog
    	this.beaconWatchdog.setRunning(false);
    	
        serverSocket.close();
        
        /*
    	 * send message
    	 * inform IOC's to disconnect
    	 * stop JMS connections
    	 */
        
    	CentralLogger.getInstance().warn(this, "InterconnectionServer: finally Stopped");
        
        this.running = false;
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
    		
    		/*
    		 * cleanup JMS connections
    		 */
    		cleanupJms();
    		/*
    		 * set up new connection
    		 */
    		setupConnections();
    		
    	}
    }
    
    
    public boolean sendLogMessage ( MapMessage message) {
    	boolean status = true;
    	
        IPreferencesService prefs = Platform.getPreferencesService();
	    String jmsTimeToLiveLogs = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.JMS_TIME_TO_LIVE_LOGS, "", null);  
	    int jmsTimeToLiveLogsInt = Integer.parseInt(jmsTimeToLiveLogs);
    	
    	try{
    		Destination logDestination = getLogSession().createTopic( PreferenceProperties.JMS_LOG_CONTEXT);

            // Create a MessageProducer from the Session to the Topic or Queue
        	MessageProducer logSender = getLogSession().createProducer( logDestination);
        	logSender.setDeliveryMode( DeliveryMode.PERSISTENT);
        	logSender.setTimeToLive( jmsTimeToLiveLogsInt);

	        //message = logSession.createMapMessage();
	        //message = jmsLogMessageNewClientConnected( statisticId);
        	logSender.send(message);
        	logSender.close();
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
		newTagValuePair =  new TagValuePairs ( "SEVERITY", "NO_ALARM");
		result.add(newTagValuePair);
		//TODO: add ioc-NAME
		//newTagValuePair =  new TagValuePairs ( "NAME", getIocNameFromIpAddress(statisticId));
		//result.add(newTagValuePair);
		
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

	public int getSuccessfullJmsSentCountdown() {
		return successfullJmsSentCountdown;
	}

	synchronized void setSuccessfullJmsSentCountdown(int successfullJmsSentCountdown) {
		this.successfullJmsSentCountdown = successfullJmsSentCountdown;
	}
	
	public void setSuccessfullJmsSentCountdown(boolean success) {
		/*
		 * countdown is set to i.e. 50 (PreferenceProperties.CLIENT_REQUEST_THREAD_UNSUCCESSSFULL_COUNTDOWN)
		 * decrement for every unsuccessful transaction
		 * increment back to 50 if successfull
		 * 
		 * if 0 is reached: restart JMS connections
		 */
		int countdown = 0;
		if ( success) {
			setSuccessfullJmsSentCountdown( PreferenceProperties.CLIENT_REQUEST_THREAD_UNSUCCESSSFULL_COUNTDOWN );
		} else {
			countdown = getSuccessfullJmsSentCountdown();
			if ( countdown-- < 0) {
				restartJms();
				setSuccessfullJmsSentCountdown( PreferenceProperties.CLIENT_REQUEST_THREAD_UNSUCCESSSFULL_COUNTDOWN );
			} else {
				setSuccessfullJmsSentCountdown(countdown); 
			}
		}
	}

	public boolean isQuit() {
		return quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	public Connection getAlarmConnection() {
		return this.alarmConnection;
	}

	public Connection getLogConnection() {
		return this.logConnection;
	}

	public Connection getPutLogConnection() {
		return this.putLogConnection;
	}

	public Collector getNumberOfMessagesCollector() {
		return numberOfMessagesCollector;
	}

	public void setNumberOfMessagesCollector(Collector numberOfMessagesCollector) {
		this.numberOfMessagesCollector = numberOfMessagesCollector;
	}

	public Collector getNumberOfIocFailoverCollector() {
		return numberOfIocFailoverCollector;
	}

	public void setNumberOfIocFailoverCollector(
			Collector numberOfIocFailoverCollector) {
		this.numberOfIocFailoverCollector = numberOfIocFailoverCollector;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public Destination getPutLogDestination() {
		return putLogDestination;
	}

	public void setPutLogDestination(Destination putLogDestination) {
		this.putLogDestination = putLogDestination;
	}

	public Session getAlarmSession() {
		return alarmSession;
	}

	public void setAlarmSession(Session alarmSession) {
		this.alarmSession = alarmSession;
	}

	public Session getLogSession() {
		return logSession;
	}

	public void setLogSession(Session logSession) {
		this.logSession = logSession;
	}

	public Session getPutLogSession() {
		return putLogSession;
	}

	public void setPutLogSession(Session putLogSession) {
		this.putLogSession = putLogSession;
	}

	public String getLocalHostName() {
		return localHostName;
	}

	public void setLocalHostName(String localHostName) {
		this.localHostName = localHostName;
	}

	public ExecutorService getCommandExecutor() {
		return commandExecutor;
	}

	public void setCommandExecutor(ExecutorService commandExecutor) {
		this.commandExecutor = commandExecutor;
	}
}
