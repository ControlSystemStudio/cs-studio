
/* 
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron, 
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
 *
 */

package org.csstudio.alarm.syslog2jms;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.MapMessage;

import org.csstudio.alarm.syslog2jms.management.Restart;
import org.csstudio.alarm.syslog2jms.preferences.PreferenceConstants;
import org.csstudio.platform.utility.jms.JmsSimpleProducer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class controls all aspects of the application's execution
 */
public class Syslog2JmsApplication implements IApplication, Stoppable,
                                              IGenericServiceListener<ISessionService> {

    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(Syslog2JmsApplication.class);

    /** The session service for the XMPP login */
    private ISessionService xmppService;
    
    /** The JMS producer / publisher */
    private JmsSimpleProducer jmsProducer;
    private JmsSimpleProducer jmsProducerBeacon;
    
    /** Object that is used as a lock */
    private Object lock;
    
    /** Flag that indicates if this application should run */
    private boolean running;
    
    /** Flag that indicates if this application should restart */
    private boolean restart;
    
    /** This application name	*/
    private String thisApplicationName = "Syslog2Jms";
    
    private JmsHeartBeat jmsHeartBeat;
    
    /** Thread Executor	*/
    private ExecutorService executor;
    
    /** Beacon repetition Rate */
    private int beaconRepRate;
    
    /** Local Host Name */
    private String localHostName = "notDefined";
    

    public Syslog2JmsApplication() {
        jmsProducer = null;
        jmsProducerBeacon = null;
        lock = new Object();
        xmppService = null;
        running = true;
        restart = false;
        beaconRepRate = 100;
        
        try {
			final java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			localHostName = localMachine.getHostName();
		}
		catch (final java.net.UnknownHostException uhe) {
		}
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
    public Object start(IApplicationContext context) throws Exception {
		
		DatagramPacket  packet      = null;
        byte 			buffer[]	= null;

		
	    LOG.info( thisApplicationName + " is starting.");
	    	    
	    if ((initJms() == false) | (initJmsBeacon() == false)) {
	        LOG.error("JMS connection failed.");
	        LOG.error("Stopping {}", Activator.PLUGIN_ID);
	        return IApplication.EXIT_OK;
	    }
	    
	    //
		// check beacon timeout of connections to IOCs beaconTimeout
		//
	    IPreferencesService pref = Platform.getPreferencesService();
		final int beaconRepRate = pref.getInt(Activator.PLUGIN_ID,
	    		PreferenceConstants.BEACON_REP_RATE, 15000, null);
		this.beaconRepRate = beaconRepRate;
	    this.jmsHeartBeat = new JmsHeartBeat( beaconRepRate, jmsProducerBeacon, localHostName);  // mS
	    
	    
	    
	    // Tell the world that we are starting
	    MapMessage message = jmsProducer.createMapMessage();
	    if (message != null) {
	        message.setString("TYPE", "log");
	        message.setString("EVENTTIME", jmsProducer.getCurrentDateAsString());
	        message.setString("STATUS", "NO_ALARM");
	        message.setString("SEVERITY", "NO_ALARM");
	        message.setString("TEXT", "Syslog2Jms lebt...");
	        message.setString("APPLICATION-ID", "Syslog2Jms");
	        message.setString("HOST", localHostName);
	        
	        jmsProducer.sendMessage(message);
	    }
	    
        Restart.staticInject(this);
        Activator.getDefault().addSessionServiceListener(this);

	    // Before calling this, bring up all needed resources
	    context.applicationRunning();
	    
	    // open socket for syslog messages
	    DatagramSocket datagramSocket = createDatagramSocket();
	    
	    //
		// create thread pool using the Executor Service
		//
	    final int numberofReadThreads = pref.getInt(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.NUMBER_OF_THREADS, 100, null);
	    this.executor = Executors.newFixedThreadPool(numberofReadThreads);
	    
	    LOG.info("Start working...");
	    
		final int bufferSize = pref.getInt(Activator.PLUGIN_ID,
	    		PreferenceConstants.BUFFER_SIZE, 1024, null);
	    
	    while (running) {
/*	    	synchronized (lock) {
	    		continue;
	    	}
*/	    	
	    	try
            {
    			/*
    			 * always a 'fresh' buffer!
    			 * buffer can be overwritten if a new message arrives before we've copied the contents!!!
    			 */
    			buffer	=  new byte[ bufferSize];
                packet = new DatagramPacket( buffer, buffer.length);

                datagramSocket.setSoTimeout(15000);	// timeout of 15 seconds to allow remote management to stop this process
                datagramSocket.receive(packet);

                /*
                 * unpack the packet here!
                 * if we do this way down in the thread - it might be overwritten!!
                 */

                final String packetData = new String(packet.getData(), 0, packet.getLength());

                LOG.debug("Received packet: " + packetData);

/*
                ParseSyslogMessage newMessage = new ParseSyslogMessage( packetData, datagramSocket,
                		packet.getAddress(), packet.getPort(), packet.getLength());
*/
                
                ParseSyslogMessage newMessage = new ParseSyslogMessage( packetData, jmsProducer, packet.getAddress(), packet.getPort());

                /*
                 * execute runnable by thread pool executor
                 */
                this.executor.execute(newMessage);

                // increment statistics
                // numberOfMessagesCollector.incrementCount();
            }
//            catch(final IOException ioe) {
            	/*
            	 * If the quit flag is set, the exception occurs because the
            	 * socket was closed. That's ok. Otherwise, it is an actual
            	 * error and must be handled.
            	 */
//            	LOG.error( thisApplicationName, " IO Error in main loop", ioe);
//            	}
            catch (SocketTimeoutException e) {
            	// this is fine
            	// we come here every 15000 milliseconds to see whether the while loop shall be continued
            }
            catch (PortUnreachableException e) {
            	LOG.debug( thisApplicationName, " : port unreachable - STOP");
            	continue;
            }
            catch (IOException e) {
            	LOG.debug( thisApplicationName, " : I/O Exception");
            }
	    }
            
        LOG.debug( thisApplicationName, " : leaving main loop - to STOP");
	    
	    // stop beacon
	    jmsHeartBeat.setRunning(false);
	    /*
		 * wait for beacon thread to stop and send last STOP message
		 */
		try {
			Thread.sleep( beaconRepRate);

		} catch (final InterruptedException e) {
			// Ok, if interrupted it will take place more early
		}
	    
		// last chance to tell the world we#re down
		LOG.info( thisApplicationName, " : STOP");
		
	    if (jmsProducer != null) {
	        jmsProducer.closeAll();
	    }
	    
	    if (xmppService != null) {
	           synchronized (xmppService) {
	                try {
	                    xmppService.wait(500);
	                } catch (InterruptedException ie) {
	                    // Can be ignored
	                }
	            }

	        xmppService.disconnect();
	    }
	    
	    Integer exitCode = IApplication.EXIT_OK;
	    if (restart) {
	        exitCode = IApplication.EXIT_RESTART;
	        LOG.info("Restarting {}", Activator.PLUGIN_ID);
	    } else {
	        LOG.info("Stopping {}", Activator.PLUGIN_ID);
	    }
	    
		return exitCode;
	}
	
	protected DatagramSocket createDatagramSocket() throws SocketException, UnknownHostException {
		DatagramSocket datagramSocket = null;
		
		IPreferencesService pref = Platform.getPreferencesService();
		final int dataPortNumber = pref.getInt(Activator.PLUGIN_ID,
	    		PreferenceConstants.DATA_PORT_NUMBER, 514, null);
		
		try
        {
        	LOG.info( thisApplicationName + " trying to initialize UDP socket. Port: " + dataPortNumber);
        	datagramSocket = new DatagramSocket( dataPortNumber );
        }
        catch(final IOException ioe)
        {
        	System.out.println(thisApplicationName + " ** ERROR ** : Could not initialize UDP socket. Port: " + dataPortNumber);
        	System.out.println("\n" + thisApplicationName + " *** EXCEPTION *** : " + ioe.getMessage());
        	LOG.info( thisApplicationName, " Could not initialize UDP socket. Port: " + dataPortNumber);
        }
		return datagramSocket;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
    public void stop() {
	    running = false;
	    restart = false;
	    synchronized (lock) {
	        lock.notify();
		}
	}

    @Override
    public void stopWorking() {
        running = false;
        restart = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void setRestart() {
        running = false;
        restart = true;
        synchronized (lock) {
            lock.notify();
        }
    }

    private boolean initJms() {
        
        IPreferencesService pref = Platform.getPreferencesService();
        String url = pref.getString(Activator.PLUGIN_ID, PreferenceConstants.JMS_PRODUCER_URL, "", null);
        String factory = pref.getString(Activator.PLUGIN_ID, PreferenceConstants.JMS_PRODUCER_FACTORY, "", null);
        String topic = pref.getString(Activator.PLUGIN_ID, PreferenceConstants.JMS_PRODUCER_TOPIC_NAME, "", null);
        
        if ((url.length() * factory.length() * topic.length()) == 0) {
            LOG.error("The preferences do not contain a valid configuration for the JMS connection.");
            LOG.error("Stopping {}", Activator.PLUGIN_ID);
            return false;
        }
        
        LOG.info("Try to create a JMS producer with:");
        LOG.info(" URL           {}", url);
        LOG.info(" Factory class {}", factory);
        LOG.info(" TOPICS        {}", topic);
        
        jmsProducer = new JmsSimpleProducer("Syslog2JmsProducer", url, factory, topic);
        
        return jmsProducer.isConnected();
    }
    
private boolean initJmsBeacon() {
        
        IPreferencesService pref = Platform.getPreferencesService();
        String url = pref.getString(Activator.PLUGIN_ID, PreferenceConstants.JMS_PRODUCER_URL, "", null);
        String factory = pref.getString(Activator.PLUGIN_ID, PreferenceConstants.JMS_PRODUCER_FACTORY, "", null);
        String topic = pref.getString(Activator.PLUGIN_ID, PreferenceConstants.JMS_PRODUCER_TOPIC_NAME_BEACON, "", null);
        
        if ((url.length() * factory.length() * topic.length()) == 0) {
            LOG.error("The preferences do not contain a valid configuration for the JMS connection.");
            LOG.error("Stopping {}", Activator.PLUGIN_ID);
            return false;
        }
        
        LOG.info("Try to create a JMS producer with:");
        LOG.info(" URL           {}", url);
        LOG.info(" Factory class {}", factory);
        LOG.info(" TOPICS        {}", topic);
        
        jmsProducerBeacon = new JmsSimpleProducer("Syslog2JmsProducerBeacon", url, factory, topic);
        
        return jmsProducerBeacon.isConnected();
    }
    
    @Override
    public void bindService(ISessionService service) {
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String xmppUser = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_USER_NAME, "anonymous", null);
        String xmppPassword = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        String xmppServer = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krynfs.desy.de", null);

        try {
            service.connect(xmppUser, xmppPassword, xmppServer);
            xmppService = service;
        } catch (Exception e) {
            LOG.warn("XMPP connection is not available: {}", e.getMessage());
        }
    }

    @Override
    public void unbindService(ISessionService service) {
        // Nothing to do here
    }
}
