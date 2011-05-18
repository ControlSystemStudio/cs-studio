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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.GregorianCalendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.internal.IocControlMessageListener;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.statistic.Collector;
import org.csstudio.platform.utility.jms.sharedconnection.IMessageListenerSession;
import org.csstudio.platform.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Receives messages from IOCs and creates {@link ClientRequest}s to handle
 * them. Also sets up the JMS connections and makes them available for other
 * classes.
 *
 * @author Matthias Clausen, Markus Moeller, Joerg Rathlev
 */
public class InterconnectionServer
{
    private static final String IOC_CONTROL_TOPIC = "IOC_CONTROL";
	private static InterconnectionServer		thisServer = null;
    private DatagramSocket              serverSocket    = null;
	private int							sendMessageErrorCount	= 0;
	private	volatile boolean         	quit        = false;
	private int messageCounter = 0;
	private String						localHostName = "defaultLocalHost";
	private BeaconWatchdog				beaconWatchdog = null;
	private int numberOfJmsServerFailover = -1;

    public final String NAME    = "IcServer";

    private int sendCommandId;			// PreferenceProperties.SENT_START_ID

    // Thread Executor
    private ExecutorService executor;
    private ExecutorService commandExecutor;

    private Collector	jmsMessageWriteCollector = null;
    private Collector	clientRequestTheadCollector = null;
    private Collector	numberOfMessagesCollector = null;
    private Collector	numberOfIocFailoverCollector = null;
    private Collector	beaconReplyTimeCollector = null;
    private Collector	messageReplyTimeCollector = null;
    private Collector	numberOfDuplicateMessagesCollector = null;

    private ISharedConnectionHandle _sharedSenderConnection;

    /**
     * This latch is counted down when the main method of this server exits.
     * This allows another thread to await the termination of the server.
     */
    private final CountDownLatch exitSignal = new CountDownLatch(1);

	private IocControlMessageListener _iocControlMessageListener;
	private IMessageListenerSession _iocControlListenerSession;

	private RedundantIocBeaconSender _redundantIocBeaconSender;

	/**
	 * Creates the JMS connections.
	 *
	 * @return <code>true</code> if successful, <code>false</code> otherwise.
	 * @throws JMSException
	 *             if an error occurs.
	 */
	synchronized private void createJmsConnections() throws JMSException
	{
		// remember how often we came here
		numberOfJmsServerFailover++;

		_sharedSenderConnection = SharedJmsConnections.sharedSenderConnection();

		_iocControlMessageListener = new IocControlMessageListener();
		_iocControlListenerSession = SharedJmsConnections.startMessageListener(
				_iocControlMessageListener, new String[] { IOC_CONTROL_TOPIC },
				Session.AUTO_ACKNOWLEDGE);
	}

	/**
	 * Closes the JMS connections.
	 */
	private void closeJmsConnections () {
		_sharedSenderConnection.release();
		_iocControlListenerSession.close();
	}

    private void disconnectFromIocs() {
    	final String[] listOfNodes = IocConnectionManager.INSTANCE.getNodeNameArray();
    	final InetAddress[] listOfIocInetAddresses = IocConnectionManager.INSTANCE.getListOfIocInetAdresses();

        final IPreferencesService prefs = Platform.getPreferencesService();
	    final String commandPortNumber = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.COMMAND_PORT_NUMBER, "", null);

		final int commandPortNum = Integer.parseInt(commandPortNumber);

