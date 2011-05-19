package org.epics.css.dal.spi;

import java.util.HashMap;
import java.util.Iterator;

import javax.naming.directory.DirContext;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.PropertyFamily;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.simple.RemoteInfo;

public class DefaultPropertyFactoryBroker implements PropertyFactoryBroker {

	private static DefaultPropertyFactoryBroker manager;

	private final HashMap<String, PropertyFactory> factories = new HashMap<String, PropertyFactory>();
	private String[] supportedTypes = null;
	private String defaultPlugType=Plugs.SIMULATOR_PLUG_TYPE;
	private AbstractApplicationContext ctx;
	private LinkPolicy linkPolicy;
	private PropertyFactoryService propertyFactoryService=null;

	public static final synchronized DefaultPropertyFactoryBroker getInstance() {
		if (manager == null) {
			manager = new DefaultPropertyFactoryBroker();

			final AbstractApplicationContext ctx = new DefaultApplicationContext("Default Property Factory Borker Context");
			manager.initialize(ctx, LinkPolicy.ASYNC_LINK_POLICY);
		}
		return manager;
	}
	
	public static final DefaultPropertyFactoryBroker getInstance(AbstractApplicationContext ctx, LinkPolicy lp) {
		if (ctx == null) return getInstance();
		DefaultPropertyFactoryBroker manager = new DefaultPropertyFactoryBroker();
		manager.initialize(ctx, lp);
		return manager;
	}

	private DefaultPropertyFactoryBroker() {

	}

	/**
	 * Return default plug type, which is used for all remote names, which does not
	 * explicitly declare plug or connection type.
	 *
	 * <p>
	 * By default (if not set) plug type equals to Simulator.
	 * </p>
	 *
	 *  @return default plug type
	 */
	public String getDefaultPlugType() {
		return defaultPlugType;
	}

	/**
	 * Sets default plug type, which is used for all remote names, which does not
	 * explicitly declare plug or connection type.
	 *
	 * <p>
	 * So far supported values are: EPICS, TINE, Simulator.
	 * By default (if not set) plug type equals to Simulator.
	 * </p>
	 *
	 * @param defautl plug type.
	 */
	public void setDefaultPlugType(final String plugType) {
		defaultPlugType = plugType;
	}

	private PropertyFactory getPropertyFactory(String type) throws InstantiationException {
		if (type==null) {
			type=defaultPlugType;
		}
		synchronized (factories) {
			PropertyFactory f = factories.get(type);
			if (f != null) {
	            return f;
	        }
			
			try {
				f= getPropertyFactoryService().getPropertyFactory(ctx, linkPolicy, type);
				factories.put(type, f);
			} catch (Throwable t) {
				throw new InstantiationException(t.toString());
			}

			return f;
		}
	}

	public String[] getSupportedPlugTypes() {
		if (supportedTypes == null) {
    		supportedTypes = Plugs.getInstance(ctx.getConfiguration()).getPlugNames();
    	}
    	return supportedTypes;
	}

	public RemoteInfo asyncLinkProperty(final RemoteInfo name,
			final Class<? extends DynamicValueProperty<?>> type, final LinkListener<?> l)
			throws InstantiationException, RemoteException {
		return getPropertyFactory(name.getPlugType()).asyncLinkProperty(name, type, l);
	}

	public RemoteInfo asyncLinkProperty(final String name,
			final Class<? extends DynamicValueProperty<?>> type, final LinkListener<?> l)
			throws InstantiationException, RemoteException {
		return asyncLinkProperty(
				RemoteInfo.fromString(name,RemoteInfo.DAL_TYPE_PREFIX+defaultPlugType),
				type,
				l);
	}

	public DynamicValueProperty<?> getProperty(final String uniqueName)
			throws InstantiationException, RemoteException {
		return getProperty(RemoteInfo.fromString(uniqueName,RemoteInfo.DAL_TYPE_PREFIX+defaultPlugType));
	}

	public DynamicValueProperty<?> getProperty(final RemoteInfo ri)
			throws InstantiationException, RemoteException {
		return getPropertyFactory(ri.getPlugType()).getProperty(ri);
	}

	public <P extends DynamicValueProperty<?>> P getProperty(final String uniqueName,
	                                                         final Class<P> type,
	                                                         final LinkListener<?> l) throws InstantiationException, RemoteException {
		return getProperty(
				RemoteInfo.fromString(uniqueName,RemoteInfo.DAL_TYPE_PREFIX+defaultPlugType),
				type,
				l);
	}

	public <P extends DynamicValueProperty<?>> P getProperty(final RemoteInfo ri,
	                                                         final Class<P> type,
	                                                         final LinkListener<?> l) throws InstantiationException, RemoteException {
		return getPropertyFactory(ri.getPlugType()).getProperty(ri, type, l);
	}

	public PropertyFamily getPropertyFamily() {
		// has no sense
		return null;
	}

	public void destroy(final DynamicValueProperty<?> property) {
		synchronized (factories) {
			final Iterator<PropertyFactory> it = factories.values().iterator();
			while (it.hasNext()) {
				it.next().getPropertyFamily().destroy(property);
			}
		}
	}

	public AbstractApplicationContext getApplicationContext() {
		return ctx;
	}

	public DirContext getDefaultDirectory() {
		// has no sense
		return null;
	}

	public LinkPolicy getLinkPolicy() {
		return linkPolicy;
	}

	public AbstractPlug getPlug() {
		// has no sense
		return null;
	}

	public String getPlugType() {
		// has no sense
		return null;
	}

	public void initialize(final AbstractApplicationContext ctx, final LinkPolicy policy) {
		this.ctx = ctx;
		this.linkPolicy = policy;
	}

	public boolean isPlugShared() {
		// has no sense
		return false;
	}

	public void releaseAll() {
		synchronized (factories) {
			final Iterator<PropertyFactory> it = factories.values().iterator();
			while (it.hasNext()) {
				it.next().getPropertyFamily().destroyAll();
			}
		}
	}

	public void setPropertyFactoryService(PropertyFactoryService propertyFactoryService) {
		this.propertyFactoryService = propertyFactoryService;
	}

	public PropertyFactoryService getPropertyFactoryService() {
		if (propertyFactoryService==null) {
			propertyFactoryService= DefaultPropertyFactoryService.getPropertyFactoryService();
		}
		return propertyFactoryService;
	}

}
