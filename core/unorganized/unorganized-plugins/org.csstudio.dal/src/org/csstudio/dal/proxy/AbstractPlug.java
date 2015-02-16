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

package org.csstudio.dal.proxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.csstudio.dal.EventSystemListener;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.ConnectionException;
import org.csstudio.dal.context.Identifier;
import org.csstudio.dal.context.IdentifierUtilities;
import org.csstudio.dal.context.PlugContext;
import org.csstudio.dal.context.PlugEvent;
import org.csstudio.dal.device.AbstractDevice;
import org.csstudio.dal.impl.PropertyUtilities;
import org.csstudio.dal.spi.AbstractFactory;
import org.csstudio.dal.spi.AbstractFactorySupport;
import org.csstudio.dal.spi.Plugs;

import com.cosylab.util.ListenerList;


/**
 * This is abstract plug class which helps plug implementators write own
 * plug. This plug has implemented support for following features: proxy
 * object sharing.
 *
 * @author ikriznar
 */
public abstract class AbstractPlug implements PlugContext
{
	/**
	 * By default caching of proxy references is enabled.
	 */
	private static final boolean DEFAULT_CACHING_ENABLED=true;
	
	static class AbstractProxyHolder {
		int count;
		protected String id;
		public AbstractProxyHolder(String id) {
			count = 1;
			this.id=id;
		}
		
		static String toID(Proxy<?> p) {
			return toID(p.getUniqueName(),p.getClass());
		}
		static String toID(String name, Class<?> type) {
			return name+"::"+type.getName();
		}
	}
	
	class PropertyProxyHolder extends AbstractProxyHolder
	{
		PropertyProxyHolder(PropertyProxy<?,?> p)
		{
			super(toID(p));
			proxy = p;
		}

		PropertyProxy<?,?> proxy;
	}

	class DirectoryProxyHolder extends AbstractProxyHolder
	{
		DirectoryProxyHolder(DirectoryProxy<?> p)
		{
			super(toID(p));
			proxy = p;
		}

		DirectoryProxy<?> proxy;
	}

	class DeviceProxyHolder extends AbstractProxyHolder
	{
		DeviceProxyHolder(DeviceProxy<?> p)
		{
			super(toID(p));
			proxy = p;
		}

		DeviceProxy<?> proxy;
	}

	private Map<String, PropertyProxyHolder> propertyCache;
	private Map<String, DirectoryProxyHolder> directoryCache;
	private Map<String, DeviceProxyHolder> deviceCache;
	private boolean cachingEnabled = DEFAULT_CACHING_ENABLED;
	private Properties configuration;
	private Map<Class<?extends AbstractDevice>, Class<?extends AbstractDevice>> deviceImplementationClasses =
		new HashMap<Class<?extends AbstractDevice>, Class<?extends AbstractDevice>>();
	private Map<Class<?extends SimpleProperty<?>>, Class<?extends SimpleProperty<?>>> propertyImplementationClasses =
		new HashMap<Class<?extends SimpleProperty<?>>, Class<?extends SimpleProperty<?>>>();
	private Map<Class<?extends AbstractDevice>, Class<?extends DeviceProxy<?>>> deviceProxiesImplementationClasses =
		new HashMap<Class<?extends AbstractDevice>, Class<?extends DeviceProxy<?>>>();
	private Map<Class<?extends SimpleProperty<?>>, Class<?extends PropertyProxy<?,?>>> propertyProxiesImplementationClasses =
		new HashMap<Class<?extends SimpleProperty<?>>, Class<?extends PropertyProxy<?,?>>>();
	protected ListenerList plugListeners;
	private Identifier identifier;
	private Logger logger;
	protected boolean debug = false;
	// TODO (jpenning) no longer in use?
	protected AbstractApplicationContext applicationContext;

	/**
	 * Constructs a new plug instance which is associated with one specific
	 * application context. 
	 * 
	 * @param applicationContext
	 */
	protected AbstractPlug(AbstractApplicationContext applicationContext) {
		this(applicationContext.getConfiguration());
		this.applicationContext = applicationContext;
	}
	
	/**
	 * Creates new plug instance with proxy caching enabled.
	 */
	protected AbstractPlug(Properties configuration)
	{
		this(configuration,DEFAULT_CACHING_ENABLED);
	}

	/**
	     * Creates new plug instance.
	     * @param cachingEnabled disables proxy caching if <code>false</code>
	     */
	protected AbstractPlug(Properties configuration, boolean cachingEnabled)
	{
		super();
		// at the moment we do not use weak reference
		propertyCache = new HashMap<String, PropertyProxyHolder>(100);
		directoryCache = new HashMap<String, DirectoryProxyHolder>(100);
		deviceCache = new HashMap<String, DeviceProxyHolder>(100);
		this.cachingEnabled = cachingEnabled;

		if (configuration == null) {
			this.configuration = new Properties();
		} else {
			this.configuration = (Properties)configuration.clone();
		}
		
		getLogger().info("'"+getPlugType()+"' started.");
	}

	/**
	 * Creates new proxy for remote object. The proxy object should be
	 * returned immediatelly, even if this means that connection process to
	 * remote entity was initiated in parallel and is not finished when proxy
	 * object is returned.<p>Design contract is that in case when
	 * lookup of remote entity takes long time, than proxy object is returned
	 * immediatelly even if not connected and if connection fails,  it may
	 * fail later when appropriate property is already created and delivered
	 * to application layer. In this case appropriate event must be fired by
	 * the proxy.</p>
	 *
	 * @param uniqueName unique remote name or remote entity
	 * @param type implementation class of returned proxy. If type is simply
	 *        PropertyProxy than plug decides  which proxy implementation to
	 *        use.
	 *
	 * @return proxy for remote property
	 *
	 */
	protected abstract <T extends PropertyProxy<?,?>> T createNewPropertyProxy(
	    String uniqueName, Class<T> type) throws ConnectionException;

