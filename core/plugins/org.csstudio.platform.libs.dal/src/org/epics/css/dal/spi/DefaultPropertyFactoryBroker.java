package org.epics.css.dal.spi;

import java.util.HashMap;
import java.util.Iterator;

import javax.naming.directory.DirContext;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.PlugContext;
import org.epics.css.dal.context.PropertyFamily;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simulation.SimulatorPlug;

public class DefaultPropertyFactoryBroker implements PropertyFactoryBroker {

	private static DefaultPropertyFactoryBroker manager;

	private final HashMap<String, PropertyFactory> factories = new HashMap<String, PropertyFactory>();
	private String[] supportedTypes = null;
	private String defaultPlugType=SimulatorPlug.PLUG_TYPE;
	private AbstractApplicationContext ctx;
	private LinkPolicy linkPolicy;

	public static final synchronized DefaultPropertyFactoryBroker getInstance() {
		if (manager == null) {
			manager = new DefaultPropertyFactoryBroker();

			final AbstractApplicationContext ctx = new DefaultApplicationContext("Default Property Factory Borker Context");
			manager.initialize(ctx, LinkPolicy.ASYNC_LINK_POLICY);
		}
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

	private PropertyFactory getPropertyFactory(String type) {
		if (type==null) {
			type=defaultPlugType;
		}
		PropertyFactory f = factories.get(type);
		if (f != null) {
            return f;
        }
		try {
			f = (PropertyFactory) Plugs.getInstance(ctx.getConfiguration()).getPropertyFactoryClassForPlug(type).newInstance();
			f.initialize(ctx, linkPolicy);
			factories.put(type, f);
		} catch (final InstantiationException e) {
		} catch (final IllegalAccessException e) {
		}
		return f;
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
			final Class<P> type, final LinkListener<?> l) throws InstantiationException,
			RemoteException {
		return getProperty(
				RemoteInfo.fromString(uniqueName,RemoteInfo.DAL_TYPE_PREFIX+defaultPlugType),
				type,
				l);
	}

	public <P extends DynamicValueProperty<?>> P getProperty(final RemoteInfo ri,
			final Class<P> type, final LinkListener<?> l) throws InstantiationException,
			RemoteException {
		return getPropertyFactory(ri.getPlugType()).getProperty(ri, type, l);
	}

	public PropertyFamily getPropertyFamily() {
		// has no sense
		return null;
	}

	public void destroy(final DynamicValueProperty<?> property) {
		final Iterator<PropertyFactory> it = factories.values().iterator();
		while(it.hasNext()) {
			it.next().getPropertyFamily().destroy(property);
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

	public PlugContext getPlug() {
		// has no sense
		return null;
	}

	public String getPlugType() {
		return getPlug().getPlugType();
	}

	public void initialize(final AbstractApplicationContext ctx, final LinkPolicy policy) {
		this.ctx = ctx;
		this.linkPolicy = policy;
	}

	public boolean isPlugShared() {
		// has no sense
		return false;
	}

}
