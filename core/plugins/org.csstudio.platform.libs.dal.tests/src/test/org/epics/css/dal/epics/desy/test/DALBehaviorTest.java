/**
 * 
 */
package org.epics.css.dal.epics.desy.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.epics.css.dal.CharacteristicInfo;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.epics.EPICSPlug;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.Plugs;
import org.epics.css.dal.spi.PropertyFactory;

import junit.framework.TestCase;

/**
 * These tests are used to demonstrate the desired behavior of DAL.
 *
 */
public class DALBehaviorTest extends TestCase {
	
	private static final long SLEEP_TIME_MSEC = 10000;
	private static final String CONSTANT_PV = "ConstantPV";
	private static final String DOES_NOT_EXIST_NAME = "alarmTest:DOES_NOT_EXIST";
	private static final String EPICS_DEMO1 = "EpicsDemo1";
	private static final String EPICS_DEMO2 = "EpicsDemo2";
	private static final String PV_AI_01 = "PV_AI_01";

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
	
	private SimpleDALBroker broker;
	
	// fields used with testUnexistingChannel
	private boolean testUnexistingChannelDataUpdateReply;
	private boolean testUnexistingChannelStateUpdateReply;
	private ConnectionState testUnexistingChannelLastConnectionStateChannel;
	private boolean testUnexistingChannelConditionChangeReply;
	private ConnectionState testUnexistingChannelLastConnectionStateProperty;
	private boolean testUnexistingChannelErrorResponseReply;
	private boolean testUnexistingChannelErrorResponseIsError;
	private boolean testUnexistingChannelTimelagStartsReply;
	private boolean testUnexistingChannelTimelagStopsReply;
	private boolean testUnexistingChannelTimeoutStartsReply;
	private boolean testUnexistingChannelTimeoutStopsReply;
	private boolean testUnexistingChannelValueChangedReply;
	private boolean testUnexistingChannelValueUpdatedReply;
	
	// fields used with testStatusCharacteristics
	private boolean testStatusCharacteristicsDataUpdateReply;
	private boolean testStatusCharacteristicsStatusFailed;
	private boolean testStatusCharacteristicsSeverityFailed;
	private Object severity;
	private Object status;
	
	// fields used with testConnectReconnect
	private boolean testConnectReconnectStateUpdateReply1;
	private boolean testConnectReconnectDataUpdateReply1;
	private boolean testConnectReconnectStateUpdateReply2;
	private boolean testConnectReconnectDataUpdateReply2;
	private boolean testConnectReconnectStateUpdateReply3;
	private boolean testConnectReconnectDataUpdateReply3;
	private boolean testConnectReconnectStateUpdateHasValue1;
	private boolean testConnectReconnectStateUpdateHasValue2;
	private boolean testConnectReconnectStateUpdateHasValue3;
	private AnyData testConnectReconnectStateUpdateReplyData1;
	private AnyData testConnectReconnectStateUpdateReplyData2;
	private AnyData testConnectReconnectStateUpdateReplyData3;
	private boolean testConnectReconnectHasValueFailedValue1;
	private boolean testConnectReconnectHasValueFailedValue2;
	private boolean testConnectReconnectHasValueFailedValue3;
	private boolean testConnectReconnectHasValueFailedNoValue1;
	private boolean testConnectReconnectHasValueFailedNoValue2;
	private boolean testConnectReconnectHasValueFailedNoValue3;
	
	// fields used with testConnectReconnectDAL
	private boolean testConnectReconnectDALConditionChangeReply1;
	private boolean testConnectReconnectDALValueUpdateReply1;
	private boolean testConnectReconnectDALConditionChangeReply2;
	private boolean testConnectReconnectDALValueUpdateReply2;
	private boolean testConnectReconnectDALConditionChangeReply3;
	private boolean testConnectReconnectDALValueUpdateReply3;
	private boolean testConnectReconnectDALConditionChangeReplyHasValue1;
	private boolean testConnectReconnectDALConditionChangeReplyHasValue2;
	private boolean testConnectReconnectDALConditionChangeReplyHasValue3;
	