	/**
	 * Creates new directory for proxy. This method is called only if
	 * <code>createNewPropertyProxy</code> returns object that does not
	 * imlement DirectoryProxy also.<p>Design contract is that in case
	 * when lookup of remote entity takes long time, than proxy object is
	 * returned immediatelly even if not connected and if connection fails,
	 * it may fail later when appropriate property is already created and
	 * delivered to application layer. In this case appropriate event must be
	 * fired by the proxy.</p>
	 *
	 * @param uniqueName
	 *
	 * @return directory for proxy
	 */
	protected abstract DirectoryProxy<?> createNewDirectoryProxy(String uniqueName)
		throws ConnectionException;

	/**
	 * Creates new proxy for remote object. The proxy object should be
	 * returned immediatelly, even if this means that connection process to
	 * remote entity was initiated in parallel and is not finished when proxy
	 * object is returned.<p>Design contract is that in case when
	 * lookup of remote entity takes long time, than proxy object is returned
	 * immediatelly even if not connected and if connection fails,  it may
	 * fail later when appropriate property is already created and delivered
	 * to application layer. In this case appropriate event must be fired by
	 * the proxy.</p>
	 *
	 * @param <T> exact DeviceProxy type
	 * @param uniqueName unique remote name or remote entity
	 * @param type implementation class of returned proxy. If type is simply
	 *        PropertyProxy than plug decides  which proxy implementation to
	 *        use.
	 *
	 * @return proxy for remote property
	 *
	 */
	protected abstract <T extends DeviceProxy<?>> T createNewDeviceProxy(
	    String uniqueName, Class<T> type) throws ConnectionException;

	/**
	 * Returns plug type string, which is distinguishing for plug which
	 * creates  proxies for particular communication layer.<p>For
	 * example plug that connects to EPICS device my return string "EPICS".</p>
	 *
	 * @return plug distinguishing type name
	 */
	public abstract String getPlugType();

	
	/**
	 * Returns property implementation class
	 *  
	 * @param uniquePropertyName property name
	 * @return property implementation class
	 * @throws RemoteException if remote request was issued and did not sucseede
	 */
	protected abstract Class<?extends SimpleProperty<?>>  getPropertyImplementationClass (String uniquePropertyName) throws RemoteException;
	
	/**
	 * Returns property proxy implementation class
	 * 
	 * @param uniquePropertyName property name
	 * @return property proxy  implementation class
	 * @throws RemoteException if remote request was issued and was not successfull
	 */
	protected abstract Class<?extends PropertyProxy<?,?>> getPropertyProxyImplementationClass(String uniquePropertyName) throws RemoteException;
	
	
	/**
	 * Returns proxy object for unique name.  Object is obtained by
	 * procedure in following order:
	 *  <ol>
	 *      <li>Internal cache of used objects is checked if proxy
	 *      with this name already exists.</li>
	 *      <li>Internal cache of object no longer used but not yet
	 *      deleted is checked if proxy with this name already exists.</li>
	 *      <li>Only if upper methods fails, new proxy is created
	 *      and cached in used queue.</li>
	 *  </ol>
	 *  <p>For returned proxy use count is increased for one.</p>
	 *
	 * @param uniqueName an unique name of remote object
	 * @param type class of property proxy, if <code>PropertyProxy.class</code>
	 *        than plug implementation decide which proxy will be returned.
	 *
	 * @return reused or new proxy object representing unique name
	 *
	 * @throws RemoteException if instantiation of object fails
	 * @throws NullPointerException if unique name parameter is null
	 */
	public <TT extends PropertyProxy<?,?>> TT getPropertyProxy(String uniqueName,
	    Class<TT> type) throws ConnectionException
	{
		if (uniqueName == null) {
			throw new NullPointerException("uniqueName");
		}
		try {
	
			PropertyProxy<?,?> p;
			if (PropertyProxy.class.equals(type)) {
				p= getPropertyProxyFromCache(uniqueName);
			} else {
				p= getPropertyProxyFromCache(uniqueName,type);
			}
			
			if (p != null) {
				if (!type.isInstance(p)) {
					throw new ConnectionException(this,
					    "PropertyProxy in cache for '" + uniqueName
					    + "' is of type '" + p.getClass().getName()
					    + "' and not of expected type '" + type.getName() + "'.");
				}
	
				getLogger().debug("'"+uniqueName+"' reused    (c:"+p.getConnectionInfo()+" t:"+type.getName()+").");
				return type.cast(p);
			}
		
			TT pp = createNewPropertyProxy(uniqueName, type);
			
			getLogger().debug("'"+uniqueName+"' created   (c:"+pp.getConnectionInfo()+" t:"+type.getName()+").");

			putPropertyProxyToCache(pp);

			return pp;
		} catch (ConnectionException e) {
			getLogger().warn("'"+uniqueName+"' failed (t:"+type.getName()+").", e);
			throw e;
		} catch (Exception e) {
			getLogger().error("'"+uniqueName+"' connect failed, internal error possible (t:"+type.getName()+").", e);
			throw new ConnectionException(this, "Internal error while connecting to '"+uniqueName+"'.", e);
		}

	}

