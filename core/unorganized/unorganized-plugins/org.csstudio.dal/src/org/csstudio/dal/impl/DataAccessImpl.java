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

package org.csstudio.dal.impl;

import org.csstudio.dal.DataAccess;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.proxy.AbstractPlug;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.proxy.PropertyProxyWrapper;
import org.csstudio.dal.proxy.Proxy;
import org.csstudio.dal.proxy.SyncPropertyProxy;

import com.cosylab.util.ListenerList;


/**
 * Default implementation of <code>DataAccess</code> interface.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public abstract class DataAccessImpl<T> implements DataAccess<T>
{
    protected PropertyProxy<T,?> proxy=null;
    protected SyncPropertyProxy<T,?> sproxy=null;
    protected Class<T> valClass=null;
    private ListenerList dvListeners=null;
    protected T lastValue=null;

    /**
         * Constructor.
         * @param valClass datatype class
         */
    protected DataAccessImpl(Class<T> valClass)
    {
        this.valClass = valClass;
    }

    /**
     * Initializes this instance. Before data access is initialized, it
     * will throw exceptions if used.
     *
     * @param proxy the proxy which presents remote connection
     *
     * @throws NullPointerException is thrown if supplied proxy is null
     */
    protected void initialize(PropertyProxy<T,?> proxy)
    {
        if (proxy == null) {
            throw new NullPointerException("proxy");
        }

        this.proxy = proxy;

        if (proxy instanceof SyncPropertyProxy) {
            sproxy = (SyncPropertyProxy<T,?>)proxy;
        } else {
            sproxy = new PropertyProxyWrapper<T, AbstractPlug>((PropertyProxy<T, AbstractPlug>) proxy);
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#addDynamicValueListener(org.csstudio.dal.DynamicValueListener)
     */
    public <P extends SimpleProperty<T>> void addDynamicValueListener(DynamicValueListener<T, P> l)
    {
        getDvListeners().add(l);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#removeDynamicValueListener(org.csstudio.dal.DynamicValueListener)
     */
    public <P extends SimpleProperty<T>> void removeDynamicValueListener(DynamicValueListener<T, P> l)
    {
        getDvListeners().remove(l);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#getDynamicValueListeners()
     */
    public DynamicValueListener<T, SimpleProperty<T>>[] getDynamicValueListeners()
    {
        if (hasDynamicValueListeners()) {
            return (DynamicValueListener<T, SimpleProperty<T>>[])getDvListeners().toArray(new DynamicValueListener[getDvListeners()
                                                                                                                   .size()]);
        }
        return new DynamicValueListener[0];
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#getDataType()
     */
    public Class<T> getDataType()
    {
        //by now, Java does not support geting class object out of type parameter
        //this is the only solution
        return valClass;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#isSettable()
     */
    public boolean isSettable()
    {
        if (proxy == null) throw new IllegalStateException("Proxy is null");
        return proxy.isSettable();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#setValue(T)
     */
    public void setValue(T value) throws DataExchangeException
    {
        if (sproxy == null || !sproxy.getConnectionState().isConnected()) {
            throw new DataExchangeException(this, "Proxy not connected");
        }
        sproxy.setValueSync(value);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#getValue()
     */
    public T getValue() throws DataExchangeException
    {
        if (sproxy == null || !sproxy.getConnectionState().isConnected()) {
            throw new DataExchangeException(this, "Proxy not connected");
        }

        return lastValue=sproxy.getValueSync();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#getLatestReceivedValue()
     */
    public T getLatestReceivedValue()
    {
        return lastValue;
    }

    /**
     * Returns the PropertyProxy which represents the remote connection of this
     * DataAccess.
     *
     * @return the property proxy
     */
    public PropertyProxy<T,?> getProxy()
    {
        return proxy;
    }

    /**
     * Releases the PropertyProxy which represents remote connection of this
     * DataAccess and disconnects all listeners and monitors. But it does not call destroy on proxy itself
     *
     *  @param boolean if <code>true</code> then if possible property should do also final
     *  cleanup and destroy for internal structure, if <code>false</code> only proxies are release and
     *  property is prepared for connection to new proxy.
     *
     * @return the property proxy
     */
    public Proxy<?>[] releaseProxy(boolean destroy) {
        Proxy<?>[] temp = new Proxy<?>[]{proxy};
        proxy = null;
        sproxy = null;
        if (destroy && hasDynamicValueListeners()) {
            getDvListeners().clear();
        }
        return temp;
    }

    protected ListenerList getDvListeners() {
        if (dvListeners==null) {
            synchronized (this) {
                if (dvListeners==null) {
                    dvListeners= new ListenerList(DynamicValueListener.class);
                }
            }
        }
        return dvListeners;
    }

    public boolean hasDynamicValueListeners() {
        return dvListeners!=null && dvListeners.size()>0;
    }
}

/* __oOo__ */