	// fields used with testConnectionInitialization
	private ConnectionState testConnectionInitializationStateUpdateLastConnectionState;
	private boolean testConnectionInitializationStateUpdateHasValue;
	private boolean testConnectionInitializationStateUpdateHasMetadata;
	private ConnectionState testConnectionInitializationConditionChangeLastConnectionState;
	private AnyData testConnectionInitializationAnyData;
	private boolean testConnectionInitializationErrorResponseReply;
	private boolean testConnectionInitializationTimelagStartsReply;
	private boolean testConnectionInitializationTimelagStopsReply;
	private boolean testConnectionInitializationTimeoutStartsReply;
	private boolean testConnectionInitializationTimeoutStopsReply;
	private boolean testConnectionInitializationValueChangedReply;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		
		// simulator plug
		System.setProperty(Plugs.PLUGS, SimulatorPlug.PLUG_TYPE);
		System.setProperty(Plugs.PLUGS_DEFAULT, SimulatorPlug.PLUG_TYPE);
		System.setProperty(Plugs.PLUG_PROPERTY_FACTORY_CLASS + SimulatorPlug.PLUG_TYPE,
		    PropertyFactoryImpl.class.getName());
		
		// EPICS plug
		System.setProperty(Plugs.PLUGS, EPICSPlug.PLUG_TYPE);
		System.setProperty(Plugs.PLUGS_DEFAULT, EPICSPlug.PLUG_TYPE);
		System.setProperty(Plugs.PLUG_PROPERTY_FACTORY_CLASS + EPICSPlug.PLUG_TYPE,
				org.epics.css.dal.epics.PropertyFactoryImpl.class.getName());
		
		broker = SimpleDALBroker.getInstance();
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Testing connecing to unexisting EPICS channel.
	 * 
	 * @throws Exception
	 */
	public void testUnexistingChannel() throws Exception {
		System.out.println(">>> testUnexistingChannel: started at "+FORMAT.format(new Date()));
		
		testUnexistingChannelDataUpdateReply = false;
		testUnexistingChannelStateUpdateReply = false;
		testUnexistingChannelLastConnectionStateChannel = null;
		testUnexistingChannelLastConnectionStateProperty = null;
		testUnexistingChannelConditionChangeReply = false;
		testUnexistingChannelErrorResponseReply = false;
		testUnexistingChannelErrorResponseIsError = false;
		testUnexistingChannelTimelagStartsReply = false;
		testUnexistingChannelTimelagStopsReply = false;
		testUnexistingChannelTimeoutStartsReply = false;
		testUnexistingChannelTimeoutStopsReply = false;
		testUnexistingChannelValueChangedReply = false;
		testUnexistingChannelValueUpdatedReply = false;
		
		ConnectionParameters cp = newConnectionParameters(DOES_NOT_EXIST_NAME, null, String.class);
		
		broker.registerListener(cp,
                          new ChannelListener() {

							public void channelDataUpdate(AnyDataChannel channel) {
								System.out.println("--> channelDataUpdate at "+FORMAT.format(new Date()));
								/*
								 * No update should be fired here.
								 */
								testUnexistingChannelDataUpdateReply = true;
							}

							public void channelStateUpdate(AnyDataChannel channel) {
								System.out.println("--> channelStateUpdate at "+FORMAT.format(new Date()));
								/*
								 * May be more updates, but last should tell that status is CONNECTION_FAILED.
								 */
								testUnexistingChannelStateUpdateReply = true;
								testUnexistingChannelLastConnectionStateChannel = channel.getProperty().getConnectionState();
							}});
		
		broker.registerListener(cp, new DynamicValueAdapter() {

			public void conditionChange(DynamicValueEvent event) {
				System.out.println("++> conditionChange at "+FORMAT.format(new Date()));
				/*
				 * May be more updates, but last should tell that status is CONNECTION_FAILED.
				 */
				testUnexistingChannelConditionChangeReply = true;
				testUnexistingChannelLastConnectionStateProperty = ((DynamicValueProperty)event.getProperty()).getConnectionState();
			}

			public void errorResponse(DynamicValueEvent event) {
				System.out.println("++> errorResponse at "+FORMAT.format(new Date()));
				/*
				 * There should be an error.
				 */
				testUnexistingChannelErrorResponseReply = true;
				testUnexistingChannelErrorResponseIsError = event.getCondition().isError();
			}

			public void timelagStarts(DynamicValueEvent event) {
				System.out.println("++> timelagStarts at "+FORMAT.format(new Date()));
				/*
				 * No update should be fired here.
				 */
				testUnexistingChannelTimelagStartsReply = true;
			}

			public void timelagStops(DynamicValueEvent event) {
				System.out.println("++> timelagStops at "+FORMAT.format(new Date()));
				/*
				 * No update should be fired here.
				 */
				testUnexistingChannelTimelagStopsReply = true;
			}

			public void timeoutStarts(DynamicValueEvent event) {
				System.out.println("++> timeoutStarts at "+FORMAT.format(new Date()));
				/*
				 * No update should be fired here.
				 */
				testUnexistingChannelTimeoutStartsReply = true;
			}

			public void timeoutStops(DynamicValueEvent event) {
				System.out.println("++> timeoutStops at "+FORMAT.format(new Date()));
				/*
				 * No update should be fired here.
				 */
				testUnexistingChannelTimeoutStopsReply = true;
			}

			public void valueChanged(DynamicValueEvent event) {
				System.out.println("++> valueChanged at "+FORMAT.format(new Date()));
				/*
				 * No update should be fired here.
				 */
				testUnexistingChannelValueChangedReply = true;
			}

			public void valueUpdated(DynamicValueEvent event) {
				System.out.println("++> valueUpdated at "+FORMAT.format(new Date()));
				/*
				 * No update should be fired here.
				 */
				testUnexistingChannelValueUpdatedReply = true;
			}
			
		});
		
		System.out.println(">>> testUnexistingChannel: waitnig");
		Thread.sleep(4*SLEEP_TIME_MSEC);
		
		assertTrue(testUnexistingChannelStateUpdateReply);
		assertEquals(ConnectionState.CONNECTION_FAILED, testUnexistingChannelLastConnectionStateChannel);
		assertFalse(testUnexistingChannelDataUpdateReply);
		assertTrue(testUnexistingChannelConditionChangeReply);
		assertEquals(ConnectionState.CONNECTION_FAILED, testUnexistingChannelLastConnectionStateProperty);
		assertTrue(testUnexistingChannelErrorResponseReply);
		assertTrue(testUnexistingChannelErrorResponseIsError);
		assertFalse(testUnexistingChannelTimelagStartsReply);
		assertFalse(testUnexistingChannelTimelagStopsReply);
		assertFalse(testUnexistingChannelTimeoutStartsReply);
		assertFalse(testUnexistingChannelTimeoutStopsReply);
		assertFalse(testUnexistingChannelValueChangedReply);
		assertFalse(testUnexistingChannelValueUpdatedReply);
		
		System.out.println(">>> testUnexistingChannel: finishing at "+FORMAT.format(new Date())+"\n");
	}

