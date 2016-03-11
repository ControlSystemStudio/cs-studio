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
package org.csstudio.dal.spi;

import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.ConnectionException;
import org.csstudio.dal.context.LinkBlocker;
import org.csstudio.dal.context.LinkListener;
import org.csstudio.dal.context.PropertyContext;
import org.csstudio.dal.context.PropertyFamily;
import org.csstudio.dal.impl.DynamicValuePropertyImpl;
import org.csstudio.dal.impl.PropertyFamilyImpl;
import org.csstudio.dal.impl.SynchronizedPropertyFamilyImpl;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.simple.RemoteInfo;


/**
 * @author ikriznar
 *
 */
public abstract class AbstractPropertyFactory extends AbstractFactorySupport
    implements PropertyFactory
{
    private PropertyFamilyImpl family;
    private Set<String> connecting;

    /**
     * Default constructor.
     */
    protected AbstractPropertyFactory()
    {
        super();
        family = new PropertyFamilyImpl(this);
        connecting= new HashSet<String>();
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.spi.AbstractFactorySupport#initialize(org.csstudio.dal.context.AbstractApplicationContext, org.csstudio.dal.spi.LinkPolicy)
     */
    @Override
    public void initialize(AbstractApplicationContext ctx, LinkPolicy policy) {
        super.initialize(ctx,policy);
        boolean sync = Boolean.parseBoolean(ctx.getConfiguration().getProperty(AbstractFactory.SYNCHRONIZE_FAMILY,"false"));
        if (sync) {
            family = new SynchronizedPropertyFamilyImpl(this);
        } else {
            family = new PropertyFamilyImpl(this);
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.PropertyFactory#getProperty(java.lang.String)
     */
    public DynamicValueProperty<?> getProperty(String uniqueName)
        throws InstantiationException, RemoteException
    {
        return createProperty(uniqueName, null, null);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.PropertyFactory#getProperty(org.csstudio.dal.context.RemoteInfo)
     */
    public DynamicValueProperty<?> getProperty(RemoteInfo ri)
        throws InstantiationException, RemoteException
    {
        return createProperty(ri.getRemoteName(), null, null);
    }


    private DynamicValueProperty<?> createProperty (String uniqueName,
            Class<? extends DynamicValueProperty<?>> type, LinkListener<?> l) throws RemoteException
    {
        String uid= uniqueName+type;

        if (propertiesCached) {
            DynamicValueProperty<?> p = getFromFamily(uniqueName, type);
            if (p!=null) {
                return p;
            }

            synchronized (this) {
                long timer= System.currentTimeMillis();
                while (connecting.contains(uid) && System.currentTimeMillis()-timer<60000) {
                    try {
                        wait(60000);
                    } catch (InterruptedException e) {
                        Logger.getLogger(this.getClass()).debug("Wait interrupted.", e);
                    }
                }

                p = getFromFamily(uniqueName, type);
                if (p!=null) {
                    return p;
                }

                connecting.add(uid);
            }
        }

        try {
            // Creates device implementation
            Class<?extends SimpleProperty<?>> impClass = getPlugInstance()
                .getPropertyImplementationClass(type, uniqueName);
            DynamicValuePropertyImpl<?> property = (DynamicValuePropertyImpl<?>)impClass.getConstructor(String.class,
                    PropertyContext.class).newInstance(uniqueName, family);

            if (l != null) {
                property.addLinkListener(l);
            }

            if (linkPolicy != LinkPolicy.NO_LINK_POLICY) {
                connect(uniqueName, type,impClass, property);

                if (linkPolicy == LinkPolicy.SYNC_LINK_POLICY) {
                    LinkBlocker.blockUntillConnected(property,
                        Plugs.getConnectionTimeout(ctx.getConfiguration(), 30000) * 2,
                        true);
                }
            }

            family.add(property);
            return property;
        } catch (ConnectionException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(this, "Failed to instantiate '"+uniqueName+"'.", e);
        } finally {
            if (propertiesCached) {
                synchronized (this) {
                    connecting.remove(uid);
                    this.notifyAll();
                }
            }
        }
    }

    /**
     * Tries to get property from family (cache).
     * @param uniqueName
     * @param type
     * @return property if found, otherwise null
     */
    private DynamicValueProperty<?> getFromFamily(String uniqueName,
            Class<? extends DynamicValueProperty<?>> type) {
        if (type == null) {
            return family.getFirst(uniqueName);
        }

        return family.getFirst(uniqueName,type);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.PropertyFactory#getProperty(java.lang.String, java.lang.Class, org.csstudio.dal.context.LinkListener)
     */
    public <P extends DynamicValueProperty<?>> P getProperty(String uniqueName,
        Class<P> type, LinkListener<?> l)
        throws InstantiationException, RemoteException
        {
        if (type == null) {
            throw new IllegalArgumentException("type may not be null");
        }

        return type.cast(createProperty(uniqueName,type,l));
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.PropertyFactory#getProperty(org.csstudio.dal.context.RemoteInfo, java.lang.Class, org.csstudio.dal.context.LinkListener)
     */
    public <P extends DynamicValueProperty<?>> P getProperty(RemoteInfo ri,
        Class<P> type, LinkListener<?> l)
        throws InstantiationException, RemoteException
    {
        return getProperty(ri.getRemoteName(), type, l);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.PropertyFactory#asyncLinkProperty(org.csstudio.dal.context.RemoteInfo, org.csstudio.dal.context.LinkListener)
     */
    public RemoteInfo asyncLinkProperty(RemoteInfo name,
        Class<?extends DynamicValueProperty<?>> type, LinkListener<?> l)
        throws InstantiationException, RemoteException
    {

        String uid= name.getRemoteName()+type;

        DynamicValuePropertyImpl<?> property = null;

        if (propertiesCached) {
            property= (DynamicValuePropertyImpl<?>)getFromFamily(name.getRemoteName(), type);

            if (property==null) {
                synchronized (this) {
                    long timer= System.currentTimeMillis();
                    while (connecting.contains(uid) && System.currentTimeMillis()-timer<60000) {
                        try {
                            wait(60000);
                        } catch (InterruptedException e) {
                            Logger.getLogger(this.getClass()).debug("Wait interrupted.", e);
                        }
                    }

                    property= (DynamicValuePropertyImpl<?>)getFromFamily(name.getRemoteName(), type);

                    if (property==null) {
                        connecting.add(uid);
                    }
                }
            }
        }

        try {
            Class<?extends SimpleProperty<?>> impClass = getPlugInstance()
            .getPropertyImplementationClass(type, name.getRemoteName());

            boolean newProp= false;

            if (property == null) {
                // Creates device implementation

                try {
                    property = (DynamicValuePropertyImpl<?>)impClass.getConstructor(String.class,
                            PropertyContext.class)
                        .newInstance(name.getRemoteName(), family);
                    family.add(property);

                    newProp=true;
                } catch (Exception e) {
                    throw new RemoteException(this, "Failed to instantiate '"+name+"'.", e);
                }
            }

            if (l != null) {
                property.addLinkListener(l);
            }

            /*if (!newProp && property.isConnected()) {
                l.connected(new ConnectionEvent(property, ConnectionState.CONNECTED));
            } else if (!newProp && property.isConnectionFailed()) {
                l.connectionFailed(new ConnectionEvent(property,
                        ConnectionState.CONNECTION_FAILED));
            } else*/
            if (newProp) {
                try {
                    connect(name.getRemoteName(),type, impClass, property);
                } catch (ConnectionException e) {
                    throw e;
                }
            }
            return name;
        } finally {
            if (propertiesCached) {
                synchronized (this) {
                    connecting.remove(uid);
                    this.notifyAll();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.PropertyFactory#asyncLinkProperty(java.lang.String, org.csstudio.dal.context.LinkListener)
     */
    public RemoteInfo asyncLinkProperty(String name,
        Class<?extends DynamicValueProperty<?>> type, LinkListener<?> l)
        throws InstantiationException, RemoteException
    {
        try {
            return asyncLinkProperty(getPlugInstance().createRemoteInfo(name),
                type, l);
        } catch (NamingException e) {
            throw new InstantiationException("Failed to construct RemoteInfo: "
                + e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.PropertyFactory#getPropertyFamily()
     */
    public PropertyFamily getPropertyFamily()
    {
        return family;
    }

    /**
     * Connects device implementation with device and directory proxy.
     * @param uniqueName
     * @param type
     * @param device
     * @throws ConnectionException
     */
    private void connect(String uniqueName,
        Class<?extends SimpleProperty<?>> type, Class<?extends SimpleProperty<?>> implementationType, DynamicValuePropertyImpl<?> property)
        throws ConnectionException
    {
        // creates proxy implementation
        PropertyProxy proxy = null;
        DirectoryProxy<?> dir = null;

        try {
            Class<?extends PropertyProxy<?,?>> proxyImplType = getPlugInstance()
                .getPropertyProxyImplementationClass(type, implementationType, uniqueName);
            proxy = getPlugInstance().getPropertyProxy(uniqueName, proxyImplType);

            if (proxy instanceof DirectoryProxy) {
                dir = (DirectoryProxy<?>)proxy;
            } else {
                dir = getPlugInstance().getDirectoryProxy(uniqueName);
            }

            property.initialize(proxy, dir);

            // cleanup if something fails, we don't want to leave hanging proxies
            // exception is rethrown
        } catch (ConnectionException e) {
            if (proxy != null) {
                getPlugInstance().releaseProxy(proxy);
            }

            if (dir != null) {
                getPlugInstance().releaseProxy(dir);
            }

            throw e;
        } catch (RemoteException e) {
            if (proxy != null) {
                getPlugInstance().releaseProxy(proxy);
            }

            if (dir != null) {
                getPlugInstance().releaseProxy(dir);
            }

            throw new ConnectionException(this,"Failed to obtain implementation class.",e);
        } catch (RuntimeException e) {
            if (proxy != null) {
                getPlugInstance().releaseProxy(proxy);
            }

            if (dir != null) {
                getPlugInstance().releaseProxy(dir);
            }

            throw e;
        }
    }

    protected void destroyAll()
    {
        family.destroyAll();
    }
}

/* __oOo__ */
