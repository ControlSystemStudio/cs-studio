package de.desy.css.dal.tine;

import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.context.ConnectionException;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.device.AbstractDevice;
import org.epics.css.dal.impl.DoublePropertyImpl;
import org.epics.css.dal.impl.PropertyUtilities;
import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;

/**
 * Implementation of DAL plugin for TINE.
 * 
 * @author Jaka Bobnar, Cosylab
 * 
 */
public class TINEPlug extends AbstractPlug {

	/** Plug type string */
	public static final String PLUG_TYPE = "TINE";

	/** Default authority */
	public static final String DEFAULT_AUTHORITY = "DEFAULT";

	/** Singleton instance of plug */
	private static TINEPlug instance;

	/**
	 * Returns the instance of this class with null properties
	 * 
	 * @return
	 */
	public static final TINEPlug getInstance() {
		return getInstance(null);
	}

	/**
	 * Returns the instance of this plug with specified configuration
	 * 
	 * @param conf
	 *            configuration properties
	 * @return
	 */
	public static final synchronized TINEPlug getInstance(Properties conf) {
		if (instance == null) {
			instance = new TINEPlug(conf);
		}

		return instance;
	}

	/**
	 * Singleton object.
	 * 
	 * @param configuration
	 */
	private TINEPlug(Properties configuration) {
		super(configuration);
		if (configuration != null) {
			getConfiguration().putAll(configuration);
		}
		getConfiguration().putAll(System.getProperties());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPlugType()
	 */
	@Override
	public String getPlugType() {
		return PLUG_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewDeviceProxy(java.lang.String,
	 *      java.lang.Class)
	 */
	@Override
	protected <T extends DeviceProxy> T createNewDeviceProxy(String uniqueName,
			Class<T> type) {
		throw new UnsupportedOperationException("Devices not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewDirectoryProxy(java.lang.String)
	 */
	@Override
	protected DirectoryProxy createNewDirectoryProxy(String uniqueName) {
		// directory is already added to cache in createNewPropertyProxy method
		throw new RuntimeException(
				"Error in factory implementation, PropertyProxy must be created first.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewPropertyProxy(java.lang.String,
	 *      java.lang.Class)
	 */
	@Override
	protected <T extends PropertyProxy> T createNewPropertyProxy(
			String uniqueName, Class<T> type) throws ConnectionException {
		try {
			PropertyProxy p = type.getConstructor(String.class).newInstance(
					uniqueName);
			// adding to directory cache as well
			if (p instanceof DirectoryProxy) {
				putDirectoryProxyToCache((DirectoryProxy) p);
			}

			return type.cast(p);
		} catch (Exception e) {
			throw new ConnectionException(this,
					"Failed to instantitate property proxy '" + uniqueName
							+ "' for type '" + type.getName() + "'.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractPlug#getDeviceImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends AbstractDevice> getDeviceImplementationClass(
			String uniqueDeviceName) {
		throw new UnsupportedOperationException("Devices not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractPlug#getDeviceProxyImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends DeviceProxy> getDeviceProxyImplementationClass(
			String uniqueDeviceName) {
		throw new UnsupportedOperationException("Devices not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends SimpleProperty> getPropertyImplementationClass(
			String uniquePropertyName) {
		// return
		// PropertyProxyUtilities.getPropertyImplementationClass(uniquePropertyName);
		// AW: HOTFIX!
		return DoublePropertyImpl.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyProxyImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends PropertyProxy> getPropertyProxyImplementationClass(
			String uniquePropertyName) {
		// return
		// PropertyProxyUtilities.getPropertyProxyImplementationClass(uniquePropertyName);
		// AW: HOTFIX!
		return DoublePropertyProxyImpl.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.Class)
	 */
	@Override
	public Class<? extends SimpleProperty> getPropertyImplementationClass(
			Class<? extends SimpleProperty> type, String propertyName) {
		Class<? extends SimpleProperty> impl = super
				.getPropertyImplementationClass(type, propertyName);
		if (impl == null)
			return PropertyUtilities.getImplementationClass(type);
		else
			return impl;
	}

	public Class<? extends PropertyProxy> getPropertyProxyImplementationClass(
			Class<? extends SimpleProperty> type,
			Class<? extends SimpleProperty> implementationType,
			String uniquePropertyName) {
		Class<? extends PropertyProxy> impl = super
				.getPropertyProxyImplementationClass(type, implementationType,
						uniquePropertyName);
		if (impl == null)
			return PropertyProxyUtilities.getProxyImplementationClass(type);
		else
			return impl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractPlug#releaseInstance()
	 */
	@Override
	public void releaseInstance() throws Exception {
		//		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.context.PlugContext#createRemoteInfo(java.lang.String)
	 */
	public RemoteInfo createRemoteInfo(String uniqueName)
			throws NamingException {
		return new RemoteInfo(uniqueName, DEFAULT_AUTHORITY, PLUG_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.context.PlugContext#getDefaultDirectory()
	 */
	public DirContext getDefaultDirectory() {
		return null;
	}

}