	/**
	 * Testing STATUS characteristics.
	 * 
	 * @throws Exception
	 */
	public void testStatusCharacteristics() throws Exception {
		System.out.println(">>> testStatusCharacteristics: started at "+FORMAT.format(new Date()));

		testStatusCharacteristicsDataUpdateReply = false;
		testStatusCharacteristicsSeverityFailed = false;
		testStatusCharacteristicsStatusFailed = false;
		severity = null;
		status = null;
		
		broker.registerListener(newConnectionParameters(CONSTANT_PV, null, String.class),
				new ChannelListener() {

			public void channelDataUpdate(AnyDataChannel channel) {
				System.out.println("--> channelDataUpdate at "+FORMAT.format(new Date()));
				/*
				 * May be more updates, but last should contain C_SEVERITY and C_STATUS characteristics.
				 */
				testStatusCharacteristicsDataUpdateReply = true;
				
				try {
					status = channel.getProperty().getCharacteristic(CharacteristicInfo.C_STATUS.getName());
					testStatusCharacteristicsStatusFailed = false;
				} catch (DataExchangeException e) {
					testStatusCharacteristicsStatusFailed = true;
				}
				try {
					severity = channel.getProperty().getCharacteristic(CharacteristicInfo.C_SEVERITY.getName());
					testStatusCharacteristicsSeverityFailed = false;
				} catch (DataExchangeException e) {
					testStatusCharacteristicsSeverityFailed = true;
				}
			}

			public void channelStateUpdate(AnyDataChannel channel) {
				System.out.println("--> channelStateUpdate at "+FORMAT.format(new Date()));
			}});

		System.out.println(">>> testStatusCharacteristics: waitnig");
		Thread.sleep(2*SLEEP_TIME_MSEC);
		
		System.out.println(">>> testStatusCharacterustucs: severity = "+severity+", status = "+status);
		assertTrue(testStatusCharacteristicsDataUpdateReply);
		assertFalse(testStatusCharacteristicsSeverityFailed);
		assertNotNull(severity);
		assertFalse(testStatusCharacteristicsStatusFailed);
		assertNotNull(status);
		
		System.out.println(">>> testStatusCharacteristics: finishing at "+FORMAT.format(new Date())+"\n");
	}