	/**
	 * Returns proxy object for unique name.  Object is obtained by
	 * procedure in following order:
	 *  <ol>
	 *      <li>Internal cache of used objects is checked if proxy
	 *      with this name already exists.</li>
	 *      <li>Internal cache of object no longer used but not yet
	 *      deleted is checked if proxy with this name already exists.</li>
	 *      <li>Only if upper methods fails, new proxy is created
	 *      and cached in used queue.</li>
	 *  </ol>
	 *  <p>For returned proxy use count in increased for one.</p>
	 *
	 * @param uniqueName an unique name of remote object
	 *
	 * @return reused or new proxy object representing unique name
	 *
	 * @throws ConnectionException if instantiation of object fails
	 */
	public PropertyProxy<?,?> getPropertyProxy(String uniqueName)
		throws ConnectionException
	{
		return getPropertyProxy(uniqueName, PropertyProxy.class);
	}

	/**
	 * Returns directory for proxy object of unique name.  Object is
	 * obtained by procedure in following order:
	 *  <ol>
	 *      <li>Internal cache of used objects is checked if proxy
	 *      with this name already exists.</li>
	 *      <li>Internal cache of object no longer used but not yet
	 *      deleted is checked if proxy with this name already exists.</li>
	 *      <li>Only if upper methods fails, new proxy is created
	 *      and cached in used queue.</li>
	 *  </ol>
	 *  <p>For returned proxy use count in increased for one.</p>
	 *
	 * @param uniqueName an unique name of remote object
	 *
	 * @return reused or new proxy object representing unique name
	 *
	 * @throws ConnectionException if instantiation of object fails
	 * @throws NullPointerException  if unique name parameter is null
	 */
	public DirectoryProxy<?> getDirectoryProxy(String uniqueName)
		throws ConnectionException
	{
		if (uniqueName == null) {
			throw new NullPointerException("uniqueName");
		}

		DirectoryProxy<?> d = getDirectoryProxyFromCache(uniqueName);

		if (d != null) {
			return d;
		}

		d = createNewDirectoryProxy(uniqueName);

		putDirectoryProxyToCache(d);

		return d;
	}

	/**
	 * Releases the proxy from cache and destroys it if proxy use count becomes 0.
	 *
	 * @param proxy Proxy to release
	 */
	public void releaseProxy(Proxy<?> proxy)
	{
		if (proxy == null) {
			throw new NullPointerException("proxy");
		}

		boolean canDestroy = true;

		synchronized (this) {
			if (proxy instanceof DirectoryProxy) {
				canDestroy = releaseDirectoryProxyFromCache((DirectoryProxy<?>)proxy);
			}

			if (proxy instanceof PropertyProxy) {
				canDestroy &= releasePropertyProxyFromCache((PropertyProxy<?,?>)proxy);
			}
			
			if (proxy instanceof DeviceProxy) {
				canDestroy &=releaseDeviceProxyFromCache((DeviceProxy<?>)proxy);
			}
		}

		if (canDestroy) {
			try {
				proxy.destroy();
				getLogger().debug("'"+proxy.getUniqueName()+"' destroyed (c:"+proxy.getConnectionInfo()+" t:"+proxy.getClass().getName()+").");
			} catch (Exception e) {
				getLogger().warn("'"+proxy.getUniqueName()+"' destroyed with error (c:"+proxy.getConnectionInfo()+" t:"+proxy.getClass().getName()+").",e);
			}
		} else {
			getLogger().debug("'"+proxy.getUniqueName()+"' release indicated (c:"+proxy.getConnectionInfo()+" t:"+proxy.getClass().getName()+").");
		}
	}

	/**
	 * Destroys immediatelly all proxies which are no longer in use.
	 */
	public void flushCache()
	{
		// TODO: nothing to do at the moment, there is no intermediate cache
	}

	/**
	 * Returns PropertyProxy from cache. Warning: this operation
	 * increases use count by 1.
	 *
	 * @param uniqueName
	 *
	 * @return property proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized PropertyProxy<?,?> getPropertyProxyFromCache(
	    String uniqueName)
	{
		PropertyProxyHolder h = propertyCache.get(uniqueName);

		if (h == null) {
			return null;
		}

		h.count++;

		return h.proxy;
	}

	/**
	 * Returns PropertyProxy from cache. Warning: this operation
	 * increases use count by 1.
	 *
	 * @param uniqueName
	 * @param type
	 *
	 * @return property proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized PropertyProxy<?,?> getPropertyProxyFromCache(
	    String uniqueName, Class<?> type)
	{
		PropertyProxyHolder h = propertyCache.get(AbstractProxyHolder.toID(uniqueName,type));

		if (h == null) {
			return null;
		}

		h.count++;

		return h.proxy;
	}

	/**
	 * Returns DirectoryProxy from cache. Warning: this operation
	 * increases use count by 1.
	 *
	 * @param uniqueName
	 *
	 * @return property proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized DirectoryProxy<?> getDirectoryProxyFromCache(
	    String uniqueName)
	{
		DirectoryProxyHolder h = directoryCache.get(uniqueName);

		if (h == null) {
			return null;
		}

		h.count++;

		return h.proxy;
	}

	/**
	 * Returns DirectoryProxy from cache. Warning: this operation
	 * increases use count by 1.
	 *
	 * @param uniqueName
	 * @param type
	 *
	 * @return property proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized DirectoryProxy<?> getDirectoryProxyFromCache(
	    String uniqueName, Class<?> type)
	{
		DirectoryProxyHolder h = directoryCache.get(AbstractProxyHolder.toID(uniqueName,type));

		if (h == null) {
			return null;
		}

		h.count++;

		return h.proxy;
	}

	/**
	 * Stores PropertyProxy in cache with use count set to 1.
	 *
	 * @param proxy
	 */
	protected synchronized void putPropertyProxyToCache(PropertyProxy<?,?> proxy)
	{
		PropertyProxyHolder h = new PropertyProxyHolder(proxy);
		propertyCache.put(h.id, h);
		if (!propertyCache.containsKey(proxy.getUniqueName())) {
			propertyCache.put(proxy.getUniqueName(), h);
		}
	}

