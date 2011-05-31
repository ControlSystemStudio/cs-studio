package org.epics.css.dal.simple.test;

import java.util.Properties;

import junit.framework.TestCase;

import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.Request;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.simulation.DeviceFactoryImpl;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.spi.Plugs;

public class SimpleDALBrokerTest extends TestCase {
	
	SimpleDALBroker broker;
	
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
		
		broker = SimpleDALBroker.getInstance();
	}
	
	@Override
	protected void tearDown() throws Exception {
		broker = null;
	}
	
	public void testDoubleProperty() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		ConnectionParameters cp = new ConnectionParameters(ri, Double.class);
		double addValue = 5.0;
		
		double initialValue = Double.NaN;
		try {
			initialValue = (Double) broker.getValue(cp);
		} catch (Exception e) {
			fail();
		}
		assertTrue(initialValue != Double.NaN);
		
		try {
			broker.setValue(ri, new Double(initialValue+addValue));
		} catch (Exception e) {
			fail();
		}
		
		double newValue = Double.NaN;
		try {
			newValue = (Double) broker.getValue(ri);
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(initialValue+addValue, newValue);
		
		newValue = Double.NaN;
		try {
			newValue = (Double) broker.getValue(ri, Double.class);
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(initialValue+addValue, newValue);
		
		Request<Double> request = null;
		try {
			request = broker.setValueAsync(cp, new Double(initialValue+2*addValue), new ResponseListener<Double>() {

				public void responseError(ResponseEvent<Double> event) {
					System.out.println(">>> testDoubleProperty/setValueAsync/responseError");
				}

				public void responseReceived(ResponseEvent<Double> event) {
					System.out.println(">>> testDoubleProperty/setValueAsync/responseReceived");
				}
				
			});
		} catch (Exception e) {
			fail();
		}
		
		while(!request.isCompleted()) {
			Thread.yield();
		}
		
		request = null;
		newValue = Double.NaN;
		try {
			request = broker.getValueAsync(cp, new ResponseListener<Double>() {

				public void responseError(ResponseEvent<Double> event) {
					System.out.println(">>> testDoubleProperty/getValueAsync/responseError");
				}

				public void responseReceived(ResponseEvent<Double> event) {
					System.out.println(">>> testDoubleProperty/getValueAsync/responseReceived");
				}
				
			});
		} catch (Exception e) {
			fail();
		}
		
		while(!request.isCompleted()) {
			Thread.yield();
		}
		
		newValue = request.getLastResponse().getValue();
		
		assertEquals(initialValue+2*addValue, newValue);
		
	}
	
	@SuppressWarnings("unchecked")
	public void testChannelListener() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		ConnectionParameters cp = new ConnectionParameters(ri, Double.class);
		double addValue = 5.0;
		
		try {
			broker.registerListener(cp, new ChannelListener() {

				public void channelDataUpdate(AnyDataChannel channel) {
					System.out.println(">>> testChannelListener/ChannelListener/channelDataUpdate");
				}

				public void channelStateUpdate(AnyDataChannel channel) {
					System.out.println(">>> testChannelListener/ChannelListener/channelStateUpdate");
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		try {
			broker.registerListener(cp, new DynamicValueListener() {

				public void conditionChange(DynamicValueEvent event) {
					System.out.println(">>> testChannelListener/DynamicValueListener/conditionChange");
				}

				public void errorResponse(DynamicValueEvent event) {
					System.out.println(">>> testChannelListener/DynamicValueListener/errorResponse");
					
				}

				public void timelagStarts(DynamicValueEvent event) {
					System.out.println(">>> testChannelListener/DynamicValueListener/timelagStarts");
				}

				public void timelagStops(DynamicValueEvent event) {
					System.out.println(">>> testChannelListener/DynamicValueListener/timelagStops");
				}

				public void timeoutStarts(DynamicValueEvent event) {
					System.out.println(">>> testChannelListener/DynamicValueListener/timeoutStarts");
				}

				public void timeoutStops(DynamicValueEvent event) {
					System.out.println(">>> testChannelListener/DynamicValueListener/timeoutStops");
				}

				public void valueChanged(DynamicValueEvent event) {
					System.out.println(">>> testChannelListener/DynamicValueListener/valueChanged");
				}

				public void valueUpdated(DynamicValueEvent event) {
					System.out.println(">>> testChannelListener/DynamicValueListener/valueUpdated");
				}
				
			});
		} catch (Exception e1) {
			fail();
		}
		
		double initialValue = Double.NaN;
		try {
			initialValue = (Double) broker.getValue(cp);
		} catch (Exception e) {
			fail();
		}
		assertTrue(initialValue != Double.NaN);
		
		System.out.println(">>>>>> SYNC SET");
		try {
			broker.setValue(ri, new Double(initialValue+addValue));
		} catch (Exception e) {
			fail();
		}
		
		double newValue = Double.NaN;
		try {
			newValue = (Double) broker.getValue(ri);
		} catch (Exception e) {
			fail();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		assertEquals(initialValue+addValue, newValue);
		
		System.out.println(">>>>>> ASYNC SET");
		Request<Double> request = null;
		try {
			request = broker.setValueAsync(cp, new Double(initialValue+2*addValue), new ResponseListener<Double>() {

				public void responseError(ResponseEvent<Double> event) {
					System.out.println(">>> testChannelListener/setValueAsync/responseError");
				}

				public void responseReceived(ResponseEvent<Double> event) {
					System.out.println(">>> testChannelListener/setValueAsync/responseReceived");
				}
				
			});
		} catch (Exception e) {
			fail();
		}
		
		while(!request.isCompleted()) {
			Thread.yield();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println(">>>>>> ASYNC GET");
		request = null;
		newValue = Double.NaN;
		try {
			request = broker.getValueAsync(cp, new ResponseListener<Double>() {

				public void responseError(ResponseEvent<Double> event) {
					System.out.println(">>> testChannelListener/getValueAsync/responseError");
				}

				public void responseReceived(ResponseEvent<Double> event) {
					System.out.println(">>> testChannelListener/getValueAsync/responseReceived");
				}
				
			});
		} catch (Exception e) {
			fail();
		}
		
		while(!request.isCompleted()) {
			Thread.yield();
		}
		
		newValue = request.getLastResponse().getValue();
		
		assertEquals(initialValue+2*addValue, newValue);
	}
	
	
	public void testMultiChannelInitialization() {
		
		
		
		
	}

}
