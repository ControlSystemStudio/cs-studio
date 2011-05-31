package org.epics.css.dal.simple.test;

import java.util.Properties;

import junit.framework.TestCase;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.impl.DoubleAnyDataImpl;
import org.epics.css.dal.simulation.DeviceFactoryImpl;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.spi.DefaultPropertyFactoryBroker;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.Plugs;

public class DefaultPropertyFactoryBorkerTest extends TestCase {
	
	private DefaultPropertyFactoryBroker broker;
	
	@Override
	protected void setUp() throws Exception {
		Properties p = new Properties();
		p.put(Plugs.PLUGS, SimulatorPlug.PLUG_TYPE);
		p.put(Plugs.PLUGS_DEFAULT, SimulatorPlug.PLUG_TYPE);
		p.put(Plugs.PLUG_PROPERTY_FACTORY_CLASS + SimulatorPlug.PLUG_TYPE,
		    PropertyFactoryImpl.class.getName());
		p.put(Plugs.PLUG_DEVICE_FACTORY_CLASS + SimulatorPlug.PLUG_TYPE,
		    DeviceFactoryImpl.class.getName());
		System.getProperties().putAll(p);
		
		broker = DefaultPropertyFactoryBroker.getInstance();
	}
	
	@Override
	protected void tearDown() throws Exception {
		broker = null;
	}
	
	public void testProperties1() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		DynamicValueProperty<?> property = null;
		
		try {
			property = broker.getProperty(ri);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(property);
		assertTrue(property instanceof DoubleProperty);
	}
	
	public void testProperties2() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		DoubleProperty property = null;
		
		try {
			property = broker.getProperty(ri, DoubleProperty.class, new LinkListener<DoubleProperty>() {

				public void connected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties2/LinkListener/connected");
				}

				public void operational(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties2/LinkListener/operational");
				}

				public void connectionFailed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties2/LinkListener/connectionFailed");
				}

				public void connectionLost(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties2/LinkListener/connectionLost");
				}

				public void destroyed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties2/LinkListener/destroyed");
				}

