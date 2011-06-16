package org.csstudio.utility.casnooper;
/*
 * Copyright (c) 2004 by Cosylab
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file "LICENSE-CAJ". If the license is not included visit Cosylab web site,
 * <http://www.cosylab.com>.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */



import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.cas.ProcessVariableExistanceCallback;
import gov.aps.jca.cas.ProcessVariableExistanceCompletion;
import gov.aps.jca.cas.ServerContext;

import java.net.InetSocketAddress;

import org.csstudio.platform.statistic.Collector;
import org.csstudio.utility.casnooper.channel.ChannelCollector;
import org.csstudio.utility.casnooper.channel.NumberOfBroadcasts;
import org.csstudio.utility.casnooper.channel.NumberOfBroadcastsPerSecond;
import org.csstudio.utility.casnooper.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import com.cosylab.epics.caj.cas.util.examples.CounterProcessVariable;

/**
 * Simple snooper server.
 * @author <a href="mailto:matej.sekoranjaATcosylab.com">Matej Sekoranja</a>
 * @version $id$
 */
public class SnooperServer {

    private static final Logger LOG = LoggerFactory.getLogger(SnooperServer.class);
	private static SnooperServer thisSnooperServer = null;
	private static TimerProcessor timerProcessor = null;
	private volatile int broadcastCounter = 0; 
	private SnopperServerImpl server = null;
	private NumberOfBroadcasts numberOfBroadcastsChannel = null;
	private NumberOfBroadcastsPerSecond numberOfBroadcastsPerSecondChannel = null;
	private static String localHostName = null; 
	private static Collector caBroadcastCollector = null;
	