	/**
	 * Stores PropertyProxy in cache with use count set to 1.
	 *
	 * @param proxy
	 */
	protected synchronized void putDirectoryProxyToCache(DirectoryProxy<?> proxy)
	{
		DirectoryProxyHolder h = new DirectoryProxyHolder(proxy);
		directoryCache.put(h.id, h);
		if (!directoryCache.containsKey(proxy.getUniqueName())) {
			directoryCache.put(proxy.getUniqueName(), h);
		}
	}

	/**
	 * Reduces use count for DirectoryProxy for one and removes it form
	 * cache if use count is 0.
	 *
	 * @param proxy
	 *
	 * @return <code>true</code> if cache does not hold any reference to proxy.
	 *         This can happend if proxy was not present in cache or proxy
	 *         use count was reduced to 0.
	 */
	protected synchronized boolean releaseDirectoryProxyFromCache(
	    DirectoryProxy<?> proxy)
	{
		DirectoryProxyHolder h = directoryCache.get(AbstractProxyHolder.toID(proxy));

		if (h == null) {
			return true;
		}

		h.count--;

		if (h.count <= 0) {
			directoryCache.remove(h.id);
			if (directoryCache.get(proxy.getUniqueName())==h) {
				directoryCache.remove(proxy.getUniqueName());
			}

			return true;
		}

		return false;
	}

