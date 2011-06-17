package org.csstudio.cagateway;



import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.cas.ProcessVariableExistanceCallback;
import gov.aps.jca.cas.ProcessVariableExistanceCompletion;
import gov.aps.jca.cas.ServerContext;

import java.net.InetSocketAddress;
import java.util.HashMap;

import org.csstudio.platform.statistic.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CaServer {
	
    private static final Logger LOG = LoggerFactory.getLogger(CaServer.class);
    
    /*
     * Flag indicating the remote stop command
     */
    private volatile boolean _stopped = false;
    
	private static Collector numberOfServedChannelsCollector = null;


	public static Collector getNumberOfServedChannelsCollector() {
		return numberOfServedChannelsCollector;
	}

	public static void setNumberOfServedChannelsCollector(Collector numberOfServedChannels) {
		CaServer.numberOfServedChannelsCollector = numberOfServedChannels;
	}

	private HashMap<String, MetaData>	availableRemoteDevices = null;
	
	public HashMap<String, MetaData> getAvailableRemoteDevices() {
		return availableRemoteDevices;
	}

	private GatewayServerImpl server = null;
	
	
	private static CaServer thisGatewayServer = null;
	private static String localHostName;
	
		private CaServer() {}
		
		public static CaServer getGatewayInstance(){
			//
			// get an instance of our sigleton
			//
			if ( thisGatewayServer == null) {
				synchronized (CaServer.class) {
					if (thisGatewayServer == null) {
						thisGatewayServer = new CaServer();
					}
				}
			}
			return thisGatewayServer;
		}
		
		/**
		 * Implementation of the server.
		 * @author msekoranja
		 */
		class GatewayServerImpl extends com.cosylab.epics.caj.cas.util.DefaultServerImpl
		{
	    	
//			public ProcessVariable processVariableAttach(String aliasName,
//					ProcessVariableEventCallback clientAddress,
//					ProcessVariableAttachCallback arg2) throws CAStatusException,
//					IllegalArgumentException, IllegalStateException {	
//				System.out.println(aliasName +" ProcessVariableRequest");
//				return new PV(aliasName, clientAddress, availableRemoteDevices);
//			}

			@Override
            public ProcessVariableExistanceCompletion processVariableExistanceTest(
					String aliasName, InetSocketAddress clientAddress,
					ProcessVariableExistanceCallback asyncCompletionCallback) throws CAException,
					IllegalArgumentException, IllegalStateException {
				
//				System.out.println(aliasName +" ExistenceRequest");
				
				if(DoocsClient.getInstance().findChannelName(aliasName, availableRemoteDevices, thisGatewayServer) != null){
//					System.out.println(aliasName +" exists here");
					
					return ProcessVariableExistanceCompletion.EXISTS_HERE;

				}
				else
//					System.out.println(aliasName +" doesn't exists here");
				
				return ProcessVariableExistanceCompletion.DOES_NOT_EXIST_HERE;	
			}	
		}
		
	    public final synchronized void stop() {
	        LOG.debug("caServer: stop() was called, stopping server");
	        try {
                context.shutdown();
            } catch (IllegalStateException e) {
                LOG.debug("caServer shutdown, Illegal state exception: {}", e);
            } catch (CAException e) {
                LOG.debug("caServer shutdown, CA exception: ", e);
            }
	        try {
                context.destroy();
            } catch (IllegalStateException e) {
                LOG.debug("caServer shutdown, Illegal state exception: ", e);
            } catch (CAException e) {
                LOG.debug("caServer shutdown, CA exception: ", e);
            }
	    }
		
		/**
	     * JCA server context.
	     */
	    private ServerContext context = null;
	    
	    /**
	     * Initialize JCA context.
	     * @throws CAException	throws on any failure.
	     */
	    private void initialize() throws CAException {
	    	
	    	//get local host name
	        createLocalHostName();
	        
	        // create HashMap for the channels we currently support in this instance
	        availableRemoteDevices = new HashMap<String, MetaData>();
	        
	    	// initialize statistic collector
	    	initializeStatisticCollectors();
	        
			// Get the JCALibrary instance.
			JCALibrary jca = JCALibrary.getInstance();

			// Create server implmentation
			this.setServer(new GatewayServerImpl());
			
			// Create a context with default configuration values.
			context = jca.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, this.getServer());

			// Display basic information about the context.
	        System.out.println(context.getVersion().getVersionString());
	        context.printInfo(); System.out.println();
	    }
	    
	    private void initializeStatisticCollectors() {
	    	
	        /*
	         * set up collectors (statistic)
	         */
	        numberOfServedChannelsCollector = new Collector();
	        numberOfServedChannelsCollector.setApplication("caGateway on " + getLocalHostName());
	        numberOfServedChannelsCollector.setDescriptor("Channel Access Gateway for DOOCS ...");
	        numberOfServedChannelsCollector.setContinuousPrint(false);
	        numberOfServedChannelsCollector.setContinuousPrintCount(1000.0);
	        numberOfServedChannelsCollector.getAlarmHandler().setDeadband(5.0);
	        numberOfServedChannelsCollector.getAlarmHandler().setHighAbsoluteLimit(500.0);	// # of channels
	        numberOfServedChannelsCollector.getAlarmHandler().setHighRelativeLimit(500.0);	// 
	    
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
			    
				System.out.println("Running gateway...");
				LOG.info("Start caGateway on: {}", localHostName);

				// run server 
				context.run(0);
			
				System.out.println("Done.");
				LOG.info("Stop caGateway on: {}", localHostName);

			} catch (Throwable th) {
				th.printStackTrace();
			}
			finally {
			    // always finalize
			    destroy();
			}

		}
		
		public static String getLocalHostName() {
			return localHostName;
		}
		
		public static void setLocalHostName(String localHostName) {
			CaServer.localHostName = localHostName;
		}

		public void setServer(GatewayServerImpl server) {
			this.server = server;
		}

		public GatewayServerImpl getServer() {
			return server;
		}
		
		public void addAvailableRemoteDevices ( String channelName, MetaData metaData) {
			availableRemoteDevices.put(channelName, metaData);
			getNumberOfServedChannelsCollector().incrementValue();
			return;
		}
		
		public void removeAvailableRemoteDevices ( String channelName) {
			availableRemoteDevices.remove(channelName);
			getNumberOfServedChannelsCollector().decrementValue();
			return;
		}
}		


