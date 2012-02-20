package org.csstudio.cagateway;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.cas.ProcessVariableExistanceCallback;
import gov.aps.jca.cas.ProcessVariableExistanceCompletion;
import gov.aps.jca.cas.ProcessVariableReadCallback;
import gov.aps.jca.cas.ProcessVariableWriteCallback;
import gov.aps.jca.cas.ServerContext;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBR_String;
import gov.aps.jca.dbr.DBR_TIME_String;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import java.net.InetSocketAddress;
import java.util.HashMap;

import org.csstudio.platform.statistic.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosylab.epics.caj.cas.handlers.AbstractCASResponseHandler;
import com.cosylab.epics.caj.cas.util.NumericProcessVariable;
import com.cosylab.epics.caj.cas.util.StringProcessVariable;
import com.cosylab.epics.caj.cas.util.examples.CounterProcessVariable;


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

	public static void setNumberOfServedChannelsCollector(final Collector numberOfServedChannels) {
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
					final String aliasName, final InetSocketAddress clientAddress,
					final ProcessVariableExistanceCallback asyncCompletionCallback) throws CAException,
					IllegalArgumentException, IllegalStateException {

//				System.out.println(aliasName +" ExistenceRequest");

				if(DoocsClient.getInstance().findChannelName(aliasName, availableRemoteDevices, thisGatewayServer) != null){
//					System.out.println(aliasName +" exists here");

					return ProcessVariableExistanceCompletion.EXISTS_HERE;

				} else {
                    // try local records
					synchronized (pvs)
					{
						return pvs.containsKey(aliasName) ?
								ProcessVariableExistanceCompletion.EXISTS_HERE :
								ProcessVariableExistanceCompletion.DOES_NOT_EXIST_HERE;
					}
                }
			}
		}

	    public final synchronized void stop() {
	        LOG.warn("caServer: stop() was called, stopping server");
	        try {
                context.shutdown();
            } catch (final IllegalStateException e) {
                LOG.warn("caServer shutdown, Illegal state exception: {}", e);
            } catch (final CAException e) {
                LOG.warn("caServer shutdown, CA exception: ", e);
            }
	        try {
                context.destroy();
            } catch (final IllegalStateException e) {
                LOG.warn("caServer shutdown, Illegal state exception: ", e);
            } catch (final CAException e) {
                LOG.warn("caServer shutdown, CA exception: ", e);
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
			final JCALibrary jca = JCALibrary.getInstance();

			// Create server implmentation
			this.setServer(new GatewayServerImpl());

			// Create a context with default configuration values.
			context = jca.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, this.getServer());

			// Display basic information about the context.
	        System.out.println(context.getVersion().getVersionString());
	        context.printInfo(); System.out.println();

	        // add channels manually
	        String newRecord = "CA:DOOCS:Gateway:alive";
	        final NumericProcessVariable myAliveRecord = new CounterProcessVariable(newRecord, null, 0, 7, 1, 1000,
	        		2, 5, 1, 6);
	        this.server.registerProcessVaribale(myAliveRecord);
	        LOG.info("Create Record " + newRecord +" on: ", localHostName);

	        newRecord = "CA:DOOCS:Gateway:Ramp";
	        final NumericProcessVariable myRampRecord = new CounterProcessVariable(newRecord, null, 0, 1000, 1, 500,
	        		200, 800, 100, 900);
	        this.server.registerProcessVaribale(myRampRecord);
	        LOG.info("Create Record " + newRecord +" on: ", localHostName);

	        newRecord = "CA:DOOCS:Gateway:TickTack";
	        final NumericProcessVariable myTickTackRecord = new CounterProcessVariable(newRecord, null, 0, 1, 1, 1000,
	        		-1, -1, -1, 0);
	        this.server.registerProcessVaribale(myTickTackRecord);
	        LOG.info("Create Record " + newRecord +" on: ", localHostName);


	        // translation for DOOCS channels
	        // write to DoocsName and see result in EpicsName
	        // write to EpicsName and see result in DoocsName

	        newRecord = "CA:DOOCS:Gateway:DoocsName";
	        final MyPV myDoocsNameRecord = new MyPV(newRecord, null, "Doocs");
	        this.server.registerProcessVaribale(myDoocsNameRecord);
	        LOG.info("Create Record " + newRecord +" on: ", localHostName);

	        newRecord = "CA:DOOCS:Gateway:EpicsName";
	        final MyPV myEpicsNameRecord = new MyPV(newRecord, null, "Epics");
	        this.server.registerProcessVaribale(myEpicsNameRecord);
	        LOG.info("Create Record " + newRecord +" on: ", localHostName);

	        myDoocsNameRecord.setPartner(myEpicsNameRecord);
	        myEpicsNameRecord.setPartner(myDoocsNameRecord);

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
				final java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
				setLocalHostName( localMachine.getHostName());
			}
			catch (final java.net.UnknownHostException uhe) {
			}
	    }

	    /**
	     * Destroy JCA server  context.
	     */
	    public void destroy() {

	        try {

	            // Destroy the context, check if never initialized.
	            if (context != null) {
                    context.destroy();
                }

	        } catch (final Throwable th) {
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

			} catch (final Throwable th) {
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

		public static void setLocalHostName(final String localHostName) {
			CaServer.localHostName = localHostName;
		}

		public void setServer(final GatewayServerImpl server) {
			this.server = server;
		}

		public GatewayServerImpl getServer() {
			return server;
		}

		public void addAvailableRemoteDevices ( final String channelName, final MetaData metaData) {
			availableRemoteDevices.put(channelName, metaData);
			getNumberOfServedChannelsCollector().incrementValue();
			return;
		}

		public void removeAvailableRemoteDevices ( final String channelName) {
			availableRemoteDevices.remove(channelName);
			getNumberOfServedChannelsCollector().decrementValue();
			return;
		}


		private static class MyPV extends StringProcessVariable {

        	private DBR_TIME_String timeDBR;
			private MyPV _partner;
			private String _newName;
			private final String _controlSystemName;
			private static DoocsClient _doocsClient;

			public MyPV(final String name, final ProcessVariableEventCallback eventCallback, final String controlSystemName) {
				super(name, eventCallback);
				_newName = controlSystemName + " undefined";
				_controlSystemName = controlSystemName;
				_doocsClient = DoocsClient.getInstance();
			}

			public void setPartner(final MyPV partner) {
				_partner = partner;
			}

			@Override
			public CAStatus writeValue(final DBR_String value,
					final ProcessVariableWriteCallback asyncWriteCallback) throws CAException {
				// TODO Auto-generated method stub
				final String newValue = (value).getStringValue()[0];
				String translatedName = "translated name";

				if ( ! _newName.equals(newValue)) {
					// dirty trick to avoid endless loop between readValue and writeValue
//					System.out.println("CA:DOOCS:Gateway:" + _controlSystemName + " Name (write)= " + newValue);

					_newName = newValue;

					// post event if there is an interest
					changeValueAndPostEvent (_newName );
					if (_controlSystemName.equals("Epics")) {
						translatedName = epics2DoocsTranslator(_newName);
					} else {
						translatedName = doocs2EpicsTranslator(_newName);
					}
					_partner.changeValueAndPostEvent ( translatedName);
//					if (interest)
//					{
//						// set event mask
//						int mask = Monitor.VALUE | Monitor.LOG;
//						// create and fill-in DBR
//						DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
//						((DBR_TIME_String)monitorDBR).getStringValue()[0] = newEpicsName;
//						fillInStatusAndTime((TIME)monitorDBR);
//						// port event
//			 	    	eventCallback.postEvent(mask, monitorDBR);
//					}
					return CAStatus.NORMAL;
				} else {
					return CAStatus.NORMAL;
				}
			}

			public void changeValueAndPostEvent (final String newString) {
				// post event if there is an interest
				if (interest)
				{
					// set event mask
					final int mask = Monitor.VALUE | Monitor.LOG;
					// create and fill-in DBR
					final DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
					((DBR_TIME_String)monitorDBR).getStringValue()[0] = newString;
					fillInStatusAndTime((TIME)monitorDBR);
					// port event
		 	    	eventCallback.postEvent(mask, monitorDBR);
				}
			}
			/**
			 * Fill-in status and time to DBR.
			 * @param timeDBR DBR to fill-in.
			 */
			protected void fillInStatusAndTime(final TIME timeDBR)
			{
				// set status and severity
				timeDBR.setStatus( 0);
				timeDBR.setSeverity(0);

				// set timestamp
				timeDBR.setTimeStamp( new TimeStamp());
			}

			@Override
            protected CAStatus readValue(final DBR_TIME_String value,
					final ProcessVariableReadCallback asyncReadCallback) throws CAException {
				// TODO Auto-generated method stub
				// it is always at least DBR_TIME_Int DBR
				timeDBR = value;

				// set status and time
				fillInStatusAndTime(timeDBR);

				// set scalar value
				timeDBR.getStringValue()[0] = _newName;

//				System.out.println("CA:DOOCS:Gateway:DoocsName (read)= " + _newName);
				// return read completion status
				return CAStatus.NORMAL;
			}

			public static String epics2DoocsTranslator ( final String epicsName) {
				String doocsName = "translated into DOOCS name";
				doocsName = _doocsClient.findChannelName(epicsName, null, null);
				if ( doocsName != null) {
					return doocsName;
				} else {
					return "no translation available";
				}
			}

			public static String doocs2EpicsTranslator ( String doocsName) {
				final String epicsName = "translated  into EPICS name";
				String doocsFacility;
				String doocsDevice;
				String doocsLocation;
				String doocsProperty;
				final DoocsClient _doocsClient;

				_doocsClient = DoocsClient.getInstance();
				// initial setting
				doocsFacility 	= "undefFacility"; //"?Facility?";
				doocsDevice 	= "undefDevice"; //"?Device?";
				doocsLocation 	= "undefLocation"; //"?Location?";
				doocsProperty 	= "undefProperty"; //"?Property?";

				if ( doocsName.startsWith("/")) {
					doocsName = doocsName.substring(1, doocsName.length());
				}
				final String[] words = doocsName.split("/");
				for ( int i=0;i < words.length;i++ ) {
					if (i == 0) {
						doocsFacility = words[0];
						if ((doocsFacility = _doocsClient.facilitiesReverse.get( doocsFacility)) == null) {
							doocsFacility = "undefFacility";
						}
					}
					if (i == 1) {
						doocsDevice = words[1];
						if ((doocsDevice = _doocsClient.devicesReverse.get( doocsDevice)) == null) {
							doocsDevice = "undefDevice";
						}
					}
					if (i == 2) {
						doocsLocation = words[2];
						if ((doocsLocation = _doocsClient.locationsReverse.get( doocsLocation)) == null) {
							doocsLocation = "undefLocation";
						}
					}
					if (i == 3) {
						doocsProperty = words[3];
						if ((doocsProperty = _doocsClient.propertiesReverse.get( doocsProperty)) == null) {
							doocsProperty = "undefProperty";
						}
					}
				}
				return doocsFacility + ":" + doocsDevice + ":" + doocsLocation + ":" + doocsProperty;
			}

		}

		public static void main(final String[] args) {
		}

		public static String epics2DoocsTranslator ( final String epicsName) {
			final String doocsName = "translated DOOCS name";
			return doocsName;
		}
}


