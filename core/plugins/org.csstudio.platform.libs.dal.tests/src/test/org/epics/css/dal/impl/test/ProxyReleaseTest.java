package org.epics.css.dal.impl.test;

import java.util.Properties;

import junit.framework.TestCase;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.device.PowerSupply;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.simulation.MultipleProxySimulatorPlug;
import org.epics.css.dal.spi.AbstractDeviceFactory;
import org.epics.css.dal.spi.AbstractPropertyFactory;
import org.epics.css.dal.spi.DefaultDeviceFactoryService;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.DeviceFactory;
import org.epics.css.dal.spi.DeviceFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;
import org.epics.css.dal.spi.PropertyFactoryService;

public class ProxyReleaseTest extends TestCase {

	public static class MultipleProxyPropertyFactory extends AbstractPropertyFactory{
		protected Class<?extends AbstractPlug> getPlugClass()
		{
			return MultipleProxySimulatorPlug.class;
		}
		public MultipleProxyPropertyFactory()
		{
			super();
		}
	}
	
	public static class MultipleProxyDeviceFactory extends AbstractDeviceFactory{
		protected Class<?extends AbstractPlug> getPlugClass()
		{
			return MultipleProxySimulatorPlug.class;
		}
		public MultipleProxyDeviceFactory()
		{
			super();
		}
	}
	
	private AbstractApplicationContext propertyCtx;
	private AbstractApplicationContext deviceCtx;
	private PropertyFactory pfac;
	private DeviceFactory dfac;
	
	private AbstractApplicationContext getPropertyContext() {
		if (propertyCtx == null) {
			propertyCtx = new DefaultApplicationContext("SimulatorJUnitTest"){
				Properties p;
				@Override
				public Properties getConfiguration() {
					if (p == null) {
						p = super.getConfiguration();
						p.put(PropertyFactoryService.DEFAULT_FACTORY_IMPL,
								MultipleProxyPropertyFactory.class.getName());
					}
					return p;
				}
			};
			
		}
		return propertyCtx;
	}
	
	private AbstractApplicationContext getDeviceContext() {
		if (deviceCtx == null) {
			deviceCtx = new DefaultApplicationContext("SimulatorJUnitTest"){
				Properties p;
				@Override
				public Properties getConfiguration() {
					if (p == null) {
						p = super.getConfiguration();
						p.put(DeviceFactoryService.DEFAULT_FACTORY_IMPL,
								MultipleProxyDeviceFactory.class.getName());
					}
					return p;
				}
			};
			
		}
		return deviceCtx;
	}
	
	private PropertyFactory getPropertyFactory() {
		if (pfac == null) {
			pfac = DefaultPropertyFactoryService.getPropertyFactoryService()
				.getPropertyFactory(getPropertyContext(), LinkPolicy.SYNC_LINK_POLICY);
		}
		return pfac;
	}
	
	private DeviceFactory getDeviceFactory() {
		if (dfac == null) {
			dfac = DefaultDeviceFactoryService.getDeviceFactoryService()
				.getDeviceFactory(getDeviceContext(), LinkPolicy.SYNC_LINK_POLICY);
		}
		return dfac;
	}
	
	public void testDeviceProxyRelease() throws RemoteException, InstantiationException, InterruptedException {
		PowerSupply ps = getDeviceFactory().getDevice("SimulatorDev", PowerSupply.class, null);
		assertNotNull(ps);
		
		//LinkListenerImpl lli = new LinkListenerImpl(this);
		//lli.setExpectedConnectionState(ConnectionState.DESTROYED);
		//ps.addLinkListener(lli);
				
		DirectoryProxy[] directories = MultipleProxySimulatorPlug.getInstance().getCachedDirectoryProxies();
		DeviceProxy[] devices = MultipleProxySimulatorPlug.getInstance().getCachedDeviceProxies();
		PropertyProxy[] properties = MultipleProxySimulatorPlug.getInstance().getCachedPropertyProxies();
		
		assertEquals(devices.length, 2);
		assertEquals(directories.length, 2);
		assertEquals(properties.length, 0);
		
//		DoubleProperty property = ps.getCurrent();
//		assertNotNull(property);
//		
//		directories = MultipleProxySimulatorPlug.getInstance().getCachedDirectoryProxies();
//		properties = MultipleProxySimulatorPlug.getInstance().getCachedPropertyProxies();
//		
//		assertEquals(directories.length, 4);
//		assertEquals(properties.length, 2);
		
		getDeviceContext().destroy();

		// TODO: once device is finsihed, it should check if link event was fired
		//lli.waitForResponse(5000);
		//assertTrue(lli.isResponseReceived());
		
		directories = MultipleProxySimulatorPlug.getInstance().getCachedDirectoryProxies();
		devices = MultipleProxySimulatorPlug.getInstance().getCachedDeviceProxies();

		assertEquals(properties.length, 0);
		assertEquals(devices.length, 0);
		assertEquals(directories.length, 0);
		
	}
	
	public void testPropertyProxyRelease() throws RemoteException, InstantiationException, InterruptedException {
		DoubleProperty property = getPropertyFactory().getProperty("Simulator", DoubleProperty.class, null);
		assertNotNull(property);
				
		LinkListenerImpl lli = new LinkListenerImpl(this);
		property.addLinkListener(lli);
		//lli.waitForResponse(5000);
		//assertTrue(lli.isResponseReceived());
		
				
		DirectoryProxy[] directories = MultipleProxySimulatorPlug.getInstance().getCachedDirectoryProxies();
		PropertyProxy[] properties = MultipleProxySimulatorPlug.getInstance().getCachedPropertyProxies();
		
		assertEquals(2, properties.length);
		assertEquals(2, directories.length);
		
		//lli.reset();
		lli.setExpectedConnectionState(ConnectionState.DISCONNECTED,ConnectionState.DESTROYED);

		getPropertyContext().destroy();

		lli.waitForResponse(5000);
		assertTrue(lli.isResponseReceived());
		
		directories = MultipleProxySimulatorPlug.getInstance().getCachedDirectoryProxies();
		properties = MultipleProxySimulatorPlug.getInstance().getCachedPropertyProxies();

		assertEquals(properties.length, 0);
		assertEquals(directories.length, 0);
		
	}
	
	
	
}
