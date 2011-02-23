package org.epics.css.dal.epics.desy.test;

import java.util.Arrays;

import org.epics.css.dal.epics.EPICSPlug;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.MetaData;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.spi.Plugs;

public class SimpleDALBrokerDemo {
	
	private SimpleDALBroker broker;
	
	public SimpleDALBrokerDemo() {
		initialize();
		runDemo();
	}
	
	private void initialize() {
		// set system properties
		
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
	
	private void runDemo() {
		RemoteInfo ri1 = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+SimulatorPlug.PLUG_TYPE, "PV_SIM_01", null, null);
		
		RemoteInfo ri2 = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+EPICSPlug.PLUG_TYPE, "PV_AI_01", null, null);
		ConnectionParameters cp2 = new ConnectionParameters(ri2, Double.class);
		
		try {
			broker.registerListener(cp2, new ChannelListener() {

				public void channelDataUpdate(AnyDataChannel channel) {
					System.out.print(">>> SimpleDALBrokerDemo/ChannelListener/channelDataUpdate: value = "+channel.getData().stringValue());
					System.out.print(", metadata = ");
					AnyData ad = channel.getData();
					MetaData md = ad.getMetaData();
					if (md != null) {
						System.out.println("alarm high: "+md.getAlarmHigh()+", alarm low: "+md.getAlarmLow()
								+", warning high: "+md.getWarnHigh()+", warning low: "+md.getWarnLow()
								+", display high: "+md.getDisplayHigh()+", display low:"+md.getDisplayLow()
								+", precision: "+md.getPrecision()+", units: "+md.getUnits()
								+", states: "+Arrays.toString(md.getStates()));
					}
					else System.out.println(md);
				}

				public void channelStateUpdate(AnyDataChannel channel) {
					System.out.println(">>> SimpleDALBrokerDemo/ChannelListener/channelStateUpdate: "+channel.getStateInfo());
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		double value = Double.NaN;
		try {
			value = (Double) broker.getValue(ri1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(">>> SimpleDALBroker.getValue(RemoteInfo) for "+ri1+" = "+value);
		
		value = Double.NaN;
		try {
			value = (Double) broker.getValue(ri2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(">>> SimpleDALBroker.getValue(RemoteInfo) for "+ri2+" = "+value);
		
		long longValue = Long.MIN_VALUE;
		try {
			longValue = (Long) broker.getValue(ri2, Long.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(">>> SimpleDALBroker.getValue(RemoteInfo) (Long type) for "+ri2+" = "+longValue);
		
		System.out.println("Now you have 20 seconds to test for connection lost...");
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("Time is up!");
		
		// TODO this should be removed
		System.exit(0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new SimpleDALBrokerDemo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