		try {
		    for ( int i=0; i<listOfNodes.length; i++ ) {
		        CentralLogger.getInstance().warn(this, "InterconnectionServer: disconnect from IOC: " + listOfNodes[i]);
		        final IocCommandSender sendCommandToIoc = new IocCommandSender( listOfIocInetAddresses[i], commandPortNum, PreferenceProperties.COMMAND_DISCONNECT);
		        getCommandExecutor().execute(sendCommandToIoc);
		    }
		} catch (final IllegalArgumentException e) {
		    CentralLogger.getInstance().fatal(this, "Creation of command sender failed:\n" + e.getMessage());
		}
    }

	/**
	 * Returns whether all IOCs are currently in the state "not selected".
	 *
	 * @return <code>true</code> if all IOCs are in state not selected,
	 *         <code>false</code> if at least one IOC is selected.
	 */
    private boolean allIocsNotSelected() {
    	for (final IocConnection conn : IocConnectionManager.INSTANCE._connectionList.values()) {
			if (conn.isSelectState()) {
				return false;
			}
		}
    	return true;
    }

    public boolean stopIcServer () {
    	CentralLogger.getInstance().info(this, "Stopping IC-Server");

    	/*
    	 * Disconnect from all IOCs.
    	 */
    	disconnectFromIocs();

    	/*
    	 *  Wait until all IOCs are in state "not selected" (max 30 seconds).
    	 *
    	 *  XXX: This does not prevent the IOCs from re-selecting this server
    	 *  while it is in the process of shutting down.
    	 */
    	final long waitingSince = System.currentTimeMillis();
    	while (!allIocsNotSelected()
    			&& System.currentTimeMillis() - waitingSince < 30000) {
    		try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				// ignore
			}
    	}

    	/*
    	 * Stop the RedundantIocBeaconSender
    	 *
    	 */
    	_redundantIocBeaconSender.stopRedundanIocBeaconServer();

    	/*
    	 * Exit the main loop. This is done by setting the 'quit' flag to true
    	 * and by closing the server socket so that, if the socket is currently
    	 * blocked in a receive call, it stops immediately instead of blocking
    	 * until the next packet arrives. (Note: this is not an "unclean" way
    	 * of closing the socket, this is a documented way of doing it.)
    	 */
    	quit = true;
    	serverSocket.close();

    	/*
    	 *  Wait until the executMe() method has exited.
    	 */
    	try {
			exitSignal.await();
		} catch (final InterruptedException e) {
			// ignore
		}

		return true;
    }

    private InterconnectionServer() {
        final IPreferencesService prefService = Platform.getPreferencesService();
	    sendCommandId = prefService.getInt(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.SENT_START_ID, 0, null);

	    this.localHostName = "localHost-ND";
		try {
			final java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			this.localHostName = localMachine.getHostName();
		}
		catch (final java.net.UnknownHostException uhe) {
		}
    }

	// TODO: this doesn't really have to be a singleton. The application
	// should simply create only a single instance, but it would improve
	// testability if this were simply a normal class.
    public static synchronized InterconnectionServer getInstance() {
		if ( thisServer == null) {
			thisServer = new InterconnectionServer();
		}
		return thisServer;
	}

    public void executeMe()
    {
        DatagramPacket  packet      = null;
        ClientRequest   newClient   = null;
        byte 			buffer[]	= null;

        setupStatisticCollectors();

        try {
        	createJmsConnections();
        } catch (final JMSException e) {
        	CentralLogger.getInstance().fatal(this, "Could not connect to JMS servers", e);
        	return;
        }

        final IPreferencesService prefs = Platform.getPreferencesService();
	    final String dataPortNumber = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.DATA_PORT_NUMBER, "", null);

		final int dataPortNum = Integer.parseInt(dataPortNumber);

		//
		// check beacon timeout of connections to IOCs beaconTimeout
		//
		final int beaconTimeoutI = prefs.getInt(Activator.PLUGIN_ID,
	    		PreferenceConstants.BEACON_TIMEOUT, 15000, null);
	    final int multiplier = prefs.getInt(Activator.PLUGIN_ID,
	    		PreferenceConstants.CHECK_DISABLED_INTERVAL_MULTIPLIER, 10, null);
	    this.beaconWatchdog = new BeaconWatchdog(beaconTimeoutI, multiplier);  // mS

		/*
		 * do we want to write out message indicators?
		 */
		final String showMessageIndicator = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.SHOW_MESSAGE_INDICATOR, "", null);
		boolean showMessageIndicatorB = false;
		if ( showMessageIndicator !=null && showMessageIndicator.equals("true")) {
			showMessageIndicatorB = true;
		}

		//
		// create thread pool using the Executor Service
		//
	    final String numberofReadThreadsS = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.NUMBER_OF_READ_THREADS, "", null);
	    final int numberofReadThreads = Integer.parseInt(numberofReadThreadsS);

	    this.executor = Executors.newFixedThreadPool(numberofReadThreads);
	    CentralLogger.getInstance().info(this, "IC-Server create Read Thread Pool with " + numberofReadThreads + " threads");

		//
		// create command thread pool using the Executor Service
		//
	    final String numberofCommandThreadsS = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.NUMBER_OF_COMMAND_THREADS, "", null);
	    final int numberofCommandThreads = Integer.parseInt(numberofCommandThreadsS);

	    this.commandExecutor = Executors.newFixedThreadPool(numberofCommandThreads);
	    CentralLogger.getInstance().info(this, "IC-Server create Command Thread Pool with " + numberofCommandThreads + " threads");


        try
        {
        	CentralLogger.getInstance().info(this, "IC-Server trying to initialize UDP socket. Port: " + dataPortNum);
        	serverSocket = new DatagramSocket( dataPortNum );
        }
        catch(final IOException ioe)
        {
        	System.out.println(NAME + " ** ERROR ** : Could not initialize UDP socket. Port: " + dataPortNum);
        	System.out.println("\n" + NAME + " *** EXCEPTION *** : " + ioe.getMessage());
        	CentralLogger.getInstance().info(this, "IC-Server Could not initialize UDP socket. Port: " + dataPortNum);

            return;
        }

        /*
         * Start RedundantIocBeaconSender
         */

        _redundantIocBeaconSender = new RedundantIocBeaconSender();
        this.executor.execute( _redundantIocBeaconSender);

        CentralLogger.getInstance().info(this, "IC-Server starting to receive messages from Port: " + dataPortNum);
        while(!isQuit())
        {

        	/*
        	 * count the number of messages and write out one line after each 100 messages
        	 */
        	if ( showMessageIndicatorB && messageCounter++ > 100){
        		messageCounter = 0;
        		System.out.println ("100 messages complete");
        	}

    		try
            {
    			/*
    			 * always a 'fresh' buffer!
    			 * buffer can be overwritten if a new message arrives before we've copied the contents!!!
    			 */
    			buffer	=  new byte[ PreferenceProperties.BUFFER_ZIZE];
                packet = new DatagramPacket( buffer, buffer.length);

                serverSocket.receive(packet);

                //
            	// start time to calculate the beacon reply time
            	final GregorianCalendar startTime = new GregorianCalendar();

                /*
                 * unpack the packet here!
                 * if we do this way down in the thread - it might be overwritten!!
                 */

                final String packetData = new String(packet.getData(), 0, packet.getLength());

                CentralLogger.getInstance().debug(this, "Received packet: " + packetData);


                newClient = new ClientRequest( this, packetData, serverSocket,
                		packet.getAddress(), packet.getPort(), packet.getLength(), startTime);

                /*
                 * execute runnable by thread pool executor
                 */
                this.executor.execute(newClient);

                // increment statistics
                numberOfMessagesCollector.incrementCount();
            }
            catch(final IOException ioe) {
            	/*
            	 * If the quit flag is set, the exception occurs because the
            	 * socket was closed. That's ok. Otherwise, it is an actual
            	 * error and must be handled.
            	 */
            	if (!isQuit()) {
            		// TODO: is this error handling good enough?
            		CentralLogger.getInstance().error(this, "IO Error in main loop", ioe);
            	}
            }
        }
        CentralLogger.getInstance().debug( this, "InterconnectionServer: leaving main loop - to STOP");

		if (!serverSocket.isClosed()) {
			serverSocket.close();
		}

        // shutdown beacon watchdog
    	this.beaconWatchdog.setRunning(false);

    	closeJmsConnections();

    	// FIXME (jpenning, bknerr) : obsolete LDAP stuff
    	//Engine.getInstance().setRunning( false); // - gracefully stop LDAP engine

    	// shutdown thread pools
    	executor.shutdown();
    	commandExecutor.shutdown();

    	boolean cleanShutdown;
    	try {
			cleanShutdown = executor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			cleanShutdown = false;
		}
		if (!cleanShutdown) {
			CentralLogger.getInstance().warn(this, "Not all ClientRequests were finished!");
		}

    	try {
			cleanShutdown = commandExecutor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			cleanShutdown = false;
		}
		if (!cleanShutdown) {
			CentralLogger.getInstance().warn(this, "Not all Commands were finished!");
		}

    	CentralLogger.getInstance().info(this, "InterconnectionServer: finally Stopped");

        exitSignal.countDown();
        return;
    }

	/**
	 *
	 */
	private void setupStatisticCollectors() {
		jmsMessageWriteCollector = new Collector();
        jmsMessageWriteCollector.setApplication("IC-Server-" + getLocalHostName());
        jmsMessageWriteCollector.setDescriptor("Time to write JMS message");
        jmsMessageWriteCollector.setContinuousPrint(false);
        jmsMessageWriteCollector.setContinuousPrintCount(1000.0);
        jmsMessageWriteCollector.getAlarmHandler().setDeadband(5.0);
        jmsMessageWriteCollector.getAlarmHandler().setHighAbsoluteLimit(500.0);	// 500ms
        jmsMessageWriteCollector.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%

        /*
         * TODO: the clientRequestTheadCollector was used to limit the number of
         * concurrently running client threads. The server now uses a fixed size
         * thread pool, so this is not really useful anymore. Remove?
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

        numberOfMessagesCollector = new Collector();
        numberOfMessagesCollector.setApplication("IC-Server-" + getLocalHostName());
        numberOfMessagesCollector.setDescriptor("Number of Messages received");

        numberOfIocFailoverCollector = new Collector();
        numberOfIocFailoverCollector.setApplication("IC-Server-" + getLocalHostName());
        numberOfIocFailoverCollector.setDescriptor("Number of IOC failover");

        numberOfDuplicateMessagesCollector = new Collector();
        numberOfDuplicateMessagesCollector.setApplication("IC-Server-" + getLocalHostName());
        numberOfDuplicateMessagesCollector.setDescriptor("Number DUPLICATE Messages");

        beaconReplyTimeCollector = new Collector();
        beaconReplyTimeCollector.setApplication("IC-Server-" + getLocalHostName());
        beaconReplyTimeCollector.setDescriptor("Time to reply to beacons");
        beaconReplyTimeCollector.setContinuousPrint(false);
        beaconReplyTimeCollector.setContinuousPrintCount(1000.0);
        beaconReplyTimeCollector.getAlarmHandler().setDeadband(10.0);
        beaconReplyTimeCollector.getAlarmHandler().setHighAbsoluteLimit(PreferenceProperties.IOC_BEACON_TIMEOUT);	// 500ms
        beaconReplyTimeCollector.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
        beaconReplyTimeCollector.setHardLimit( 2* PreferenceProperties.IOC_BEACON_TIMEOUT);

        messageReplyTimeCollector = new Collector();
        messageReplyTimeCollector.setApplication("IC-Server-" + getLocalHostName());
        messageReplyTimeCollector.setDescriptor("Time to reply to messages");
        messageReplyTimeCollector.setContinuousPrint(false);
        messageReplyTimeCollector.setContinuousPrintCount(1000.0);
        messageReplyTimeCollector.getAlarmHandler().setDeadband(10.0);
        messageReplyTimeCollector.getAlarmHandler().setHighAbsoluteLimit(PreferenceProperties.IOC_MESSAGE_TIMEOUT);	// 500ms
        messageReplyTimeCollector.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
        messageReplyTimeCollector.setHardLimit( 2* PreferenceProperties.IOC_MESSAGE_TIMEOUT);
	}

	// XXX: This seems like a bad design. Also, I don't know if its safe to
	// close the connections here, they might be in use by some other thread.
    public void countJmsSendMessageErrorAndReconnectIfTooManyErrors () {
    	this.sendMessageErrorCount++;
		// wait for ERROR_COUNT_BEFORE_SWITCH_JMS_SERVER
		// increase waiting time proportional to the number of failovers (getNumberOfJmsServerFailover())
    	if (this.sendMessageErrorCount >
    			numberOfJmsServerFailover * PreferenceProperties.ERROR_COUNT_BEFORE_SWITCH_JMS_SERVER) {
    		closeJmsConnections();
    		try {
				createJmsConnections();
			} catch (final JMSException e) {
				/*
				 * XXX: This error should be handled better. (It was not handled
				 * at all in the old version.)
				 */
				CentralLogger.getInstance().error(this, "Failed to open JMS connection", e);
			}
    	}
    }

    public boolean sendLogMessage(final MapMessage message, final Session session) {
    	boolean status = true;

        final IPreferencesService prefs = Platform.getPreferencesService();
	    final String jmsTimeToLiveLogs = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.JMS_TIME_TO_LIVE_LOGS, "", null);
	    final int jmsTimeToLiveLogsInt = Integer.parseInt(jmsTimeToLiveLogs);

    	try{
    		final Destination logDestination = session.createTopic( PreferenceProperties.JMS_LOG_CONTEXT);

            // Create a MessageProducer from the Session to the Topic or Queue
        	final MessageProducer logSender = session.createProducer( logDestination);
        	logSender.setDeliveryMode( DeliveryMode.PERSISTENT);
        	logSender.setTimeToLive( jmsTimeToLiveLogsInt);

        	logSender.send(message);
        	logSender.close();
			return status;
		}
		catch(final JMSException jmse)
	    {
			status = false;
	        System.out.println("ClientRequest : send NewClientConnected-LOG message : *** EXCEPTION *** : " + jmse.getMessage());
	        return status;
	    }
	}

	public Collector getJmsMessageWriteCollector() {
		return jmsMessageWriteCollector;
	}

	public Collector getClientRequestTheadCollector() {
		return clientRequestTheadCollector;
	}

	public Collector getBeaconReplyTimeCollector() {
		return beaconReplyTimeCollector;
	}

	public Collector getMessageReplyTimeCollector() {
		return messageReplyTimeCollector;
	}

	public Collector getNumberOfDuplicateMessagesCollector() {
		return numberOfDuplicateMessagesCollector;
	}

	public synchronized int getSendCommandId() {
		return sendCommandId++;
	}

	public boolean isQuit() {
		return quit;
	}

	public Collector getNumberOfIocFailoverCollector() {
		return numberOfIocFailoverCollector;
	}

	/**
	 * Creates a new JMS session.
	 *
	 * @return the session.
	 * @throws JMSException
	 *             if an error occurs.
	 */
	public Session createJmsSession() throws JMSException {
		return _sharedSenderConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	public String getLocalHostName() {
		return localHostName;
	}

	public ExecutorService getCommandExecutor() {
		return commandExecutor;
	}
}
