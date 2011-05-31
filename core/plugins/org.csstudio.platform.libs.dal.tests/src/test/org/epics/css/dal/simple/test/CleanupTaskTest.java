package org.epics.css.dal.simple.test;

import java.util.Properties;

import org.epics.css.dal.RemoteException;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.simulation.DeviceFactoryImpl;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.spi.Plugs;

import com.cosylab.util.CommonException;

import junit.framework.TestCase;

public class CleanupTaskTest extends TestCase {

	SimpleDALBroker broker;
	
	private static final int TIMEOUT = 122000;
	
	protected void setUp() throws Exception {
		Properties p = new Properties();
		p.put(Plugs.PLUGS, SimulatorPlug.PLUG_TYPE);
		p.put(Plugs.PLUGS_DEFAULT, SimulatorPlug.PLUG_TYPE);
		p.put(Plugs.PLUG_PROPERTY_FACTORY_CLASS + SimulatorPlug.PLUG_TYPE,
		    PropertyFactoryImpl.class.getName());
		p.put(Plugs.PLUG_DEVICE_FACTORY_CLASS + SimulatorPlug.PLUG_TYPE,
		    DeviceFactoryImpl.class.getName());
		System.getProperties().putAll(p);
		
		broker = SimpleDALBroker.getInstance();
	}
	
	public void testCleanup() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		ConnectionParameters cp = new ConnectionParameters(ri, Double.class);
		double addValue = 5.0;
		
		ChannelListener cl = new ChannelListener() {

			public void channelDataUpdate(AnyDataChannel channel) {
				System.out.println("testCleanup/channelDataUpdate");
			}

			public void channelStateUpdate(AnyDataChannel channel) {
				System.out.println("testCleanup/channelStateUpdate");
			}
		};
		
		double initialValue = Double.NaN;
		try {
			initialValue = (Double) broker.getValue(cp);
		} catch (Exception e) {
			fail();
		}
		assertTrue(initialValue != Double.NaN);
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 0);
		
		try {
			broker.setValue(ri, new Double(initialValue+addValue));
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 0);
		
		try {
			broker.registerListener(cp, cl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			broker.deregisterListener(cp, cl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 0);
		
		// test multiple property holders at the same time
		initialValue = Double.NaN;
		try {
			initialValue = (Double) broker.getValue(cp);
		} catch (Exception e) {
			fail();
		}
		assertTrue(initialValue != Double.NaN);
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			broker.setValue(ri, new Double(initialValue+addValue));
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			broker.registerListener(cp, cl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		RemoteInfo ri2 = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty1", null, null);
		ConnectionParameters cp2 = new ConnectionParameters(ri2, Double.class);
		double someValue = Double.NaN;
		try {
			someValue = (Double) broker.getValue(cp2);
		} catch (Exception e) {
			fail();
		}
		assertTrue(someValue != Double.NaN);
		
		assertEquals(broker.getPropertiesMapSize(), 2);
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			broker.deregisterListener(cp, cl);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertEquals(broker.getPropertiesMapSize(), 1);

		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}

		assertEquals(broker.getPropertiesMapSize(), 0);
		
		
	}
	
	public void testDestroy() throws RemoteException, InstantiationException, CommonException {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		ConnectionParameters cp = new ConnectionParameters(ri, Double.class);
		
		ChannelListener cl = new ChannelListener() {

			public void channelDataUpdate(AnyDataChannel channel) {
				System.out.println("testDestroy/channelDataUpdate");
			}

			public void channelStateUpdate(AnyDataChannel channel) {
				System.out.println("testDestroy/channelStateUpdate");
			}
		};
		
		SimpleDALBroker.getInstance().registerListener(cp,cl);
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		SimpleDALBroker.getInstance().registerListener(cp,cl);
		
		
		SimpleDALBroker.getInstance().deregisterListener(cp,cl);
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 1);
		
		SimpleDALBroker.getInstance().deregisterListener(cp,cl);
		
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		
		assertEquals(broker.getPropertiesMapSize(), 0);
	}

}
