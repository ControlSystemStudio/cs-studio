package org.epics.css.dal.spi;

import java.util.HashMap;

import javax.naming.directory.DirContext;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.PlugContext;
import org.epics.css.dal.context.PropertyFamily;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.simple.RemoteInfo;

public class DefaultPropertyFactoryBroker implements PropertyFactoryBroker {
	
	private static DefaultPropertyFactoryBroker manager;
	
	private HashMap<String, PropertyFactory> factories = new HashMap<String, PropertyFactory>();
	private String[] supportedTypes = null;
	private String defaultPlugType;
	private AbstractApplicationContext ctx;
	private LinkPolicy linkPolicy;

	public static final synchronized DefaultPropertyFactoryBroker getInstance() {
		if (manager == null) {
			manager = new DefaultPropertyFactoryBroker();
			
			AbstractApplicationContext ctx = new DefaultApplicationContext("Default Property Factory Borker Context");
			manager.initialize(ctx, LinkPolicy.ASYNC_LINK_POLICY);
		}
		return manager;
	}
	
	private DefaultPropertyFactoryBroker() {
		
	}
	
	public String getDefaultPlugType() {
		return defaultPlugType;
	}

	public void setDefaultPlugType(String plugType) {
		defaultPlugType = plugType;
	}

	private PropertyFactory getPropertyFactory(String type) {
		PropertyFactory f = factories.get(type);
		if (f != null) return f;
		try {
			f = (PropertyFactory) Plugs.getInstance(ctx.getConfiguration()).getPropertyFactoryClassForPlug(type).newInstance();
			f.initialize(ctx, linkPolicy);
			factories.put(type, f);
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return f;
	}
	
	public String[] getSupportedPlugTypes() {
		if (supportedTypes == null) {
    		supportedTypes = Plugs.getInstance(ctx.getConfiguration()).getPlugNames();
    	}
    	return supportedTypes;
	}
	
	public RemoteInfo asyncLinkProperty(RemoteInfo name,
			Class<? extends DynamicValueProperty<?>> type, LinkListener<?> l)
			throws InstantiationException, RemoteException {
		return getPropertyFactory(name.getPlugType()).asyncLinkProperty(name, type, l);
	}

	public RemoteInfo asyncLinkProperty(String name,
			Class<? extends DynamicValueProperty<?>> type, LinkListener<?> l)
			throws InstantiationException, RemoteException {
		return asyncLinkProperty(
				RemoteInfo.remoteInfoFromString(name,RemoteInfo.DAL_TYPE_PREFIX+defaultPlugType),
				type,
				l);
	}

	public DynamicValueProperty<?> getProperty(String uniqueName)
			throws InstantiationException, RemoteException {
		return getProperty(RemoteInfo.remoteInfoFromString(uniqueName,RemoteInfo.DAL_TYPE_PREFIX+defaultPlugType));
	}

	public DynamicValueProperty<?> getProperty(RemoteInfo ri)
			throws InstantiationException, RemoteException {
		return getPropertyFactory(ri.getPlugType()).getProperty(ri);
	}

	public <P extends DynamicValueProperty<?>> P getProperty(String uniqueName,
			Class<P> type, LinkListener<?> l) throws InstantiationException,
			RemoteException {
		return getProperty(
				RemoteInfo.remoteInfoFromString(uniqueName,RemoteInfo.DAL_TYPE_PREFIX+defaultPlugType),
				type,
				l);
	}

	public <P extends DynamicValueProperty<?>> P getProperty(RemoteInfo ri,
			Class<P> type, LinkListener<?> l) throws InstantiationException,
			RemoteException {
		return getPropertyFactory(ri.getPlugType()).getProperty(ri, type, l);
	}

	public PropertyFamily getPropertyFamily() {
		// has no sense
		return null;
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
 
	public void initialize(AbstractApplicationContext ctx, LinkPolicy policy) {
		this.ctx = ctx;
		this.linkPolicy = policy;
	}

	public boolean isPlugShared() {
		// has no sense
		return false;
	}

}
