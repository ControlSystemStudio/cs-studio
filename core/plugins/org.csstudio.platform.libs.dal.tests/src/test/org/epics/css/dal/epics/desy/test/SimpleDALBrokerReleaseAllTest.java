package org.epics.css.dal.epics.desy.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.TestCase;

import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.epics.EPICSPlug;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.spi.Plugs;

public class SimpleDALBrokerReleaseAllTest extends TestCase {
	
	private static final int TIMEOUT = 2000;
	
	private class TestListener extends DynamicValueAdapter implements ChannelListener, PropertyChangeListener {
		
		private int id;
		private boolean updates = false;
		
		public TestListener(int id) {
			this.id = id;
		}
		
		public void conditionChange(DynamicValueEvent event) {
			System.out.println(">>>#"+id+">>> property = "+event.getProperty().getName()+", condition = "+event.getCondition());
			updates = true;
		}

		public void valueChanged(DynamicValueEvent event) {
			System.out.println(">>>#"+id+">>> property = "+event.getProperty().getName()+", value = "+event.getValue());
			updates = true;
		}

		public void valueUpdated(DynamicValueEvent event) {
			System.out.println(">>>#"+id+">>> property = "+event.getProperty().getName()+", value = "+event.getValue());
			updates = true;
		}
		
		public void channelDataUpdate(AnyDataChannel channel) {
			System.out.println(">>>#"+id+">>> channel = "+channel.getUniqueName()+", value = "+channel.getData().anyValue());
			updates = true;
		}

		public void channelStateUpdate(AnyDataChannel channel) {
			System.out.println(">>>#"+id+">>> channel = "+channel.getUniqueName()+", state = "+channel.getStateInfo());
			updates = true;
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			System.out.println(">>>#"+id+">>> source = "+evt.getSource()+", value = "+evt.getNewValue());
			updates = true;
		}
		
	}
	
	SimpleDALBroker broker1;
	SimpleDALBroker broker2;
	
	protected void setUp() throws Exception {
		// EPICS plug
		System.setProperty(Plugs.PLUGS, EPICSPlug.PLUG_TYPE);
		System.setProperty(Plugs.PLUGS_DEFAULT, EPICSPlug.PLUG_TYPE);
		System.setProperty(Plugs.PLUG_PROPERTY_FACTORY_CLASS + EPICSPlug.PLUG_TYPE,
				org.epics.css.dal.epics.PropertyFactoryImpl.class.getName());
//		System.setProperty(AbstractFactory.SHARE_PLUG, "false");
		
		broker1 = SimpleDALBroker.getInstance();
		broker2 = SimpleDALBroker.newInstance(new DefaultApplicationContext("ReleaseAllTestContext"));
	}
	
	public void testReleaseAll() throws Exception {
		RemoteInfo ri1 = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"EPICS", "EpicsDemo1", null, null);
		ConnectionParameters cp1 = new ConnectionParameters(ri1, Double.class);
		
		RemoteInfo ri2 = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"EPICS", "EpicsDemo2", null, null);
		ConnectionParameters cp2 = new ConnectionParameters(ri2, Double.class);
		
		TestListener tl1 = new TestListener(1);
		TestListener tl2 = new TestListener(2);
		
		broker1.registerListener(cp1, (ChannelListener) tl1);
		broker1.registerListener(cp2, (ChannelListener) tl1);
		broker1.registerListener(cp1, (DynamicValueListener) tl1);
		broker1.registerListener(cp2, (DynamicValueListener) tl1);
		broker1.registerListener(cp1, (PropertyChangeListener) tl1);
		broker1.registerListener(cp2, (PropertyChangeListener) tl1);
		
		broker2.registerListener(cp1, (ChannelListener) tl2);
		broker2.registerListener(cp2, (ChannelListener) tl2);
		broker2.registerListener(cp1, (DynamicValueListener) tl2);
		broker2.registerListener(cp2, (DynamicValueListener) tl2);
		broker2.registerListener(cp1, (PropertyChangeListener) tl2);
		broker2.registerListener(cp2, (PropertyChangeListener) tl2);
		
		Thread.sleep(TIMEOUT);
		
		assertTrue(tl1.updates);
		assertEquals(2, broker1.getPropertiesMapSize());
		
		assertTrue(tl2.updates);
		assertEquals(2, broker2.getPropertiesMapSize());
		
		broker1.releaseAll();
		tl1.updates = false;
		tl2.updates = false;
		
		Thread.sleep(TIMEOUT);
		
		assertFalse(tl1.updates);
		assertEquals(0, broker1.getPropertiesMapSize());
		assertTrue(tl2.updates);
		assertEquals(2, broker2.getPropertiesMapSize());
		
		broker2.releaseAll();
		tl2.updates = false;
		
		Thread.sleep(TIMEOUT);
		
		assertFalse(tl1.updates);
		assertEquals(0, broker1.getPropertiesMapSize());
		assertFalse(tl2.updates);
		assertEquals(0, broker2.getPropertiesMapSize());
		
		
	}

}
