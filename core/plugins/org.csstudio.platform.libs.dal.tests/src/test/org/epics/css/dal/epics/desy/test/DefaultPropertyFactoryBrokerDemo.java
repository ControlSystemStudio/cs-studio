package org.epics.css.dal.epics.desy.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.epics.css.dal.CharacteristicInfo;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.Request;
import org.epics.css.dal.epics.EPICSPlug;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.spi.DefaultPropertyFactoryBroker;
import org.epics.css.dal.spi.Plugs;

public class DefaultPropertyFactoryBrokerDemo {
	
	private DefaultPropertyFactoryBroker broker;
	
	public DefaultPropertyFactoryBrokerDemo() {
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
		
		broker = DefaultPropertyFactoryBroker.getInstance();
		
	}
	
	private void runDemo() {
		RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"EPICS", "PV_AI_01", null, null);
		DynamicValueProperty<?> property = null;
		
		try {
			property = broker.getProperty(ri);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
			e1.printStackTrace();
		}
		
		final DynamicValueProperty<?> dvp = property;
		property.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(CharacteristicInfo.C_META_DATA.getName())) {
					
					System.out.println("PropertyChangeEvent for "+CharacteristicInfo.C_META_DATA.getName()+":");
					
					MetaData md = dvp.getData().getMetaData();
					
					System.out.println("MetaData.getAlarmHigh = "+md.getAlarmHigh());
					System.out.println("MetaData.getAlarmLow = "+md.getAlarmLow());
					System.out.println("MetaData.getDisplayHigh = "+md.getDisplayHigh());
					System.out.println("MetaData.getDisplayLow = "+md.getDisplayLow());
					System.out.println("MetaData.getPrecision = "+md.getPrecision());
					System.out.println("MetaData.getUnits = "+md.getUnits());
					System.out.println("MetaData.getWarnHigh = "+md.getWarnHigh());
					System.out.println("MetaData.getWarnLow = "+md.getWarnLow());
					System.out.println("MetaData.getStates = "+Arrays.toString(md.getStates()));
				}
			}});
		
		AnyData ad = dvp.getData();
		
		System.out.println("AnyData = "+ad.anyValue());
		
		MetaData md = ad.getMetaData();
		
		System.out.println("Initial MetaData = "+md);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// TODO this should be removed
		System.exit(0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new DefaultPropertyFactoryBrokerDemo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