	/**
	 * Reduces use count for PropertyProxy for one and removes it form
	 * cache if use count is 0.
	 *
	 * @param proxy
	 *
	 * @return <code>true</code> if cache does not hold any reference to proxy.
	 *         This can happend if proxy was not present in cache or proxy
	 *         use count was reduced to 0.
	 */
	protected synchronized boolean releasePropertyProxyFromCache(
	    PropertyProxy<?,?> proxy)
	{
		PropertyProxyHolder h = propertyCache.get(AbstractProxyHolder.toID(proxy));

		if (h == null) {
			return true;
		}

		h.count--;

		if (h.count <= 0) {
			propertyCache.remove(h.id);
			if (propertyCache.get(proxy.getUniqueName())==h) {
				propertyCache.remove(proxy.getUniqueName());
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns proxy object for unique name.  Object is obtained by
	 * procedure in following order:
	 *  <ol>
	 *      <li>Internal cache of used objects is checked if proxy
	 *      with this name already exists.</li>
	 *      <li>Internal cache of object no longer used but not yet
	 *      deleted is checked if proxy with this name already exists.</li>
	 *      <li>Only if upper methods fails, new proxy is created
	 *      and cached in used queue.</li>
	 *  </ol>
	 *  <p>For returned proxy use count in increased for one.</p>
	 *
	 * @param uniqueName an unique name of remote object
	 *
	 * @return reused or new proxy object representing unique name
	 *
	 * @throws ConnectionException if instantiation of object fails
	 */
	public DeviceProxy<?> getDeviceProxy(String uniqueName)
		throws ConnectionException
	{
		return getDeviceProxy(uniqueName, DeviceProxy.class);
	}

	/**
	 * Returns proxy object for unique name.  Object is obtained by
	 * procedure in following order:
	 *  <ol>
	 *      <li>Internal cache of used objects is checked if proxy
	 *      with this name already exists.</li>
	 *      <li>Internal cache of object no longer used but not yet
	 *      deleted is checked if proxy with this name already exists.</li>
	 *      <li>Only if upper methods fails, new proxy is created
	 *      and cached in used queue.</li>
	 *  </ol>
	 *  <p>For returned proxy use count in increased for one.</p>
	 *
	 * @param <TT> exact devyce proxy type
	 * @param uniqueName an unique name of remote object
	 * @param type class of property proxy, if <code>PropertyProxy.class</code>
	 *        than plug implementation decide which proxy will be returned.
	 *
	 * @return reused or new proxy object representing unique name
	 *
	 * @throws ConnectionException if instantiation of object fails
	 * @throws NullPointerException if unique name parameter is null
	 */
	public <TT extends DeviceProxy<?>> TT getDeviceProxy(String uniqueName,
	    Class<TT> type) throws ConnectionException
	{
		if (uniqueName == null) {
			throw new NullPointerException("uniqueName");
		}

		
		DeviceProxy<?> p;
		
		if (DeviceProxy.class.equals(type)) {
			p= getDeviceProxyFromCache(uniqueName);
		} else {
			p= getDeviceProxyFromCache(uniqueName,type);
		}

		if (p != null) {
			if (!type.isInstance(p)) {
				throw new ConnectionException(this,
				    "DeviceProxy in cache for '" + uniqueName
				    + "' is of type '" + p.getClass().getName()
				    + "' and not of expected type '" + type.getName() + "'.");
			}

			return type.cast(p);
		}

		TT pp = createNewDeviceProxy(uniqueName, type);

		putDeviceProxyToCache(pp);

		return pp;
	}

	/**
	 * Returns DeviceProxy from cache. Warning: this operation
	 * increases use count by 1.
	 *
	 * @param uniqueName
	 *
	 * @return Device proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized DeviceProxy<?> getDeviceProxyFromCache(
	    String uniqueName)
	{
		DeviceProxyHolder h = deviceCache.get(uniqueName);

		if (h == null) {
			return null;
		}

		h.count++;

		return h.proxy;
	}
	/**
	 * Returns DeviceProxy from cache. Warning: this operation
	 * increases use count by 1.
	 *
	 * @param uniqueName
	 * @param type
	 *
	 * @return Device proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized DeviceProxy<?> getDeviceProxyFromCache(
	    String uniqueName, Class<?> type)
	{
		DeviceProxyHolder h = deviceCache.get(AbstractProxyHolder.toID(uniqueName,type));

		if (h == null) {
			return null;
		}

		h.count++;

		return h.proxy;
	}

	/**
	 * Stores DeviceProxy in cache with use count set to 1.
	 *
	 * @param proxy
	 */
	protected synchronized void putDeviceProxyToCache(DeviceProxy<?> proxy)
	{
		DeviceProxyHolder h = new DeviceProxyHolder(proxy);
		deviceCache.put(h.id, h);
		if (!deviceCache.containsKey(proxy.getUniqueName())) {
			deviceCache.put(proxy.getUniqueName(), h);
		}
	}

	/**
	 * Reduces use count for DeviceProxy for one and removes it form
	 * cache if use count is 0.
	 *
	 * @param proxy
	 *
	 * @return <code>true</code> if cache does not hold any reference to proxy.
	 *         This can happend if proxy was not present in cache or proxy
	 *         use count was reduced to 0.
	 */
	protected synchronized boolean releaseDeviceProxyFromCache(
	    DeviceProxy<?> proxy)
	{
		DeviceProxyHolder h = deviceCache.get(AbstractProxyHolder.toID(proxy));

		if (h == null) {
			return true;
		}

		h.count--;

		if (h.count <= 0) {
			deviceCache.remove(h.id);
			if (deviceCache.get(proxy.getUniqueName())==h) {
				deviceCache.remove(proxy.getUniqueName());
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns <code>true</code> if proxy caching is enabled for this
	 * plug.
	 *
	 * @return <code>true</code> if proxy caching is enabled for this plug
	 */
	public boolean isCachingEnabled()
	{
		return cachingEnabled;
	}

	/**
	 * Returns the class that implements property interface given as parameter.
	 *
	 * @param type property type (can be null)
	 * @param propertyName property name
	 * 
	 * @return implementor of given interface
	 * @throws RemoteException if remote request was issued and did not succeed
	 */
	public Class<?extends SimpleProperty<?>> getPropertyImplementationClass(
	    Class<?extends SimpleProperty<?>> type, String propertyName) throws RemoteException
	{
		Class<?extends SimpleProperty<?>> propImplClass = null;
		if (type !=null)
		{	
			/*
			 * Try first with internal override for implementations
			 */
			propImplClass = propertyImplementationClasses.get(type);
			if (propImplClass != null && propImplClass.isAssignableFrom(type)) {
				throw new RemoteException(this,"Property implementation type '" + propImplClass.getName() +
						"' loaded from the internal cache does not implement the requested type '" + type.getName() +"'.");
			}
			/*
			 * Try default llookup, which will return DAL default implementation classes for defautl DAL Property interfaces.
			 */
			if (propImplClass == null) {
				propImplClass =  PropertyUtilities.getImplementationClass(type);
			}
		}
		if (propImplClass == null)
		{
			/*
			 * Resolve implementation class according to remote name. This is strongly plug implementation dependant.
			 */
			propImplClass = getPropertyImplementationClass(propertyName);
			if (propImplClass != null && propImplClass.isAssignableFrom(type)) {
				throw new RemoteException(this,"Property implementation type '" + propImplClass.getName() +
						"' returned by a remote call for the property '" + propertyName +"' does not implement the requested type"+
						(type != null ? "'" + type.getName() + "'.": "."));
			}

		}
		return propImplClass;
	}
	
	/**
	 * Returns the class that implements device interface given as parameter.
	 *
	 * @param type
	 * @return implementor of given interface
	 * @throws RemoteException if remote request was issued and did not succeed
	 */
	public Class<?extends AbstractDevice> getDeviceImplementationClass(
	    Class<?extends AbstractDevice> type, String deviceName) throws RemoteException
	{
		Class<?extends AbstractDevice> deviceImplClass = null;
		if (type !=null)
		{
			deviceImplClass = deviceImplementationClasses.get(type);
			if (deviceImplClass != null && deviceImplClass.isAssignableFrom(type)) {
				throw new RemoteException(this,"Device implementation type '" + deviceImplClass.getName() +
						"' loaded from the internal cache does not implement the requested type '" + type.getName() +"'.");
			}
		}
		if (deviceImplClass == null)
		{
			deviceImplClass = getDeviceImplementationClass(deviceName);
			if (deviceImplClass != null && deviceImplClass.isAssignableFrom(type)) {
				throw new RemoteException(this,"Device implementation type '" + deviceImplClass.getName() +
						"' returned by a remote call for device '" + deviceName + "' does not implement the requested type" +
						(type != null ? "'" + type.getName() + "'.": "."));
			}
		}
		return deviceImplClass;
	}

	/**
	 * Returns device implementation class
	 *  
	 * @param uniqueDeviceName device name
	 * @return device implementation class
	 */
	protected abstract Class<?extends AbstractDevice>  getDeviceImplementationClass (String uniqueDeviceName);

	/**
	 * Returns the class that implements property proxy that can be connected to the given
	 * interface of a property.
	 *
	 * By default is lookup done first by type if it's not <code>null</code> then by property name. 
	 *
	 *
	 * @param type property type (can be <code>null</code>)
	 * @param implementationType property implementation type (can be <code>null</code>)
	 * @param uniquePropertyName name
	 *  
	 * @return property proxy for the given property 
	 * @throws RemoteException if remote request was issued and was not successfull
	 */
	public Class<?extends PropertyProxy<?,?>> getPropertyProxyImplementationClass(
	    Class<?extends SimpleProperty<?>> type, Class<?extends SimpleProperty<?>> implementationType, String uniquePropertyName) throws RemoteException
	{
		Class<? extends PropertyProxy<?,?>> proxyImplType = null;
		
		if (type !=null) {
			proxyImplType = propertyProxiesImplementationClasses.get(type);
		}
		if (proxyImplType == null && implementationType != null)
		{
			proxyImplType = propertyProxiesImplementationClasses.get(implementationType);
		}
		if (proxyImplType == null) {
			proxyImplType = getPropertyProxyImplementationClass(uniquePropertyName);
		}
		return proxyImplType;
	}
	
	/**
	 * Returns the class that implements device proxy that can be connected to the given
	 * interface of an abstrac device.
	 *
	 * @param type device type (can be <code>null</code>)
	 * @param implementationType device implementation type (can be <code>null</code>)
	 * @param uniqueDeviceName name
	 * @throws RemoteException if remote request was issued and was not successfull

	 * @return implementor of given interface
	 */
	public Class<?extends DeviceProxy<?>> getDeviceProxyImplementationClass(
	    Class<?extends AbstractDevice> type, Class<?extends AbstractDevice> implementationType, String uniqueDeviceName) throws RemoteException
	{
		Class<? extends DeviceProxy<?>> proxyImplType = null;
		
		if (type !=null) {
			proxyImplType = deviceProxiesImplementationClasses.get(type);
		}
		if (proxyImplType == null && implementationType != null)
		{
			proxyImplType = deviceProxiesImplementationClasses.get(implementationType);
		}
		if (proxyImplType == null) {
			proxyImplType = getDeviceProxyImplementationClass(uniqueDeviceName);
		}
		return proxyImplType;
	}

	/**
	 * Returns device proxy implementation class
	 * 
	 * @param uniqueDeviceName device name
	 * @return device proxy  implementation class
	 * @throws RemoteException if remote request was issued and was not successfull
	 */
	protected abstract Class<?extends DeviceProxy<?>> getDeviceProxyImplementationClass(String uniqueDeviceName) throws RemoteException;

	/**
	 * Puts a device implementation class to a map. When calling getDeviceImplementationClass(Class T)
	 * method, it will return the class that was put in as an <code>impl</code> parameter
	 * to this method, where T is the type parameter of this method.
	 *
	 * @param type interface that should be associated with a class impl
	 * @param impl implementation of the type interface
	 */
	public void registerDeviceImplementationClass(
	    Class<?extends AbstractDevice> type, Class<?extends AbstractDevice> impl)
	{
		deviceImplementationClasses.put(type, impl);
	}

	/**
	 * Puts a property implementation class to a map. When calling getPropertyImplementationClass(Class T)
	 * method, it will return the class that was put in as an <code>impl</code> parameter
	 * to this method, where T is the type parameter of this method.
	 *
	 * @param type interface that should be associated with a class impl
	 * @param impl implementation of the type interface
	 */
	public <T> void registerPropertyImplementationClass(
	    Class<? extends SimpleProperty<T>> type, Class<? extends SimpleProperty<T>> impl)
	{
		propertyImplementationClasses.put(type, impl);
	}

	/**
	 * Puts a device proxy implementation class to a map. When calling
	 * getDeviceProxyImplementationClass(Class T) method, it will return
	 * the class that was put in as an <code>impl</code> parameter
	 * to this method, where T is the type parameter of this method.
	 *
	 * @param type interface that should be associated with a class impl
	 * @param impl device proxy implementor associated with an abstract device class
	 */
	public void registerDeviceProxyImplementationClass(
	    Class<?extends AbstractDevice> type, Class<?extends DeviceProxy<?>> impl)
	{
		deviceProxiesImplementationClasses.put(type, impl);
	}

	/**
	 * Puts a property proxy implementation class to a map. When calling
	 * getPropertyProxyImplementationClass(Class T) method, it will return
	 * the class that was put in as an <code>impl</code> parameter
	 * to this method, where T is the type parameter of this method.
	 *
	 * @param type interface that should be associated with a class impl
	 * @param impl property proxy implementor associated with a simple property class
	 */
	public <T> void registerPropertyProxyImplementationClass(
	    Class<? extends SimpleProperty<T>> type, Class<? extends PropertyProxy<T,?>> impl)
	{
		propertyProxiesImplementationClasses.put(type, impl);
	}

	/**
	 * Returns DeviceProxy from cache. This does not increase use count.
	 *
	 * @param uniqueName
	 *
	 * @return Device proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized DeviceProxy<?> _getDeviceProxyFromCache(
	    String uniqueName)
	{
		DeviceProxyHolder h = deviceCache.get(uniqueName);

		if (h == null) {
			return null;
		}

		return h.proxy;
	}

	/**
	 * Returns DeviceProxy from cache. This does not increase use count.
	 *
	 * @param uniqueName
	 * @param type
	 *
	 * @return Device proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized DeviceProxy<?> _getDeviceProxyFromCache(
	    String uniqueName, Class<?> type)
	{
		DeviceProxyHolder h = deviceCache.get(AbstractProxyHolder.toID(uniqueName, type));

		if (h == null) {
			return null;
		}

		return h.proxy;
	}

	/**
	 * Returns PropertyProxy from cache. This does not increase use count.
	 *
	 * @param uniqueName
	 *
	 * @return Property proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized PropertyProxy<?,?> _getPropertyProxyFromCache(
	    String uniqueName)
	{
		PropertyProxyHolder h = propertyCache.get(uniqueName);

		if (h == null) {
			return null;
		}

		return h.proxy;
	}

	/**
	 * Returns PropertyProxy from cache. This does not increase use count.
	 *
	 * @param uniqueName
	 * @param type
	 *
	 * @return Property proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized PropertyProxy<?,?> _getPropertyProxyFromCache(
	    String uniqueName, Class<?> type)
	{
		PropertyProxyHolder h = propertyCache.get(AbstractProxyHolder.toID(uniqueName,type));

		if (h == null) {
			return null;
		}

		return h.proxy;
	}

	/**
	 * Returns DirectoryProxy from cache. This does not increase use count.
	 *
	 * @param uniqueName
	 *
	 * @return Directory proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized DirectoryProxy<?> _getDirectoryProxyFromCache(
	    String uniqueName)
	{
		DirectoryProxyHolder h = directoryCache.get(uniqueName);

		if (h == null) {
			return null;
		}

		return h.proxy;
	}
	/**
	 * Returns DirectoryProxy from cache. This does not increase use count.
	 *
	 * @param uniqueName
	 * @param type
	 *
	 * @return Directory proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized DirectoryProxy<?> _getDirectoryProxyFromCache(
	    String uniqueName, Class<?> type)
	{
		DirectoryProxyHolder h = directoryCache.get(AbstractProxyHolder.toID(uniqueName,type));

		if (h == null) {
			return null;
		}

		return h.proxy;
	}

	/**
	 * <p>This method <b>MUST</b> be implemented by plug implementation if plug want to support 
	 * default behavior with shared plug instance</p>.
	 * 
	 * <p>This method is used by default DAL factories to access plug instances. Implementation may
	 * choose one of the following strategies how this method is implemented:
	 * </p>
	 *
	 * <ul>
	 *   <li><b>Singleton plug:</b> This method always returns the same instance, thus singleton.
	 *   This means that this particular DAL implementation will always use same plug.</li>
	 *   <li><b>Multiple plugs:</b> Implementation may decide to return different plug instance.
	 *   In this case it may be wiser to declare in AbstractFactorySupport this plug to be 
	 *   non-shared.</li>
	 * </ul>
	 *
	 * <p> Which strategy will be used could be hard-coded in AbstractFactorySupport or dynamically 
	 * decided from application context configuration with AbstractFactory.SHARE_PLUG property.</p>
	 *
	 *
	 * @param configuration Properties with configuration, whcih is provided by application context,
	 * which initiated plug construction.
	 * @return new or reused plug instance, depends on plug implementation strategy
	 * @throws Exception if construction fails.
	 * 
	 * @see AbstractFactorySupport
	 * @see AbstractFactory#SHARE_PLUG
	 * @see AbstractFactory#isPlugShared()
	 *
	 */
	public static AbstractPlug getInstance(Properties configuration)
		throws Exception
	{
		throw new Exception(
		    "This method MUST be implemented by this plug class, if plug instance is shared.");
	}
	
	/**
	 * <p>This method <b>MUST</b> be implemented <b>ONLY</b> by plug implementation, which 
	 * does not want to share plug instance. See for more detail AbstractFactorySupport class.</p>
	 * 
	 * <p>Purpose of this method is to provide own plug instance for each application context.
	 * This is required if for example plug need application context to access user or application
	 * specific resources in order to start remote protocol and this resources can not be shared 
	 * among several applications.</p> 
	 * 
	 * <p>Default DAL factory implementation (see AbstractFactorySupport) will try to create
	 * plug instance trough this method <b>only</p> if factory has declared in 
	 * AbstractFactorySupport constructor that plug should be created only in non-shared mode or
	 * dynamically decided from application context configuration with AbstractFactory.SHARE_PLUG 
	 * property set to <code>true</code>.</p>
	 * 
	 * <p>Plug, which is created with this method, will not be used any more after application
	 * context signals end-of-life-cycle with destroy event. For this reason plug must listen to 
	 * context life-cycle events and perform cleanup of any allocated resources once application
	 * context has been destroyed.</p>
	 * 
	 * @param context Application context which initiates plug construction
	 * @return new plug instance created specially for provided application context
	 * @throws Exception if construction fails.
	 * 
	 * @see AbstractPlug#getInstance(Properties)
 	 * @see AbstractFactorySupport
	 * @see AbstractFactory#SHARE_PLUG
	 * @see AbstractFactory#isPlugShared()
	 */
	public static AbstractPlug getInstance(AbstractApplicationContext context) 
		throws Exception
	{
		throw new Exception(
	    "This method MUST be implemented by this plug class, if plug instance is not shared among applications.");
	}

	/**
	 * This method <b>MUST</b> be implemented by plug implementation.
	 * This method is used by default factories to destroy plug instances. Implementation may
	 * destroy plug instance or not (if singleton stratrgy is used):
	 * <ul>
	 * <li> if this plug instance is nto shared (or singelton) is should destroy the plug isntance.</li>
	 * <li> if this plug isntancce is shared (singelton) then this method must not destroy the plug instance.</li>
	 * </ul>
	 * @throws Exception if construction fails.
	 */
	public abstract void releaseInstance()
		throws Exception;

	/**
	 * Return active configuration of this plug.
	 * @return Returns the configuration.
	 */
	public Properties getConfiguration()
	{
		return configuration;
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.dal.EventSystemContext#addEventSystemListener(org.csstudio.dal.EventSystemListener)
	 */
	public void addEventSystemListener(EventSystemListener<PlugEvent<?>> l)
			throws RemoteException {
		if (plugListeners == null) {
			plugListeners = new ListenerList(EventSystemListener.class);
		}

		if (!plugListeners.contains(l)) {
			plugListeners.add(l);
		}
	}
	/* (non-Javadoc)
	 * @see org.csstudio.dal.EventSystemContext#addEventSystemListener(java.util.Map, org.csstudio.dal.EventSystemListener)
	 */
	public void addEventSystemListener(EventSystemListener<PlugEvent<?>> l,
	    Map<String, Object> parameters) throws RemoteException
	{
		if (plugListeners == null) {
			plugListeners = new ListenerList(EventSystemListener.class);
		}

		if (!plugListeners.contains(l)) {
			plugListeners.add(l);
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.EventSystemContext#getEventSystemListeners()
	 */
	@SuppressWarnings("unchecked")
	public EventSystemListener<PlugEvent<?>>[] getEventSystemListeners()
	{
		return (EventSystemListener<PlugEvent<?>>[])plugListeners.toArray();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Identifiable#getIdentifier()
	 */
	public Identifier getIdentifier()
	{
		if (identifier == null) {
			identifier = IdentifierUtilities.createIdentifier(this);
		}

		return identifier;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.EventSystemContext#getSupportedParameters()
	 */
	public Map<String, Object> getSupportedEventSystemParameters()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Identifiable#isDebug()
	 */
	public boolean isDebug()
	{
		return debug;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.EventSystemContext#removeEventSystemListener(org.csstudio.dal.EventSystemListener)
	 */
	public void removeEventSystemListener(EventSystemListener<PlugEvent<?>> l)
	{
		if (plugListeners != null) {
			plugListeners.remove(l);
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.EventSystemContext#removeEventSystemListener(java.util.Map, org.csstudio.dal.EventSystemListener)
	 */
	public void removeEventSystemListener(EventSystemListener<PlugEvent<?>> l,
	    Map<String, Object> parameters)
	{
		if (plugListeners != null) {
			plugListeners.remove(l);
		}
	}
	
	/**
	 * Returns the Logger used by this plug. This logger logs all exceptions and 
	 * printouts within the DAL implementation.
	 * @return
	 */
	public Logger getLogger() {
		if (logger == null) {
			logger = Plugs.getPlugLogger(getPlugType());
		}
		return logger;
	}
	
	/**
	 * Returns an array of all PropertyProxies, which are held in the cache of
	 * this plug.
	 * @return
	 */
	public synchronized PropertyProxy<?,?>[] getCachedPropertyProxies() {
		PropertyProxyHolder[] holders = propertyCache.values().toArray(new PropertyProxyHolder[propertyCache.size()]);
		PropertyProxy<?,?>[] proxies = new PropertyProxy[propertyCache.size()];
		for (int i = 0; i < holders.length; i++) {
			proxies[i] = holders[i].proxy;
		}
		return proxies;
	}
	
	/**
	 * Returns an array of all DeviceProxies, which are held in the cache of
	 * this plug.
	 * @return
	 */
	public synchronized DeviceProxy<?>[] getCachedDeviceProxies() {
		DeviceProxyHolder[] holders = deviceCache.values().toArray(new DeviceProxyHolder[deviceCache.size()]);
		DeviceProxy<?>[] proxies = new DeviceProxy[deviceCache.size()];
		for (int i = 0; i < holders.length; i++) {
			proxies[i] = holders[i].proxy;
		}
		return proxies;
	}
	
	/**
	 * Returns an array of all DirectoryProxies, which are held in the cache of
	 * this plug.
	 * @return
	 */
	public synchronized DirectoryProxy<?>[] getCachedDirectoryProxies() {
		DirectoryProxyHolder[] holders = directoryCache.values().toArray(new DirectoryProxyHolder[directoryCache.size()]);
		DirectoryProxy<?>[] proxies = new DirectoryProxy[directoryCache.size()];
		for (int i = 0; i < holders.length; i++) {
			proxies[i] = holders[i].proxy;
		}
		return proxies;
	}
	
	/**
	 * Returns an iterator over the cached PropertyProxies. This iterator does
	 * not support remove of elements.
	 * @return
	 */
	public synchronized Iterator<PropertyProxy<?,?>> cachedPropertyProxiesIterator() {
		final Iterator<PropertyProxyHolder> holder = propertyCache.values().iterator();
		return new Iterator<PropertyProxy<?,?>>(){
			public boolean hasNext() {
				return holder.hasNext();
			}
			public PropertyProxy<?,?> next() {
				return holder.next().proxy;
			}
			public void remove() {
				throw new UnsupportedOperationException("Removing PropertyProxy from cache is not allowed.");
			}
		};
	}
	
	/**
	 * Returns an iterator over the cached DeviceProxies. This iterator does
	 * not support remove of elements.
	 * @return
	 */
	public synchronized Iterator<DeviceProxy<?>> cachedDeviceProxiesIterator() {
		final Iterator<DeviceProxyHolder> holder = deviceCache.values().iterator();
		return new Iterator<DeviceProxy<?>>(){
			public boolean hasNext() {
				return holder.hasNext();
			}
			public DeviceProxy<?> next() {
				return holder.next().proxy;
			}
			public void remove() {
				throw new UnsupportedOperationException("Removing DeviceProxy from cache is not allowed.");
			}
		};
	}
	
	/**
	 * Returns an iterator over the cached DirectoryProxies. This iterator does
	 * not support remove of elements.
	 * @return
	 */
	public synchronized Iterator<DirectoryProxy<?>> cachedDirectoryProxiesIterator() {
		final Iterator<DirectoryProxyHolder> holder = directoryCache.values().iterator();
		return new Iterator<DirectoryProxy<?>>(){
			public boolean hasNext() {
				return holder.hasNext();
			}
			public DirectoryProxy<?> next() {
				return holder.next().proxy;
			}
			public void remove() {
				throw new UnsupportedOperationException("Removing DirectoryProxy from cache is not allowed.");
			}
		};
	}
}


/* __oOo__ */