	/**
	 * Testing if newly connected or reconnected channels get right events at beginning.
	 * Following is checked:
	 *  - when connecting some condition changes my be fired signaling following status transitions: INITIAL, CONNECTING, CONNECTED
	 *  - then first value comes: DynamicValueCondition returns hasValue() true (this is true if ConnectionState.CONNECTED, latest received 
	 *  value is different from null and there is no timeout). 
	 *  - value update is fired.
	 *  
	 * If  channel listener is registered to already established and working connection, then following two events are fired:
	 *  - channelStateUpdated. 
	 *  - channelDataupdated.
	 * 
	 * @throws Exception
	 */
	public void testConnectReconnect() throws Exception {
		System.out.println(">>> testConnectReconnect: started at "+FORMAT.format(new Date()));
		
		testConnectReconnectStateUpdateReply1 = false;
		testConnectReconnectDataUpdateReply1 = false;
		testConnectReconnectStateUpdateReply2 = false;
		testConnectReconnectDataUpdateReply2 = false;
		testConnectReconnectStateUpdateReply3 = false;
		testConnectReconnectDataUpdateReply3 = false;
		testConnectReconnectStateUpdateHasValue1 = false;
		testConnectReconnectStateUpdateHasValue2 = false;
		testConnectReconnectStateUpdateHasValue3 = false;
		testConnectReconnectStateUpdateReplyData1 = null;
		testConnectReconnectStateUpdateReplyData2 = null;
		testConnectReconnectStateUpdateReplyData3 = null;
		testConnectReconnectHasValueFailedValue1 = false;
		testConnectReconnectHasValueFailedValue2 = false;
		testConnectReconnectHasValueFailedValue3 = false;
		testConnectReconnectHasValueFailedNoValue1 = false;
		testConnectReconnectHasValueFailedNoValue2 = false;
		testConnectReconnectHasValueFailedNoValue3 = false;
		
		ConnectionParameters cp = newConnectionParameters(EPICS_DEMO1, null, String.class);
		ChannelListener cl1 = new ChannelListener() {

			public void channelDataUpdate(AnyDataChannel channel) {
				System.out.println("--> channelDataUpdate(1) at "+FORMAT.format(new Date()));
				testConnectReconnectDataUpdateReply1 = true;
			}

			public void channelStateUpdate(AnyDataChannel channel) {
				System.out.println("--> channelStateUpdate(1) at "+FORMAT.format(new Date()));
				testConnectReconnectStateUpdateReply1 = true;
				/*
				 * May be more updates, but the last one has to have a value (DynamicValueCondition.hasValue() == true). 
				 */
				testConnectReconnectStateUpdateHasValue1 = false;
				
				DynamicValueProperty prop = channel.getProperty();
				boolean hasValue = prop.getCondition().hasValue();
				System.out.println("--> testConnectReconnect: channelStateUpdate(1): property.hasValue = "+hasValue);
				if  (hasValue) {
					testConnectReconnectStateUpdateHasValue1 = true;
					testConnectReconnectStateUpdateReplyData1 = channel.getData();
					if (prop.getConnectionState() != ConnectionState.CONNECTED || prop.getCondition().containsAllStates(DynamicValueState.NO_VALUE) || prop.isTimeout()) {
						System.out.println("--> testConnectReconnect: channelStateUpdate(1): connectionState = "+prop.getConnectionState()+", contains NO_VALUE = "+prop.getCondition().containsAllStates(DynamicValueState.NO_VALUE)+", isTimeout = "+prop.isTimeout());
						testConnectReconnectHasValueFailedValue1 = true;
					}
				} else {
					System.out.println("--> testConnectReconnect: channelStateUpdate(1): connectionState = "+prop.getConnectionState()+", contains NO_VALUE = "+prop.getCondition().containsAllStates(DynamicValueState.NO_VALUE)+", isTimeout = "+prop.isTimeout());
					if (prop.getConnectionState() == ConnectionState.CONNECTED && prop.getLatestReceivedValue() != null && !prop.isTimeout()) {
						testConnectReconnectHasValueFailedNoValue1 = true;
					}
				}
			}
		};
		
		broker.registerListener(cp, cl1);
		
		System.out.println(">>> testConnectReconnect: waitinig");
		Thread.sleep(SLEEP_TIME_MSEC);
		
		broker.deregisterListener(cp, cl1);
		
		assertTrue(testConnectReconnectDataUpdateReply1);
		assertTrue(testConnectReconnectStateUpdateReply1);
		assertTrue(testConnectReconnectStateUpdateHasValue1);
		assertNotNull(testConnectReconnectStateUpdateReplyData1);
		assertFalse(testConnectReconnectHasValueFailedValue1);
		assertFalse(testConnectReconnectHasValueFailedNoValue1);
		
		ChannelListener cl2 = new ChannelListener() {

			public void channelDataUpdate(AnyDataChannel channel) {
				System.out.println("--> channelDataUpdate(2) at "+FORMAT.format(new Date()));
				testConnectReconnectDataUpdateReply2 = true;
			}

			public void channelStateUpdate(AnyDataChannel channel) {
				System.out.println("--> channelStateUpdate(2) at "+FORMAT.format(new Date()));
				testConnectReconnectStateUpdateReply2 = true;
				/*
				 * May be more updates, but the last one has to have a value (DynamicValueCondition.hasValue() == true). 
				 */
				testConnectReconnectStateUpdateHasValue2 = false;
				
				DynamicValueProperty prop = channel.getProperty();
				boolean hasValue = prop.getCondition().hasValue();
				System.out.println("--> testConnectReconnect: channelStateUpdate(2): property.hasValue = "+hasValue);
				if  (hasValue) {
					testConnectReconnectStateUpdateHasValue2 = true;
					testConnectReconnectStateUpdateReplyData2 = channel.getData();
					if (prop.getConnectionState() != ConnectionState.CONNECTED || prop.getCondition().containsAllStates(DynamicValueState.NO_VALUE) || prop.isTimeout()) {
						System.out.println("--> testConnectReconnect: channelStateUpdate(2): connectionState = "+prop.getConnectionState()+", contains NO_VALUE = "+prop.getCondition().containsAllStates(DynamicValueState.NO_VALUE)+", isTimeout = "+prop.isTimeout());
						testConnectReconnectHasValueFailedValue2 = true;
					}
				} else {
					System.out.println("--> testConnectReconnect: channelStateUpdate(2): connectionState = "+prop.getConnectionState()+", contains NO_VALUE = "+prop.getCondition().containsAllStates(DynamicValueState.NO_VALUE)+", isTimeout = "+prop.isTimeout());
					if (prop.getConnectionState() == ConnectionState.CONNECTED && prop.getLatestReceivedValue() != null && !prop.isTimeout()) {
						testConnectReconnectHasValueFailedNoValue2 = true;
					}
				}
			}
		};
		
		broker.registerListener(cp, cl2);
		
		System.out.println(">>> testConnectReconnect: waitnig");
		Thread.sleep(SLEEP_TIME_MSEC);
		
		broker.deregisterListener(cp, cl2);
		
		assertTrue(testConnectReconnectDataUpdateReply2);
		assertTrue(testConnectReconnectStateUpdateReply2);
		assertTrue(testConnectReconnectStateUpdateHasValue2);
		assertNotNull(testConnectReconnectStateUpdateReplyData2);
		assertFalse(testConnectReconnectHasValueFailedValue2);
		assertFalse(testConnectReconnectHasValueFailedNoValue2);
		
		System.out.println(">>> testConnectReconnect: waitnig for 80 seconds");
		Thread.sleep(80000);
		
		ChannelListener cl3 = new ChannelListener() {

			public void channelDataUpdate(AnyDataChannel channel) {
				System.out.println("--> channelDataUpdate(3) at "+FORMAT.format(new Date()));
				testConnectReconnectDataUpdateReply3 = true;
			}

			public void channelStateUpdate(AnyDataChannel channel) {
				System.out.println("--> channelStateUpdate(3) at "+FORMAT.format(new Date()));
				testConnectReconnectStateUpdateReply3 = true;
				/*
				 * May be more updates, but the last one has to have a value (DynamicValueCondition.hasValue() == true). 
				 */
				testConnectReconnectStateUpdateHasValue3 = false;
				
				DynamicValueProperty prop = channel.getProperty();
				boolean hasValue = prop.getCondition().hasValue();
				System.out.println("--> testConnectReconnect: channelStateUpdate(3): property.hasValue = "+hasValue);
				if  (hasValue) {
					testConnectReconnectStateUpdateHasValue3 = true;
					testConnectReconnectStateUpdateReplyData3 = channel.getData();
					if (prop.getConnectionState() != ConnectionState.CONNECTED || prop.getCondition().containsAllStates(DynamicValueState.NO_VALUE) || prop.isTimeout()) {
						System.out.println("--> testConnectReconnect: channelStateUpdate(3): connectionState = "+prop.getConnectionState()+", contains NO_VALUE = "+prop.getCondition().containsAllStates(DynamicValueState.NO_VALUE)+", isTimeout = "+prop.isTimeout());
						testConnectReconnectHasValueFailedValue3 = true;
					}
				} else {
					System.out.println("--> testConnectReconnect: channelStateUpdate(3): connectionState = "+prop.getConnectionState()+", contains NO_VALUE = "+prop.getCondition().containsAllStates(DynamicValueState.NO_VALUE)+", isTimeout = "+prop.isTimeout());
					if (prop.getConnectionState() == ConnectionState.CONNECTED && prop.getLatestReceivedValue() != null && !prop.isTimeout()) {
						testConnectReconnectHasValueFailedNoValue3 = true;
					}
				}
			}
		};
		
		broker.registerListener(cp, cl3);
		
		System.out.println(">>> testConnectReconnect: waitnig");
		Thread.sleep(SLEEP_TIME_MSEC);
		
		broker.deregisterListener(cp, cl3);
		
		assertTrue(testConnectReconnectDataUpdateReply3);
		assertTrue(testConnectReconnectStateUpdateReply3);
		assertTrue(testConnectReconnectStateUpdateHasValue3);
		assertNotNull(testConnectReconnectStateUpdateReplyData3);
		assertFalse(testConnectReconnectHasValueFailedValue3);
		assertFalse(testConnectReconnectHasValueFailedNoValue3);
		
		System.out.println(">>> testConnectReconnect: finishing at "+FORMAT.format(new Date())+"\n");

	}
	
