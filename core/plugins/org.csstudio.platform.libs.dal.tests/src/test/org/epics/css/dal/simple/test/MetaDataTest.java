package org.epics.css.dal.simple.test;

import java.util.Properties;

import junit.framework.TestCase;

import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.DataFlavor;
import org.epics.css.dal.simple.MetaData;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.simple.impl.DoubleAnyDataImpl;
import org.epics.css.dal.simple.impl.DoubleSeqAnyDataImpl;
import org.epics.css.dal.simple.impl.LongAnyDataImpl;
import org.epics.css.dal.simple.impl.MetaDataImpl;
import org.epics.css.dal.simulation.DeviceFactoryImpl;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.spi.Plugs;

public class MetaDataTest extends TestCase {

	private static final int WAIT = 2000;
	
	private SimpleDALBroker broker;
	private boolean metaDataUpdated;
	
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
		metaDataUpdated = false;
	}
	
	public void testDoubleProperty() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleProperty", null, null);
		ConnectionParameters cp = new ConnectionParameters(ri, Double.class);
		
		try {
			broker.registerListener(cp, new ChannelListener() {

				public void channelDataUpdate(AnyDataChannel channel) {
					AnyData ad = channel.getData();
					MetaData md = ad.getMetaData(); 
					if (md != null) {
						assertTrue(ad instanceof DoubleAnyDataImpl);
						assertTrue(md instanceof MetaDataImpl);
						metaDataUpdated = true;
					}
				}

				public void channelStateUpdate(AnyDataChannel channel) {
					// not important
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		if (!metaDataUpdated) {
			try {
				Thread.sleep(WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!metaDataUpdated) fail();
		
	}
	
	public void testDoubleSeqProperty() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "DoubleSeqProperty", null, null);
		ConnectionParameters cp = new ConnectionParameters(ri, double[].class);
		
		try {
			broker.registerListener(cp, new ChannelListener() {

				public void channelDataUpdate(AnyDataChannel channel) {
					AnyData ad = channel.getData();
					MetaData md = ad.getMetaData(); 
					if (md != null) {
						assertTrue(ad instanceof DoubleSeqAnyDataImpl);
						assertTrue(md instanceof MetaDataImpl);
						metaDataUpdated = true;
					}
				}

				public void channelStateUpdate(AnyDataChannel channel) {
					// not important
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		if (!metaDataUpdated) {
			try {
				Thread.sleep(WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!metaDataUpdated) fail();
		
	}
	
	public void testLongProperty() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "LongProperty", null, null);
		ConnectionParameters cp = new ConnectionParameters(ri, Long.class);
		
		try {
			broker.registerListener(cp, new ChannelListener() {

				public void channelDataUpdate(AnyDataChannel channel) {
					AnyData ad = channel.getData();
					MetaData md = ad.getMetaData(); 
					if (md != null) {
						assertTrue(ad instanceof LongAnyDataImpl);
						assertTrue(md instanceof MetaDataImpl);
						metaDataUpdated = true;
					}
				}

				public void channelStateUpdate(AnyDataChannel channel) {
					// not important
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		if (!metaDataUpdated) {
			try {
				Thread.sleep(WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!metaDataUpdated) fail();
		
	}
	
	public void testEnumProperty() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"Simulator", "EnumProperty", null, null);
		ConnectionParameters cp = new ConnectionParameters(ri, DataFlavor.ENUM, DataFlavor.ENUM);
		
		try {
			broker.registerListener(cp, new ChannelListener() {

				public void channelDataUpdate(AnyDataChannel channel) {
					AnyData ad = channel.getData();
					MetaData md = ad.getMetaData(); 
					if (md != null) {
						assertTrue(ad instanceof LongAnyDataImpl);
						assertTrue(md instanceof MetaDataImpl);
						metaDataUpdated = true;
					}
				}

				public void channelStateUpdate(AnyDataChannel channel) {
					// not important
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		if (!metaDataUpdated) {
			try {
				Thread.sleep(WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!metaDataUpdated) fail();
		
	}
	
}
