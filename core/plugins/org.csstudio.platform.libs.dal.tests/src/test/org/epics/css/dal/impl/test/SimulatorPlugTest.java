/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.epics.css.dal.impl.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.epics.css.dal.CharacteristicInfo;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueMonitor;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.ExpertMonitor;
import org.epics.css.dal.LongProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.StringProperty;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.PropertyFamily;
import org.epics.css.dal.device.PowerSupply;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.simulation.DeviceFactoryImpl;
import org.epics.css.dal.simulation.DoublePropertyProxyImpl;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.simulation.SimulatorUtilities;
import org.epics.css.dal.spi.DefaultDeviceFactoryService;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.DeviceFactory;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.Plugs;
import org.epics.css.dal.spi.PropertyFactory;

public class SimulatorPlugTest extends TestCase{
	
	private DeviceFactory factory;
	private AbstractApplicationContext ctx;
	private PropertyFactory pfac;
	private DoubleProperty prop;
	private DoublePropertyProxyImpl propProxy;
	
	public void testPlugCache() throws RemoteException, InstantiationException {
		String deviceName = "PowerSupply";
		
		PowerSupply device = getDeviceFactory().getDevice(deviceName, PowerSupply.class, null);
		DeviceProxy proxy = SimulatorPlug.getInstance().getDeviceProxyFromCache1(deviceName);
		assertNotNull(proxy);
				
		String propertyName = deviceName + "/current";
		DynamicValueProperty property = device.getCurrent();
		PropertyProxy propertyProxy = SimulatorPlug.getInstance().getSimulatedPropertyProxy(propertyName);
		assertNotNull(propertyProxy);
		
		DirectoryProxy directory = SimulatorPlug.getInstance().getDirectoryProxyFromCache1(propertyName);
		assertNotNull(directory);
				
		getDeviceFactory().getDeviceFamily().destroy(device);
		proxy = SimulatorPlug.getInstance().getDeviceProxyFromCache1(deviceName);
		assertNull(proxy);
		
		propertyProxy = SimulatorPlug.getInstance().getSimulatedPropertyProxy(propertyName);
		assertNull(propertyProxy);
		
		directory = SimulatorPlug.getInstance().getDirectoryProxyFromCache1(propertyName);
		assertNull(directory);
	}
	
	public void testPlugCacheWithoutDevice() {
		String propertyName = getProperty().getName();
		PropertyProxy propertyProxy = SimulatorPlug.getInstance().getSimulatedPropertyProxy(propertyName);
		assertNotNull(propertyProxy);
		DirectoryProxy directory = SimulatorPlug.getInstance().getDirectoryProxyFromCache1(propertyName);
		assertNotNull(directory);
		
		pfac.getPropertyFamily().destroy(getProperty());
		
		propertyProxy = SimulatorPlug.getInstance().getSimulatedPropertyProxy(propertyName);
		assertNull(propertyProxy);
		directory = SimulatorPlug.getInstance().getDirectoryProxyFromCache1(propertyName);
		assertNull(directory);
	}
	