	/**
	 * Testing reconnect on level of DAL, no DALBroker.
	 * 
	 * @see DALBehaviorTest#testConnectReconnect()
	 * 
	 * @throws Exception
	 */
	public void testConnectReconnectDAL() throws Exception {
		System.out.println(">>> testConnectReconnectDAL: started at "+FORMAT.format(new Date()));
		
		DefaultApplicationContext ctx = new DefaultApplicationContext("DALBehaviorTest");
		PropertyFactory pfac = DefaultPropertyFactoryService.getPropertyFactoryService()
		.getPropertyFactory(ctx, LinkPolicy.ASYNC_LINK_POLICY);
		
		testConnectReconnectDALConditionChangeReply1 = false;
		testConnectReconnectDALValueUpdateReply1 = false;
		testConnectReconnectDALConditionChangeReply2 = false;
		testConnectReconnectDALValueUpdateReply2 = false;
		testConnectReconnectDALConditionChangeReply3 = false;
		testConnectReconnectDALValueUpdateReply3 = false;
		testConnectReconnectDALConditionChangeReplyHasValue1 = false;
		testConnectReconnectDALConditionChangeReplyHasValue2 = false;
		testConnectReconnectDALConditionChangeReplyHasValue3 = false;
		
		ConnectionParameters cp = newConnectionParameters(EPICS_DEMO2, null, String.class);
		
		DynamicValueProperty prop1 = pfac.getProperty(cp.getRemoteInfo(), cp.getConnectionType().getDALType(), null);
		
		DynamicValueListener dvl1 = new DynamicValueAdapter() {

			public void conditionChange(DynamicValueEvent event) {
				System.out.println("--> conditionChange(1) at "+FORMAT.format(new Date()));
				testConnectReconnectDALConditionChangeReply1 = true;
				testConnectReconnectDALConditionChangeReplyHasValue1 = event.getCondition().hasValue();
			}

			public void valueChanged(DynamicValueEvent event) {
				System.out.println("--> valueChanged(1) at "+FORMAT.format(new Date()));
				testConnectReconnectDALValueUpdateReply1 = true;
			}

			public void valueUpdated(DynamicValueEvent event) {
				System.out.println("--> valueUpdated(1) at "+FORMAT.format(new Date()));
				testConnectReconnectDALValueUpdateReply1 = true;
			}
			
		};
		
		prop1.addDynamicValueListener(dvl1);
		
		System.out.println(">>> testConnectReconnectDAL: waitinig");
		Thread.sleep(SLEEP_TIME_MSEC);
		
		prop1.removeDynamicValueListener(dvl1);
		
		assertTrue(testConnectReconnectDALValueUpdateReply1);
		assertTrue(testConnectReconnectDALConditionChangeReply1);
		assertTrue(testConnectReconnectDALConditionChangeReplyHasValue1);
		
		DynamicValueListener dvl2 = new DynamicValueAdapter() {

			public void conditionChange(DynamicValueEvent event) {
				System.out.println("--> conditionChange(2) at "+FORMAT.format(new Date()));
				testConnectReconnectDALConditionChangeReply2 = true;
				testConnectReconnectDALConditionChangeReplyHasValue2 = event.getCondition().hasValue();
			}

			public void valueChanged(DynamicValueEvent event) {
				System.out.println("--> valueChanged(2) at "+FORMAT.format(new Date()));
				testConnectReconnectDALValueUpdateReply2 = true;
			}

			public void valueUpdated(DynamicValueEvent event) {
				System.out.println("--> valueUpdated(2) at "+FORMAT.format(new Date()));
				testConnectReconnectDALValueUpdateReply2 = true;
			}
			
		};
		
		prop1.addDynamicValueListener(dvl2);
		
		System.out.println(">>> testConnectReconnectDAL: waitnig");
		Thread.sleep(SLEEP_TIME_MSEC);
		
		prop1.removeDynamicValueListener(dvl2);
		
		assertTrue(testConnectReconnectDALValueUpdateReply2);
		assertTrue(testConnectReconnectDALConditionChangeReply2);
		assertTrue(testConnectReconnectDALConditionChangeReplyHasValue2);
		
		pfac.getPropertyFamily().destroy(prop1);
		
		System.out.println(">>> testConnectReconnectDAL: waitnig for 20 seconds");
		Thread.sleep(20000);
		
		DynamicValueProperty prop3 = pfac.getProperty(cp.getRemoteInfo(), cp.getConnectionType().getDALType(), null);
		
		DynamicValueListener dvl3 = new DynamicValueAdapter() {

			public void conditionChange(DynamicValueEvent event) {
				System.out.println("--> conditionChange(3) at "+FORMAT.format(new Date()));
				testConnectReconnectDALConditionChangeReply3 = true;
				testConnectReconnectDALConditionChangeReplyHasValue3 = event.getCondition().hasValue();
			}

			public void valueChanged(DynamicValueEvent event) {
				System.out.println("--> valueChanged(3) at "+FORMAT.format(new Date()));
				testConnectReconnectDALValueUpdateReply3 = true;
			}

			public void valueUpdated(DynamicValueEvent event) {
				System.out.println("--> valueUpdated(3) at "+FORMAT.format(new Date()));
				testConnectReconnectDALValueUpdateReply3 = true;
			}
			
		};		
		
		prop3.addDynamicValueListener(dvl3);
		
		System.out.println(">>> testConnectReconnectDAL: waitnig");
		Thread.sleep(SLEEP_TIME_MSEC);
		
		prop3.removeDynamicValueListener(dvl3);
		
		assertTrue(testConnectReconnectDALValueUpdateReply3);
		assertTrue(testConnectReconnectDALConditionChangeReply3);
		assertTrue(testConnectReconnectDALConditionChangeReplyHasValue3);
		
		System.out.println(">>> testConnectReconnectDAL: finishing at "+FORMAT.format(new Date())+"\n");
	}
	
