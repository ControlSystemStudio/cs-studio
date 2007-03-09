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

package org.epics.css.dal.proxy;

import com.cosylab.util.ListenerList;

import org.epics.css.dal.EventSystemListener;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.context.ConnectionException;
import org.epics.css.dal.context.Identifier;
import org.epics.css.dal.context.IdentifierUtilities;
import org.epics.css.dal.context.PlugContext;
import org.epics.css.dal.context.PlugEvent;
import org.epics.css.dal.device.AbstractDevice;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * This is abstract plug class which helps plug implementators write own
 * plug. This plug has implemented support for following features: proxy
 * object sharing.
 *
 * @author ikriznar
 */
public abstract class AbstractPlug implements PlugContext
{
	class PropertyProxyHolder
	{
		PropertyProxyHolder(PropertyProxy p)
		{
			proxy = p;
			count = 1;
		}

		PropertyProxy proxy;
		int count;
	}

	class DirectoryProxyHolder
	{
		DirectoryProxyHolder(DirectoryProxy p)
		{
			proxy = p;
			count = 1;
		}

		DirectoryProxy proxy;
		int count;
	}

	class DeviceProxyHolder
	{
		DeviceProxyHolder(DeviceProxy p)
		{
			proxy = p;
			count = 1;
		}

		DeviceProxy proxy;
		int count;
	}

	private Map<String, PropertyProxyHolder> propertyCache;
	private Map<String, DirectoryProxyHolder> directoryCache;
	private Map<String, DeviceProxyHolder> deviceCache;
	private boolean cachingEnabled = true;
	private Properties configuration;
	private Map<Class<?extends AbstractDevice>, Class<?extends AbstractDevice>> deviceImplementationClasses =
		new HashMap<Class<?extends AbstractDevice>, Class<?extends AbstractDevice>>();
	private Map<Class<?extends SimpleProperty>, Class<?extends SimpleProperty>> propertyImplementationClasses =
		new HashMap<Class<?extends SimpleProperty>, Class<?extends SimpleProperty>>();
	private Map<Class<?extends AbstractDevice>, Class<?extends DeviceProxy>> deviceProxiesImplementationClasses =
		new HashMap<Class<?extends AbstractDevice>, Class<?extends DeviceProxy>>();
	private Map<Class<?extends SimpleProperty>, Class<?extends PropertyProxy>> propertyProxiesImplementationClasses =
		new HashMap<Class<?extends SimpleProperty>, Class<?extends PropertyProxy>>();
	protected ListenerList plugListeners;
	private Identifier identifier;
	protected boolean debug = false;

	/**
	 * Creates new plug instance with proxy caching enabled.
	 */
	protected AbstractPlug(Properties configuration)
	{
		super();
		// at the moment we do not use weak reference
		propertyCache = new HashMap<String, PropertyProxyHolder>(100);
		directoryCache = new HashMap<String, DirectoryProxyHolder>(100);
		deviceCache = new HashMap<String, DeviceProxyHolder>(100);

		if (configuration == null) {
			this.configuration = new Properties();
		} else {
			this.configuration = (Properties)configuration.clone();
		}
	}