	protected void setUp() throws Exception
	{
		if (ctx == null) {
			ctx = new DefaultApplicationContext("SimulatorJUnitTest");
			SimulatorUtilities.configureSimulatorPlug(ctx.getConfiguration());
		}

		if (pfac == null) {
			pfac = DefaultPropertyFactoryService.getPropertyFactoryService()
				.getPropertyFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);
			assertNotNull(pfac);
			assertEquals(LinkPolicy.SYNC_LINK_POLICY, pfac.getLinkPolicy());
			assertEquals(ctx, pfac.getApplicationContext());
			assertEquals(PropertyFactoryImpl.class, pfac.getClass());
		}
		if (factory == null) {
			DeviceFactoryImpl d= new DeviceFactoryImpl();
			factory = DefaultDeviceFactoryService.getDeviceFactoryService()
				.getDeviceFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);
			assertNotNull(factory);
			assertEquals(LinkPolicy.SYNC_LINK_POLICY, factory.getLinkPolicy());
			assertEquals(ctx, factory.getApplicationContext());
			assertEquals(DeviceFactoryImpl.class, factory.getClass());
		}
	}
	
	public DynamicValueProperty getProperty()
	{
		if (prop == null) {
			String name = new String("DoubleProperty");

			try {
				prop = pfac.getProperty(name, DoubleProperty.class, null);
				propProxy= (DoublePropertyProxyImpl)SimulatorPlug.getInstance().getSimulatedPropertyProxy(name);
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.toString());
			}
		}

		return prop;
	}
	
	private DeviceFactory getDeviceFactory() {
		return factory;
	}
	
	public void testPropertiesWithSameName() {
		
		try {
			
			String name= "PropA";
			StringProperty propS= pfac.getProperty(name, StringProperty.class, null);
			
			assertEquals(name, propS.getName());
			
			DoubleProperty propD= pfac.getProperty(name, DoubleProperty.class, null);
			
			assertNotNull(propD);
			assertEquals(name, propD.getName());

			LongProperty propL= pfac.getProperty(name, LongProperty.class, null);
			
			assertNotNull(propL);
			assertEquals(name, propL.getName());
			
			assertEquals(3, pfac.getPropertyFamily().size());
			assertTrue(pfac.getPropertyFamily().contains(propS));
			assertTrue(pfac.getPropertyFamily().contains(propL));
			assertTrue(pfac.getPropertyFamily().contains(propD));
			assertTrue(pfac.getPropertyFamily().contains(name));
			
			PropertyFamily pcm= pfac.getPropertyFamily();
			
			DynamicValueProperty[] p= pcm.get(name);
			assertNotNull(p);
			
			Set set= new HashSet(Arrays.asList(p));
			assertTrue(set.contains(propD));
			assertTrue(set.contains(propS));
			assertTrue(set.contains(propL));
			
			p= pcm.get(name,DoubleProperty.class);
			assertNotNull(p);
			assertEquals(1, p.length);
			assertEquals(propD, p[0]);

			p= pcm.get(name,LongProperty.class);
			assertNotNull(p);
			assertEquals(1, p.length);
			assertEquals(propL, p[0]);

			pcm.destroy(propL);
			assertEquals(2, pcm.size());
			assertTrue(pcm.contains(propD));
			assertTrue(pcm.contains(propS));
			assertTrue(pcm.contains(name));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
	}
	
	public void testCharacteristicChange() {
		try {
			
			PropertyListener l= new PropertyListener();
			
			getProperty().addPropertyChangeListener(l);
			
			propProxy.simulateCharacteristicChange("test", 1l);
			propProxy.simulateCharacteristicChange("test", 2l);
			
			assertNotNull(l.event);
			assertEquals("test", l.event.getPropertyName());
			assertEquals(1l, l.event.getOldValue());
			assertEquals(2l, l.event.getNewValue());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	
	}
	
	public void testConditionChange() {
		try {
			
			PropertyListener l= new PropertyListener();
			
			getProperty().addDynamicValueListener(l);
			
			
			DynamicValueCondition cond= new DynamicValueCondition(EnumSet.of(DynamicValueState.ALARM),new Timestamp(),"DESC1");
			propProxy.setCondition(cond);
			
			DynamicValueCondition c1= getProperty().getCondition();
			
			assertNotNull(c1);
			assertEquals(cond, c1);
			
			assertTrue(l.conditionChange>1);
			assertEquals(cond, l.lastCondition);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	public void testExpertMonitor() {
		try {
			
			PropertyListener l= new PropertyListener();
			
			Map<String, Object> param= new HashMap<String, Object>();
			param.put("Test", 123);
			
			ExpertMonitor m= getProperty().createNewExpertMonitor(l, param);
			
			DynamicValueMonitor[] mon= getProperty().getMonitors();
			
			assertNotNull(mon);
			assertTrue(mon.length>0);
			
			boolean found=false;
			for (DynamicValueMonitor mm : mon) {
				if (mm.equals(m)) {
					found=true;
					break;
				}
			}
			assertTrue(found);

			m.destroy();
			
			assertTrue(m.isDestroyed());
			
			DynamicValueMonitor[] mon1= getProperty().getMonitors();
			
			assertNotNull(mon);
			assertEquals(mon1.length,mon.length-1);
			
			for (DynamicValueMonitor mm : mon1) {
				assertFalse(mm.equals(m));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	public void testDelayedConnect() {
		
		try {
			
			SimulatorUtilities.putConfiguration(SimulatorUtilities.CONNECTION_DELAY, new Long(3000));
			
			PropertyFactory pf= DefaultPropertyFactoryService.getPropertyFactoryService()
				.getPropertyFactory(ctx, LinkPolicy.ASYNC_LINK_POLICY);
			
			PropertyListener l= new PropertyListener();
			
			DynamicValueProperty p= pf.getProperty("DELAYED");
			
			assertEquals(ConnectionState.CONNECTING, p.getConnectionState());
			
			p.addDynamicValueListener(l);
			p.addLinkListener(l);
			
			assertEquals(0, l.valueChanged);
			assertEquals(0, l.valueUpdated);
			assertEquals(0, l.connected);
			
	
			Thread.yield();
			Thread.sleep(3100);
			
			assertEquals(ConnectionState.CONNECTED, p.getConnectionState());
			assertEquals(1, l.valueChanged);
			assertEquals(0, l.valueUpdated);
			assertEquals(1, l.connected);
			
			SimulatorUtilities.putConfiguration(SimulatorUtilities.CONNECTION_DELAY, new Long(0));

			p= pf.getProperty("NONDELAYED");
			
			assertEquals(ConnectionState.CONNECTED, p.getConnectionState());
			
			p.addDynamicValueListener(l);
			p.addLinkListener(l);
			
			assertEquals(1, l.valueChanged);
			assertEquals(0, l.valueUpdated);
			assertEquals(1, l.connected);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testTwoFactories() {
		

		try {
			
			
			String name= "ReuseProperty";

			DynamicValueProperty p1= pfac.getProperty(name);
			PropertyListener l1= new PropertyListener();
			
			p1.addLinkListener(l1);
			p1.addDynamicValueListener(l1);
			
			Thread.sleep(1000);
			
			assertEquals(ConnectionState.CONNECTED, p1.getConnectionState());

			assertEquals(1, l1.valueChanged);
			//assertEquals(1, l1.conditionChange);
			//assertEquals(1, l1.connecting);
			//assertEquals(1, l1.connected);
			//assertEquals(1, l1.ready);
		
			Object m1= p1.getCharacteristic(CharacteristicInfo.C_DESCRIPTION.getName());
			assertNotNull(m1);
			assertEquals(m1, p1.getDescription());
			assertNotNull(p1.getData());
			assertNotNull(p1.getData().getMetaData());
			assertEquals(m1, p1.getData().getMetaData().getDescription());

			PropertyFactory pfac2 = DefaultPropertyFactoryService.getPropertyFactoryService()
			.getPropertyFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);

			DynamicValueProperty p2= pfac2.getProperty(name);
			PropertyListener l2= new PropertyListener();
			
			p2.addLinkListener(l2);
			p2.addDynamicValueListener(l2);
			
			Thread.sleep(1000);

			assertEquals(1, l2.valueChanged);
			//assertEquals(1, l2.conditionChange);
			//assertEquals(1, l2.connecting);
			//assertEquals(1, l2.connected);
			//assertEquals(1, l2.ready);
		
			assertEquals(ConnectionState.CONNECTED, p2.getConnectionState());
		
			assertEquals(p1.getLatestReceivedValue(), p2.getLatestReceivedValue());

			Object m2= p2.getCharacteristic(CharacteristicInfo.C_DESCRIPTION.getName());
			assertNotNull(m2);
			assertEquals(m2, p2.getDescription());
			assertNotNull(p2.getData());
			assertNotNull(p2.getData().getMetaData());
			assertEquals(m2, p2.getData().getMetaData().getDescription());
			assertEquals(m1, m2);
		
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
		
	}
	
}
