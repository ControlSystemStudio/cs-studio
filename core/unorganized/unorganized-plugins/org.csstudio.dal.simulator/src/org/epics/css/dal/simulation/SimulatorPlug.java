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

/**
 *
 */
package org.epics.css.dal.simulation;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.apache.log4j.Logger;
import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.EnumPropertyCharacteristics;
import org.csstudio.dal.NumericPropertyCharacteristics;
import org.csstudio.dal.PropertyCharacteristics;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.SequencePropertyCharacteristics;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.ConnectionException;
import org.csstudio.dal.device.AbstractDevice;
import org.csstudio.dal.impl.AbstractDeviceImpl;
import org.csstudio.dal.impl.PropertyUtilities;
import org.csstudio.dal.proxy.AbstractPlug;
import org.csstudio.dal.proxy.DeviceProxy;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.simple.impl.DataUtil;
import org.csstudio.dal.spi.Plugs;
import org.epics.css.dal.directory.DALDescriptor;
import org.epics.css.dal.directory.DescriptorType;
import org.epics.css.dal.directory.DeviceDescriptor;
import org.epics.css.dal.directory.DeviceDescriptorImpl;
import org.epics.css.dal.directory.DirectoryUtilities;
import org.epics.css.dal.simulation.ps.PowerSupplyImpl;

import com.cosylab.naming.URIName;


/**
 * @author ikriznar
 *
 */
public class SimulatorPlug extends AbstractPlug
{
	class ScheduledTask extends TimerTask
	{
		private Runnable r;

		public ScheduledTask(Runnable r)
		{
			this.r = r;
		}