	/**
	 * Test demonstrates the sequence of events during initialization of connection.
	 * 
	 * @throws Exception
	 */
	public void testConnectionInitialization() throws Exception {
		System.out.println(">>> testConnectionInitialization: started at "+FORMAT.format(new Date()));

		testConnectionInitializationAnyData = null;
		
		testConnectionInitializationStateUpdateHasValue = false;
		testConnectionInitializationStateUpdateHasMetadata = false;
		testConnectionInitializationStateUpdateLastConnectionState = null;
		testConnectionInitializationConditionChangeLastConnectionState = null;
		
		testConnectionInitializationErrorResponseReply = false;
		testConnectionInitializationTimelagStartsReply = false;
		testConnectionInitializationTimelagStopsReply = false;
		testConnectionInitializationTimeoutStartsReply = false;
		testConnectionInitializationTimeoutStopsReply = false;
		testConnectionInitializationValueChangedReply = false;
		
		ConnectionParameters cp = newConnectionParameters(PV_AI_01, null, Double.class); 
		
		broker.registerListener(cp,
				new ChannelListener() {

			public void channelDataUpdate(AnyDataChannel channel) {
				System.out.println("--> channelDataUpdate at "+FORMAT.format(new Date()));
			}

			public void channelStateUpdate(AnyDataChannel channel) {
				System.out.println("--> channelStateUpdate at "+FORMAT.format(new Date()));
				testConnectionInitializationStateUpdateLastConnectionState = channel.getProperty().getConnectionState();
				testConnectionInitializationAnyData = channel.getData();
				testConnectionInitializationStateUpdateHasValue = channel.getProperty().getCondition().hasValue();
				testConnectionInitializationStateUpdateHasMetadata = channel.isMetaDataInitialized();
				if (testConnectionInitializationStateUpdateHasValue && testConnectionInitializationStateUpdateHasMetadata) {
					System.out.println("... AnyData valid = "+testConnectionInitializationAnyData.isValid());
					System.out.println("... severity = "+testConnectionInitializationAnyData.getSeverity().getSeverityInfo());
					System.out.println("... status = "+testConnectionInitializationAnyData.getStatus());
					System.out.println("... value = "+testConnectionInitializationAnyData.anyValue());
					System.out.println("... timestamp = "+testConnectionInitializationAnyData.getTimestamp());
					System.out.println("... display limits = ("+testConnectionInitializationAnyData.getMetaData().getDisplayLow()+", "+testConnectionInitializationAnyData.getMetaData().getDisplayHigh()+")");
					System.out.println("... alarm limits = ("+testConnectionInitializationAnyData.getMetaData().getAlarmLow()+", "+testConnectionInitializationAnyData.getMetaData().getAlarmHigh()+")");
				}
			}});
		
		broker.registerListener(cp, new DynamicValueAdapter() {

			public void conditionChange(DynamicValueEvent event) {
				System.out.println("--> conditionChange at "+FORMAT.format(new Date())+", condition = "+event.getCondition());
				testConnectionInitializationConditionChangeLastConnectionState = ((DynamicValueProperty) event.getProperty()).getConnectionState();
			}

			public void errorResponse(DynamicValueEvent event) {
				/*
				 * This should not happen.
				 */
				testConnectionInitializationErrorResponseReply = true;
			}

			public void timelagStarts(DynamicValueEvent event) {
				/*
				 * This should not happen.
				 */
				testConnectionInitializationTimelagStartsReply = true;
			}

			public void timelagStops(DynamicValueEvent event) {
				/*
				 * This should not happen.
				 */
				testConnectionInitializationTimelagStopsReply = true;
			}

			public void timeoutStarts(DynamicValueEvent event) {
				/*
				 * This should not happen.
				 */
				testConnectionInitializationTimeoutStartsReply = true;
			}

			public void timeoutStops(DynamicValueEvent event) {
				/*
				 * This should not happen.
				 */
				testConnectionInitializationTimeoutStopsReply = true;
			}

			public void valueChanged(DynamicValueEvent event) {
				System.out.println("--> valueChanged at "+FORMAT.format(new Date())+", value = "+event.getValue()+", anyData = "+event.getData());
				testConnectionInitializationValueChangedReply = true;
			}

			public void valueUpdated(DynamicValueEvent event) {
				/*
				 * Here we are not interested in this.
				 */
			}
			
		});

		System.out.println(">>> testConnectionInitialization: waitnig");
		Thread.sleep(SLEEP_TIME_MSEC);
		
		assertFalse(testConnectionInitializationErrorResponseReply);
		assertFalse(testConnectionInitializationTimelagStartsReply);
		assertFalse(testConnectionInitializationTimelagStopsReply);
		assertFalse(testConnectionInitializationTimeoutStartsReply);
		assertFalse(testConnectionInitializationTimeoutStopsReply);
		assertTrue(testConnectionInitializationValueChangedReply);
		
		assertNotNull(testConnectionInitializationAnyData);
		assertNotNull(testConnectionInitializationAnyData.getMetaData());
		
		assertEquals(ConnectionState.CONNECTED, testConnectionInitializationStateUpdateLastConnectionState);
		assertTrue(testConnectionInitializationStateUpdateHasValue);
		assertTrue(testConnectionInitializationStateUpdateHasMetadata);
		assertEquals(ConnectionState.CONNECTED, testConnectionInitializationConditionChangeLastConnectionState);
		System.out.println(">>> testConnectionInitialization: finishing at "+FORMAT.format(new Date())+"\n");
	}
	
	private ConnectionParameters newConnectionParameters(final String pvName,
			final String characteristic,
			final Class<?> type) {
		return new ConnectionParameters(newRemoteInfo(pvName, characteristic), type);
	}

	private RemoteInfo newRemoteInfo(final String pvName, final String characteristic) {
		return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", pvName, characteristic, null);
	}
	
}