				public void disconnected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties2/LinkListener/disconnected");
				}

				public void resumed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties2/LinkListener/resumed");
				}

				public void suspended(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties2/LinkListener/suspended");
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(property);
		assertTrue(property instanceof DoubleProperty);
	}
	
	public void testProperties3() {
		String ri = RemoteInfo.DAL_TYPE_PREFIX+"Simulator"+RemoteInfo.TYPE_SEPARATOR+"DoubleProperty";
		DynamicValueProperty<?> property = null;
		
		try {
			property = broker.getProperty(ri);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(property);
		assertTrue(property instanceof DoubleProperty);
	}
	
	public void testProperties4() {
		String ri = RemoteInfo.DAL_TYPE_PREFIX+"Simulator"+RemoteInfo.TYPE_SEPARATOR+"DoubleProperty";
		DoubleProperty property = null;
		
		try {
			property = broker.getProperty(ri, DoubleProperty.class, new LinkListener<DoubleProperty>() {

				public void connected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties4/LinkListener/connected");
				}

				public void operational(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties4/LinkListener/operational");
				}

				public void connectionFailed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties4/LinkListener/connectionFailed");
				}

				public void connectionLost(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties4/LinkListener/connectionLost");
				}

				public void destroyed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties4/LinkListener/destroyed");
				}

				public void disconnected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties4/LinkListener/disconnected");
				}

				public void resumed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties4/LinkListener/resumed");
				}

				public void suspended(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testProperties4/LinkListener/suspended");
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(property);
		assertTrue(property instanceof DoubleProperty);
	}
	
	public void testAsyncLinkProperty1() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		
		RemoteInfo info = null;
		try {
			info = broker.asyncLinkProperty(ri, DoubleProperty.class, new LinkListener<DoubleProperty>() {

				public void connected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty1/LinkListener/connected");
				}

				public void operational(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty1/LinkListener/operational");
				}

				public void connectionFailed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty1/LinkListener/connectionFailed");
				}

				public void connectionLost(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty1/LinkListener/connectionLost");
				}

				public void destroyed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty1/LinkListener/destroyed");
				}

				public void disconnected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty1/LinkListener/disconnected");
				}

				public void resumed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty1/LinkListener/resumed");
				}

				public void suspended(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty1/LinkListener/suspended");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(info);
		assertEquals(ri, info);
	}
	
	public void testAsyncLinkProperty2() {
		String ri = RemoteInfo.DAL_TYPE_PREFIX+"Simulator"+RemoteInfo.TYPE_SEPARATOR+"DoubleProperty";
		
		RemoteInfo info = null;
		try {
			info = broker.asyncLinkProperty(ri, DoubleProperty.class, new LinkListener<DoubleProperty>() {

				public void connected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty2/LinkListener/connected");
				}

				public void operational(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty2/LinkListener/operational");
				}

				public void connectionFailed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty2/LinkListener/connectionFailed");
				}

				public void connectionLost(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty2/LinkListener/connectionLost");
				}

				public void destroyed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty2/LinkListener/destroyed");
				}

				public void disconnected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty2/LinkListener/disconnected");
				}

				public void resumed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty2/LinkListener/resumed");
				}

				public void suspended(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testAsyncLinkProperty2/LinkListener/suspended");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(info);
		RemoteInfo rInfo = RemoteInfo.fromString(ri);
		assertEquals(rInfo.toString(), info.toString());
	}
	
	public void testInitialize() {
		AbstractApplicationContext ctx = broker.getApplicationContext();
		broker.initialize(ctx, LinkPolicy.SYNC_LINK_POLICY);
		
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		
		RemoteInfo info = null;
		try {
			info = broker.asyncLinkProperty(ri, DoubleProperty.class, new LinkListener<DoubleProperty>() {

				public void connected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/connected");
				}

				public void operational(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/operational");
				}

				public void connectionFailed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/connectionFailed");
				}

				public void connectionLost(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/connectionLost");
				}

				public void destroyed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/destroyed");
				}

				public void disconnected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/disconnected");
				}

				public void resumed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/resumed");
				}

				public void suspended(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/suspended");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(info);
		assertEquals(ri, info);
		
		DynamicValueProperty<?> property = null;
		try {
			property = broker.getProperty(ri);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(property);
		assertTrue(property instanceof DoubleProperty);
		
		property = null;
		try {
			property = broker.getProperty(ri, DoubleProperty.class, new LinkListener<DoubleProperty>() {

				public void connected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/connected");
				}

				public void operational(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/operational");
				}

				public void connectionFailed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/connectionFailed");
				}

				public void connectionLost(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/connectionLost");
				}

				public void destroyed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/destroyed");
				}

				public void disconnected(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/disconnected");
				}

				public void resumed(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/resumed");
				}

				public void suspended(ConnectionEvent<DoubleProperty> e) {
					System.out.println("testInitialize/LinkListener/suspended");
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(property);
		assertTrue(property instanceof DoubleProperty);
	}
	
	public void testChannelAsync() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		DynamicValueProperty<?> property = null;
		
		try {
			property = broker.getProperty(ri);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(property);
		assertTrue(property instanceof DoubleProperty);
		
		while (!property.isConnected()) {
			System.out.println("Waiting to connect...");
			Thread.yield();
		}
		
		try {
			Request<?> req = property.getAsynchronous();
			while (!req.isCompleted()) {
				System.out.println("Waiting to complete...");
				Thread.yield();
			}
		} catch (DataExchangeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		AnyData ad = property.getData();
		assertNotNull(ad);
		
		try {
			ad.anyValue();
			ad.doubleValue();
		} catch (Exception e) {
			fail();
		}
		
		// TODO not yet implemented for Simulator
//		MetaData md = ad.getMetaData();
//		assertNotNull(md);
	}
	
	public void testChannel() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		DynamicValueProperty<?> property = null;
		
		try {
			property = broker.getProperty(ri);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(property);
		assertTrue(property instanceof DoubleProperty);
		
		AnyData ad = property.getData();
		assertNotNull(ad);
		assertTrue(ad instanceof DoubleAnyDataImpl);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Double value = ad.doubleValue();
		assertEquals(Double.NaN, value, 0);
	}
	
	public void testStop() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		DynamicValueProperty<?> property = null;
		
		try {
			property = broker.getProperty(ri);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		assertNotNull(property);
		assertTrue(property.isConnected());
		
		try {
			property.getValue();
		} catch (DataExchangeException e) {
			e.printStackTrace();
			fail();
		}
		
		property.stop();
		
		assertFalse(property.isConnected());
		
		try {
			Object o = property.getValue();
			fail();
		} catch (DataExchangeException e) {
			// this is OK, since it has been disconnected
		}
		
		try {
			((DynamicValueProperty<Double>) property).setValue(new Double(-1.0));
			fail();
		} catch (DataExchangeException e) {
			// this is OK, since it has been disconnected
		}
		
		try {
			property.setValueAsObject(new Double(-1.0));
			fail();
		} catch (DataExchangeException e) {
			// this is OK, since it has been disconnected
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
		
		try {
			property.getAsynchronous();
			fail();
		} catch (DataExchangeException e) {
			// this is OK, since it has been disconnected
		}
		
		try {
			((DynamicValueProperty<Double>) property).setAsynchronous(new Double(-1.0), null);
			fail();
		} catch (DataExchangeException e) {
			// this is OK, since it has been disconnected
		}
		
		try {
			((DynamicValueProperty<Double>) property).createNewMonitor(new DynamicValueAdapter<Double, DynamicValueProperty<Double>>());
			fail();
		} catch (IllegalStateException e) {
			// this is OK, since it has been disconnected
		} catch (RemoteException er) {
			er.printStackTrace();
			fail();
		}
		
		try {
			property.getCondition();
			fail();
		} catch (IllegalStateException e) {
			// this is OK, since it has been disconnected
		}
		
		try {
			property.getUniqueName();
			fail();
		} catch (IllegalStateException e) {
			// this is OK, since it has been disconnected
		}
		
		try {
			property.isSettable();
			fail();
		} catch (IllegalStateException e) {
			// this is OK, since it has been disconnected
		}
		
	}

}
