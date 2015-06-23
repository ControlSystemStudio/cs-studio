package org.csstudio.dal.spi;

import java.util.HashMap;
import java.util.Iterator;

import javax.naming.directory.DirContext;

import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.LinkListener;
import org.csstudio.dal.context.PropertyFamily;
import org.csstudio.dal.impl.DefaultApplicationContext;
import org.csstudio.dal.proxy.AbstractPlug;
import org.csstudio.dal.simple.RemoteInfo;

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

            final AbstractApplicationContext ctx = new DefaultApplicationContext("Default Property Factory Broker Context");
            manager.initialize(ctx, LinkPolicy.ASYNC_LINK_POLICY);
        }
        return manager;
    }

    public static final DefaultPropertyFactoryBroker getInstance(final AbstractApplicationContext ctx, final LinkPolicy lp) {
        if (ctx == null) {
            return getInstance();
        }
        final DefaultPropertyFactoryBroker manager = new DefaultPropertyFactoryBroker();
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
    @Override
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
    @Override
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
            } catch (final Throwable t) {
                throw new InstantiationException(t.toString());
            }

            return f;
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public AbstractApplicationContext getApplicationContext() {
        return ctx;
    }

    @Override
    public DirContext getDefaultDirectory() {
        // has no sense
        return null;
    }

    @Override
    public LinkPolicy getLinkPolicy() {
        return linkPolicy;
    }

    @Override
    public AbstractPlug getPlug() {
        // has no sense
        return null;
    }

    @Override
    public String getPlugType() {
        // has no sense
        return null;
    }

    public void initialize(final AbstractApplicationContext ctx, final LinkPolicy policy) {
        this.ctx = ctx;
        this.linkPolicy = policy;
    }

    @Override
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

    public void setPropertyFactoryService(final PropertyFactoryService propertyFactoryService) {
        this.propertyFactoryService = propertyFactoryService;
    }

    public PropertyFactoryService getPropertyFactoryService() {
        if (propertyFactoryService==null) {
            propertyFactoryService= DefaultPropertyFactoryService.getPropertyFactoryService();
        }
        return propertyFactoryService;
    }

}