	public static SnooperServer getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( thisSnooperServer == null) {
			synchronized (SnooperServer.class) {
				if (thisSnooperServer == null) {
					thisSnooperServer = new SnooperServer();
				}
			}
		}
		return thisSnooperServer;
	}

	/**
	 * Implementation of the server.
	 * @author msekoranja
	 */
	class SnopperServerImpl extends com.cosylab.epics.caj.cas.util.DefaultServerImpl
	{
		
		/**
		 * Map of PVs.
		 */
//		protected Map pvs = new HashMap();
		

		
		/**
		 * @see gov.aps.jca.cas.Server#processVariableExistanceTest(java.lang.String, java.net.InetSocketAddress, gov.aps.jca.cas.ProcessVariableExistanceCallback)
		 */
		@Override
        public ProcessVariableExistanceCompletion processVariableExistanceTest(String aliasName, InetSocketAddress clientAddress,
																			   ProcessVariableExistanceCallback asyncCompletionCallback)
			throws CAException, IllegalArgumentException, IllegalStateException
		{
			/*
			 * do the snooper stuff
			 */
			
		    Object[] args = new Object[]{aliasName, clientAddress, getBroadcastCounter()};
			LOG.debug("Request for '{}' from client {} count {}", args);
//			System.out.println("Request for '" + aliasName + "' from client " + clientAddress + " count " + getBroadcastCounter());
			try {
				incrementBroadcastCounter();
				
				collector.addBMessage(aliasName, clientAddress);
			} catch (Exception e) {
				LOG.error("Error in existance test: ", e);
			}
			
			synchronized (pvs)
			{
				return pvs.containsKey(aliasName) ?
						ProcessVariableExistanceCompletion.EXISTS_HERE :
						ProcessVariableExistanceCompletion.DOES_NOT_EXIST_HERE;
			}
		}
		
	}

	/**
     * JCA server context.
     */
    private ServerContext context = null;
    
    /**
     * caSnooper message collector
     */
    private static ChannelCollector collector;
    
    /**
     * Initialize JCA context.
     * @throws CAException	throws on any failure.
     */
    private void initialize() throws CAException {
    	
        //get local host name
        createLocalHostName();
        
    	// initialize statistic collector
    	initializeStatisticCollectors();
        
		// Get the JCALibrary instance.
		JCALibrary jca = JCALibrary.getInstance();

		// Create server implmentation
		this.server = new SnopperServerImpl();
		
		// Create a context with default configuration values.
		context = jca.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, this.server);

		// Display basic information about the context.
        System.out.println(context.getVersion().getVersionString());
        context.printInfo(); System.out.println();
        
        // register process variables
		registerProcessVariables(this.server); 
    }
    
    private void initializeStatisticCollectors() {
    	
    	IPreferencesService prefs = Platform.getPreferencesService();
	    String connectionId = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.CONNECTION_CLIENT_ID, "", null);
        /*
         * set up collectors (statistic)
         */
        caBroadcastCollector = new Collector();
        caBroadcastCollector.setApplication(connectionId + "-" + getLocalHostName());
        caBroadcastCollector.setDescriptor("Channel Access Broadcasts");
        caBroadcastCollector.setContinuousPrint(false);
        caBroadcastCollector.setContinuousPrintCount(1000.0);
        caBroadcastCollector.getAlarmHandler().setDeadband(5.0);
        caBroadcastCollector.getAlarmHandler().setHighAbsoluteLimit(500.0);	// 500/sec
        caBroadcastCollector.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
    
    }
    
    private void createLocalHostName() {
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
    }
    
    /**
     * Register process variables.
     * @param server
     */
	private void registerProcessVariables(DefaultServerImpl server) {
		/*
		 * get host name of localcaSnooper
		 */
		String localHostName = "unknownCaSnooper";
		try {
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			localHostName = localMachine.getHostName();
		}
		catch (java.net.UnknownHostException ex) { 
		}
		
		System.out.println("localHostName: " + localHostName);

		// broadcast PV (int)
		numberOfBroadcastsChannel = new NumberOfBroadcasts(localHostName + ":broadcPerPeriod", null, 0, 10000, 1, 100000, -1, 1000, -1, 5000, this);
		this.server.registerProcessVaribale(numberOfBroadcastsChannel);
		System.out.println("Created channel: " + localHostName + ":broadcPerPeriod");
		
		// broadcast per second PV (double)
		short precision = 3;
		numberOfBroadcastsPerSecondChannel = new NumberOfBroadcastsPerSecond(localHostName + ":broadcPerSec", null, 0, 1000, precision, -1, 250, -1, 500, this);
		this.server.registerProcessVaribale(numberOfBroadcastsPerSecondChannel);
		System.out.println("Created channel: " + localHostName + ":broadcPerSec");
		
		// counter PV
		CounterProcessVariable counter = new CounterProcessVariable(localHostName + ":countsPerPeriod", null, -10, 10, 1, 100, -7, 7, -9, 9);
		server.registerProcessVaribale(counter);
		System.out.println("Created channel: " + localHostName + ":countsPerPeriod");
	}
 
    /**
     * Destroy JCA server  context.
     */
    public void destroy() {
        
        try {

            // Destroy the context, check if never initialized.
            if (context != null)
                context.destroy();
            
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
    
	/**
	 * @param channelName
	 */
	public void execute() {

		try {
			
			// initialize context
			initialize();
		    
			System.out.println("Running server...");
			LOG.info("Start caSnooper on: {}", localHostName);
			
			// start time based processor
			IPreferencesService prefs = Platform.getPreferencesService();
			timerProcessor = new TimerProcessor( this, 2000, Integer.parseInt(prefs.getString(Activator.getDefault().getPluginId(),
		    		PreferenceConstants.UPDATE_TIME, "", null)));
			
			System.out.println("Starting TimerProcessor...");

			// run server 
			context.run(0);
			
			System.out.println("Done.");
			LOG.info("Stop caSnooper on: {}", localHostName);

		} catch (Throwable th) {
			th.printStackTrace();
		}
		finally {
		    // always finalize
		    destroy();
		}

	}
	
	
//	/**
//	 * Program entry point. 
//	 * @param args	command-line arguments
//	 */
//	public static void main(String[] args) {
//		// execute
//		new SnooperServer().execute();
//	}

	synchronized public int getBroadcastCounter() {
		return broadcastCounter;
	}
	
	synchronized public int getBroadcastCounterAndZero() {
		int localBroadcastCounter = 0;
		localBroadcastCounter = broadcastCounter;
		broadcastCounter = 0;
		return localBroadcastCounter;
	}

	synchronized public void setBroadcastCounter(int broadcastCounter) {
		this.broadcastCounter = broadcastCounter;
	}
	
	synchronized public void incrementBroadcastCounter() {
		this.broadcastCounter++;
	}

	public SnopperServerImpl getServer() {
		return server;
	}

	public void setServer(SnopperServerImpl server) {
		this.server = server;
	}

	public NumberOfBroadcasts getNumberOfBroadcastsChannel() {
		return numberOfBroadcastsChannel;
	}

	public void setNumberOfBroadcastsChannel(NumberOfBroadcasts numberOfBroadcastsChannel) {
		this.numberOfBroadcastsChannel = numberOfBroadcastsChannel;
	}

	public NumberOfBroadcastsPerSecond getNumberOfBroadcastsPerSecondChannel() {
		return numberOfBroadcastsPerSecondChannel;
	}

	public void setNumberOfBroadcastsPerSecondChannel(
			NumberOfBroadcastsPerSecond numberOfBroadcastsPerSecondChannel) {
		this.numberOfBroadcastsPerSecondChannel = numberOfBroadcastsPerSecondChannel;
	}

	public static TimerProcessor getTimerProcessor() {
		return timerProcessor;
	}

	public static void setTimerProcessor(TimerProcessor newTimerProcessor) {
		timerProcessor = newTimerProcessor;
	}

	public static String getLocalHostName() {
		return localHostName;
	}

	public static void setLocalHostName(String localHostName) {
		SnooperServer.localHostName = localHostName;
	}

	public static Collector getCaBroadcastCollector() {
		return caBroadcastCollector;
	}

	public static void setCaBroadcastCollector(Collector caBroadcastCollector) {
		SnooperServer.caBroadcastCollector = caBroadcastCollector;
	}
	
	public void setListnere(ChannelCollector instance){
		collector = instance;
	}
	
}