	/**
	     * Creates new plug instance.
	     * @param cachingEnabled disables proxy caching if <code>false</code>
	     */
	protected AbstractPlug(Properties configuration, boolean cachingEnabled)
	{
		this(configuration);
		this.cachingEnabled = cachingEnabled;
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
	protected abstract <T extends PropertyProxy> T createNewPropertyProxy(
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
	protected abstract DirectoryProxy createNewDirectoryProxy(String uniqueName)
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
	protected abstract <T extends DeviceProxy> T createNewDeviceProxy(
	    String uniqueName, Class<T> type) throws ConnectionException;

	/**
	 * Returns plug type string, which is distinguishing for plug which
	 * creates  proxies for particular communication layer.<p>For
	 * example plug that connects to EPICS device my return string "EPICS".</p>
	 *
	 * @return plug destingushing type name
	 */
	public abstract String getPlugType();

	
	/**
	 * Returns property implementation class
	 *  
	 * @param uniquePropertyName property name
	 * @return property implementation class
	 */
	protected abstract Class<?extends SimpleProperty>  getPropertyImplementationClass (String uniquePropertyName);
	
	/**
	 * Returns property proxy implementation class
	 * 
	 * @param uniquePropertyName property name
	 * @return property proxy  implementation class
	 */
	protected abstract Class<?extends PropertyProxy> getPropertyProxyImplementationClass(String uniquePropertyName);
	
	
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
	 * @param type class of property proxy, if <code>PropertyProxy.class</code>
	 *        than plug implementation decide which proxy will be returned.
	 *
	 * @return reused or new proxy object representing unique name
	 *
	 * @throws RemoteException if instantiation of object fails
	 * @throws NullPointerException if unique name parameter is null
	 */
	public <TT extends PropertyProxy> TT getPropertyProxy(String uniqueName,
	    Class<TT> type) throws ConnectionException
	{
		if (uniqueName == null) {
			throw new NullPointerException("uniqueName");
		}

		PropertyProxy<?> p = getPropertyProxyFromCache(uniqueName);

		if (p != null) {
			if (!type.isInstance(p)) {
				throw new ConnectionException(this,
				    "PropertyProxy in cache for '" + uniqueName
				    + "' is of type '" + p.getClass().getName()
				    + "' and not of expected type '" + type.getName() + "'.");
			}

			return type.cast(p);
		}

		TT pp = createNewPropertyProxy(uniqueName, type);

		putPropertyProxyToCache(pp);

		return pp;
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
	public PropertyProxy<?> getPropertyProxy(String uniqueName)
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
	public DirectoryProxy getDirectoryProxy(String uniqueName)
		throws ConnectionException
	{
		if (uniqueName == null) {
			throw new NullPointerException("uniqueName");
		}

		DirectoryProxy d = getDirectoryProxyFromCache(uniqueName);

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
	public void releaseProxy(Proxy proxy)
	{
		if (proxy == null) {
			throw new NullPointerException("proxy");
		}

		boolean canDestroy = true;

		synchronized (this) {
			if (proxy instanceof DirectoryProxy) {
				canDestroy = releaseDirectoryProxyFromCache(proxy.getUniqueName());
			}

			if (proxy instanceof PropertyProxy) {
				canDestroy &= releasePropertyProxyFromCache(proxy.getUniqueName());
			}
		}

		if (canDestroy) {
			try {
				proxy.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	protected synchronized PropertyProxy getPropertyProxyFromCache(
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
	 * Returns DirectoryProxy from cache. Warning: this operation
	 * increases use count by 1.
	 *
	 * @param uniqueName
	 *
	 * @return property proxy from cache or <code>null</code> is not cached
	 */
	protected synchronized DirectoryProxy getDirectoryProxyFromCache(
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
	 * Stores PropertyProxy in cache with use count set to 1.
	 *
	 * @param proxy
	 */
	protected synchronized void putPropertyProxyToCache(PropertyProxy proxy)
	{
		PropertyProxyHolder h = new PropertyProxyHolder(proxy);
		propertyCache.put(proxy.getUniqueName(), h);
	}

	/**
	 * Stores PropertyProxy in cache with use count set to 1.
	 *
	 * @param proxy
	 */
	protected synchronized void putDirectoryProxyToCache(DirectoryProxy proxy)
	{
		DirectoryProxyHolder h = new DirectoryProxyHolder(proxy);
		directoryCache.put(proxy.getUniqueName(), h);
	}

	/**
	 * Reduces use count for DirectoryProxy for one and removes it form
	 * cache if use count is 0.
	 *
	 * @param uniqueName
	 *
	 * @return <code>true</code> if cache does not hold any reference to proxy.
	 *         This can happend if proxy was not present in cache or proxy
	 *         use count was reduced to 0.
	 */
	protected synchronized boolean releaseDirectoryProxyFromCache(
	    String uniqueName)
	{
		DirectoryProxyHolder h = directoryCache.get(uniqueName);

		if (h == null) {
			return true;
		}

		h.count--;

		if (h.count <= 0) {
			directoryCache.remove(uniqueName);

			return true;
		}

		return false;
	}

	/**
	 * Reduces use count for PropertyProxy for one and removes it form
	 * cache if use count is 0.
	 *
	 * @param uniqueName
	 *
	 * @return <code>true</code> if cache does not hold any reference to proxy.
	 *         This can happend if proxy was not present in cache or proxy
	 *         use count was reduced to 0.
	 */
	protected synchronized boolean releasePropertyProxyFromCache(
	    String uniqueName)
	{
		PropertyProxyHolder h = propertyCache.get(uniqueName);

		if (h == null) {
			return true;
		}

		h.count--;

		if (h.count <= 0) {
			propertyCache.remove(uniqueName);

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
	public DeviceProxy getDeviceProxy(String uniqueName)
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
	public <TT extends DeviceProxy> TT getDeviceProxy(String uniqueName,
	    Class<TT> type) throws ConnectionException
	{
		if (uniqueName == null) {
			throw new NullPointerException("uniqueName");
		}

		DeviceProxy p = getDeviceProxyFromCache(uniqueName);

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
	protected synchronized DeviceProxy getDeviceProxyFromCache(
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
	 * Stores DeviceProxy in cache with use count set to 1.
	 *
	 * @param proxy
	 */
	protected synchronized void putDeviceProxyToCache(DeviceProxy proxy)
	{
		DeviceProxyHolder h = new DeviceProxyHolder(proxy);
		deviceCache.put(proxy.getUniqueName(), h);
	}

	/**
	 * Reduces use count for DeviceProxy for one and removes it form
	 * cache if use count is 0.
	 *
	 * @param uniqueName
	 *
	 * @return <code>true</code> if cache does not hold any reference to proxy.
	 *         This can happend if proxy was not present in cache or proxy
	 *         use count was reduced to 0.
	 */
	protected synchronized boolean releaseDeviceProxyFromCache(
	    String uniqueName)
	{
		DeviceProxyHolder h = deviceCache.get(uniqueName);

		if (h == null) {
			return true;
		}

		h.count--;

		if (h.count <= 0) {
			deviceCache.remove(uniqueName);

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
	 */
	public Class<?extends SimpleProperty> getPropertyImplementationClass(
	    Class<?extends SimpleProperty> type, String propertyName)
	{
		Class<?extends SimpleProperty> propImplClass = null;
		if (type !=null)
		{
			propImplClass = propertyImplementationClasses.get(type);
		}
		if (propImplClass == null)
		{
			propImplClass = getPropertyImplementationClass(propertyName);
		}
		return propImplClass;
	}

	/**
	 * Returns the class that implements device interface given as parameter.
	 *
	 * @param type
	 * @return implementor of given interface
	 */
	public Class<?extends AbstractDevice> getDeviceImplementationClass(
	    Class<?extends AbstractDevice> type)
	{
		return deviceImplementationClasses.get(type);
	}

	/**
	 * Returns the class that implements property proxy that can be connected to the given
	 * interface of a property.
	 *
	 * By default is lookup done first by type if it's not <code>null</code> then by property name. 
	 *
	 *
	 * @param type property type (can be <code>null</code>)
	 * @param implementationType property implementation type (can be <code>null</code>)
	 * @param property name
	 *  
	 * @return property proxy for the given property 
	 */
	public Class<?extends PropertyProxy> getPropertyProxyImplementationClass(
	    Class<?extends SimpleProperty> type, Class<?extends SimpleProperty> implementationType, String uniquePropertyName)
	{
		Class<? extends PropertyProxy> proxyImplType = null;
		
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
	 * @param type
	 * @return implementor of given interface
	 */
	public Class<?extends DeviceProxy> getDeviceProxyImplementationClass(
	    Class<?extends AbstractDevice> type)
	{
		return deviceProxiesImplementationClasses.get(type);
	}

	/**
	 * Puts a device implementation class to a map. When calling getDeviceImplementationClass(Class T)
	 * method, it will return the class that was put in as an <code>impl</code> parameter
	 * to this method, where T is the type parameter of this method.
	 *
	 * @param type interface that should be associated with a class impl
	 * @param impl implementation of the type interface
	 */
	public void putDeviceImplementationClass(
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
	public void putPropertyImplementationClass(
	    Class<?extends SimpleProperty> type, Class<?extends SimpleProperty> impl)
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
	public void putDeviceProxyImplementationClass(
	    Class<?extends AbstractDevice> type, Class<?extends DeviceProxy> impl)
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
	public void putPropertyProxyImplementationClass(
	    Class<?extends SimpleProperty> type, Class<?extends PropertyProxy> impl)
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
	protected synchronized DeviceProxy _getDeviceProxyFromCache(
	    String uniqueName)
	{
		DeviceProxyHolder h = deviceCache.get(uniqueName);

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
	protected synchronized PropertyProxy _getPropertyProxyFromCache(
	    String uniqueName)
	{
		PropertyProxyHolder h = propertyCache.get(uniqueName);

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
	protected synchronized DirectoryProxy _getDirectoryProxyFromCache(
	    String uniqueName)
	{
		DirectoryProxyHolder h = directoryCache.get(uniqueName);

		if (h == null) {
			return null;
		}

		return h.proxy;
	}

	/**
	 * This method <b>MUST</b> be implemented by plug implementation.
	 * This method is used by default factories to access plug instances. Implementation may
	 * choose one of the folowing strategies how this method is implemented:
	 *
	 * <ul>
	 *   <li><b>Singleton plug:</b> This method always returns same instance, thus singleton.
	 *   This means that this particular DAL implementation will allways use same plug.</li>
	 *   <li><b>Multiple plugs:</b> Implementation may decide to return different plug instancces.
	 *   This behavior may depand on configuration, which is provided as parameter.
	 *   As consequence plug implementation may reuse plugs with similar or compatible configurations.</li>
	 * </ul>
	 *
	 * <p> Which strategy will be used could be hardoced or dynamically decided upon configuration. This
	 * Behaviour is plug specific and has no DAL wide constraints.</p>
	 *
	 *
	 * @param configuration Properties onfiguration, whcih is provided by application context,
	 * which initiated plug construction.
	 * @return new or reused plug instance, depends on plug implementation strategy
	 * @throws Exception if construction fails.
	 *
	 */
	public static AbstractPlug getInstance(Properties configuration)
		throws Exception
	{
		throw new Exception(
		    "This method MUST be implemented by this plug class.");
	}

	/**
	 * This method <b>MUST</b> be implemented by plug implementation.
	 * This method is used by default factories to destroy plug instances. Implementation may
	 * destroy plug instance or not (if singleton stratrgy is used).
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
	 * @see org.epics.css.dal.EventSystemContext#addEventSystemListener(org.epics.css.dal.EventSystemListener)
	 */
	public void addEventSystemListener(EventSystemListener<PlugEvent> l)
		throws RemoteException
	{
		if (plugListeners == null) {
			plugListeners = new ListenerList(EventSystemListener.class);
		}

		if (!plugListeners.contains(l)) {
			plugListeners.add(l);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.EventSystemContext#addEventSystemListener(java.util.Map, org.epics.css.dal.EventSystemListener)
	 */
	public void addEventSystemListener(EventSystemListener<PlugEvent> l,
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
	 * @see org.epics.css.dal.EventSystemContext#getEventSystemListeners()
	 */
	public EventSystemListener<PlugEvent>[] getEventSystemListeners()
	{
		return (EventSystemListener<PlugEvent>[])plugListeners.toArray();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Identifiable#getIdentifier()
	 */
	public Identifier getIdentifier()
	{
		if (identifier == null) {
			identifier = IdentifierUtilities.createIdentifier(this);
		}

		return identifier;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.EventSystemContext#getSupportedParameters()
	 */
	public Map<String, Object> getSupportedEventSystemParameters()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Identifiable#isDebug()
	 */
	public boolean isDebug()
	{
		return debug;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.EventSystemContext#removeEventSystemListener(org.epics.css.dal.EventSystemListener)
	 */
	public void removeEventSystemListener(EventSystemListener<PlugEvent> l)
	{
		if (plugListeners != null) {
			plugListeners.remove(l);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.EventSystemContext#removeEventSystemListener(java.util.Map, org.epics.css.dal.EventSystemListener)
	 */
	public void removeEventSystemListener(EventSystemListener<PlugEvent> l,
	    Map<String, Object> parameters)
	{
		if (plugListeners != null) {
			plugListeners.remove(l);
		}
	}
} /* __oOo__ */


/* __oOo__ */
