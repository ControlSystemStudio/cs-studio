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

package org.csstudio.dal.spi;

import org.csstudio.dal.RemoteException;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.Connectable;
import org.csstudio.dal.context.ConnectionEvent;
import org.csstudio.dal.context.ConnectionException;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.context.DeviceFamily;
import org.csstudio.dal.context.LinkBlocker;
import org.csstudio.dal.context.LinkListener;
import org.csstudio.dal.context.Linkable;
import org.csstudio.dal.device.AbstractDevice;
import org.csstudio.dal.impl.AbstractDeviceImpl;
import org.csstudio.dal.impl.DeviceBean;
import org.csstudio.dal.impl.DeviceFamilyImpl;
import org.csstudio.dal.impl.SynchronizedDeviceFamilyImpl;
import org.csstudio.dal.proxy.DeviceProxy;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.simple.RemoteInfo;

import javax.naming.NamingException;


/**
 * This is default implementation of <code>DeviceFactory</code> interface which
 * uses common glue code classes to provide devices. Implementation of device factory
 * is enchouraged to extend from this class in order to minimize work and mentain
 * compatibility with common glue code approach.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public abstract class AbstractDeviceFactory extends AbstractFactorySupport
    implements DeviceFactory
{
    private DeviceFamilyImpl<AbstractDevice> family;

    /**
     * Default contructor.
     *
     */
    protected AbstractDeviceFactory()
    {
        family = new DeviceFamilyImpl<AbstractDevice>(this);
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
            family = new SynchronizedDeviceFamilyImpl<AbstractDevice>(this);
        } else {
            family = new DeviceFamilyImpl<AbstractDevice>(this);
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#getDevice(java.lang.String)
     */
    public AbstractDevice getDevice(String uniqueName)
        throws InstantiationException, RemoteException
    {
        return getDevice(uniqueName, AbstractDevice.class, null);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#getDevice(org.csstudio.dal.context.RemoteInfo)
     */
    public AbstractDevice getDevice(RemoteInfo ri)
        throws InstantiationException, RemoteException
    {
        return getDevice(ri.getRemoteName(), AbstractDevice.class, null);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#getDevice(java.lang.String, java.lang.Class, org.csstudio.dal.context.LinkListener)
     */
    public <D extends AbstractDevice> D getDevice(String uniqueName,
        Class<D> type, LinkListener<? extends Linkable> l)
        throws InstantiationException, RemoteException
    {
        return createDevice(uniqueName, type, l);
    }

    private <D extends AbstractDevice> D createDevice(String uniqueName,
            Class<D> type, LinkListener<? extends Linkable> l)
            throws InstantiationException, RemoteException
        {

        if (family.contains(uniqueName)) {
            return type.cast(family.get(uniqueName));
        }

        try {
            // Creates device implementation
            Class<? extends AbstractDevice> impClass = getPlugInstance()
                .getDeviceImplementationClass(type,uniqueName);
            AbstractDeviceImpl device = (AbstractDeviceImpl)impClass.getConstructor(String.class,DeviceFamily.class)
                .newInstance(uniqueName,family);

            if (l != null) {
                device.addLinkListener(l);
            }

            if (linkPolicy != LinkPolicy.NO_LINK_POLICY) {
                connect(uniqueName, type, impClass, device);

                if (linkPolicy == LinkPolicy.SYNC_LINK_POLICY) {
                    LinkBlocker.blockUntillConnected(device,
                        Plugs.getConnectionTimeout(ctx.getConfiguration(), 30000) * 2,
                        true);
                }
            }

            family.add(device);

            return type.cast(device);
        } catch (ConnectionException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException(this, "Failed to instantiate IFC.", e);
        }
    }

    /**
     * Connects device implementation with device and directory proxy.
     * @param uniqueName
     * @param type
     * @param device
     * @throws ConnectionException
     */
    private void connect(String uniqueName,
        Class<?extends AbstractDevice> type, Class<?extends AbstractDevice> implementationType, AbstractDeviceImpl device)
        throws ConnectionException
    {
        // creates proxy implementation
        DeviceProxy proxy = null;
        DirectoryProxy dir = null;

        try {
            Class<?extends DeviceProxy> proxyImplType = getPlugInstance()
                .getDeviceProxyImplementationClass(type,implementationType,uniqueName);
            proxy = getPlugInstance().getDeviceProxy(uniqueName, proxyImplType);

            if (proxy instanceof DirectoryProxy) {
                dir = (DirectoryProxy)proxy;
            } else {
                dir = getPlugInstance().getDirectoryProxy(uniqueName);
            }

            device.initialize(proxy, dir);

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

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#getDevice(org.csstudio.dal.context.RemoteInfo, java.lang.Class, org.csstudio.dal.context.LinkListener)
     */
    public <D extends AbstractDevice> D getDevice(RemoteInfo ri, Class<D> type,
        LinkListener<?> l) throws InstantiationException, RemoteException
    {
        return getDevice(ri.getRemoteName(), type, l);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#asyncLinkDevice(org.csstudio.dal.context.RemoteInfo, org.csstudio.dal.context.LinkListener)
     */
    public RemoteInfo asyncLinkDevice(RemoteInfo name,
        Class<?extends AbstractDevice> type, LinkListener<?> l)
        throws InstantiationException, RemoteException
    {
        AbstractDeviceImpl device = (AbstractDeviceImpl)family.get(name
                .getRemoteName());

        if (device == null) {
            try {
                // Creates device implementation
                Class<? extends AbstractDevice> impClass = getPlugInstance()
                    .getDeviceImplementationClass(type,name.getRemoteName());
                device = (AbstractDeviceImpl)impClass.getConstructor(String.class,DeviceFamily.class)
                    .newInstance(name.getRemoteName(),family);

                connect(name.getRemoteName(), type, impClass,  device);

                family.add(device);
            } catch (ConnectionException e) {
                throw e;
            } catch (Exception e) {
                throw new RemoteException(this, "Failed to instantiate IFC.", e);
            }
        }

        if (l != null) {
            device.addLinkListener(l);
        }

        if (device.isConnected()) {
            l.connected(new ConnectionEvent(device, ConnectionState.CONNECTED));
        } else if (device.isConnectionFailed()) {
            l.connectionFailed(new ConnectionEvent(device,
                    ConnectionState.CONNECTION_FAILED));
        }

        return name;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#asyncLinkDevice(java.lang.String, org.csstudio.dal.context.LinkListener)
     */
    public RemoteInfo asyncLinkDevice(String name,
        Class<?extends AbstractDevice> type, LinkListener<?> l)
        throws InstantiationException, RemoteException
    {
        try {
            return asyncLinkDevice(getPlugInstance().createRemoteInfo(name),
                type, l);
        } catch (NamingException e) {
            throw new InstantiationException("Failed to construct RemoteInfo: "
                + e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#getDeviceFamily()
     */
    public DeviceFamily getDeviceFamily()
    {
        return family;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#reconnectDevice(org.csstudio.dal.device.AbstractDevice)
     */
    public void reconnectDevice(AbstractDevice device)
        throws ConnectionException
    {
        if (!Connectable.class.isAssignableFrom(device.getClass())) {
            throw new ConnectionException(device,
                "Device " + device.getUniqueName() + " is not Connectable.");
        }

        if (!DeviceBean.class.isAssignableFrom(device.getClass())) {
            throw new ConnectionException(device,
                "Device " + device.getUniqueName()
                + " is not DeviceBean based implementation.");
        }

        Connectable c = (Connectable)device;

        if (c.getConnectionState() != ConnectionState.READY) {
            throw new ConnectionException(device,
                "Device " + device.getUniqueName()
                + " is not in Ready mode, but in " + c.getConnectionState()
                + ".");
        }

        connect(device.getUniqueName(),
            (Class<?extends AbstractDevice>)device.getClass(),
            (Class<?extends AbstractDevice>)device.getClass(),
            (AbstractDeviceImpl)device);
    }

    protected void destroyAll()
    {
        family.destroyAll();
    }
}

/* __oOo__ */
