/**
 * 
 */
package org.epics.css.dal.epics.desy.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.epics.EPICSPlug;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.spi.Plugs;

/**
 * These tests are used to demonstrate the desired behavior of DAL.
 *
 */
public class ExceptionWhenNoChannelAndTypeTest extends TestCase {
	
	private static final long SLEEP_TIME_MSEC = 5000;
	private static final String DOES_NOT_EXIST_NAME = "alarmTest:DOES_NOT_EXIST";

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
	
	private SimpleDALBroker broker;
	
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
	
	/**
	 * Testing registering listeners to unexisting EPICS channel with Java type parameter.
	 * 
	 * @throws Exception
	 */
	public void testUnexistingChannelWithType() throws Exception {
		System.out.println(">>> testUnexistingChannelWithType: started at "+FORMAT.format(new Date()));
		
		ConnectionParameters cp = new ConnectionParameters(new RemoteInfo("DAL-EPICS", DOES_NOT_EXIST_NAME, null, null), String.class);
		
		try {
			broker.registerListener(cp, new ChannelListener() {

				public void channelDataUpdate(AnyDataChannel channel) {
					// do nothing
					
				}

				public void channelStateUpdate(AnyDataChannel channel) {
					// do nothing
					
				}});
			
			broker.registerListener(cp, new DynamicValueAdapter() {});
			
			broker.registerListener(cp, new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					// do nothing
					
				}});
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception caught: "+e);
		}
		
		System.out.println(">>> testUnexistingChannelWithType: waitnig");
		Thread.sleep(SLEEP_TIME_MSEC);
		
		System.out.println(">>> testUnexistingChannelWithType: finishing at "+FORMAT.format(new Date())+"\n");
	}
	
	/**
	 * Testing registering listeners to unexisting EPICS channel without Java type parameter.
	 * 
	 * @throws Exception
	 */
	public void testUnexistingChannelWithoutType() throws Exception {
		System.out.println(">>> testUnexistingChannelWithoutType: started at "+FORMAT.format(new Date()));
		
		ConnectionParameters cp = new ConnectionParameters(new RemoteInfo("DAL-EPICS", DOES_NOT_EXIST_NAME, null, null));
		
		try {
			broker.registerListener(cp, new ChannelListener() {

				public void channelDataUpdate(AnyDataChannel channel) {
					// do nothing
				}

				public void channelStateUpdate(AnyDataChannel channel) {
					// do nothing
				}});
			
			broker.registerListener(cp, new DynamicValueAdapter() {});
			
			broker.registerListener(cp, new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					// do nothing
				}});
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception caught: "+e);
		}
		
		System.out.println(">>> testUnexistingChannelWithoutType: waitnig");
		Thread.sleep(SLEEP_TIME_MSEC);
		
		System.out.println(">>> testUnexistingChannelWithoutType: finishing at "+FORMAT.format(new Date())+"\n");
	}
	
}