		@Override
		public void run()
		{
			try {
				r.run();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).warn("Simulator error.", e);
			}
		}
	}

	private static SimulatorPlug instance;

	public static SimulatorPlug getInstance()
	{
		return getInstance((Properties)null);
	}

	public static synchronized SimulatorPlug getInstance(Properties conf)
	{
		if (instance == null) {
			instance = new SimulatorPlug(conf);
		}

		return instance;
	}
	public static synchronized SimulatorPlug getInstance(AbstractApplicationContext ctx)
	{
		return new SimulatorPlug(ctx);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#releaseInstance()
	 */
	@Override
	public void releaseInstance() throws Exception {
		// noop, since it is a singleton
	}

	private ThreadPoolExecutor executor;
	private Timer timer;
	private DirContext simulatorContext;
	public static final String PLUG_TYPE = Plugs.SIMULATOR_PLUG_TYPE;
	public static final String SCHEME_SUFFIX = "Simulator";
	public static final String DEFAULT_AUTHORITY = "DEFAULT";

	protected SimulatorPlug(Properties configuration)
	{
		super(configuration);
	}
	public SimulatorPlug(AbstractApplicationContext ctx) {
		super(ctx);
	}

	public void execute(Runnable r)
	{
		if (executor == null) {
			synchronized (this) {
				if (executor == null) {
					executor = new ThreadPoolExecutor(10,10,0,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
				}
			}
		}
		executor.execute(r);
	}

	public TimerTask schedule(Runnable r, long rate)
	{
		ScheduledTask t = new ScheduledTask(r);

		if (timer == null) {
			synchronized (this) {
				if (timer == null) {
					timer = new Timer("SimulatorPlugTimer");
				}
			}
		}

		timer.scheduleAtFixedRate(t, 0, rate);

		return t;
	}

	
	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewProxy(java.lang.String, java.lang.Class)
	 */
	@Override
	protected <T extends PropertyProxy<?,?>> T createNewPropertyProxy(
		    String uniqueName, Class<T> type) throws ConnectionException
	{
		try {
			if (type == PropertyProxy.class) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				PropertyProxyImpl<?> p = new PropertyProxyImpl(uniqueName,this,type);
				putDirectoryProxyToCache(p);

				return type.cast(p);
			}

			//			if (!PropertyProxyImpl.class.isAssignableFrom(type)) {
			//				throw new IllegalArgumentException("Simulator plug can not instantiate class "+type.getName());
			//			}
			PropertyProxy<?,?> p = type.getConstructor(String.class,SimulatorPlug.class)
				.newInstance(uniqueName,this);

			/*
			if (PropertyProxyImpl.class.isAssignableFrom(p.getClass()))
			{
			    ((PropertyProxyImpl)p).delayedConnect(5000);
			}*/

			// adding to directory cache as well
			if (p instanceof DirectoryProxy) {
				putDirectoryProxyToCache((DirectoryProxy<?>)p);
			}

			return type.cast(p);
		} catch (Exception e) {
			throw new ConnectionException(this,
			    "Failed to instantiate simulation proxy '" + uniqueName
			    + "' for type '" + type.getName() + "'.", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPlugType()
	 */
	@Override
	public String getPlugType()
	{
		return PLUG_TYPE;
	}

	@Override
	protected DirectoryProxy<?> createNewDirectoryProxy(String uniqueName)
	{
		throw new RuntimeException(
		    "Error in factory implementation, PropertyProxy must be created first.");
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewDeviceProxy(java.lang.String, java.lang.Class)
	 */
	@Override
	protected <T extends DeviceProxy<?>> T createNewDeviceProxy(
	    String uniqueName, Class<T> type) throws ConnectionException
	{
		try {
			if (type == DeviceProxy.class) {
				DeviceProxyImpl p = new DeviceProxyImpl(uniqueName,this);
				putDirectoryProxyToCache(p);

				return type.cast(p);
			}

			//			if (!DeviceProxyImpl.class.isAssignableFrom(type)) {
			//				throw new IllegalArgumentException("Simulator plug can not instantiate class "+type.getName());
			//			}
			DeviceProxy<?> p = type.getConstructor(String.class,SimulatorPlug.class)
				.newInstance(uniqueName,this);

			// adding to directory cache as well
			if (p instanceof DirectoryProxy) {
				putDirectoryProxyToCache((DirectoryProxy<?>)p);
			}

			return type.cast(p);
		} catch (Exception e) {
			throw new ConnectionException(this,
			    "Failed to instantiate simulation proxy '" + uniqueName
			    + "' for type '" + type.getName() + "'.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.Class)
	 */
	@Override
	public Class<?extends SimpleProperty<?>> getPropertyImplementationClass(
	    Class<?extends SimpleProperty<?>> type, String propertyName) throws RemoteException
	{
		if (type==null) {
			type= DoubleProperty.class;
		}
		Class<?extends SimpleProperty<?>> impl = super
			.getPropertyImplementationClass(type, propertyName);

		if (impl == null) {
			return PropertyUtilities.getImplementationClass(type);
		} else {
			return impl;
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getDeviceImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends AbstractDevice> getDeviceImplementationClass(String uniqueDeviceName) {
		if (uniqueDeviceName.startsWith("PS")) return PowerSupplyImpl.class;
		if (uniqueDeviceName == "PowerSupply") return PowerSupplyImpl.class;
		return AbstractDeviceImpl.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyProxyImplementationClass(java.lang.Class)
	 */
	@Override
	public Class<?extends PropertyProxy<?,?>> getPropertyProxyImplementationClass(
	    Class<? extends SimpleProperty<?>> type, Class<? extends SimpleProperty <?>> implType, String propertyName) throws RemoteException
	{
		Class<?extends PropertyProxy<?,?>> impl = super
			.getPropertyProxyImplementationClass(type,implType,propertyName);
		
		if (impl == null) {
			return SimulatorUtilities.getPropertyProxyImplementationClass(type,implType);
		} else {
			return impl;
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getDeviceProxyImplementationClass(java.lang.Class, java.lang.Class, java.lang.String)
	 */
	@Override
	public Class<? extends DeviceProxy<?>> getDeviceProxyImplementationClass(Class<? extends AbstractDevice> type, Class<? extends AbstractDevice> implementationType, String uniqueDeviceName) throws RemoteException {
		Class<?extends DeviceProxy<?>> impl = super.getDeviceProxyImplementationClass(type, implementationType,
				uniqueDeviceName);
		if (impl == null) {
			return SimulatorUtilities.getDeviceProxyImplementationClass(type);
		} else {
			return impl;
		}
	}
	
	/**
	 * Returns simulated property proxy.
	 * @param uniqueName proxy name
	 * @return if exists returns simulated property proxy, otherwise <code>null</code>
	 */
	public PropertyProxyImpl<?> getSimulatedPropertyProxy(String uniqueName)
	{
		return (PropertyProxyImpl<?>)_getPropertyProxyFromCache(uniqueName);
	}

	/**
	 * Returns simulated device proxy.
	 * @param uniqueName proxy name
	 * @return if exists returns simulated device proxy, otherwise <code>null</code>
	 */
	public DeviceProxyImpl getSimulatedDeviceProxy(String uniqueName)
	{
		return (DeviceProxyImpl)_getDeviceProxyFromCache(uniqueName);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PlugContext#getDefaultDirectory()
	 */
	public DirContext getDefaultDirectory()
	{
		if (simulatorContext != null) {
			return simulatorContext;
		}

		try {
			DirContext initialContext = DirectoryUtilities.getInitialContext();

			URIName name = new URIName(org.csstudio.dal.simple.RemoteInfo.DAL_TYPE_PREFIX + SCHEME_SUFFIX,
				    null, null, null);
			URIName nameS = (URIName)name.getPrefix(1);
			Object simContext = initialContext.lookup(nameS);

			if (simContext == null) {
				simulatorContext = (DirContext)initialContext.createSubcontext(nameS);
			} else {
				simulatorContext = (DirContext)simContext;
			}

			//bind proxy implementations
			URIName ppi = new URIName(null, DEFAULT_AUTHORITY,
				    "PropertyProxyImpl", null);
			Attributes characteristics = new org.epics.css.dal.directory.Attributes();
			characteristics.put(NumericPropertyCharacteristics.C_DESCRIPTION,
			    "Simulated Property");
			//characteristics.put(NumericPropertyCharacteristics.C_DISPLAY_NAME, name);
			characteristics.put(NumericPropertyCharacteristics.C_POSITION,
			    new Double(0));
			characteristics.put(NumericPropertyCharacteristics.C_PROPERTY_TYPE,
			    "property");
			characteristics.put(NumericPropertyCharacteristics.C_RESOLUTION,
			    0xFFFF);
			characteristics.put(NumericPropertyCharacteristics.C_SCALE_TYPE,
			    "linear");
			characteristics.put(NumericPropertyCharacteristics.C_UNITS, "amper");
			simulatorContext.bind(ppi, new PropertyProxyImpl<Object>(ppi.toString(),null,Object.class),
			    characteristics);

			ppi = new URIName(null, DEFAULT_AUTHORITY,
				    "DoublePropertyProxyImpl", null);

			Attributes characteristicsD = new org.epics.css.dal.directory.Attributes();
			characteristicsD.put(NumericPropertyCharacteristics.C_DESCRIPTION,
			    "Simulated Property");
			//characteristicsD.put(NumericPropertyCharacteristics.C_DISPLAY_NAME, name);
			characteristicsD.put(NumericPropertyCharacteristics.C_POSITION,
			    new Double(0));
			characteristicsD.put(NumericPropertyCharacteristics.C_PROPERTY_TYPE,
			    "property");
			characteristicsD.put(NumericPropertyCharacteristics.C_RESOLUTION,
			    0xFFFF);
			characteristicsD.put(NumericPropertyCharacteristics.C_SCALE_TYPE,
			    "linear");
			characteristicsD.put(NumericPropertyCharacteristics.C_UNITS, "amper");
			characteristicsD.put(NumericPropertyCharacteristics.C_FORMAT, "%.4f");
			characteristicsD.put(NumericPropertyCharacteristics.C_GRAPH_MAX,
			    new Double(10));
			characteristicsD.put(NumericPropertyCharacteristics.C_GRAPH_MIN,
			    new Double(-10));
			characteristicsD.put(NumericPropertyCharacteristics.C_MAXIMUM,
			    new Double(10));
			characteristicsD.put(NumericPropertyCharacteristics.C_MINIMUM,
			    new Double(-10));
			characteristicsD.put(NumericPropertyCharacteristics.C_WARNING_MAX,
				    new Double(8));
			characteristicsD.put(NumericPropertyCharacteristics.C_WARNING_MIN,
				    new Double(-8));
			characteristicsD.put(NumericPropertyCharacteristics.C_ALARM_MAX,
				    new Double(9));
			characteristicsD.put(NumericPropertyCharacteristics.C_ALARM_MIN,
				    new Double(-9));
			characteristicsD.put(CharacteristicInfo.C_META_DATA.getName(), DataUtil.createMetaData(characteristicsD));
			simulatorContext.bind(ppi,
			    new DoublePropertyProxyImpl(ppi.toString(),null), characteristicsD);

			ppi = new URIName(null, DEFAULT_AUTHORITY,
				    "DoubleSeqPropertyProxyImpl", null);

			Attributes characteristicsDS = new org.epics.css.dal.directory.Attributes();
			characteristicsDS.put(NumericPropertyCharacteristics.C_DESCRIPTION,
			    "Simulated Property");
			//characteristicsDS.put(NumericPropertyCharacteristics.C_DISPLAY_NAME, name);
			characteristicsDS.put(NumericPropertyCharacteristics.C_POSITION,
			    new Double(0));
			characteristicsDS.put(NumericPropertyCharacteristics.C_PROPERTY_TYPE,
			    "property");
			characteristicsDS.put(NumericPropertyCharacteristics.C_RESOLUTION,
			    0xFFFF);
			characteristicsDS.put(NumericPropertyCharacteristics.C_SCALE_TYPE,
			    "linear");
			characteristicsDS.put(NumericPropertyCharacteristics.C_UNITS,
			    "amper");
			characteristicsDS.put(NumericPropertyCharacteristics.C_FORMAT,
			    "%.4f");
			characteristicsDS.put(NumericPropertyCharacteristics.C_GRAPH_MAX,
			    new Double(10));
			characteristicsDS.put(NumericPropertyCharacteristics.C_GRAPH_MIN,
			    new Double(-10));
			characteristicsDS.put(NumericPropertyCharacteristics.C_MAXIMUM,
			    new Double(10));
			characteristicsDS.put(NumericPropertyCharacteristics.C_MINIMUM,
			    new Double(-10));
			characteristicsDS.put(NumericPropertyCharacteristics.C_WARNING_MAX,
				    new Double(8));
			characteristicsDS.put(NumericPropertyCharacteristics.C_WARNING_MIN,
				    new Double(-8));
			characteristicsDS.put(NumericPropertyCharacteristics.C_ALARM_MAX,
				    new Double(9));
			characteristicsDS.put(NumericPropertyCharacteristics.C_ALARM_MIN,
				    new Double(-9));
			characteristicsDS.put(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH,
			    new Integer(5));
			characteristicsDS.put(CharacteristicInfo.C_META_DATA.getName(), DataUtil.createMetaData(characteristicsDS));
			simulatorContext.bind(ppi,
			    new DoubleSeqPropertyProxyImpl(ppi.toString(),null),
			    characteristicsDS);

			ppi = new URIName(null, DEFAULT_AUTHORITY, "LongPropertyProxyImpl",
				    null);

			Attributes characteristicsL = new org.epics.css.dal.directory.Attributes();
			characteristicsL.put(NumericPropertyCharacteristics.C_DESCRIPTION,
			    "Simulated Property");
			//characteristicsL.put(NumericPropertyCharacteristics.C_DISPLAY_NAME, name);
			characteristicsL.put(NumericPropertyCharacteristics.C_POSITION,
			    new Double(0));
			characteristicsL.put(NumericPropertyCharacteristics.C_PROPERTY_TYPE,
			    "property");
			characteristicsL.put(NumericPropertyCharacteristics.C_RESOLUTION,
			    0xFFFF);
			characteristicsL.put(NumericPropertyCharacteristics.C_SCALE_TYPE,
			    "linear");
			characteristicsL.put(NumericPropertyCharacteristics.C_UNITS, "amper");
			characteristicsL.put(NumericPropertyCharacteristics.C_FORMAT, "%d");
			characteristicsL.put(NumericPropertyCharacteristics.C_GRAPH_MAX,
			    new Long(10));
			characteristicsL.put(NumericPropertyCharacteristics.C_GRAPH_MIN,
			    new Long(-10));
			characteristicsL.put(NumericPropertyCharacteristics.C_MAXIMUM,
			    new Long(10));
			characteristicsL.put(NumericPropertyCharacteristics.C_MINIMUM,
			    new Long(-10));
			characteristicsL.put(NumericPropertyCharacteristics.C_WARNING_MAX,
				    new Long(8));
			characteristicsL.put(NumericPropertyCharacteristics.C_WARNING_MIN,
				    new Long(-8));
			characteristicsL.put(NumericPropertyCharacteristics.C_ALARM_MAX,
				    new Long(9));
			characteristicsL.put(NumericPropertyCharacteristics.C_ALARM_MIN,
				    new Long(-9));
			characteristicsL.put(CharacteristicInfo.C_META_DATA.getName(), DataUtil.createMetaData(characteristicsL));
			simulatorContext.bind(ppi,
			    new LongPropertyProxyImpl(ppi.toString(),null), characteristicsL);

			ppi = new URIName(null, DEFAULT_AUTHORITY, "EnumPropertyProxyImpl",
				    null);

			Attributes characteristicsEN = new org.epics.css.dal.directory.Attributes();
			characteristicsEN.put(NumericPropertyCharacteristics.C_DESCRIPTION,
			    "Simulated Property");
			//characteristicsEN.put(NumericPropertyCharacteristics.C_DISPLAY_NAME, name);
			characteristicsEN.put(NumericPropertyCharacteristics.C_POSITION,
			    new Double(0));
			characteristicsEN.put(NumericPropertyCharacteristics.C_PROPERTY_TYPE,
			    "property");
			characteristicsEN.put(NumericPropertyCharacteristics.C_RESOLUTION,
			    0xFFFF);
			characteristicsEN.put(NumericPropertyCharacteristics.C_SCALE_TYPE,
			    "linear");
			characteristicsEN.put(NumericPropertyCharacteristics.C_UNITS,
			    "amper");
			characteristicsEN.put(NumericPropertyCharacteristics.C_FORMAT, "%d");
			characteristicsEN.put(NumericPropertyCharacteristics.C_GRAPH_MAX,
			    new Long(3));
			characteristicsEN.put(NumericPropertyCharacteristics.C_GRAPH_MIN,
			    new Long(0));
			characteristicsEN.put(NumericPropertyCharacteristics.C_MAXIMUM,
			    new Long(3));
			characteristicsEN.put(NumericPropertyCharacteristics.C_MINIMUM,
			    new Long(0));
			characteristicsEN.put(EnumPropertyCharacteristics.C_ENUM_DESCRIPTIONS,
			    new String[]{ "On", "Off", "not connected" });
			characteristicsEN.put(EnumPropertyCharacteristics.C_ENUM_VALUES,
			    new Double[]{ 1.1, 1.2, 1.3 });
			characteristicsEN.put(CharacteristicInfo.C_META_DATA.getName(), 
					DataUtil.createMetaData(characteristicsEN));
			simulatorContext.bind(ppi,
			    new EnumPropertyProxyImpl(ppi.toString(),null), characteristicsEN);

			ppi = new URIName(null, DEFAULT_AUTHORITY,
				    "StringPropertyProxyImpl", null);

			Attributes characteristicsS = new org.epics.css.dal.directory.Attributes();
			characteristicsS.put(PropertyCharacteristics.C_DESCRIPTION,
			    "Simulated Property");
			characteristicsS.put(PropertyCharacteristics.C_POSITION, new Double(0));
			characteristicsS.put(PropertyCharacteristics.C_PROPERTY_TYPE,
			    "property");

			simulatorContext.bind(ppi,
			    new StringPropertyProxyImpl(ppi.toString(), this), characteristicsS);

			//add devices
			for (int i = 0; i < 5; i++) {
				String uniqueName = "test/PS" + i;
				URIName uri = new URIName(null, null, uniqueName, null);
				DeviceDescriptor desc = new DeviceDescriptorImpl();
				desc.setName(uniqueName);
				desc.setClassType(AbstractDevice.class);
				desc.putAttributeValue(DALDescriptor.DESCRIPTOR_TYPE,
				    DescriptorType.DEVICE);
				simulatorContext.bind(uri, desc);
			}

			return simulatorContext;
		} catch (NamingException e) {
			throw new RuntimeException("Failed to instantiate context.", e);
		}
	}

	public org.csstudio.dal.simple.RemoteInfo createRemoteInfo(String uniqueName)
		throws NamingException
	{
		return new org.csstudio.dal.simple.RemoteInfo(PLUG_TYPE, uniqueName, null, null);
	}

	@Override
	protected Class<? extends SimpleProperty<?>> getPropertyImplementationClass(String uniquePropertyName) {
		return null;
	}

	@Override
	protected Class<? extends PropertyProxy<?,?>> getPropertyProxyImplementationClass(String uniquePropertyName) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getDeviceProxyImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends DeviceProxy<?>> getDeviceProxyImplementationClass(String uniqueDeviceName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public DeviceProxy<?> getDeviceProxyFromCache1(String uniqueName) {
		DeviceProxy<?> proxy = super._getDeviceProxyFromCache(uniqueName);
		return proxy;
	}
	
	public DirectoryProxy<?> getDirectoryProxyFromCache1(String uniqueName) {
		DirectoryProxy<?> proxy = super._getDirectoryProxyFromCache(uniqueName);
		return proxy;
	}
}

/* __oOo__ */
