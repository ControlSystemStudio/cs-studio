package org.epics.css.dal.tango;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.epics.css.dal.RemoteException;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.ConnectionException;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.device.AbstractDevice;
import org.epics.css.dal.impl.PropertyUtilities;
import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.ApiDefs;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.Database;

/**
 * 
 * <code>TangoPlug</code> is the DAL plug for the tango control system.
 * It uses the TangoORB library to connect to the remote server. 
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class TangoPropertyPlug extends AbstractPlug {

	/** Plug type string */
	public static final String PLUG_TYPE = "TANGOProperty";
	
	/** Default authority */
	public static final String DEFAULT_AUTHORITY = "DEFAULT";
	
	/** Singleton instance of plug */
	private static TangoPropertyPlug instance;
	
	private Database database;
	private HashMap<String,fr.esrf.TangoApi.DeviceProxy> deviceProxies;
	
	/**
	 * Returns the instance of this class with null properties. This
	 * method returns the singleton instance of the plug.
	 * 
	 * @return the singleton instance of the TangoPlug
	 */
	public static final TangoPropertyPlug getInstance()
	{
		return TangoPropertyPlug.getInstance((Properties)null);
	}

	/**
	 * Returns the instance of this plug with specified configuration.
	 * This method returns the singleton instance. 
	 * 
	 * @param conf configuration properties
	 * 
	 * @return the singleton instance of the TangoPlug
	 */
	public static final synchronized TangoPropertyPlug getInstance(Properties conf)
	{
		if (TangoPropertyPlug.instance == null) {
			TangoPropertyPlug.instance = new TangoPropertyPlug(conf);
		}

		return TangoPropertyPlug.instance;
	}
	
	/**
	 * Returns the instance of this plug with specified configuration. This
	 * method returns a new TangoPlug instance, which is associated with the
	 * given context.
	 * 
	 * @param conf configuration properties
	 * @see AbstractPlug#getInstance(AbstractApplicationContext)
	 * 
	 * @return the tango plug instance
	 */
	public static final synchronized TangoPropertyPlug getInstance(AbstractApplicationContext ctx)
	{
		return new TangoPropertyPlug(ctx);
	}
	
	/**
	 * Constructs a TangoPlug.
	 * 
	 * @param configuration the configuration
	 */
	private TangoPropertyPlug(Properties configuration) {
		super(configuration);
		initialize();
	}
	
	/**
	 * Constructs a new TangoPlug.
	 * 
	 * @param ctx the application context
	 */
	private TangoPropertyPlug(AbstractApplicationContext ctx) {
		super(ctx);
		initialize();
	}
	
	/**
	 * Initializes this plug and establish connection to tango database.
	 * 
	 * @throws ConnectionException if failed to connect to tango database
	 */
	private void initialize() {
		getConfiguration().putAll(System.getProperties());
		try {
			deviceProxies = new HashMap<String,fr.esrf.TangoApi.DeviceProxy>();
			database = ApiUtil.get_db_obj();
			getLogger().log(Level.FINE,database.get_info());
			ApiUtil.set_asynch_cb_sub_model(ApiDefs.PUSH_CALLBACK);			
		} catch (DevFailed e) {
			getLogger().log(Level.SEVERE,"Could not connect to the Tango database.",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPlugType()
	 */
	@Override
	public String getPlugType() {
		return TangoPropertyPlug.PLUG_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewDeviceProxy(java.lang.String, java.lang.Class)
	 */
	@Override
	protected <T extends DeviceProxy> T createNewDeviceProxy(String uniqueName, Class<T> type) {
		throw new UnsupportedOperationException("Devices not supported with this plug.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewDirectoryProxy(java.lang.String)
	 */
	@Override
	protected DirectoryProxy createNewDirectoryProxy(String uniqueName) {
		// directory is already added to cache in createNewPropertyProxy method
		throw new RuntimeException("Error in factory implementation, PropertyProxy must be created first.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#createNewPropertyProxy(java.lang.String, java.lang.Class)
	 */
	@Override
	protected <T extends PropertyProxy<?>> T createNewPropertyProxy(String uniqueName, Class<T> type) throws ConnectionException {
		try {
			PropertyProxy<?> p = type.getConstructor(String.class).newInstance(uniqueName);
			// adding to directory cache as well
			if (p instanceof DirectoryProxy) {
				putDirectoryProxyToCache((DirectoryProxy)p);
			}
			
			if (p instanceof PropertyProxyImpl) {
				PropertyName pn = new PropertyName(uniqueName);
				((PropertyProxyImpl<?>)p).initialize(getTangoDeviceProxy(pn.getDeviceName()));
			}
			
			return type.cast(p);
		} catch (Exception e) {
			throw new ConnectionException(this,"Failed to instantitate property proxy '"+uniqueName+"' for type '"+type.getName()+"'.",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getDeviceImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends AbstractDevice> getDeviceImplementationClass(String uniqueDeviceName) {
		throw new UnsupportedOperationException("Devices not supported in this plug.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getDeviceProxyImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends DeviceProxy> getDeviceProxyImplementationClass(String uniqueDeviceName) {
		throw new UnsupportedOperationException("Devices not supported in this plug.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends SimpleProperty<?>> getPropertyImplementationClass(String uniquePropertyName) {
		PropertyName pn = new PropertyName(uniquePropertyName);
		try {
			return ProxyUtilities.getPropertyImplementationClass(pn.getDeviceName(),pn.getPropertyName());
		} catch (DevFailed e) {
			getLogger().log(Level.WARNING, "Could not obtain property implementation class for '" + uniquePropertyName +"'.",e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyProxyImplementationClass(java.lang.String)
	 */
	@Override
	protected Class<? extends PropertyProxy<?>> getPropertyProxyImplementationClass(String uniquePropertyName) {
		PropertyName pn = new PropertyName(uniquePropertyName);
		try {
			return ProxyUtilities.getPropertyProxyImplementationClass(pn.getDeviceName(),pn.getPropertyName());
		} catch (DevFailed e) {
			getLogger().log(Level.WARNING, "Could not obtain property proxy implementation class for '" + uniquePropertyName +"'.",e);
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.Class)
	 */
	@Override
	public Class<?extends SimpleProperty<?>> getPropertyImplementationClass(
			    Class<?extends SimpleProperty<?>> type, String propertyName) {
		if (type != null) {
			return PropertyUtilities.getImplementationClass(type);
		} else {
			return getPropertyImplementationClass(propertyName);
		} 
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#getPropertyProxyImplementationClass(java.lang.Class, java.lang.Class, java.lang.String)
	 */
	@Override
	public Class<?extends PropertyProxy<?>> getPropertyProxyImplementationClass(
		    Class<?extends SimpleProperty<?>> type, Class<?extends SimpleProperty<?>> implementationType, String uniquePropertyName) throws RemoteException
	{
		PropertyName pn = new PropertyName(uniquePropertyName);
		Class<? extends PropertyProxy<?>> impl = null;
		try {
			impl = ProxyUtilities.getPropertyProxyImplementationClass(pn.getDeviceName(),pn.getPropertyName(), type);
		} catch (DevFailed e) {
			getLogger().log(Level.WARNING, "Could not obtain property proxy implementation class for '" + uniquePropertyName +"' of property type '" + implementationType +"'.",e);
		}
			
		if (impl == null) {
			impl = super.getPropertyProxyImplementationClass(type,implementationType, uniquePropertyName);
		}
		if (impl == null) {
			return ProxyUtilities.getProxyImplementationClass(type);
		} else {
			return impl;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractPlug#releaseInstance()
	 */
	@Override
	public void releaseInstance() throws Exception {
		//	
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.context.PlugContext#createRemoteInfo(java.lang.String)
	 */
	public RemoteInfo createRemoteInfo(String uniqueName) throws NamingException {
		return new RemoteInfo(uniqueName, TangoPropertyPlug.DEFAULT_AUTHORITY, TangoPropertyPlug.PLUG_TYPE);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.context.PlugContext#getDefaultDirectory()
	 */
	public DirContext getDefaultDirectory() {
		throw new UnsupportedOperationException("Directory not supported in tango.");
	}
	
	/**
	 * Constructs a new Tango Device Proxy for the given proxy name. The proxy
	 * is taken from cache. If it doesn't exist yet, a new one is created
	 * and then cached.
	 * 
	 * @param devName the device name
	 * @return the tango device proxy
	 * @throws DevFailed if creation of the proxy failed
	 */
	private fr.esrf.TangoApi.DeviceProxy getTangoDeviceProxy(String devName) throws DevFailed {
		fr.esrf.TangoApi.DeviceProxy proxy = deviceProxies.get(devName);
		if (proxy == null) {
			proxy = new fr.esrf.TangoApi.DeviceProxy(devName);
			deviceProxies.put(devName,proxy);
		}
		return proxy;
	}
}
