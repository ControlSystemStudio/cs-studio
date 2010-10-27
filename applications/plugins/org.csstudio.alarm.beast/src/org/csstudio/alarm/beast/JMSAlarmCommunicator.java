/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.net.InetAddress;
import java.text.SimpleDateFormat;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.logging.JMSLogMessage;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.security.User;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.csstudio.platform.utility.jms.JMSConnectionListener;

/** Base class for creating a JMS sender/producer.
 *  <p>
 *  Creates a background thread that connects to JMS
 *  and handles work-queue items for sending;
 *  subscribes to incoming messages and invokes callback routine
 *  for 'map' messages.
 *  <p>
 *  Can use the same or different topics for the sending/receiving.
 *  @author Kay Kasemir
 */
public class JMSAlarmCommunicator implements Runnable, JMSConnectionListener,
                                             ExceptionListener, MessageListener
{
    // private static final long CLOSE_TIMEOUT_MILLI = 10* 1000;

    final protected SimpleDateFormat date_format =
        new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);

    private volatile String configuration;

    /** Topics used to write resp. read */
    final private String write_topic, read_topic;
    
    /** Subscribe to read from write_topic? */
    final private boolean write_with_readback;

    /** Work queue handled by the worker thread that connects to JMS */
    final private WorkQueue queue = new WorkQueue();
    
    /** Worker thread */
    final private Thread thread;
    
    /** Flag to signal thread to run or quit */
    private volatile boolean run = true;
    
    /** JMS connection */
    private Connection connection;

    /** Name of JMS server, updated in JMSConnectionListener */
    private volatile String jms_server = Messages.NoJMSConnection;

    /** User for messages. Updated with authenticated user */
    private String user = System.getProperty("user.name"); //$NON-NLS-1$

    /** Host for messages */
	final private String host = InetAddress.getLocalHost().getHostName();

    /** JMS Session.
     *  Derived code has access, but should only do so within the
     *  JMS communicator thread, i.e. within a Runnable that's queued
     *  into the communicator's work queue.
     *  @see #queueJMSCommunication(Runnable)
     */
    protected Session session;
    
    /** JMS MessageProducer for write_topic.
     *  Access comments from session apply.
     */
    protected MessageProducer producer = null;
    
    /** JMS MessageConsumer for readback from write_topic */
    private MessageConsumer readback_consumer = null;

    /** JMS MessageConsumer for read_topic */
    private MessageConsumer consumer = null;

    private volatile boolean is_connected = false;

    /** Initialize
     *  @param configuration Configuration name (root element name)
     *  @param write_topic JMS Topic for writing. <code>null</code> if read-only
     *  @param read_topic JMS Topic for reading. <code>null</code> if write-only
     *  @param write_with_readback Also subscribe to the write_topic to read
     *                             messages from there?
     *  @see #queueJMSCommunication(Runnable)
     *  @see #handleMapMessage(MapMessage)
     */
    public JMSAlarmCommunicator(final String configuration,
                                final String write_topic,
                                final String read_topic,
                                final boolean write_with_readback) throws Exception
    {
        this.configuration = configuration;
        this.write_topic = write_topic;
        this.read_topic = read_topic;
        this.write_with_readback = write_with_readback;
        thread = new Thread(this, "JMS Alarm Communicator"); //$NON-NLS-1$
        thread.setDaemon(true);
    }
    
    /** @return Debug representation */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
	    return "Server=" + jms_server +
	    	   ", write topic=" + write_topic + (write_with_readback ? " (with readback)" : "") + 
	    		", read topic=" + read_topic;
    }

	/** Start the communication thread
     *  @see #close()
     */
    public void start()
    {
    	thread.start();
    }
    
    /** @return Name of JMS server or some text that indicates
     *          disconnected state. For information, not to determine
     *          exact connection state.
     */
    public String getJMSServerName()
    {
        return jms_server;
    }

    /** Determine connection state.
     *  <p>
     *  <b>Note: This is not perfect.</b>
     *  <p>
     *  When using ActiveMQ with failover, the connect() call will
     *  hang until a connection.
     *  So isConnected() will start out <code>false</code>,
     *  then turn <code>true</code> after the initial connection.
     *  On subsequent connection errors, ActiveMQ will internally try to
     *  re-connect while we might be hung in a write;
     *  isConnected() will not update to reflect subsequent errors.
     *  <p>
     *  Unclear how dependable the JMSConnectionListener is.
     *  For now that's only used to track the server name,
     *  not the actual connection state.
     * 
     *  @return <code>true</code> when connected
     */
    public boolean isConnected()
    {
        return is_connected;
    }

    /** Communicator thread Runnable */
	public void run()
	{
	    try
	    {
	        connect();
	    }
	    catch (Exception ex)
	    {	// Error in connect: Quit
	        CentralLogger.getInstance().getLogger(this).error(ex);
	        return;
	    }
	    is_connected = true;
	    // Handle Work Queue entries or just wait
	    while (run)
	    {
	        if (write_topic != null)
	            queue.execute(500);
	        else
	        {
	            try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    // Ignore
                }
	        }
	    }
        is_connected = false;
	    disconnect();
	}

	/** Called by communicator thread to connect to JMS
	 *  @throws Exception on error
	 */
	protected void connect() throws Exception
	{
	    final String url = Preferences.getJMS_URL(); 
	    if (url == null)
	        throw new Exception("JMS URL is null"); //$NON-NLS-1$
	    final String user = Preferences.getJMS_User();
	    final String password = Preferences.getJMS_Password();
        
	    connection = JMSConnectionFactory.connect(url, user, password);
	    JMSConnectionFactory.addListener(connection, this);
	    connection.setExceptionListener(this);
	    try
	    {
	    	// When server is unavailable, we'll hang in here,
	    	// and might be interrupted by close()
	    	connection.start();
	    }
	    catch (JMSException ex)
	    {
	    	if (run == false)
	        	return;
	    	throw ex;
	    }
	    session = connection.createSession(/* transacted */ false,
	                                       Session.AUTO_ACKNOWLEDGE);
	    if (write_topic != null)
	    {
	        final Topic topic = session.createTopic(write_topic);
    	    producer = session.createProducer(topic);
    	    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    	    if (write_with_readback)
    	    {
                readback_consumer = session.createConsumer(topic);
                readback_consumer.setMessageListener(this);
    	    }
	    }
	    if (read_topic != null)
	    {
            final Topic topic = session.createTopic(read_topic);
	        consumer = session.createConsumer(topic);
	        consumer.setMessageListener(this);
	    }
	}

	/** Will be called when communicator thread ends */
	protected void disconnect()
	{
	    try
	    {
	        if (consumer != null)
	        {
	            consumer.close();
	            consumer = null;
	        }
            if (readback_consumer != null)
            {
                readback_consumer.close();
                readback_consumer = null;
            }
	        if (producer != null)
	        {
	            producer.close();
	            producer = null;
	        }
	        session.close();
	    }
	    catch (Exception ex)
	    {
	        CentralLogger.getInstance().getLogger(this).warn(
	                "JMS shutdown error " + ex.getMessage(), ex); //$NON-NLS-1$
	    }
	}

	/** Close the JMS connections.
	 *  <p>
	 *  Must be called to release resources when no longer used.
	 */
    public void close()
	{
	    run = false;
	    
	    // Wait for the thread to exit so that we don't receive any
	    // additional JMS updates?
	    // On the other hand, the tread might not exit at all if it's
	    // still hung in the initial connect().
	    // Wait with a timeout?
	    
	    // For now we don't wait at all for JMS thread to close down.
	    
//	    try
//	    {
//	        thread.join(CLOSE_TIMEOUT_MILLI);
//	        // If thread won't stop, log that but otherwise continue
//	        if (thread.isAlive())
//	            throw new Exception("JMSAlarmCommunicator refuses to end");
//	    }
//        catch (Exception ex)
//        {
//            CentralLogger.getInstance().getLogger(this).warn(
//                    "JMS shutdown error " + ex.getMessage(), ex);
//        }
	}

	/** Add task to the work queue of the JMS communication thread.
	 *  Will be executed after thread managed to connect to JMS.
	 *  @param task Task to add to the queue
	 */
	protected void queueJMSCommunication(final Runnable task)
	{
        queue.add(task);
	}

	/** Create map message with basic fields already initialized
     *  @param application APPLICATION_ID property
     *  @param type TYPE property
     *  @param text TEXT property
     *  @return MapMessage
     *  @throws Exception on error
     */
    protected MapMessage createBasicMapMessage(final String application,
            final String type, final String text) throws Exception
    {
        final MapMessage map = session.createMapMessage();
        map.setString(JMSLogMessage.TYPE, type);
        map.setString(JMSAlarmMessage.CONFIG, configuration);
        map.setString(JMSLogMessage.TEXT, text);
        map.setString(JMSLogMessage.APPLICATION_ID, application);
        map.setString(JMSLogMessage.HOST, host);
        User loggedUser = SecurityFacade.getInstance().getCurrentUser();
        if (loggedUser == null)  //if no user logged in...
        	user = System.getProperty("user.name"); //$NON-NLS-1$
        else
        	user = loggedUser.getUsername();
        map.setString(JMSLogMessage.USER, user);
        return map;
    }
    
    /** @see JMSConnectionListener */
    public void linkDown()
    {
        jms_server = Messages.NoJMSConnection;
    }

    /** @see JMSConnectionListener */
    public void linkUp(final String server)
    {
        jms_server = server;
    }

    /** @see ExceptionListener */
    public void onException(final JMSException ex)
    {
        CentralLogger.getInstance().getLogger(this).error(
                "JMS Exception " + ex.getMessage(), ex); //$NON-NLS-1$
    }
    
    /** @see MessageListener */
    @SuppressWarnings("nls")
    public void onMessage(final Message message)
    {
        if (message instanceof MapMessage)
            handleMapMessage((MapMessage) message);
        else
            CentralLogger.getInstance().getLogger(this).error(
                "Message type " + message.getClass().getName() + " not handled");
    }

    /** Derived class must implement to handle received messages.
     *  Default implementation generates warning for unhandled message
     *  @param message The received MapMessage
     */
    @SuppressWarnings("nls")
    protected void handleMapMessage(MapMessage message)
    {
        CentralLogger.getInstance().getLogger(this).warn(
                "JMS Alarm Communicator received unhandled message from " +
                read_topic);
    }
}
